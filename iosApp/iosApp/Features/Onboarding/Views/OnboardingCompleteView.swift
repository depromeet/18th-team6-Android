import SwiftUI

struct OnboardingCompleteView: View {
    let action: OnboardingViewAction

    var body: some View {
        RegistrationCompleteScreen(action: action.onComplete)
    }
}
