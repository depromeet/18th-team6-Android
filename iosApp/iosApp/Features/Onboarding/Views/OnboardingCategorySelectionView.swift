import SwiftUI

struct OnboardingCategorySelectionView: View {
    let data: OnboardingViewData
    let action: OnboardingViewAction

    var body: some View {
        OnboardingStepScaffold(
            currentStep: 1,
            action: action
        ) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
                OnboardingTitleBlock(
                    title: "어떤 소모품을 관리해볼까요?",
                    subtitle: "원하는 카테고리가 없다면, 추후 직접 등록도 가능해요."
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                    OnboardingSelectedCountText(count: data.selectedCount)

                    LazyVStack(spacing: OBRitSpacing.s2) {
                        ForEach(data.options) { option in
                            OnboardingCategoryCard(
                                option: option,
                                selected: data.selectedOptionIds.contains(option.id),
                                action: {
                                    action.onToggleOption(option)
                                }
                            )
                        }
                    }
                }
            }
        } bottomBar: {
            OnboardingBottomCTA(
                text: "다음 단계로",
                enabled: data.canContinue,
                action: action.onNext
            )
        }
    }
}
