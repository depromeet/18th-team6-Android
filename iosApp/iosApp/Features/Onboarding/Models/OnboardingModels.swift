import SwiftUI

struct OnboardingItemOption: Identifiable, Equatable {
    let id: Int
    let title: String
    let imageURL: String
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
