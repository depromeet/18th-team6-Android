import SwiftUI

struct OnboardingReplacementPeriodView: View {
    let data: OnboardingViewData
    let action: OnboardingViewAction

    @State private var currentItemID: Int?
    @State private var pickerItemID: Int?

    var body: some View {
        OnboardingStepScaffold(
            currentStep: 2,
            action: action
        ) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s10) {
                OnboardingTitleBlock(
                    title: "소모품의 상세 정보를\n입력해주세요",
                    subtitle: "원활한 관리를 위해 구체적인 정보를 입력해주세요"
                )

                VStack(spacing: OBRitSpacing.s5) {
                    ZStack(alignment: .top) {
                        TabView(selection: currentItemIDBinding) {
                            ForEach(data.selectedOptions) { option in
                                OnboardingItemDetailCard(
                                    option: option,
                                    name: data.itemName(for: option),
                                    period: data.replacementPeriod(for: option),
                                    quantity: data.quantity(for: option),
                                    isPeriodDropdownExpanded: pickerItemID == option.id,
                                    action: action,
                                    onTogglePeriodDropdown: {
                                        togglePicker(for: option)
                                    },
                                    onSelectPeriod: { period in
                                        select(period, for: option)
                                    }
                                )
                                .tag(Optional(option.id))
                                .padding(.horizontal, OBRitSpacing.s4)
                            }
                        }
                        .tabViewStyle(.page(indexDisplayMode: .never))
                        .frame(height: OnboardingDetailMetrics.cardPagerRenderHeight(expanded: pickerItemID != nil))
                    }
                    .frame(height: OnboardingDetailMetrics.cardPagerHeight)
                    .zIndex(pickerItemID == nil ? 0 : 1)

                    OnboardingDetailIndicator(
                        options: data.selectedOptions,
                        currentItemID: currentItemID ?? data.selectedOptions.first?.id
                    )
                }
                .frame(maxWidth: .infinity)
            }
        } bottomBar: {
            OnboardingBottomCTA(
                text: "소모품 등록하기",
                enabled: data.canContinue,
                isProcessing: data.isProcessing,
                action: action.onNext
            )
        }
        .animation(.easeOut(duration: 0.18), value: pickerItemID)
        .onAppear(perform: syncCurrentItemIfNeeded)
        .onChange(of: data.selectedOptionIds) { _, _ in
            syncCurrentItemIfNeeded()
        }
    }

    private var currentItemIDBinding: Binding<Int?> {
        Binding(
            get: {
                currentItemID ?? data.selectedOptions.first?.id
            },
            set: { newValue in
                currentItemID = newValue
            }
        )
    }

    private func syncCurrentItemIfNeeded() {
        guard let firstOption = data.selectedOptions.first else {
            currentItemID = nil
            pickerItemID = nil
            return
        }

        if let currentItemID,
           data.selectedOptionIds.contains(currentItemID) {
            return
        }

        currentItemID = firstOption.id
    }

    private func togglePicker(for option: OnboardingItemOption) {
        pickerItemID = pickerItemID == option.id ? nil : option.id
    }

    private func select(_ period: OnboardingReplacementPeriod, for option: OnboardingItemOption) {
        action.onSelectReplacementPeriod(option, period)
        pickerItemID = nil
    }
}

private struct OnboardingDetailIndicator: View {
    let options: [OnboardingItemOption]
    let currentItemID: Int?

    var body: some View {
        OBRitPageIndicator(
            count: options.count,
            selectedIndex: selectedIndex
        )
        .opacity(options.count > 1 ? 1 : 0)
    }

    private var selectedIndex: Int {
        guard let currentItemID,
              let index = options.firstIndex(where: { $0.id == currentItemID }) else {
            return 0
        }
        return index
    }
}

private enum OnboardingDetailMetrics {
    static let cardPagerHeight: CGFloat = 360

    static func cardPagerRenderHeight(expanded: Bool) -> CGFloat {
        expanded ? 520 : cardPagerHeight
    }
}
