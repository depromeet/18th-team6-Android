import SwiftUI

struct OnboardingCategorySelectionView: View {
    let data: OnboardingViewData
    let action: OnboardingViewAction

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            OBRitDepthTopBar(
                title: "소모품 등록",
                backgroundColor: false,
                showRightButton: false,
                onBackClick: action.onBack
            )
            OnboardingTitleBlock(
                title: "소모품을 선택해볼까요?",
                subtitle: "Obrit과 함께 관리할 소모품을 선택해주세요."
            )
            .frame(maxWidth: .infinity, alignment: .leading)
            .multilineTextAlignment(.leading)
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.top, OBRitSpacing.s5)
            .padding(.bottom, OBRitSpacing.s8)

            HStack(alignment: .top, spacing: OBRitSpacing.s6) {
                categorySidebar
                itemList
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        }
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .safeAreaInset(edge: .bottom, spacing: 0) {
            OnboardingBottomBarContainer(horizontalPadding: OBRitSpacing.s5) {
                selectionFooter
            }
        }
    }

    private var categorySidebar: some View {
        ScrollView(showsIndicators: false) {
            VStack(spacing: OBRitSpacing.s1) {
                ForEach(data.groups) { group in
                    OnboardingGroupChip(
                        group: group,
                        isActive: data.selectedGroupId == group.id,
                        hasSelection: data.hasSelection(in: group),
                        onSelect: { action.onSelectGroup(group) }
                    )
                }
            }
            .padding(.bottom, OBRitSpacing.s6)
        }
        .frame(width: OnboardingSelectionMetrics.sidebarWidth)
    }

    private var itemList: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
            if let group = data.selectedGroup {
                HStack(spacing: OBRitSpacing.s1) {
                    Text(group.name)
                        .foregroundStyle(OBRitColors.textDefaultDefault)
                    Text("\(data.visibleOptions.count)")
                        .foregroundStyle(OBRitColors.textPositiveDefault)
                }
                .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)
            }

            GeometryReader { geometry in
                ScrollView(showsIndicators: false) {
                    itemGrid(width: geometry.size.width)
                        .padding(.bottom, OBRitSpacing.s6)
                }
                .id(data.selectedGroupId)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }

    @ViewBuilder
    private func itemGrid(width: CGFloat) -> some View {
        if data.visibleOptions.isEmpty {
            Text("이 카테고리에는 소모품이 없어요.")
                .obritTextStyle(OBRitTypography.lg, color: OBRitColors.textDefaultSecondary)
                .frame(maxWidth: .infinity, alignment: .leading)
        } else {
            LazyVGrid(columns: columns, spacing: OBRitSpacing.s6) {
                ForEach(data.visibleOptions) { option in
                    OnboardingSelectableItem(
                        option: option,
                        isSelected: data.selectedOptionIds.contains(option.id),
                        imageSize: min(OnboardingSelectionMetrics.imageSize, max(1, (width - OBRitSpacing.s6 * 2) / 3)),
                        onToggle: { action.onToggleOption(option) }
                    )
                }
            }
        }
    }

    private var columns: [GridItem] {
        Array(repeating: GridItem(.flexible(), spacing: OBRitSpacing.s6, alignment: .top), count: 3)
    }

    private var selectionFooter: some View {
        OnboardingBottomCTA(
            text: "Obrit 시작하기",
            enabled: data.canContinue,
            isProcessing: data.isProcessing,
            action: action.onNext
        )
    }
}

private struct OnboardingGroupChip: View {
    let group: OnboardingCategoryGroup
    let isActive: Bool
    let hasSelection: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            HStack(spacing: OBRitSpacing.s1) {
                Text(group.name)
                    .multilineTextAlignment(.leading)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: isActive ? OBRitFontWeight.semiBold : OBRitFontWeight.medium,
                        color: isActive ? OBRitColors.common00 : OBRitColors.gray400
                    )
                if hasSelection {
                    Image("onboarding_group_selected_dot")
                        .frame(width: OnboardingSelectionMetrics.indicatorSize, height: OnboardingSelectionMetrics.indicatorSize)
                        .accessibilityHidden(true)
                }
                Spacer(minLength: 0)
            }
            .padding(.horizontal, OBRitSpacing.s3)
            .padding(.vertical, OBRitSpacing.s2_5)
            .frame(maxWidth: .infinity, minHeight: OBRitSpacing.s11, alignment: .leading)
            .background(isActive ? OBRitColors.gray800 : .clear)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(group.name)
        .accessibilityValue(hasSelection ? "선택한 소모품 있음" : "선택한 소모품 없음")
        .accessibilityAddTraits(isActive ? .isSelected : [])
    }
}

private struct OnboardingSelectableItem: View {
    let option: OnboardingItemOption
    let isSelected: Bool
    let imageSize: CGFloat
    let onToggle: () -> Void

    var body: some View {
        Button(action: onToggle) {
            VStack(spacing: OBRitSpacing.s2) {
                OnboardingItemImage(urlString: option.imageURL, size: imageSize)
                    .overlay {
                        Circle()
                            .strokeBorder(
                                isSelected ? OBRitColors.borderPositiveDefault : .clear,
                                lineWidth: OBRitSpacing.s0_5
                            )
                    }
                Text(option.title)
                    .obritTextStyle(OBRitTypography.lg, weight: OBRitFontWeight.medium, color: OBRitColors.common00)
                    .multilineTextAlignment(.center)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .frame(maxWidth: .infinity, alignment: .top)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(option.title)
        .accessibilityValue(isSelected ? "선택됨" : "선택 안 됨")
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}

private enum OnboardingSelectionMetrics {
    static let sidebarWidth: CGFloat = 92
    static let imageSize: CGFloat = 68
    static let indicatorSize: CGFloat = 7
}
