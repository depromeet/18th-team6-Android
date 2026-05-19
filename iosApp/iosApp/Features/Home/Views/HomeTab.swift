import SwiftUI

struct HomeTab: View {
    let dashboard: HomeDashboard
    let selectedStatusFilter: HomeStatusFilter
    let statusFilterCounts: [HomeStatusFilter: Int]
    let selectedWarningSort: HomeWarningSort
    let quickItems: [HomeConsumableItem]
    let action: HomeViewAction

    var body: some View {
        VStack(spacing: 0) {
            HomeTodayStatusSection(summary: dashboard.summary)
            HomeInventorySection(summary: dashboard.summary, items: dashboard.usageItems)
            HomeQuickItemSection(
                items: quickItems,
                selectedFilter: selectedStatusFilter,
                filterCounts: statusFilterCounts,
                onSelectFilter: action.onSelectStatusFilter,
                onSelect: action.onSelectConsumable
            )
            HomeQuickItemPreviewSection(
                items: quickItems,
                selectedSort: selectedWarningSort,
                onSelectSort: action.onSelectWarningSort,
                onShowList: action.onShowList,
                onSelect: action.onSelectConsumable
            )
            HomeUsageListSection(
                items: dashboard.usageItems,
                onSelect: action.onSelectConsumable
            )
        }
    }
}
