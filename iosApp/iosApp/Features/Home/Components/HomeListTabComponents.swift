import SwiftUI

struct HomeListTabContentView: View {
    let state: HomeListTabState
    let action: HomeListTabAction

    var body: some View {
        switch state {
        case .loading:
            ProgressView()
                .tint(OBRitColors.green300)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(OBRitColors.backgroundDefaultDefault)
        case .loadFailed:
            Text("소모품 목록을 불러오지 못했어요")
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(OBRitColors.backgroundDefaultDefault)
        case let .success(viewData):
            HomeListTabSuccessView(viewData: viewData, action: action)
        }
    }
}

struct HomeListTabAction {
    let onSearch: () -> Void
    let onNotification: () -> Void
    let onProfile: () -> Void
    let onRegisterDirect: () -> Void
    let onSelectItem: (Int) -> Void
    let onOpenFilterSheet: () -> Void
    let onOpenSortSheet: () -> Void
    let onDismissBottomSheet: () -> Void
    let onUpdateDraftReplacementDday: (Double) -> Void
    let onUpdateDraftStockCount: (Double) -> Void
    let onApplyFilters: () -> Void
    let onResetDraftFilters: () -> Void
    let onClearReplacementDdayFilter: () -> Void
    let onClearStockCountFilter: () -> Void
    let onSelectSortOption: (HomeListTabSortOption) -> Void
    let onLoadNextPageIfNeeded: (Int?) -> Void
    let onFilterBarVisibleChange: (Bool) -> Void
}

private struct HomeListTabSuccessView: View {
    @State private var isFabMenuPresented = false

    let viewData: HomeListTabViewData
    let action: HomeListTabAction

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                VStack(spacing: 0) {
                    Color.clear.frame(height: geometry.safeAreaInsets.top)
                    OBRitHomeTopBar(
                        backgroundColor: false,
                        onSearchClick: action.onSearch,
                        onNotificationClick: action.onNotification,
                        onProfileClick: action.onProfile
                    )
                    if viewData.totalItemCount > 0 {
                        HomeListFilterSortBar(viewData: viewData, action: action)
                            .transition(.move(edge: .top).combined(with: .opacity))
                            .opacity(viewData.isFilterBarVisible ? 1 : 0)
                            .frame(height: viewData.isFilterBarVisible ? HomeListTabMetrics.filterBarHeight : 0)
                            .clipped()
                    }
                    HomeListScrollableContent(viewData: viewData, action: action)
                }
                .ignoresSafeArea(edges: .top)

                VStack {
                    Spacer(minLength: 0)
                    HStack {
                        Spacer(minLength: 0)
                        OBRitFloatingActionMenu(
                            isPresented: $isFabMenuPresented,
                            items: [
                                OBRitFloatingActionMenuItem(
                                    id: "itemRegistration",
                                    title: "직접 등록",
                                    action: action.onRegisterDirect
                                )
                            ],
                            accessibilityLabel: "소모품 등록"
                        )
                        .padding(.trailing, OBRitSpacing.s5)
                        .padding(.bottom, OBRitSpacing.s6)
                    }
                }

                if let bottomSheet = viewData.bottomSheet {
                    HomeListBottomSheetOverlay(
                        bottomSheet: bottomSheet,
                        viewData: viewData,
                        action: action
                    )
                }
            }
            .background(OBRitColors.backgroundDefaultDefault)
        }
        .animation(.easeOut(duration: 0.2), value: viewData.isFilterBarVisible)
    }
}

private struct HomeListScrollableContent: View {
    let viewData: HomeListTabViewData
    let action: HomeListTabAction

    @State private var previousContentMinY: CGFloat = 0

    var body: some View {
        if viewData.items.isEmpty {
            HomeListEmptyState()
        } else {
            ScrollView(showsIndicators: false) {
                GeometryReader { proxy in
                    Color.clear
                        .preference(
                            key: HomeListScrollOffsetPreferenceKey.self,
                            value: proxy.frame(in: .named(HomeListTabMetrics.scrollSpaceName)).minY
                        )
                }
                .frame(height: 0)

                LazyVStack(spacing: OBRitSpacing.s2) {
                    ForEach(viewData.items) { item in
                        Button {
                            action.onSelectItem(item.id)
                        } label: {
                            OBRitCardList(
                                level: item.cardLevel,
                                title: item.title,
                                daysInUseLabel: item.daysInUseLabel,
                                replaceLabel: item.replaceLabel,
                                sparesLabel: item.sparesLabel
                            ) {
                                Image(item.assetName)
                                    .resizable()
                                    .scaledToFit()
                                    .padding(OBRitSpacing.s2)
                            }
                        }
                        .buttonStyle(.plain)
                        .onAppear {
                            action.onLoadNextPageIfNeeded(item.id)
                        }
                    }
                }
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.bottom, OBRitSpacing.s32)
            }
            .coordinateSpace(name: HomeListTabMetrics.scrollSpaceName)
            .onPreferenceChange(HomeListScrollOffsetPreferenceKey.self) { value in
                updateFilterBarVisibility(contentMinY: value)
            }
        }
    }

    private func updateFilterBarVisibility(contentMinY: CGFloat) {
        let delta = contentMinY - previousContentMinY
        defer { previousContentMinY = contentMinY }

        guard abs(delta) > HomeListTabMetrics.scrollDirectionThreshold else { return }

        if contentMinY >= 0 {
            action.onFilterBarVisibleChange(true)
        } else if delta < 0 {
            action.onFilterBarVisibleChange(false)
        } else {
            action.onFilterBarVisibleChange(true)
        }
    }
}

private struct HomeListFilterSortBar: View {
    let viewData: HomeListTabViewData
    let action: HomeListTabAction

    var body: some View {
        HStack(spacing: OBRitSpacing.s4) {
            HStack(spacing: OBRitSpacing.s2) {
                HomeListFilterIconButton(action: action.onOpenFilterSheet)
                HomeListToolbarChip(
                    title: viewData.filters.maxReplacementDday.map { "\($0.ddayText) 이하" } ?? "디데이",
                    selected: viewData.filters.maxReplacementDday != nil,
                    icon: viewData.filters.maxReplacementDday == nil ? "chevron.down" : "xmark",
                    action: viewData.filters.maxReplacementDday == nil
                        ? action.onOpenFilterSheet
                        : action.onClearReplacementDdayFilter
                )
                HomeListToolbarChip(
                    title: viewData.filters.maxStockCount.map { "\($0)개 이하" } ?? "여분",
                    selected: viewData.filters.maxStockCount != nil,
                    icon: viewData.filters.maxStockCount == nil ? "chevron.down" : "xmark",
                    action: viewData.filters.maxStockCount == nil
                        ? action.onOpenFilterSheet
                        : action.onClearStockCountFilter
                )
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            HomeListToolbarChip(
                title: viewData.sortOption.title,
                selected: false,
                filled: true,
                icon: "chevron.down",
                action: action.onOpenSortSheet
            )
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.vertical, OBRitSpacing.s4)
    }
}

private struct HomeListFilterIconButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "slider.horizontal.3")
                .font(.system(size: OBRitSpacing.s4, weight: .semibold))
                .foregroundStyle(OBRitColors.common00)
                .frame(width: HomeListTabMetrics.toolbarButtonHeight, height: HomeListTabMetrics.toolbarButtonHeight)
                .background(OBRitColors.gray800)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
        }
        .buttonStyle(.plain)
        .accessibilityLabel("필터")
    }
}

private struct HomeListToolbarChip: View {
    let title: String
    let selected: Bool
    let filled: Bool
    let icon: String
    let action: () -> Void

    init(
        title: String,
        selected: Bool,
        filled: Bool = false,
        icon: String,
        action: @escaping () -> Void
    ) {
        self.title = title
        self.selected = selected
        self.filled = filled
        self.icon = icon
        self.action = action
    }

    var body: some View {
        Button(action: action) {
            HStack(spacing: OBRitSpacing.s0_5) {
                Text(title)
                Image(systemName: icon)
                    .font(.system(size: OBRitSpacing.s3, weight: .bold))
            }
            .lineLimit(1)
            .obritTextStyle(
                OBRitTypography.base,
                weight: selected || filled ? OBRitFontWeight.semiBold : OBRitFontWeight.regular,
                color: contentColor
            )
            .padding(.horizontal, OBRitSpacing.s3)
            .frame(height: HomeListTabMetrics.toolbarButtonHeight)
            .background(backgroundColor)
            .overlay {
                if !selected && !filled {
                    RoundedRectangle(cornerRadius: OBRitRadius.small)
                        .stroke(OBRitColors.borderDefaultSecondary, lineWidth: OBRitSpacing.px)
                }
            }
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
        }
        .buttonStyle(.plain)
    }

    private var backgroundColor: Color {
        if selected {
            return OBRitColors.common00
        }

        if filled {
            return OBRitColors.gray800
        }

        return .clear
    }

    private var contentColor: Color {
        selected ? OBRitColors.common1000 : OBRitColors.common00
    }
}

private struct HomeListEmptyState: View {
    var body: some View {
        VStack(spacing: OBRitSpacing.s2) {
            Text("아직 등록된 소모품이 없어요")
                .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
            Text("가지고 계신 소모품을 등록하고 관리해 보세요")
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.gray300.opacity(0.64))
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding(.top, OBRitSpacing.s24)
        .padding(.horizontal, OBRitSpacing.s5)
    }
}

private struct HomeListBottomSheetOverlay: View {
    let bottomSheet: HomeListTabBottomSheet
    let viewData: HomeListTabViewData
    let action: HomeListTabAction

    var body: some View {
        GeometryReader { geometry in
            let bottomPadding = geometry.safeAreaInsets.bottom

            ZStack(alignment: .bottom) {
                OBRitColors.backgroundDefaultDimDefault
                    .ignoresSafeArea()
                    .onTapGesture(perform: action.onDismissBottomSheet)

                switch bottomSheet {
                case .filter:
                    OBRitBottomSheet(
                        contentHeight: bottomSheetContentHeight(
                            preferredHeight: HomeListTabMetrics.preferredFilterSheetContentHeight,
                            in: geometry,
                            bottomPadding: bottomPadding
                        ),
                        bottomPadding: bottomPadding
                    ) {
                        ScrollView(showsIndicators: false) {
                            HomeListFilterBottomSheet(viewData: viewData, action: action)
                                .frame(maxWidth: .infinity, alignment: .top)
                        }
                    }
                    .ignoresSafeArea(.container, edges: .bottom)
                case .sort:
                    OBRitBottomSheet(
                        contentHeight: bottomSheetContentHeight(
                            preferredHeight: HomeListTabMetrics.preferredSortSheetContentHeight,
                            in: geometry,
                            bottomPadding: bottomPadding
                        ),
                        bottomPadding: bottomPadding
                    ) {
                        ScrollView(showsIndicators: false) {
                            HomeListSortBottomSheet(
                                selectedOption: viewData.sortOption,
                                onSelect: action.onSelectSortOption
                            )
                            .frame(maxWidth: .infinity, alignment: .top)
                        }
                    }
                    .ignoresSafeArea(.container, edges: .bottom)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottom)
            .ignoresSafeArea(.container, edges: .bottom)
        }
        .transition(.opacity)
    }

    private func bottomSheetContentHeight(
        preferredHeight: CGFloat,
        in geometry: GeometryProxy,
        bottomPadding: CGFloat
    ) -> CGFloat {
        let availableHeight = geometry.size.height
            - geometry.safeAreaInsets.top
            - HomeListTabMetrics.bottomSheetTopMargin
            - HomeListTabMetrics.bottomSheetHeaderHeight
            - bottomPadding
        return min(preferredHeight, max(0, availableHeight))
    }
}

private struct HomeListFilterBottomSheet: View {
    let viewData: HomeListTabViewData
    let action: HomeListTabAction

    var body: some View {
        VStack(spacing: OBRitSpacing.s8) {
            VStack(spacing: OBRitSpacing.s6) {
                HomeListFilterSliderSection(
                    title: "교체 디데이",
                    valueText: (viewData.draftFilters.maxReplacementDday ?? viewData.filterBounds.maxReplacementDday).ddayText,
                    suffix: "이하",
                    value: Double(viewData.draftFilters.maxReplacementDday ?? viewData.filterBounds.maxReplacementDday),
                    range: Double(viewData.filterBounds.minReplacementDday)...Double(viewData.filterBounds.maxReplacementDday),
                    onValueChange: action.onUpdateDraftReplacementDday
                )
                HomeListFilterSliderSection(
                    title: "여분",
                    valueText: "\(viewData.draftFilters.maxStockCount ?? viewData.filterBounds.maxStockCount)개",
                    suffix: "이하",
                    value: Double(viewData.draftFilters.maxStockCount ?? viewData.filterBounds.maxStockCount),
                    range: Double(viewData.filterBounds.minStockCount)...Double(viewData.filterBounds.maxStockCount),
                    onValueChange: action.onUpdateDraftStockCount
                )
            }

            HStack(spacing: OBRitSpacing.s2_5) {
                Button(action: action.onResetDraftFilters) {
                    Image(systemName: "arrow.clockwise")
                        .font(.system(size: OBRitSpacing.s5, weight: .semibold))
                        .foregroundStyle(OBRitColors.common00)
                        .frame(width: OBRitSpacing.s14, height: OBRitSpacing.s14)
                        .background(OBRitColors.gray800)
                        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
                }
                .buttonStyle(.plain)
                .accessibilityLabel("초기화")

                Button(action: action.onApplyFilters) {
                    Text("적용")
                        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.common1000)
                        .frame(maxWidth: .infinity)
                        .frame(height: OBRitSpacing.s14)
                        .background(OBRitColors.common00)
                        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
                }
                .buttonStyle(.plain)
            }
        }
    }
}

private struct HomeListFilterSliderSection: View {
    let title: String
    let valueText: String
    let suffix: String
    let value: Double
    let range: ClosedRange<Double>
    let onValueChange: (Double) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            Text(title)
                .obritTextStyle(OBRitTypography.lg, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultSecondary)

            HStack(alignment: .firstTextBaseline, spacing: OBRitSpacing.s1) {
                Text(valueText)
                    .obritTextStyle(OBRitTypography.s6xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                Text(suffix)
                    .obritTextStyle(OBRitTypography.s2xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultTertiary)
            }

            OBRitSlider(
                value: value,
                enabled: range.lowerBound != range.upperBound,
                valueRange: range,
                onValueChange: onValueChange
            )
        }
    }
}

private struct HomeListSortBottomSheet: View {
    let selectedOption: HomeListTabSortOption
    let onSelect: (HomeListTabSortOption) -> Void

    var body: some View {
        VStack(spacing: 0) {
            ForEach(HomeListTabSortOption.allCases, id: \.self) { option in
                Button {
                    onSelect(option)
                } label: {
                    HStack {
                        Text(option.title)
                            .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                        Spacer(minLength: 0)
                        if option == selectedOption {
                            Image(systemName: "checkmark")
                                .font(.system(size: OBRitSpacing.s5, weight: .bold))
                                .foregroundStyle(OBRitColors.green300)
                        }
                    }
                    .frame(height: OBRitSpacing.s14)
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
            }
        }
    }
}

private struct HomeListScrollOffsetPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

private enum HomeListTabMetrics {
    static let filterBarHeight: CGFloat = 70
    static let toolbarButtonHeight: CGFloat = 38
    static let preferredFilterSheetContentHeight: CGFloat = 312
    static let preferredSortSheetContentHeight: CGFloat = 224
    static let bottomSheetTopMargin = OBRitSpacing.s5
    static let bottomSheetHeaderHeight = OBRitSpacing.s1 + OBRitSpacing.s8 + OBRitSpacing.s2_5
    static let scrollDirectionThreshold: CGFloat = 2
    static let scrollSpaceName = "HomeListTabScroll"
}
