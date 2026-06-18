import Foundation
import Shared

protocol ReceiptAnalyzeRepository {
    func analyze(imageData: Data, fileName: String) async throws -> ReceiptAnalyzeResult
}

actor ReceiptAnalyzeSampleRepository: ReceiptAnalyzeRepository {
    func analyze(imageData: Data, fileName: String) async throws -> ReceiptAnalyzeResult {
        ReceiptAnalyzeResult(
            receiptImageURL: "",
            purchasedDate: "2026-01-01",
            items: [
                ReceiptAnalyzeResultItem(
                    id: 0,
                    suggestedName: "면도기",
                    suggestedCategoryName: "면도기",
                    categoryId: 1,
                    iconURL: "",
                    quantity: 0,
                    suggestedReplacementIntervalDays: 30
                ),
                ReceiptAnalyzeResultItem(
                    id: 1,
                    suggestedName: "정수기 필터",
                    suggestedCategoryName: "정수기 필터",
                    categoryId: 2,
                    iconURL: "",
                    quantity: 0,
                    suggestedReplacementIntervalDays: 90
                ),
                ReceiptAnalyzeResultItem(
                    id: 2,
                    suggestedName: "칫솔",
                    suggestedCategoryName: "칫솔",
                    categoryId: 3,
                    iconURL: "",
                    quantity: 0,
                    suggestedReplacementIntervalDays: 30
                ),
                ReceiptAnalyzeResultItem(
                    id: 3,
                    suggestedName: "세탁 세제",
                    suggestedCategoryName: "세탁 세제",
                    categoryId: 4,
                    iconURL: "",
                    quantity: 0,
                    suggestedReplacementIntervalDays: 60
                )
            ]
        )
    }
}

actor SharedReceiptAnalyzeRepository: ReceiptAnalyzeRepository {
    private let receiptService: SharedReceiptService

    init(receiptService: SharedReceiptService) {
        self.receiptService = receiptService
    }

    func analyze(imageData: Data, fileName: String) async throws -> ReceiptAnalyzeResult {
        let event = "SharedReceiptAnalyzeRepository.analyze"
        let details = "fileName=\(fileName) bytes=\(imageData.count)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            let analysis = try await receiptService.analyzeReceipt(
                image: imageData.kotlinByteArray,
                fileName: fileName
            )
            let result = ReceiptAnalyzeResult(analysis: analysis)
            AppLog.success(AppLog.swiftRepository, event, "itemCount=\(result.items.count)")
            return result
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }
}

private extension ReceiptAnalyzeResult {
    init(analysis: Shared.ReceiptAnalysis) {
        self.init(
            receiptImageURL: analysis.receiptImageUrl,
            purchasedDate: analysis.purchasedDate,
            items: analysis.items.enumerated().map { index, item in
                ReceiptAnalyzeResultItem(
                    id: index,
                    suggestedName: item.suggestedName,
                    suggestedCategoryName: item.suggestedCategoryName,
                    categoryId: item.categoryId.map { Int(clamping: $0.int64Value) },
                    iconURL: item.iconUrl,
                    quantity: Int(item.quantity),
                    suggestedReplacementIntervalDays: Int(item.suggestedReplacementIntervalDays)
                )
            }
        )
    }
}

private extension Data {
    var kotlinByteArray: KotlinByteArray {
        let array = KotlinByteArray(size: Int32(count))
        for (index, byte) in enumerated() {
            array.set(index: Int32(index), value: Int8(bitPattern: byte))
        }
        return array
    }
}
