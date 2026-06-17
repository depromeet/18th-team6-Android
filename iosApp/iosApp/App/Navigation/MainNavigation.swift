import SwiftUI

struct MainNavigation: View {
    @State private var isGnbHiddenByContent = false
    @State private var isGnbDisabledByContent = false

    let selectedTab: MainTab
    let dependencies: AppDependencies
    let onNavigateItem: (ItemRoute) -> Void
    let onShowOnboarding: () -> Void
    let onSelectMainTab: (MainTab) -> Void

    init(
        selectedTab: MainTab,
        dependencies: AppDependencies,
        onNavigateItem: @escaping (ItemRoute) -> Void,
        onShowOnboarding: @escaping () -> Void,
        onSelectMainTab: @escaping (MainTab) -> Void
    ) {
        self.selectedTab = selectedTab
        self.dependencies = dependencies
        self.onNavigateItem = onNavigateItem
        self.onShowOnboarding = onShowOnboarding
        self.onSelectMainTab = onSelectMainTab
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            selectedContent

            OBRitGnb(selected: selectedTab.gnbItem) { item in
                onSelectMainTab(item.mainTab)
            }
            .padding(.bottom, OBRitSpacing.s6)
            .opacity(gnbOpacity)
            .disabled(isGnbDisabledByContent)
            .allowsHitTesting(!isGnbInteractionDisabled)
            .accessibilityHidden(isGnbHiddenByContent)
            .zIndex(1)
        }
        .background(OBRitColors.backgroundDefaultDefault)
        .onChange(of: selectedTab) { _, _ in
            isGnbHiddenByContent = false
        }
    }

    @ViewBuilder
    private var selectedContent: some View {
        switch selectedTab {
        case .home:
            HomeView(
                viewModelFactory: dependencies.makeHomeViewModel,
                refreshCenter: dependencies.refreshCenter,
                onNavigateItem: onNavigateItem,
                onShowOnboarding: onShowOnboarding,
                onShowListTab: {
                    onSelectMainTab(.homeListTab)
                },
                onBottomSheetVisibleChange: { isVisible in
                    isGnbHiddenByContent = isVisible
                },
                onRegisteredItemsAvailabilityChange: { hasRegisteredItems in
                    isGnbDisabledByContent = !hasRegisteredItems
                }
            )
        case .homeListTab:
            HomeListTab(
                viewModelFactory: dependencies.makeHomeListTabViewModel,
                refreshCenter: dependencies.refreshCenter,
                onNavigate: onNavigateItem,
                onShowOnboarding: onShowOnboarding,
                onBottomSheetVisibleChange: { isVisible in
                    isGnbHiddenByContent = isVisible
                }
            )
        }
    }

    private var isGnbInteractionDisabled: Bool {
        isGnbHiddenByContent || isGnbDisabledByContent
    }

    private var gnbOpacity: Double {
        if isGnbHiddenByContent {
            return 0
        }

        return isGnbDisabledByContent ? 0.45 : 1
    }
}

private extension MainTab {
    var gnbItem: OBRitGnbItem {
        switch self {
        case .home:
            return .home
        case .homeListTab:
            return .list
        }
    }
}

private extension OBRitGnbItem {
    var mainTab: MainTab {
        switch self {
        case .home:
            return .home
        case .list:
            return .homeListTab
        }
    }
}
