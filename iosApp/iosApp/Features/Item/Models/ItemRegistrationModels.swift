import Foundation

struct ItemKindGroup: Identifiable, Equatable {
    let id: Int
    let name: String
    let displayOrder: Int
    var isUserDefined = false
}

struct ItemKind: Identifiable, Equatable {
    let id: Int
    let title: String
    let addedCount: Int
    let imageURL: String
    var categoryGroupId: Int? = nil
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
    case complete
}

enum ItemRegistrationBottomSheet: Equatable {
    case kind
    case directKind
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
    var categoryGroups: [ItemKindGroup] = []
    var existingItemNames: [String] = []
    var prefersUserKinds = false
    var hasEditedItemName = false
    var hasEditedDirectKindName = false
    var hasCustomItemName = false
    var automaticItemName = ""
    var submittedKindSearchQuery: String?

    var filteredKinds: [ItemKind] {
        guard let query = submittedKindSearchQuery else { return itemKinds }
        return itemKinds.filter {
            $0.title.localizedCaseInsensitiveContains(query)
        }
    }

    var kindSearchSuggestions: [String] {
        let query = kindSearchQuery.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !query.isEmpty, query != submittedKindSearchQuery else { return [] }
        return Array(itemKinds.filter { $0.title.localizedCaseInsensitiveContains(query) }.map(\.title).prefix(3))
    }

    var visibleGroups: [ItemKindGroup] {
        let sorted = categoryGroups.sorted { $0.displayOrder == $1.displayOrder ? $0.id < $1.id : $0.displayOrder < $1.displayOrder }
        let groups = sorted.filter { group in group.isUserDefined || itemKinds.contains { $0.categoryGroupId == group.id } }
        if groups.contains(where: \.isUserDefined) { return groups }
        return groups + [ItemKindGroup(id: -1, name: "직접 등록", displayOrder: Int.max, isUserDefined: true)]
    }

    var hasNoSearchResults: Bool {
        submittedKindSearchQuery != nil && filteredKinds.isEmpty
    }

    var itemNameError: String? {
        guard hasEditedItemName || !draft.itemName.isEmpty else { return nil }
        return ItemRegistrationNameRule.error(for: draft.itemName, existingNames: existingItemNames)
    }

    var directKindNameError: String? {
        guard hasEditedDirectKindName || !draft.directKindName.isEmpty else { return nil }
        return ItemRegistrationNameRule.error(for: draft.directKindName, existingNames: existingItemNames)
    }

    var canSubmitForm: Bool {
        draft.selectedKind != nil &&
            ItemRegistrationNameRule.error(for: draft.itemName, existingNames: existingItemNames) == nil &&
            ItemRegistrationConfig.manualQuantityRange.contains(draft.quantity) &&
            !isProcessing
    }

    var canSubmitDirectKind: Bool {
        ItemRegistrationNameRule.error(for: draft.directKindName, existingNames: existingItemNames) == nil &&
            !isProcessing
    }

    var kindCandidateForDisplay: ItemKind? {
        selectedKindCandidate ?? draft.selectedKind
    }
}


enum ItemRegistrationNameRule {
    static let duplicateMessage = "이미 같은 이름의 소모품이 있어요"

    static func error(for name: String, existingNames: [String]) -> String? {
        guard !name.isEmpty else { return "이름을 입력해주세요" }
        guard name.count <= 15 else { return "이름은 15자 이내로 입력해주세요" }
        let allowed = name.unicodeScalars.allSatisfy { scalar in
            switch scalar.value {
            case 0x20, 0x30...0x39, 0x41...0x5A, 0x61...0x7A,
                 0x1100...0x11FF, 0x3130...0x318F, 0xA960...0xA97F,
                 0xAC00...0xD7A3, 0xD7B0...0xD7FF:
                return true
            default:
                return false
            }
        }
        guard allowed else { return "한글 및 영문, 숫자만 입력 가능합니다." }
        guard !name.hasPrefix(" "), !name.hasSuffix(" "), !name.contains("  ") else {
            return "공백은 단어 사이에 한 칸만 입력할 수 있어요"
        }
        guard !existingNames.contains(where: { identity($0) == identity(name) }) else {
            return duplicateMessage
        }
        return nil
    }

    static func availableName(for title: String, existingNames: [String]) -> String {
        let base = String(title.prefix(15)).trimmingCharacters(in: .whitespacesAndNewlines)
        let used = Set(existingNames.map(identity))
        if !used.contains(identity(base)) { return base }
        var number = 1
        while true {
            let suffix = " \(number)"
            let prefix = String(base.prefix(max(0, 15 - suffix.count)))
                .trimmingCharacters(in: .whitespacesAndNewlines)
            let candidate = prefix + suffix
            if !used.contains(identity(candidate)) { return candidate }
            number += 1
        }
    }

    private static func identity(_ name: String) -> String {
        name.precomposedStringWithCanonicalMapping
    }
}
