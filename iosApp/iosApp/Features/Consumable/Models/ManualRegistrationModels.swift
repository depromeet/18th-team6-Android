import Foundation

struct ManualConsumableKind: Identifiable, Equatable {
    let id: Int
    let title: String
    let addedCount: Int
    let imageAssetName: String
}

struct ManualConsumableImageOption: Identifiable, Equatable {
    let id: Int
    let assetName: String
}

struct ManualRegistrationDraft: Equatable {
    var selectedKind: ManualConsumableKind?
    var itemName: String
    var lastReplacementDateOption: ManualReplacementDateOption?
    var quantity: Int
    var directKindName: String
    var selectedImageOption: ManualConsumableImageOption?
}

enum ManualReplacementDateOption: Int, CaseIterable, Identifiable, Equatable {
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

enum ManualRegistrationMode: Equatable {
    case form
    case directKind
    case complete
}

enum ManualRegistrationBottomSheet: Equatable {
    case kind
}

enum ManualRegistrationViewState: Equatable {
    case success(ManualRegistrationViewData)
}

struct ManualRegistrationViewData: Equatable {
    let mode: ManualRegistrationMode
    let draft: ManualRegistrationDraft
    let kindSearchQuery: String
    let consumableKinds: [ManualConsumableKind]
    let filteredKinds: [ManualConsumableKind]
    let imageOptions: [ManualConsumableImageOption]
    let bottomSheet: ManualRegistrationBottomSheet?
    let selectedKindCandidate: ManualConsumableKind?

    var canSubmitForm: Bool {
        draft.selectedKind != nil &&
            !draft.itemName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
            draft.lastReplacementDateOption != nil &&
            draft.quantity >= ManualRegistrationConfig.quantityMinimum
    }

    var canSubmitDirectKind: Bool {
        !draft.directKindName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
            draft.selectedImageOption != nil
    }

    var kindCandidateForDisplay: ManualConsumableKind? {
        selectedKindCandidate ?? draft.selectedKind
    }
}
