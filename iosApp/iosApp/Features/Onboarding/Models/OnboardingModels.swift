import SwiftUI

struct OnboardingConsumableOption: Identifiable, Equatable {
    let id: Int
    let title: String
    let imageAssetName: String
}

enum OnboardingViewState: Equatable {
    case success(OnboardingViewData)
}

struct OnboardingViewData: Equatable {
    let options: [OnboardingConsumableOption]
    let selectedOptionIds: Set<Int>

    var canContinue: Bool {
        !selectedOptionIds.isEmpty
    }
}

enum OnboardingSampleData {
    static let options: [OnboardingConsumableOption] = [
        OnboardingConsumableOption(id: 1, title: "면도기", imageAssetName: "manual_consumable_razor"),
        OnboardingConsumableOption(id: 2, title: "정수기 필터", imageAssetName: "manual_consumable_bottle"),
        OnboardingConsumableOption(id: 3, title: "생활용품", imageAssetName: "manual_consumable_pouch")
    ]
}
