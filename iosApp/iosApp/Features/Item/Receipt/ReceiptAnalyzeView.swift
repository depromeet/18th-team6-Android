import PhotosUI
import SwiftUI

struct ReceiptAnalyzeView: View {
    @StateObject private var viewModel: ReceiptAnalyzeViewModel
    @State private var selectedPhotoItem: PhotosPickerItem?
    @State private var isCameraPickerPresented = false
    @State private var isPhotoPickerPresented = false

    let onBack: () -> Void
    let onDirectRegistration: () -> Void

    @MainActor
    init(
        viewModelFactory: @MainActor @escaping () -> ReceiptAnalyzeViewModel,
        onBack: @escaping () -> Void,
        onDirectRegistration: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        self.onBack = onBack
        self.onDirectRegistration = onDirectRegistration
    }

    @MainActor
    init(
        onBack: @escaping () -> Void,
        onDirectRegistration: @escaping () -> Void
    ) {
        self.init(
            viewModelFactory: AppDependencies.preview.makeReceiptAnalyzeViewModel,
            onBack: onBack,
            onDirectRegistration: onDirectRegistration
        )
    }

    var body: some View {
        ZStack {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            VStack(spacing: 0) {
                OBRitDepthTopBar(
                    title: "영수증 등록",
                    backgroundColor: false,
                    showRightButton: false,
                    onBackClick: onBack
                )

                content
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .photosPicker(
            isPresented: $isPhotoPickerPresented,
            selection: $selectedPhotoItem,
            matching: .images
        )
        .sheet(isPresented: $isCameraPickerPresented) {
            ReceiptCameraPicker { image in
                viewModel.analyze(image: image)
            }
        }
        .onChange(of: selectedPhotoItem) { _, item in
            guard let item else { return }
            loadPhoto(item)
        }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .idle:
            ReceiptAnalyzeIdleView(
                onOpenCamera: {
                    if UIImagePickerController.isSourceTypeAvailable(.camera) {
                        isCameraPickerPresented = true
                    } else {
                        isPhotoPickerPresented = true
                    }
                },
                onOpenPhotoLibrary: {
                    isPhotoPickerPresented = true
                },
                onDirectRegistration: onDirectRegistration
            )
        case .processing:
            ReceiptAnalyzeProcessingView()
        case let .success(result):
            ReceiptAnalyzeResultView(result: result, onRetry: viewModel.reset)
        case let .failure(message):
            ReceiptAnalyzeFailureView(
                message: message,
                onRetry: viewModel.reset,
                onDirectRegistration: onDirectRegistration
            )
        }
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
}

private struct ReceiptAnalyzeIdleView: View {
    let onOpenCamera: () -> Void
    let onOpenPhotoLibrary: () -> Void
    let onDirectRegistration: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s7) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                Text("영수증 사진을\n분석할게요")
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(
                        OBRitTypography.s6xl,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.textDefaultDefault
                    )

                Text("사진을 고르거나 촬영하면 업로드 가능한 JPEG로 변환한 뒤 분석해요")
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.textDefaultSecondary
                    )
            }

            VStack(spacing: OBRitSpacing.s3) {
                ReceiptAnalyzeActionButton(
                    title: "촬영하기",
                    subtitle: "카메라로 영수증을 찍어요",
                    systemImage: "camera",
                    action: onOpenCamera
                )

                ReceiptAnalyzeActionButton(
                    title: "사진 선택하기",
                    subtitle: "앨범에서 영수증 이미지를 골라요",
                    systemImage: "photo.on.rectangle",
                    action: onOpenPhotoLibrary
                )
            }

            Spacer(minLength: 0)

            OBRitFilledTextButton(text: "직접 등록하기", size: .middle, action: onDirectRegistration)
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.top, OBRitSpacing.s5)
        .padding(.bottom, OBRitSpacing.s6)
    }
}

private struct ReceiptAnalyzeActionButton: View {
    let title: String
    let subtitle: String
    let systemImage: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: OBRitSpacing.s4) {
                Image(systemName: systemImage)
                    .font(.title3.weight(.semibold))
                    .foregroundStyle(OBRitColors.green300)
                    .frame(width: 44, height: 44)
                    .background(OBRitColors.gray750)
                    .clipShape(Circle())

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(title)
                        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    Text(subtitle)
                        .fixedSize(horizontal: false, vertical: true)
                        .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                }

                Spacer(minLength: 0)

                Image(systemName: "chevron.right")
                    .font(.body.weight(.bold))
                    .foregroundStyle(OBRitColors.common00)
            }
            .padding(OBRitSpacing.s5)
            .background(OBRitColors.backgroundDefaultSecondary)
            .clipShape(RoundedRectangle(cornerRadius: 16))
        }
        .buttonStyle(.plain)
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

private struct ReceiptAnalyzeResultView: View {
    let result: ReceiptAnalyzeResult
    let onRetry: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
                Text("분석된 소모품")
                    .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)

                Text("\(result.items.count)개 항목을 찾았어요")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            }
            .padding(.horizontal, OBRitSpacing.s5)

            ScrollView {
                LazyVStack(spacing: OBRitSpacing.s3) {
                    ForEach(result.items) { item in
                        ReceiptAnalyzeResultItemRow(item: item)
                    }
                }
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.bottom, OBRitSpacing.s6)
            }

            OBRitFilledTextButton(text: "다른 영수증 선택하기", size: .middle, action: onRetry)
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.bottom, OBRitSpacing.s6)
        }
        .padding(.top, OBRitSpacing.s5)
    }
}

private struct ReceiptAnalyzeResultItemRow: View {
    let item: ReceiptAnalyzeResultItem

    var body: some View {
        HStack(alignment: .top, spacing: OBRitSpacing.s4) {
            OBRitRemoteImage(urlString: item.iconURL) {
                Image(systemName: "shippingbox")
                    .font(.title3.weight(.semibold))
                    .foregroundStyle(OBRitColors.textDefaultSecondary)
            }
            .frame(width: 44, height: 44)
            .clipShape(Circle())

            VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                Text(item.suggestedName)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                Text("\(item.suggestedCategoryName) · \(item.quantity)개 · \(item.suggestedReplacementIntervalDays)일 주기")
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            }

            Spacer(minLength: 0)
        }
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultSecondary)
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }
}

private struct ReceiptCameraPicker: UIViewControllerRepresentable {
    let onImagePicked: (UIImage) -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(onImagePicked: onImagePicked)
    }

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {
    }

    final class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let onImagePicked: (UIImage) -> Void

        init(onImagePicked: @escaping (UIImage) -> Void) {
            self.onImagePicked = onImagePicked
        }

        func imagePickerController(
            _ picker: UIImagePickerController,
            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]
        ) {
            if let image = info[.originalImage] as? UIImage {
                onImagePicked(image)
            }
            picker.dismiss(animated: true)
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            picker.dismiss(animated: true)
        }
    }
}

#Preview("Receipt Analyze") {
    ReceiptAnalyzeView(onBack: {}, onDirectRegistration: {})
}
