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
        self.selectedStatusFilter = .replacementDanger
        self.selectedWarningSort = .replacementRisk
    }

    var statusFilterCounts: [HomeStatusFilter: Int] {
        HomeStatusFilter.allCases.reduce(into: [:]) { result, filter in
            result[filter] = dashboard.warningItems.filter { $0.statusFilters.contains(filter) }.count
        }
    }

    var visibleWarningItems: [HomeConsumableItem] {
        let filteredItems = dashboard.warningItems.filter {
            $0.statusFilters.contains(selectedStatusFilter)
        }

        switch selectedWarningSort {
        case .replacementRisk:
            return filteredItems.sorted {
                if $0.riskRank == $1.riskRank {
                    return $0.daysInUse > $1.daysInUse
                }
                return $0.riskRank < $1.riskRank
            }
        case .longestUse:
            return filteredItems.sorted {
                if $0.daysInUse == $1.daysInUse {
                    return $0.riskRank < $1.riskRank
                }
                return $0.daysInUse > $1.daysInUse
            }
        }
    }

    func selectStatusFilter(_ filter: HomeStatusFilter) {
        selectedStatusFilter = filter
    }

    func selectWarningSort(_ sort: HomeWarningSort) {
        selectedWarningSort = sort
    }
}

enum HomeViewState {
    case success(HomeDashboard)
}
