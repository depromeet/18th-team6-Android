import SwiftUI

@MainActor
final class HomeViewModel: ObservableObject {
    @Published private(set) var state: HomeViewState
    @Published private(set) var selectedStatusFilter: HomeStatusFilter
    @Published private(set) var selectedWarningSort: HomeWarningSort

    private let dashboard: HomeDashboard

    init(dashboard: HomeDashboard = HomeSampleData.dashboard) {
        self.dashboard = dashboard
        self.state = .success(dashboard)
        self.selectedStatusFilter = Self.firstVisibleStatusFilter(in: dashboard) ?? .replacementDanger
        self.selectedWarningSort = .replacementRisk
    }

    var statusFilterCounts: [HomeStatusFilter: Int] {
        HomeStatusFilter.allCases.reduce(into: [:]) { result, filter in
            result[filter] = dashboard.warningItems.filter { $0.quickStatusFilters.contains(filter) }.count
        }
    }

    var visibleQuickItems: [HomeConsumableItem] {
        let filteredItems = dashboard.warningItems.filter {
            $0.quickStatusFilters.contains(selectedStatusFilter)
        }

        return Self.sortedQuickItems(filteredItems, for: selectedStatusFilter)
    }

    var visibleWarningItems: [HomeConsumableItem] {
        let filteredItems = dashboard.warningItems.filter {
            $0.quickStatusFilters.contains(selectedStatusFilter)
        }

        switch selectedWarningSort {
        case .replacementRisk:
            return filteredItems.sorted {
                if $0.replacementDday == $1.replacementDday {
                    return $0.stockCount < $1.stockCount
                }
                return $0.replacementDday < $1.replacementDday
            }
        case .longestUse:
            return filteredItems.sorted {
                if $0.daysInUse == $1.daysInUse {
                    return $0.replacementDday < $1.replacementDday
                }
                return $0.daysInUse > $1.daysInUse
            }
        }
    }

    func selectStatusFilter(_ filter: HomeStatusFilter) {
        guard statusFilterCounts[filter, default: 0] > 0 else { return }
        selectedStatusFilter = filter
    }

    func selectWarningSort(_ sort: HomeWarningSort) {
        selectedWarningSort = sort
    }

    private static func firstVisibleStatusFilter(in dashboard: HomeDashboard) -> HomeStatusFilter? {
        HomeStatusFilter.allCases.first { filter in
            dashboard.warningItems.contains { $0.quickStatusFilters.contains(filter) }
        }
    }

    private static func sortedQuickItems(
        _ items: [HomeConsumableItem],
        for filter: HomeStatusFilter
    ) -> [HomeConsumableItem] {
        switch filter {
        case .replacementDanger, .replacementWarning:
            return items.sorted {
                if $0.replacementDday == $1.replacementDday {
                    return $0.stockCount < $1.stockCount
                }
                return $0.replacementDday < $1.replacementDday
            }
        case .spareShortage:
            return items.sorted {
                if $0.stockCount == $1.stockCount {
                    return $0.replacementDday < $1.replacementDday
                }
                return $0.stockCount < $1.stockCount
            }
        }
    }
}

enum HomeViewState {
    case success(HomeDashboard)
}
