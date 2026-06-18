import SwiftUI

struct HomeTab: View {
    let dashboard: HomeDashboard
    let selectedStatusFilter: HomeStatusFilter
    let statusFilterCounts: [HomeStatusFilter: Int]
    let selectedWarningSort: HomeWarningSort
    let quickItems: [HomeItemItem]
    let previewItems: [HomeItemItem]
    let emptyStateMinHeight: CGFloat
    let onOpenPreviewSortSheet: () -> Void
    let action: HomeViewAction

    var body: some View {
        VStack(spacing: 0) {
            if dashboard.hasRegisteredItems {
                HomeTodayStatusSection(summary: dashboard.summary)
                HomeInventorySection(summary: dashboard.summary, items: dashboard.usageItems)
                if isQuickItemVisible {
                    HomeQuickItemSection(
                        items: quickItems,
                        selectedFilter: selectedStatusFilter,
                        filterCounts: statusFilterCounts,
                        onSelectFilter: action.onSelectStatusFilter,
                        onSelect: action.onSelectItem
                    )
                }
                HomePreviewSection(
                    items: previewItems,
                    selectedSort: selectedWarningSort,
                    onOpenSortSheet: onOpenPreviewSortSheet,
                    onShowList: action.onShowList,
                    onSelect: action.onSelectItem
                )
            }
            HomeUsageListSection(
                items: dashboard.usageItems,
                showsHeader: dashboard.hasRegisteredItems,
                emptyStateMinHeight: dashboard.hasRegisteredItems ? nil : emptyStateMinHeight,
                onSelect: action.onSelectItem,
                onRegister: action.onRegisterDirect
            )
        }
    }

    private var isQuickItemVisible: Bool {
        statusFilterCounts.values.contains { $0 > 0 }
    }
}
