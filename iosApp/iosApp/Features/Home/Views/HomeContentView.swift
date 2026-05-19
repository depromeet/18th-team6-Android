import SwiftUI

struct HomeContentView: View {
    let dashboard: HomeDashboard
    let selectedStatusFilter: HomeStatusFilter
    let statusFilterCounts: [HomeStatusFilter: Int]
    let selectedWarningSort: HomeWarningSort
    let visibleWarningItems: [HomeConsumableItem]
    let action: HomeViewAction

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                ScrollView(showsIndicators: false) {
                    VStack(spacing: 0) {
                        Color.clear.frame(height: geometry.safeAreaInsets.top)
                        OBRitHomeTopBar(
                            backgroundColor: false,
                            onSearchClick: action.onSearch,
                            onNotificationClick: action.onNotification,
                            onProfileClick: action.onProfile
                        )
                        HomeStatusSection(summary: dashboard.summary)
                        HomeInventorySection(
                            summary: dashboard.summary,
                            items: dashboard.usageItems
                        )
                        HomeWarningSection(
                            items: visibleWarningItems,
                            selectedFilter: selectedStatusFilter,
                            filterCounts: statusFilterCounts,
                            selectedSort: selectedWarningSort,
                            onSelectFilter: action.onSelectStatusFilter,
                            onSelectSort: action.onSelectWarningSort,
                            onShowList: action.onShowList,
                            onSelect: action.onSelectConsumable
                        )
                        HomeUsageListSection(
                            items: dashboard.usageItems,
                            onSelect: action.onSelectConsumable
                        )
                    }
                    .padding(.bottom, OBRitSpacing.s32)
                }
                .ignoresSafeArea(edges: .top)

                VStack {
                    Spacer(minLength: 0)
                    HStack {
                        Spacer(minLength: 0)
                        OBRitFloatingActionButton(
                            accessibilityLabel: "소모품 등록",
                            action: action.onRegister
                        )
                            .padding(.trailing, OBRitSpacing.s5)
                            .padding(.bottom, OBRitSpacing.s11)
                    }
                }
            }
            .background(OBRitColors.backgroundDefaultDefault)
        }
    }
}

#Preview {
    HomeContentView(
        dashboard: HomeSampleData.dashboard,
        selectedStatusFilter: .replacementDanger,
        statusFilterCounts: [.replacementDanger: 4, .spareShortage: 3, .replacementWarning: 3],
        selectedWarningSort: .replacementRisk,
        visibleWarningItems: HomeSampleData.dashboard.warningItems.filter {
            $0.statusFilters.contains(.replacementDanger)
        },
        action: HomeViewAction(
            onSearch: {},
            onNotification: {},
            onProfile: {},
            onRegister: {},
            onShowList: {},
            onSelectConsumable: { _ in },
            onSelectStatusFilter: { _ in },
            onSelectWarningSort: { _ in }
        )
    )
}
