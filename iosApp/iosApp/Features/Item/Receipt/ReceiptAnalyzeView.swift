import PhotosUI
import SwiftUI

struct ReceiptAnalyzeView: View {
    @StateObject private var viewModel: ReceiptAnalyzeViewModel
    @StateObject private var kindPicker: ItemRegistrationViewModel
    @State private var showsKindPicker = false
    @State private var additionalItems: [ReceiptAnalyzeResultItem] = []
    @StateObject private var cameraController = ReceiptCameraController()
    @State private var selectedPhotoItem: PhotosPickerItem?
    @State private var isPhotoPickerPresented = false
    @State private var successStep: ReceiptAnalyzeSuccessStep = .analysisResult
    @State private var removedResultItemIDs: Set<Int> = []
    @State private var currentResult: ReceiptAnalyzeResult?

    let onBack: () -> Void
    let onDirectRegistration: () -> Void
    let onComplete: () -> Void

    @MainActor
    init(
        viewModelFactory: @MainActor @escaping () -> ReceiptAnalyzeViewModel,
        kindPickerFactory: @MainActor @escaping () -> ItemRegistrationViewModel = { ItemRegistrationViewModel() },
        onBack: @escaping () -> Void,
        onDirectRegistration: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        _kindPicker = StateObject(wrappedValue: kindPickerFactory())
        self.onBack = onBack
        self.onDirectRegistration = onDirectRegistration
        self.onComplete = onComplete
    }

    @MainActor
    init(
        onBack: @escaping () -> Void,
        onDirectRegistration: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        self.init(
            viewModelFactory: AppDependencies.preview.makeReceiptAnalyzeViewModel,
            onBack: onBack,
            onDirectRegistration: onDirectRegistration,
            onComplete: onComplete
        )
    }

    var body: some View {
        ZStack {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            VStack(spacing: 0) {
                if showsTopBar {
                    OBRitDepthTopBar(
                        title: "소모품 등록",
                        backgroundColor: false,
                        showRightButton: false,
                        onBackClick: handleTopBarBack
                    )
                }

                content
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .overlay {
            if showsKindPicker {
                ItemKindPickerOverlay(viewModel: kindPicker, onDismiss: { showsKindPicker = false }, onSelect: appendKind)
            }
        }
        .photosPicker(
            isPresented: $isPhotoPickerPresented,
            selection: $selectedPhotoItem,
            matching: .images
        )
        .onChange(of: selectedPhotoItem) { _, item in
            guard let item else { return }
            loadPhoto(item)
        }
        .onChange(of: viewModel.state) { _, state in
            if case let .success(result) = state {
                if currentResult != result {
                    currentResult = result
                    successStep = .analysisResult
                    removedResultItemIDs = []
                    additionalItems = []
                    viewModel.clearRegistrationError()
                }
                return
            }
            currentResult = nil
            successStep = .analysisResult
            removedResultItemIDs = []
        }
    }

    private var showsTopBar: Bool {
        if case .idle = viewModel.state {
            return false
        }
        return true
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .idle:
            ReceiptCameraCaptureView(
                cameraController: cameraController,
                onBack: onBack,
                onCapture: viewModel.analyze,
                onOpenPhotoLibrary: {
                    isPhotoPickerPresented = true
                }
            )
        case .processing:
            ReceiptAnalyzeProcessingView()
        case let .success(result):
            switch successStep {
            case .analysisResult:
                ReceiptAnalyzeResultView(
                    result: ReceiptAnalyzeResult(receiptImageURL: result.receiptImageURL, purchasedDate: result.purchasedDate, items: result.items + additionalItems),
                    removedItemIDs: removedResultItemIDs,
                    onRemoveItem: removeResultItem,
                    onNextStep: { detailResult in
                        successStep = .detailInput(detailResult)
                        viewModel.clearRegistrationError()
                    },
                    onDirectRegistration: { showsKindPicker = true }
                )
            case let .detailInput(detailResult):
                ReceiptAnalyzeDetailInputView(
                    result: detailResult,
                    isSubmitting: viewModel.isRegistering,
                    errorMessage: viewModel.registrationErrorMessage,
                    onRegister: { draft in
                        viewModel.registerReceiptItems(draft: draft, onComplete: onComplete)
                    }
                )
            }
        case let .failure(message):
            ReceiptAnalyzeFailureView(
                message: message,
                onRetry: viewModel.reset,
                onDirectRegistration: onDirectRegistration
            )
        }
    }

    private func appendKind(_ kind: ItemKind) {
        guard case let .success(result) = viewModel.state else { return }
        let id = ((result.items + additionalItems).map(\.id).max() ?? 0) + 1
        let name = ItemRegistrationNameRule.availableName(for: kind.title, existingNames: (result.items + additionalItems).map(\.suggestedName))
        additionalItems.append(ReceiptAnalyzeResultItem(id: id, suggestedName: name, suggestedCategoryName: kind.title, categoryId: kind.id, iconURL: kind.imageURL, quantity: 1, suggestedReplacementIntervalDays: 30))
        showsKindPicker = false
    }

    private func loadPhoto(_ item: PhotosPickerItem) {
        Task {
            defer {
                selectedPhotoItem = nil
            }

            guard let data = try? await item.loadTransferable(type: Data.self),
                  let image = UIImage(data: data) else {
                return
            }
            viewModel.analyze(image: image)
        }
    }

    private func handleTopBarBack() {
        if case .success = viewModel.state,
           successStep.isDetailInput {
            successStep = .analysisResult
            viewModel.clearRegistrationError()
            return
        }

        onBack()
    }

    private func removeResultItem(_ item: ReceiptAnalyzeResultItem) {
        removedResultItemIDs.insert(item.id)
    }
}

private enum ReceiptAnalyzeSuccessStep {
    case analysisResult
    case detailInput(ReceiptAnalyzeResult)

    var isDetailInput: Bool {
        if case .detailInput = self {
            return true
        }
        return false
    }
}

private struct ReceiptAnalyzeProcessingView: View {
    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            ProgressView()
                .tint(OBRitColors.green300)
            Text("영수증을 분석하는 중이에요")
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct ReceiptAnalyzeFailureView: View {
    let message: String
    let onRetry: () -> Void
    let onDirectRegistration: () -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            Text(message)
                .multilineTextAlignment(.center)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)

            VStack(spacing: OBRitSpacing.s3) {
                OBRitFilledTextButton(text: "다시 선택하기", size: .middle, action: onRetry)
                Button("직접 등록하기", action: onDirectRegistration)
                    .buttonStyle(.plain)
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultSecondary)
            }
        }
        .padding(OBRitSpacing.s5)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview("Receipt Analyze") {
    ReceiptAnalyzeView(onBack: {}, onDirectRegistration: {}, onComplete: {})
}
