import AVFoundation
import SwiftUI

final class ReceiptCameraController: NSObject, ObservableObject {
    enum AuthorizationState {
        case notDetermined
        case authorized
        case denied
        case unavailable
    }

    @Published private(set) var authorizationState: AuthorizationState

    let session = AVCaptureSession()

    private let photoOutput = AVCapturePhotoOutput()
    private let sessionQueue = DispatchQueue(label: "com.obrit.receipt.camera.session")
    private var currentInput: AVCaptureDeviceInput?
    private var currentPosition: AVCaptureDevice.Position = .back
    private var isConfigured = false
    private var captureCompletion: ((UIImage) -> Void)?

    override init() {
        if !UIImagePickerController.isSourceTypeAvailable(.camera) {
            self.authorizationState = .unavailable
        } else {
            switch AVCaptureDevice.authorizationStatus(for: .video) {
            case .authorized:
                self.authorizationState = .authorized
            case .notDetermined:
                self.authorizationState = .notDetermined
            case .denied, .restricted:
                self.authorizationState = .denied
            @unknown default:
                self.authorizationState = .denied
            }
        }
        super.init()
    }

    func requestAccessAndStart() {
        switch authorizationState {
        case .authorized:
            start()
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                Task { @MainActor in
                    self?.authorizationState = granted ? .authorized : .denied
                    if granted {
                        self?.start()
                    }
                }
            }
        case .denied, .unavailable:
            break
        }
    }

    func start() {
        sessionQueue.async { [weak self] in
            guard let self else { return }
            do {
                try configureSessionIfNeeded(position: currentPosition)
                if !session.isRunning {
                    session.startRunning()
                }
            } catch {
                Task { @MainActor in
                    self.authorizationState = .unavailable
                }
            }
        }
    }

    func stop() {
        sessionQueue.async { [weak self] in
            guard let self, session.isRunning else { return }
            session.stopRunning()
        }
    }

    func switchCamera() {
        sessionQueue.async { [weak self] in
            guard let self else { return }
            let nextPosition: AVCaptureDevice.Position = currentPosition == .back ? .front : .back
            do {
                try configureSessionIfNeeded(position: nextPosition, forceInputUpdate: true)
                currentPosition = nextPosition
                if !session.isRunning {
                    session.startRunning()
                }
            } catch {
                Task { @MainActor in
                    self.authorizationState = .unavailable
                }
            }
        }
    }

    func capturePhoto(_ completion: @escaping (UIImage) -> Void) {
        captureCompletion = completion
        sessionQueue.async { [weak self] in
            guard let self, session.isRunning else { return }
            let settings = AVCapturePhotoSettings()
            settings.flashMode = .off
            photoOutput.capturePhoto(with: settings, delegate: self)
        }
    }

    private func configureSessionIfNeeded(
        position: AVCaptureDevice.Position,
        forceInputUpdate: Bool = false
    ) throws {
        guard !isConfigured || forceInputUpdate else { return }
        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: position) else {
            throw ReceiptCameraControllerError.cameraUnavailable
        }

        let input = try AVCaptureDeviceInput(device: device)
        session.beginConfiguration()
        session.sessionPreset = .photo

        if let currentInput {
            session.removeInput(currentInput)
        }

        guard session.canAddInput(input) else {
            session.commitConfiguration()
            throw ReceiptCameraControllerError.cameraUnavailable
        }
        session.addInput(input)
        currentInput = input

        if !isConfigured {
            guard session.canAddOutput(photoOutput) else {
                session.commitConfiguration()
                throw ReceiptCameraControllerError.cameraUnavailable
            }
            session.addOutput(photoOutput)
        }

        session.commitConfiguration()
        isConfigured = true
    }
}

extension ReceiptCameraController: AVCapturePhotoCaptureDelegate {
    func photoOutput(
        _ output: AVCapturePhotoOutput,
        didFinishProcessingPhoto photo: AVCapturePhoto,
        error: Error?
    ) {
        guard error == nil,
              let data = photo.fileDataRepresentation(),
              let image = UIImage(data: data) else {
            captureCompletion = nil
            return
        }

        Task { @MainActor in
            captureCompletion?(image)
            captureCompletion = nil
        }
    }
}

private enum ReceiptCameraControllerError: Error {
    case cameraUnavailable
}
