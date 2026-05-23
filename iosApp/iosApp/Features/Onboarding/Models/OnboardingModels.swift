import SwiftUI

struct OnboardingItemOption: Identifiable, Equatable {
    let id: Int
    let title: String
    let imageAssetName: String
}

enum OnboardingViewState: Equatable {
    case success(OnboardingViewData)
}

struct OnboardingViewData: Equatable {
    let options: [OnboardingItemOption]
    let selectedOptionIds: Set<Int>

    var canContinue: Bool {
        !selectedOptionIds.isEmpty
    }
}

enum OnboardingSampleData {
    static let options: [OnboardingItemOption] = [
        OnboardingItemOption(id: 1, title: "면도기", imageAssetName: "item_registration_razor"),
        OnboardingItemOption(id: 2, title: "정수기 필터", imageAssetName: "item_registration_bottle"),
        OnboardingItemOption(id: 3, title: "생활용품", imageAssetName: "item_registration_pouch")
    ]
}
