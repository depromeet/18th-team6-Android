import Foundation

struct ItemKind: Identifiable, Equatable {
    let id: Int
    let title: String
    let addedCount: Int
    let imageAssetName: String
}

struct ItemImageOption: Identifiable, Equatable {
    let id: Int
    let assetName: String
}

struct ItemRegistrationDraft: Equatable {
    var selectedKind: ItemKind?
    var itemName: String
    var lastReplacementDateOption: ItemReplacementDateOption?
    var quantity: Int
    var directKindName: String
    var selectedImageOption: ItemImageOption?
}

enum ItemReplacementDateOption: Int, CaseIterable, Identifiable, Equatable {
    case today
    case oneWeekAgo
    case twoWeeksAgo
    case oneMonthAgo

    var id: Int {
        rawValue
    }

    var title: String {
        switch self {
        case .today:
            return "오늘"
        case .oneWeekAgo:
            return "7일 전"
        case .twoWeeksAgo:
            return "14일 전"
        case .oneMonthAgo:
            return "30일 전"
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
    case success(ItemRegistrationViewData)
}

struct ItemRegistrationViewData: Equatable {
    var mode: ItemRegistrationMode
    var draft: ItemRegistrationDraft
    var kindSearchQuery: String
    var itemKinds: [ItemKind]
    let imageOptions: [ItemImageOption]
    var bottomSheet: ItemRegistrationBottomSheet?
    var selectedKindCandidate: ItemKind?

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
            draft.quantity >= ItemRegistrationConfig.quantityMinimum
    }

    var canSubmitDirectKind: Bool {
        !draft.directKindName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
            draft.selectedImageOption != nil
    }

    var kindCandidateForDisplay: ItemKind? {
        selectedKindCandidate ?? draft.selectedKind
    }
}
