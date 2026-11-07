import SwiftUI

struct ReceiptCameraCaptureView: View {
    @ObservedObject var cameraController: ReceiptCameraController

    let onBack: () -> Void
    let onCapture: (UIImage) -> Void
    let onOpenPhotoLibrary: () -> Void

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                cameraLayer

                ReceiptCameraScanOverlay()
                    .ignoresSafeArea()

                VStack(spacing: 0) {
                    ReceiptCameraTopBar(
                        safeAreaTop: geometry.safeAreaInsets.top,
                        onBack: onBack
                    )

                    Spacer(minLength: 0)

                    ReceiptCameraControls(
                        isCaptureEnabled: cameraController.authorizationState == .authorized,
                        onOpenPhotoLibrary: onOpenPhotoLibrary,
                        onCapture: {
                            cameraController.capturePhoto(onCapture)
                        },
                        onSwitchCamera: cameraController.switchCamera
                    )
                    .padding(.bottom, max(geometry.safeAreaInsets.bottom + 54, 88))
                }

                if cameraController.authorizationState != .authorized {
                    ReceiptCameraUnavailableView(
                        state: cameraController.authorizationState,
                        onOpenPhotoLibrary: onOpenPhotoLibrary
                    )
                    .padding(.horizontal, OBRitSpacing.s5)
                }
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .background(.black)
            .ignoresSafeArea()
            .task {
                cameraController.requestAccessAndStart()
            }
            .onDisappear {
                cameraController.stop()
            }
        }
    }

    @ViewBuilder
    private var cameraLayer: some View {
        if cameraController.authorizationState == .authorized {
            ReceiptCameraPreview(session: cameraController.session)
                .ignoresSafeArea()
                .accessibilityHidden(true)
        } else {
            Color.black.ignoresSafeArea()
        }
    }
}

private struct ReceiptCameraTopBar: View {
    let safeAreaTop: CGFloat
    let onBack: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            Color.clear
                .frame(height: safeAreaTop)

            ZStack {
                Text("영수증 촬영")
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: .white)

                HStack {
                    Button("닫기", systemImage: "xmark", action: onBack)
                        .labelStyle(.iconOnly)
                        .font(.system(size: 22, weight: .regular))
                        .foregroundStyle(.white)
                        .frame(width: 40, height: 40)
                        .contentShape(Rectangle())

                    Spacer(minLength: 0)
                }
                .padding(.horizontal, 12)
            }
            .frame(height: 56)
        }
    }
}

private struct ReceiptCameraControls: View {
    let isCaptureEnabled: Bool
    let onOpenPhotoLibrary: () -> Void
    let onCapture: () -> Void
    let onSwitchCamera: () -> Void

    var body: some View {
        HStack(spacing: 48) {
            ReceiptCameraCircleButton(
                title: "사진 선택",
                systemImage: "photo.on.rectangle",
                action: onOpenPhotoLibrary
            )

            Button("촬영", action: onCapture)
                .buttonStyle(.plain)
                .disabled(!isCaptureEnabled)
                .accessibilityInputLabels(["촬영", "사진 찍기"])
                .overlay {
                    Circle()
                        .stroke(.white, lineWidth: 4)
                        .frame(width: 68, height: 68)
                }
                .background {
                    Circle()
                        .fill(.white)
                        .frame(width: 56, height: 56)
                }
                .frame(width: 68, height: 68)
                .opacity(isCaptureEnabled ? 1 : 0.45)

            ReceiptCameraCircleButton(
                title: "카메라 전환",
                systemImage: "camera.rotate",
                action: onSwitchCamera
            )
            .disabled(!isCaptureEnabled)
            .opacity(isCaptureEnabled ? 1 : 0.45)
        }
    }
}

private struct ReceiptCameraCircleButton: View {
    let title: String
    let systemImage: String
    let action: () -> Void

    var body: some View {
        Button(title, systemImage: systemImage, action: action)
            .labelStyle(.iconOnly)
            .font(.system(size: 24, weight: .semibold))
            .foregroundStyle(.white)
            .frame(width: 60, height: 60)
            .background(.black.opacity(0.4))
            .clipShape(Circle())
            .buttonStyle(.plain)
    }
}

private struct ReceiptCameraScanOverlay: View {
    var body: some View {
        ReceiptCameraScanCutoutShape()
            .fill(.black.opacity(0.42), style: FillStyle(eoFill: true))
    }
}

private struct ReceiptCameraScanCutoutShape: Shape {
    func path(in rect: CGRect) -> Path {
        let cutoutWidth = min(320, max(0, rect.width - 92))
        let cutoutHeight = min(460, rect.height * 0.5)
        let cutoutTop = min(max(216, rect.height * 0.235), rect.maxY - cutoutHeight - 120)
        let cutoutRect = CGRect(
            x: rect.midX - cutoutWidth / 2,
            y: cutoutTop,
            width: cutoutWidth,
            height: cutoutHeight
        )

        var path = Path()
        path.addRect(rect)
        path.addRoundedRect(
            in: cutoutRect,
            cornerSize: CGSize(width: 12, height: 12)
        )
        return path
    }
}

private struct ReceiptCameraUnavailableView: View {
    let state: ReceiptCameraController.AuthorizationState
    let onOpenPhotoLibrary: () -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s4) {
            Text(message)
                .multilineTextAlignment(.center)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: .white)

            OBRitFilledTextButton(
                text: "사진 선택하기",
                size: .middle,
                action: onOpenPhotoLibrary
            )
        }
        .padding(OBRitSpacing.s5)
        .background(.black.opacity(0.55))
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }

    private var message: String {
        switch state {
        case .notDetermined:
            "카메라 권한을 확인하는 중이에요."
        case .authorized:
            ""
        case .denied:
            "카메라 권한이 필요해요.\n설정에서 카메라 접근을 허용해주세요."
        case .unavailable:
            "이 기기에서는 카메라를 사용할 수 없어요."
        }
    }
}
