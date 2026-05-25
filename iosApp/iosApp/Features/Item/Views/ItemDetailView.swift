import SwiftUI

struct ItemDetailView: View {
    let itemId: Int
    let onBack: () -> Void
    let onNavigate: (ItemRoute) -> Void
    let onMutationCompleted: () -> Void

    @StateObject private var viewModel: ItemDetailViewModel
    @State private var stockDraftQuantity = 0
    @State private var isStockSheetPresented = false
    @State private var completionModal: ItemDetailCompletionModalData?
    @State private var snackbar: ItemDetailSnackbarPresentation?
    @State private var snackbarDismissTask: Task<Void, Never>?

    @MainActor
    init(
        itemId: Int,
        viewModelFactory: @MainActor @escaping (Int) -> ItemDetailViewModel,
        onBack: @escaping () -> Void,
        onNavigate: @escaping (ItemRoute) -> Void,
        onMutationCompleted: @escaping () -> Void = {}
    ) {
        self.itemId = itemId
        self.onBack = onBack
        self.onNavigate = onNavigate
        self.onMutationCompleted = onMutationCompleted
        _viewModel = StateObject(wrappedValue: viewModelFactory(itemId))
    }

    @MainActor
    init(
        itemId: Int,
        onBack: @escaping () -> Void,
        onNavigate: @escaping (ItemRoute) -> Void
    ) {
        self.init(
            itemId: itemId,
            viewModelFactory: AppDependencies.preview.makeItemDetailViewModel,
            onBack: onBack,
            onNavigate: onNavigate
        )
    }

    var body: some View {
        ZStack {
            content
            overlay
            snackbarOverlay
        }
        .onChange(of: viewModel.effect) { _, effect in
            handleEffect(effect)
        }
        .onDisappear {
            snackbarDismissTask?.cancel()
        }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ItemDetailMessageView(title: "소모품 정보를 불러오는 중이에요")
        case let .loadFailed(message):
            ItemDetailMessageView(title: message, buttonTitle: "다시 시도", action: viewModel.retry)
        case let .success(data):
            ItemDetailScreenView(
                data: data,
                action: ItemDetailViewAction(
                    onBack: onBack,
                    moreMenuItems: data.moreMenuItems,
                    onSelectMoreMenuItem: viewModel.selectMoreMenuItem,
                    onManageStock: {
                        stockDraftQuantity = data.spareDraft.quantity
                        isStockSheetPresented = true
                    },
                    onCompleteReplacement: viewModel.requestReplacementCompletion
                )
            )
        }
    }

    @ViewBuilder
    private var overlay: some View {
        if case let .success(data) = viewModel.state {
            if isStockSheetPresented {
                OBRitColors.backgroundDefaultDimDefault
                    .ignoresSafeArea()
                    .onTapGesture {
                        isStockSheetPresented = false
                    }

                VStack {
                    Spacer(minLength: 0)
                    ItemDetailStockManagementSheet(
                        itemName: data.item.name,
                        initialQuantity: data.spareDraft.quantity,
                        quantity: $stockDraftQuantity,
                        isProcessing: data.isProcessing,
                        onCommit: { quantity in
                            isStockSheetPresented = false
                            viewModel.updateSpareQuantity(quantity)
                        },
                        onDismiss: {
                            isStockSheetPresented = false
                        }
                    )
                }
                .ignoresSafeArea(.container, edges: .bottom)
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }

            if let confirmationDialog = data.confirmationDialog {
                modalDim

                confirmationModal(for: confirmationDialog, data: data)
                    .transition(.scale.combined(with: .opacity))
            }

            if let completionModal {
                modalDim

                completionModalView(completionModal)
                    .transition(.scale.combined(with: .opacity))
            }
        }
    }

    private var modalDim: some View {
        OBRitColors.backgroundDefaultDimDefault
            .ignoresSafeArea()
            .onTapGesture {
                viewModel.dismissConfirmationDialog()
                completionModal = nil
            }
    }

    @ViewBuilder
    private func confirmationModal(
        for dialog: ItemDetailConfirmationDialog,
        data: ItemDetailViewData
    ) -> some View {
        switch dialog {
        case .delete:
            ItemDetailConfirmationModal(
                kind: .delete,
                isProcessing: data.isProcessing,
                onCancel: viewModel.dismissConfirmationDialog,
                onConfirm: viewModel.confirmCurrentDialog
            )
        }
    }

    @ViewBuilder
    private var snackbarOverlay: some View {
        if let snackbar {
            VStack {
                Spacer(minLength: 0)

                OBRitSnackbar(message: snackbar.message, icon: snackbar.icon)
                    .padding(.horizontal, OBRitSpacing.s5)
                    .padding(.bottom, ItemDetailLayout.actionBarHeight + OBRitSpacing.s5)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
            .allowsHitTesting(false)
            .animation(.easeOut(duration: 0.18), value: snackbar.id)
        }
    }

    @ViewBuilder
    private func completionModalView(
        _ modal: ItemDetailCompletionModalData
    ) -> some View {
        if modal.isLowStock {
            ItemDetailReplacementCompletionModal(
                itemName: modal.itemName,
                itemImageAssetName: modal.imageAssetName,
                remainingSpareQuantity: modal.remainingSpareQuantity,
                recordedAtText: modal.recordedAtText,
                onConfirm: { completionModal = nil },
                onCancel: { completionModal = nil }
            )
        } else {
            ItemDetailReplacementCompletionModal(
                itemName: modal.itemName,
                itemImageAssetName: modal.imageAssetName,
                daysComparedToPrevious: modal.daysComparedToPrevious,
                nextReplacementLabel: modal.nextReplacementLabel,
                recordedAtText: modal.recordedAtText,
                onConfirm: { completionModal = nil },
                onCancel: { completionModal = nil }
            )
        }
    }

    private func handleEffect(_ effect: ItemDetailViewEffect?) {
        guard let effect else { return }
        defer { viewModel.clearEffect() }

        switch effect {
        case let .navigate(destination):
            handleNavigation(destination)
        case .itemDeleted:
            onMutationCompleted()
            onBack()
        case .replacementCompleted:
            onMutationCompleted()
            if case let .success(data) = viewModel.state {
                completionModal = ItemDetailCompletionModalData(data: data)
            }
        case let .itemUpdated(message):
            onMutationCompleted()
            showSnackbar(message)
        case let .showMessage(message):
            showSnackbar(message)
        }
    }

    private func showSnackbar(_ message: String) {
        snackbarDismissTask?.cancel()
        snackbar = ItemDetailSnackbarPresentation(
            message: message,
            icon: snackbarIcon(for: message)
        )
        snackbarDismissTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: ItemDetailSnackbarMetrics.displayDurationNanoseconds)
            guard !Task.isCancelled else { return }
            snackbar = nil
        }
    }

    private func snackbarIcon(for message: String) -> OBRitSnackbarIcon {
        message.hasSuffix("했어요.") ? .success : .error
    }

    private func handleNavigation(_ destination: ItemDetailDestination) {
        switch destination {
        case let .statusInfo(itemId):
            onNavigate(.statusInfo(itemId: itemId))
        case let .edit(itemId):
            onNavigate(.edit(itemId: itemId))
        case let .spareEdit(itemId):
            onNavigate(.spareEdit(itemId: itemId))
        case let .notification(itemId):
            onNavigate(.notification(itemId: itemId))
        }
    }
}

private struct ItemDetailSnackbarPresentation: Identifiable {
    let id = UUID()
    let message: String
    let icon: OBRitSnackbarIcon
}

private enum ItemDetailSnackbarMetrics {
    static let displayDurationNanoseconds: UInt64 = 2_400_000_000
}

private struct ItemDetailMessageView: View {
    let title: String
    var buttonTitle: String?
    var action: (() -> Void)?

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            Text(title)
                .multilineTextAlignment(.center)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)

            if let buttonTitle, let action {
                OBRitFilledTextButton(text: buttonTitle, size: .middle, action: action)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }
}
