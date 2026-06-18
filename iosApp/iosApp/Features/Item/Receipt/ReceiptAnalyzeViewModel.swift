import Foundation
import SwiftUI

@MainActor
final class ReceiptAnalyzeViewModel: ObservableObject {
    @Published private(set) var state: ReceiptAnalyzeViewState = .idle

    private let repository: ReceiptAnalyzeRepository

    init(repository: ReceiptAnalyzeRepository = ReceiptAnalyzeSampleRepository()) {
        self.repository = repository
    }

    func analyze(image: UIImage) {
        state = .processing
        Task {
            await analyzeTask(image: image)
        }
    }

    func reset() {
        state = .idle
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
