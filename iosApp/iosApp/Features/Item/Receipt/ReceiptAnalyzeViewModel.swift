import Foundation
import SwiftUI

@MainActor
final class ReceiptAnalyzeViewModel: ObservableObject {
    @Published private(set) var state: ReceiptAnalyzeViewState = .idle
    @Published private(set) var isRegistering = false
    @Published private(set) var registrationErrorMessage: String?

    private let repository: ReceiptAnalyzeRepository
    private let writeRepository: ItemRegistrationWriteRepository?

    init(
        repository: ReceiptAnalyzeRepository = ReceiptAnalyzeSampleRepository(),
        writeRepository: ItemRegistrationWriteRepository? = nil
    ) {
        self.repository = repository
        self.writeRepository = writeRepository
    }

    func analyze(image: UIImage) {
        state = .processing
        Task {
            await analyzeTask(image: image)
        }
    }

    func reset() {
        state = .idle
        isRegistering = false
        registrationErrorMessage = nil
    }

    func clearRegistrationError() {
        registrationErrorMessage = nil
    }

    func registerReceiptItems(
        draft: ReceiptAnalyzeDetailDraft,
        onComplete: @escaping () -> Void
    ) {
        guard !isRegistering,
              let request = draft.createItemsRequest else {
            registrationErrorMessage = draft.rejectionMessage ?? "필수 정보를 모두 입력해주세요."
            return
        }

        guard let writeRepository else {
            onComplete()
            return
        }

        isRegistering = true
        registrationErrorMessage = nil
        Task {
            await registerReceiptItemsTask(
                request: request,
                repository: writeRepository,
                onComplete: onComplete
            )
        }
    }

    private func analyzeTask(image: UIImage) async {
        let event = "ReceiptAnalyzeViewModel.analyzeTask"
        AppLog.enter(AppLog.itemRegistrationViewModel, event)
        do {
            let imageData = try ReceiptImagePreprocessor.jpegData(from: image)
            let result = try await repository.analyze(
                imageData: imageData,
                fileName: Self.fileName()
            )
            state = .success(result)
            AppLog.success(
                AppLog.itemRegistrationViewModel,
                event,
                "bytes=\(imageData.count) itemCount=\(result.items.count)"
            )
        } catch {
            state = .failure(message: error.receiptAnalyzeMessage)
            AppLog.failure(AppLog.itemRegistrationViewModel, event, error)
        }
    }

    private static func fileName() -> String {
        "receipt-\(Int(Date.now.timeIntervalSince1970)).jpg"
    }

    private func registerReceiptItemsTask(
        request: ItemRegistrationCreateItemsRequest,
        repository: ItemRegistrationWriteRepository,
        onComplete: @escaping () -> Void
    ) async {
        let event = "ReceiptAnalyzeViewModel.registerReceiptItemsTask"
        AppLog.enter(
            AppLog.itemRegistrationViewModel,
            event,
            "count=\(request.items.count) receiptImageURL=\(request.receiptImageURL != nil)"
        )
        do {
            try await repository.createItems(request: request)
            isRegistering = false
            AppLog.success(AppLog.itemRegistrationViewModel, event, "count=\(request.items.count)")
            onComplete()
        } catch {
            isRegistering = false
            registrationErrorMessage = error.receiptAnalyzeMessage
            AppLog.failure(AppLog.itemRegistrationViewModel, event, error)
        }
    }
}

private extension Error {
    var receiptAnalyzeMessage: String {
        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        }

        return "영수증을 분석하지 못했어요."
    }
}
