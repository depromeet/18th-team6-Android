import SwiftUI

struct OnboardingCategorySelectionView: View {
    let data: OnboardingViewData
    let action: OnboardingViewAction

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            OBRitDepthTopBar(title: "소모품 등록", backgroundColor: false, showRightButton: false, onBackClick: action.onBack)
            OnboardingTitleBlock(title: "관리할 소모품을 골라주세요", subtitle: "골라서 한 번에 등록해요")
                .frame(maxWidth: .infinity, alignment: .leading)
                .multilineTextAlignment(.leading)
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.top, OBRitSpacing.s5)
                .padding(.bottom, OBRitSpacing.s5)
            VStack(spacing: OBRitSpacing.s2) {
                ItemSearchInputField(text: data.searchQuery, placeholder: "소모품명 검색", onTextChange: action.onUpdateSearchQuery, onSearch: { action.onSubmitSearch(nil) })
                if !data.searchSuggestions.isEmpty {
                    RegistrationSearchSuggestions(query: data.searchQuery, suggestions: data.searchSuggestions) { action.onSubmitSearch($0) }
                }
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.bottom, OBRitSpacing.s5)
            HStack(alignment: .top, spacing: OBRitSpacing.s4) {
                categorySidebar
                ScrollView(showsIndicators: false) {
                    LazyVStack(alignment: .leading, spacing: OBRitSpacing.s4) {
                        if data.submittedSearchQuery != nil {
                            if data.searchResults.isEmpty {
                                RegistrationNoSearchResults(error: data.newItemNameError, enabled: !data.isProcessing, onDirectRegister: action.onAddSearchItem)
                            } else {
                                ForEach(data.visibleGroups.filter { group in data.searchResults.contains { $0.categoryGroupId == group.id } }) { group in
                                    groupContent(group, options: data.searchResults.filter { $0.categoryGroupId == group.id })
                                }
                            }
                        } else if let group = data.selectedGroup {
                            groupContent(group, options: data.visibleOptions)
                        }
                    }
                    .padding(.bottom, OBRitSpacing.s6)
                }
                .id(data.submittedSearchQuery ?? "group:\(data.selectedGroupId ?? 0)")
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        }
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .safeAreaInset(edge: .bottom, spacing: 0) {
            OnboardingBottomBarContainer(horizontalPadding: OBRitSpacing.s5) {
                OnboardingBottomCTA(text: data.selectedCount == 0 ? "소모품 등록하기" : "\(data.selectedCount)개 등록하기", enabled: data.canContinue, isProcessing: data.isProcessing, action: action.onNext)
            }
        }
        .disabled(data.isProcessing)
    }

    private var categorySidebar: some View {
        ScrollViewReader { proxy in
            ScrollView(showsIndicators: false) {
                VStack(spacing: OBRitSpacing.s1) {
                    ForEach(data.visibleGroups) { group in
                        OnboardingGroupChip(group: group, isActive: data.submittedSearchQuery == nil && data.selectedGroupId == group.id, hasSelection: data.hasSelection(in: group), onSelect: { action.onSelectGroup(group) })
                            .id(group.id)
                    }
                }
                .padding(.bottom, OBRitSpacing.s6)
            }
            .onChange(of: data.selectedGroupId) { _, id in
                if let id { withAnimation { proxy.scrollTo(id) } }
            }
        }
        .frame(width: OnboardingSelectionMetrics.sidebarWidth)
    }

    private func groupContent(_ group: OnboardingCategoryGroup, options: [OnboardingItemOption]) -> some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
            HStack(spacing: OBRitSpacing.s1) {
                Text(group.name).foregroundStyle(OBRitColors.textDefaultDefault)
                Text("\(options.count)").foregroundStyle(OBRitColors.textPositiveDefault)
            }
            .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)
            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: OBRitSpacing.s2, alignment: .top), count: 3), spacing: OBRitSpacing.s6) {
                ForEach(options) { option in
                    OnboardingSelectableItem(option: option, isSelected: data.selectedOptionIds.contains(option.id), imageSize: OnboardingSelectionMetrics.imageSize, onToggle: { action.onToggleOption(option) })
                }
            }
        }
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
