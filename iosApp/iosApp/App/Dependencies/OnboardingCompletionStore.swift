import Foundation

protocol OnboardingCompletionStore {
    var hasCompletedOnboarding: Bool { get }

    func markCompleted()
}

struct UserDefaultsOnboardingCompletionStore: OnboardingCompletionStore {
    private let userDefaults: UserDefaults
    private let key: String

    init(
        userDefaults: UserDefaults = .standard,
        key: String = "com.hotsix.obrit.hasCompletedOnboarding"
    ) {
        self.userDefaults = userDefaults
        self.key = key
    }

    var hasCompletedOnboarding: Bool {
        userDefaults.bool(forKey: key)
    }

    func markCompleted() {
        userDefaults.set(true, forKey: key)
    }
}

struct PreviewOnboardingCompletionStore: OnboardingCompletionStore {
    let hasCompletedOnboarding: Bool

    func markCompleted() {}
}
