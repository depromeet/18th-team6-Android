struct OnboardingViewAction {
    let onStart: () -> Void
    let onBack: () -> Void
    let onRetry: () -> Void
    let onToggleOption: (OnboardingItemOption) -> Void
    let onUpdateItemName: (OnboardingItemOption, String) -> Void
    let onSelectReplacementPeriod: (OnboardingItemOption, OnboardingReplacementPeriod) -> Void
    let onDecrementQuantity: (OnboardingItemOption) -> Void
    let onIncrementQuantity: (OnboardingItemOption) -> Void
    let onUpdateQuantity: (OnboardingItemOption, Int) -> Void
    let onNext: () -> Void
    let onComplete: () -> Void
}
