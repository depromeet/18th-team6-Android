import Foundation

struct OnboardingItemOption: Identifiable, Equatable {
    let id: Int
    let title: String
    let addedCount: Int
    let imageURL: String
}

enum OnboardingStep: Equatable {
    case start
    case categorySelection
    case replacementPeriod
    case complete
}

enum OnboardingReplacementPeriod: Int, CaseIterable, Identifiable, Equatable {
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
            return "2-4주 전"
        case .oneToThreeMonthsAgo:
            return "1-3개월 전"
        case .unknown:
            return "잘 모르겠어요"
        }
    }

    var apiRawValue: String? {
        switch self {
        case .withinOneWeek:
            return "WITHIN_WEEK"
        case .twoToFourWeeksAgo:
            return "WITHIN_MONTH"
        case .oneToThreeMonthsAgo:
            return "WITHIN_THREE_MONTHS"
        case .unknown:
            return nil
        }
    }
}

struct OnboardingRegistrationRequest: Equatable {
    let categoryId: Int
    let name: String
    let quantity: Int
    let replacementPeriod: OnboardingReplacementPeriod
}

enum OnboardingViewState: Equatable {
    case loading
    case loadFailed(message: String)
    case success(OnboardingViewData)
}

struct OnboardingViewData: Equatable {
    var step: OnboardingStep
    var options: [OnboardingItemOption]
    var selectedOptionIds: Set<Int>
    var itemNames: [Int: String]
    var replacementPeriods: [Int: OnboardingReplacementPeriod]
    var quantities: [Int: Int]
    var isProcessing: Bool

    var selectedOptions: [OnboardingItemOption] {
        options.filter { selectedOptionIds.contains($0.id) }
    }

    var selectedCount: Int {
        selectedOptionIds.count
    }

    var canContinue: Bool {
        switch step {
        case .start:
            return !isProcessing
        case .categorySelection:
            return selectedCount > 0 && !isProcessing
        case .replacementPeriod:
            return selectedOptions.allSatisfy { option in
                let name = itemName(for: option).trimmingCharacters(in: .whitespacesAndNewlines)
                return !name.isEmpty &&
                    replacementPeriods[option.id] != nil &&
                    quantity(for: option) >= OnboardingRegistrationConfig.quantityMinimum
            } && !isProcessing
        case .complete:
            return true
        }
    }

    func itemName(for option: OnboardingItemOption) -> String {
        itemNames[option.id] ?? option.title
    }

    func replacementPeriod(for option: OnboardingItemOption) -> OnboardingReplacementPeriod? {
        replacementPeriods[option.id]
    }

    func quantity(for option: OnboardingItemOption) -> Int {
        quantities[option.id] ?? OnboardingRegistrationConfig.defaultQuantity
    }
}

enum OnboardingRegistrationConfig {
    static let itemNameMaxLength = 15
    static let quantityMinimum = 1
    static let quantityMaximum = 99
    static let defaultQuantity = 1
}
