import SwiftUI

struct HomeContentView: View {
    let dashboard: HomeDashboard
    let selectedStatusFilter: HomeStatusFilter
    let statusFilterCounts: [HomeStatusFilter: Int]
    let selectedWarningSort: HomeWarningSort
    let visibleWarningItems: [HomeConsumableItem]
    let action: HomeViewAction

    var body: some View {
        ZStack {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            ScrollView(showsIndicators: false) {
                VStack(spacing: 0) {
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
            .padding(.top, HomeChromeMetrics.topContentInset)
            .ignoresSafeArea(edges: .top)

            VStack(spacing: 0) {
                Color.clear.frame(height: HomeChromeMetrics.statusBarHeight)
                OBRitHomeTopBar(
                    backgroundColor: false,
                    onSearchClick: action.onSearch,
                    onNotificationClick: action.onNotification,
                    onProfileClick: action.onProfile
                )
                HomeStatusSection(summary: dashboard.summary)
                Spacer(minLength: 0)
            }
            .ignoresSafeArea(edges: .top)

            VStack {
                Spacer(minLength: 0)
                HStack {
                    Spacer(minLength: 0)
                    HomeRegisterButton(action: action.onRegister)
                        .padding(.trailing, OBRitSpacing.s5)
                        .padding(.bottom, OBRitSpacing.s2_5)
                }
            }
        }
        .background(OBRitColors.backgroundDefaultDefault)
    }
}

private enum HomeChromeMetrics {
    static let statusBarHeight: CGFloat = 52
    static let topBarHeight: CGFloat = 56
    static let homeHeaderHeight: CGFloat = 139
    static let topContentInset: CGFloat = statusBarHeight + topBarHeight + homeHeaderHeight
}

private struct HomeRegisterButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "plus")
                .font(.system(size: OBRitSpacing.s5, weight: .bold))
                .foregroundStyle(OBRitColors.gray900)
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
                .padding(OBRitSpacing.s4)
                .background(OBRitColors.common00)
                .clipShape(Circle())
                .shadow(color: Color.black.opacity(0.24), radius: OBRitSpacing.s6, x: 0, y: OBRitSpacing.s4)
        }
        .buttonStyle(.plain)
        .accessibilityLabel("소모품 등록")
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
