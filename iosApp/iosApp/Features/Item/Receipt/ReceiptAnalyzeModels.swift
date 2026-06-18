import Foundation

struct ReceiptAnalyzeResultItem: Identifiable, Equatable {
    let id: Int
    let suggestedName: String
    let suggestedCategoryName: String
    let categoryId: Int?
    let iconURL: String
    let quantity: Int
    let suggestedReplacementIntervalDays: Int
}

struct ReceiptAnalyzeResult: Equatable {
    let receiptImageURL: String
    let purchasedDate: String?
    let items: [ReceiptAnalyzeResultItem]
}

enum ReceiptAnalyzeViewState: Equatable {
    case idle
    case processing
    case success(ReceiptAnalyzeResult)
    case failure(message: String)
}
