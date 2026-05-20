import SwiftUI

struct MainNavigation: View {
    @State private var isGnbHiddenByContent = false

    let selectedTab: MainTab
    let onNavigateConsumable: (ConsumableRoute) -> Void
    let onNavigateMyPage: (MyPageRoute) -> Void
    let onSelectMainTab: (MainTab) -> Void

    init(
        selectedTab: MainTab,
        onNavigateConsumable: @escaping (ConsumableRoute) -> Void,
        onNavigateMyPage: @escaping (MyPageRoute) -> Void,
        onSelectMainTab: @escaping (MainTab) -> Void
    ) {
        self.selectedTab = selectedTab
        self.onNavigateConsumable = onNavigateConsumable
        self.onNavigateMyPage = onNavigateMyPage
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
            .allowsHitTesting(!isGnbHiddenByContent)
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
                onNavigateConsumable: onNavigateConsumable,
                onNavigateMyPage: onNavigateMyPage,
                onShowListTab: {
                    onSelectMainTab(.homeListTab)
                },
                onBottomSheetVisibleChange: { isVisible in
                    isGnbHiddenByContent = isVisible
                }
            )
        case .homeListTab:
            HomeListTab(
                onNavigate: onNavigateConsumable,
                onNavigateMyPage: onNavigateMyPage,
                onBottomSheetVisibleChange: { isVisible in
                    isGnbHiddenByContent = isVisible
                }
            )
        }
    }

    private var gnbOpacity: Double {
        isGnbHiddenByContent ? 0 : 1
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
