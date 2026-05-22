import Foundation

@MainActor
final class OnboardingViewModel: ObservableObject {
    @Published private(set) var state: OnboardingViewState

    private let options: [OnboardingConsumableOption]
    private var selectedOptionIds: Set<Int>

    init(
        options: [OnboardingConsumableOption] = OnboardingSampleData.options,
        selectedOptionIds: Set<Int> = [1]
    ) {
        self.options = options
        self.selectedOptionIds = selectedOptionIds
        self.state = .success(OnboardingViewData(options: options, selectedOptionIds: selectedOptionIds))
    }

    func toggleOption(_ option: OnboardingConsumableOption) {
        if selectedOptionIds.contains(option.id) {
            selectedOptionIds.remove(option.id)
        } else {
            selectedOptionIds.insert(option.id)
        }
        publish()
    }

    private func publish() {
        state = .success(OnboardingViewData(options: options, selectedOptionIds: selectedOptionIds))
    }
}
