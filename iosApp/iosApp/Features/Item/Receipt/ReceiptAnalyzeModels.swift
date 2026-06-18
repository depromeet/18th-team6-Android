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

struct ReceiptAnalyzeDetailDraft: Equatable {
    let receiptImageURL: String
    let items: [ReceiptAnalyzeDetailDraftItem]

    var canSubmit: Bool {
        rejectionMessage == nil
    }

    var rejectionMessage: String? {
        guard !items.isEmpty else {
            return "등록할 소모품이 없어요."
        }

        if items.contains(where: { $0.trimmedName.isEmpty }) {
            return "소모품 명을 입력해주세요."
        }

        if items.contains(where: { $0.trimmedName.count > ItemRegistrationConfig.itemNameMaxLength }) {
            return "소모품 명은 \(ItemRegistrationConfig.itemNameMaxLength)자 이하로 입력해주세요."
        }

        if items.contains(where: { $0.replacementDateOption == nil }) {
            return "마지막 교체 일자를 선택해주세요."
        }

        if items.contains(where: { $0.quantity < ItemRegistrationConfig.quantityMinimum }) {
            return "등록할 수량은 \(ItemRegistrationConfig.quantityMinimum)개 이상이어야 해요."
        }

        if items.contains(where: { $0.needsNewCategoryName }) {
            return "소모품 종류를 확인할 수 없어요."
        }

        return nil
    }

    var createItemsRequest: ItemRegistrationCreateItemsRequest? {
        guard canSubmit else { return nil }
        return ItemRegistrationCreateItemsRequest(
            items: items.map(\.createItemsRequestItem),
            receiptImageURL: receiptImageURL.trimmingCharacters(in: .whitespacesAndNewlines).nilIfBlank
        )
    }
}

struct ReceiptAnalyzeDetailDraftItem: Equatable {
    let id: Int
    let name: String
    let quantity: Int
    let replacementDateOption: ItemReplacementDateOption?
    let categoryId: Int?
    let newCategoryName: String?
    let newCategoryDefaultReplacementIntervalDays: Int?

    var isComplete: Bool {
        !trimmedName.isEmpty &&
            trimmedName.count <= ItemRegistrationConfig.itemNameMaxLength &&
            quantity >= ItemRegistrationConfig.quantityMinimum &&
            replacementDateOption != nil &&
            !needsNewCategoryName
    }

    var trimmedName: String {
        name.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    var needsNewCategoryName: Bool {
        categoryId == nil && newCategoryName?.nilIfBlank == nil
    }

    fileprivate var createItemsRequestItem: ItemRegistrationCreateItemsRequestItem {
        ItemRegistrationCreateItemsRequestItem(
            categoryId: categoryId,
            name: trimmedName,
            quantity: quantity,
            lastReplacementPeriod: replacementDateOption?.apiPeriod,
            newCategoryName: categoryId == nil ? newCategoryName?.nilIfBlank : nil,
            newCategoryDefaultReplacementIntervalDays: categoryId == nil ? newCategoryDefaultReplacementIntervalDays : nil
        )
    }
}

enum ReceiptAnalyzeViewState: Equatable {
    case idle
    case processing
    case success(ReceiptAnalyzeResult)
    case failure(message: String)
}

private extension String {
    var nilIfBlank: String? {
        let trimmedValue = trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmedValue.isEmpty ? nil : trimmedValue
    }
}
