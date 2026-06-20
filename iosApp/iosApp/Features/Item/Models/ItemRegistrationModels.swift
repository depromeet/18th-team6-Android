import Foundation

struct ItemKind: Identifiable, Equatable {
    let id: Int
    let title: String
    let addedCount: Int
    let imageURL: String
}

struct ItemImageOption: Identifiable, Equatable {
    let id: Int
    let imageURL: String
}

struct ItemRegistrationDraft: Equatable {
    var selectedKind: ItemKind?
    var itemName: String
    var lastReplacementDateOption: ItemReplacementDateOption?
    var quantity: Int
    var directKindName: String
    var selectedImageOption: ItemImageOption?
}

struct ItemRegistrationCreateItemRequest: Equatable {
    let categoryId: Int
    let name: String
    let quantity: Int
    let lastReplacementPeriod: ItemRegistrationLastReplacementPeriod?
}

struct ItemRegistrationCreateItemsRequest: Equatable {
    let items: [ItemRegistrationCreateItemsRequestItem]
    let receiptImageURL: String?
}

struct ItemRegistrationCreateItemsRequestItem: Equatable {
    let categoryId: Int?
    let name: String
    let quantity: Int
    let lastReplacementPeriod: ItemRegistrationLastReplacementPeriod?
    let newCategoryName: String?
    let newCategoryDefaultReplacementIntervalDays: Int?
}

enum ItemRegistrationLastReplacementPeriod: String, Equatable {
    case withinWeek = "WITHIN_WEEK"
    case withinMonth = "WITHIN_MONTH"
    case withinThreeMonths = "WITHIN_THREE_MONTHS"
    case overThreeMonths = "OVER_THREE_MONTHS"
}

enum ItemReplacementDateOption: Int, CaseIterable, Identifiable, Equatable {
    case withinOneWeek
    case twoToFourWeeksAgo
    case oneToThreeMonthsAgo
    case unknown

    var id: Int {
        rawValue
    }

    var title: String {
        switch self {
        case .withinOneWeek:
            return "1주일 이내"
        case .twoToFourWeeksAgo:
            return "2~4주 전"
        case .oneToThreeMonthsAgo:
            return "1~3개월 전"
        case .unknown:
            return "잘 모르겠어요"
        }
    }
}

extension ItemReplacementDateOption {
    var apiPeriod: ItemRegistrationLastReplacementPeriod? {
        switch self {
        case .withinOneWeek:
            .withinWeek
        case .twoToFourWeeksAgo:
            .withinMonth
        case .oneToThreeMonthsAgo:
            .withinThreeMonths
        case .unknown:
            nil
        }
    }
}

enum ItemRegistrationMode: Equatable {
    case form
    case directKind
    case complete
}

enum ItemRegistrationBottomSheet: Equatable {
    case kind
}

enum ItemRegistrationViewState: Equatable {
    case loading
    case loadFailed(message: String)
    case success(ItemRegistrationViewData)
}

struct ItemRegistrationViewData: Equatable {
    var mode: ItemRegistrationMode
    var draft: ItemRegistrationDraft
    var kindSearchQuery: String
    var itemKinds: [ItemKind]
    var imageOptions: [ItemImageOption]
    var bottomSheet: ItemRegistrationBottomSheet?
    var selectedKindCandidate: ItemKind?
    var isProcessing: Bool

    var filteredKinds: [ItemKind] {
        let query = kindSearchQuery.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !query.isEmpty else { return itemKinds }
        return itemKinds.filter {
            $0.title.localizedCaseInsensitiveContains(query)
        }
    }

    var canSubmitForm: Bool {
        draft.selectedKind != nil &&
            !draft.itemName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
            draft.lastReplacementDateOption != nil &&
            draft.quantity >= ItemRegistrationConfig.quantityMinimum &&
            !isProcessing
    }

    var canSubmitDirectKind: Bool {
        !draft.directKindName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
            draft.selectedImageOption != nil &&
            !isProcessing
    }

    var kindCandidateForDisplay: ItemKind? {
        selectedKindCandidate ?? draft.selectedKind
    }
}
