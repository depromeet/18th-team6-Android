import SwiftUI

@MainActor
final class HomeViewModel: ObservableObject {
    @Published private(set) var state: HomeViewState
    @Published private(set) var selectedStatusFilter: HomeStatusFilter
    @Published private(set) var selectedWarningSort: HomeWarningSort

    private let repository: HomeDashboardRepository
    private var dashboard: HomeDashboard?
    private var didStartInitialLoad: Bool

    init(
        repository: HomeDashboardRepository = HomeSampleDashboardRepository(),
        initialDashboard: HomeDashboard? = nil
    ) {
        self.repository = repository
        self.dashboard = initialDashboard
        self.didStartInitialLoad = initialDashboard != nil
        self.state = initialDashboard.map(HomeViewState.success) ?? .loading
        self.selectedStatusFilter = initialDashboard.flatMap(Self.firstVisibleStatusFilter(in:)) ?? .replacementDanger
        self.selectedWarningSort = .replacementRisk
    }

    var statusFilterCounts: [HomeStatusFilter: Int] {
        guard let dashboard else { return [:] }
        return HomeStatusFilter.allCases.reduce(into: [:]) { result, filter in
            result[filter] = dashboard.warningItems.filter { $0.quickStatusFilters.contains(filter) }.count
        }
    }

    var visibleQuickItems: [HomeItemItem] {
        guard let dashboard else { return [] }
        let filteredItems = dashboard.warningItems.filter {
            $0.quickStatusFilters.contains(selectedStatusFilter)
        }

        return Self.sortedQuickItems(filteredItems, for: selectedStatusFilter)
    }

    var visibleWarningItems: [HomeItemItem] {
        guard let dashboard else { return [] }
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

    func loadInitialDashboardIfNeeded() async {
        guard !didStartInitialLoad else { return }
        didStartInitialLoad = true
        await loadDashboard()
    }

    func retry() {
        Task {
            await loadDashboard()
        }
    }

    func refresh() {
        Task {
            await loadDashboard()
        }
    }

    private func loadDashboard() async {
        AppLog.enter(AppLog.homeViewModel, "HomeViewModel.loadDashboard")
        do {
            let dashboard = try await repository.dashboard()
            self.dashboard = dashboard
            if statusFilterCounts[selectedStatusFilter, default: 0] == 0 {
                selectedStatusFilter = Self.firstVisibleStatusFilter(in: dashboard) ?? .replacementDanger
            }
            state = .success(dashboard)
            AppLog.success(
                AppLog.homeViewModel,
                "HomeViewModel.loadDashboard",
                "warningCount=\(dashboard.warningItems.count) usageCount=\(dashboard.usageItems.count)"
            )
        } catch {
            if let dashboard {
                state = .success(dashboard)
                AppLog.failure(
                    AppLog.homeViewModel,
                    "HomeViewModel.loadDashboard",
                    error,
                    "usingCachedDashboard=true warningCount=\(dashboard.warningItems.count)"
                )
            } else {
                state = .loadFailed(message: error.homeMessage)
                AppLog.failure(AppLog.homeViewModel, "HomeViewModel.loadDashboard", error)
            }
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
    case loading
    case loadFailed(message: String)
    case success(HomeDashboard)
}

private extension Error {
    var homeMessage: String {
        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        }

        return "홈 정보를 불러오지 못했어요."
    }
}
