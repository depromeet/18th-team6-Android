import SwiftUI

@MainActor
final class HomeViewModel: ObservableObject {
    @Published private(set) var state: HomeViewState
    @Published private(set) var selectedStatusFilter: HomeStatusFilter
    @Published private(set) var selectedWarningSort: HomeWarningSort

    private let repository: HomeDashboardRepository
    private var dashboard: HomeDashboard

    init(
        repository: HomeDashboardRepository = HomeSampleDashboardRepository(),
        initialDashboard: HomeDashboard = HomeSampleData.dashboard,
        automaticallyLoads: Bool = true
    ) {
        self.repository = repository
        self.dashboard = initialDashboard
        self.state = .success(initialDashboard)
        self.selectedStatusFilter = Self.firstVisibleStatusFilter(in: initialDashboard) ?? .replacementDanger
        self.selectedWarningSort = .replacementRisk

        if automaticallyLoads {
            load()
        }
    }

    var statusFilterCounts: [HomeStatusFilter: Int] {
        HomeStatusFilter.allCases.reduce(into: [:]) { result, filter in
            result[filter] = dashboard.warningItems.filter { $0.quickStatusFilters.contains(filter) }.count
        }
    }

    var visibleQuickItems: [HomeItemItem] {
        let filteredItems = dashboard.warningItems.filter {
            $0.quickStatusFilters.contains(selectedStatusFilter)
        }

        return Self.sortedQuickItems(filteredItems, for: selectedStatusFilter)
    }

    var visibleWarningItems: [HomeItemItem] {
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

    func load() {
        Task {
            await loadDashboard()
        }
    }

    func retry() {
        load()
    }

    private func loadDashboard() async {
        do {
            let dashboard = try await repository.dashboard()
            self.dashboard = dashboard
            if statusFilterCounts[selectedStatusFilter, default: 0] == 0 {
                selectedStatusFilter = Self.firstVisibleStatusFilter(in: dashboard) ?? .replacementDanger
            }
            state = .success(dashboard)
        } catch {
            state = .success(dashboard)
        }
    }

    private static func firstVisibleStatusFilter(in dashboard: HomeDashboard) -> HomeStatusFilter? {
        HomeStatusFilter.allCases.first { filter in
            dashboard.warningItems.contains { $0.quickStatusFilters.contains(filter) }
        }
    }

    private static func sortedQuickItems(
        _ items: [HomeItemItem],
        for filter: HomeStatusFilter
    ) -> [HomeItemItem] {
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
