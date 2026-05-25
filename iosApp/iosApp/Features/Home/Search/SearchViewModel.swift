import Foundation

@MainActor
final class SearchViewModel: ObservableObject {
    @Published private(set) var state: SearchViewState

    private let repository: HomeListTabRepository?
    private var sourceItems: [HomeListTabItem]
    private var query = ""
    private var recentKeywords: [String]
    private var isLoading = false
    private var automaticallyLoads: Bool
    private var didStartInitialLoad = false
    private let pageSize = 100
    private let maxBootstrapPageCount = 5

    var currentQuery: String {
        query
    }

    var canRequestInitialFocus: Bool {
        if case .loading = state {
            return false
        }

        return true
    }

    init(
        sourceItems: [HomeListTabItem] = HomeListTabSampleData.items,
        recentKeywords: [String] = ["샤워기", "수세미", "정수기 필터", "수건"]
    ) {
        self.repository = nil
        self.sourceItems = sourceItems.uniqueByTitle()
        self.recentKeywords = recentKeywords
        self.automaticallyLoads = false
        self.state = .success(
            SearchViewData(
                query: "",
                recentKeywords: recentKeywords,
                suggestedKeywords: [],
                results: [],
                displayMode: recentKeywords.isEmpty ? .emptyRecent : .recentKeywords
            )
        )
        publish()
    }

    init(
        repository: HomeListTabRepository,
        recentKeywords: [String] = [],
        automaticallyLoads: Bool = true
    ) {
        self.repository = repository
        self.sourceItems = []
        self.recentKeywords = recentKeywords
        self.automaticallyLoads = automaticallyLoads
        self.state = automaticallyLoads ? .loading : .success(
            SearchViewData(
                query: "",
                recentKeywords: recentKeywords,
                suggestedKeywords: [],
                results: [],
                displayMode: recentKeywords.isEmpty ? .emptyRecent : .recentKeywords
            )
        )
    }

    func loadIfNeeded() {
        guard automaticallyLoads, !didStartInitialLoad else { return }
        didStartInitialLoad = true
        load()
    }

    func load() {
        guard let repository else {
            AppLog.enter(AppLog.searchViewModel, "SearchViewModel.load", "repository=sample")
            publish()
            return
        }

        guard !isLoading else {
            AppLog.success(AppLog.searchViewModel, "SearchViewModel.load", "skipped=alreadyLoading")
            return
        }

        isLoading = true
        state = .loading
        AppLog.enter(AppLog.searchViewModel, "SearchViewModel.load", "pageSize=\(pageSize)")
        Task {
            await loadItems(using: repository)
        }
    }

    func retry() {
        load()
    }

    func updateQuery(_ query: String) {
        guard self.query != query else { return }

        self.query = query
        publish()
        AppLog.success(
            AppLog.searchViewModel,
            "SearchViewModel.updateQuery",
            "queryLength=\(query.count) sourceCount=\(sourceItems.count)"
        )
    }

    func selectKeyword(_ keyword: String) {
        query = keyword
        recentKeywords.moveToFront(keyword)
        publish(forceResults: true)
    }

    func removeRecentKeyword(_ keyword: String) {
        recentKeywords.removeAll { $0 == keyword }
        publish()
    }

    func submitSearch() {
        let normalizedQuery = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !normalizedQuery.isEmpty else { return }
        recentKeywords.moveToFront(normalizedQuery)
        publish(forceResults: true)
    }

    private func publish(forceResults: Bool = false) {
        let normalizedQuery = query.trimmingCharacters(in: .whitespacesAndNewlines)
        let results = matchingItems(for: normalizedQuery)
        let suggestions = suggestedKeywords(for: normalizedQuery)
        let displayMode = displayMode(
            normalizedQuery: normalizedQuery,
            results: results,
            forceResults: forceResults
        )

        state = .success(
            SearchViewData(
                query: query,
                recentKeywords: recentKeywords,
                suggestedKeywords: suggestions,
                results: results,
                displayMode: displayMode
            )
        )
    }

    private func displayMode(
        normalizedQuery: String,
        results: [HomeListTabItem],
        forceResults: Bool
    ) -> SearchDisplayMode {
        if normalizedQuery.isEmpty {
            return recentKeywords.isEmpty ? .emptyRecent : .recentKeywords
        }

        if results.isEmpty {
            return .noResult
        }

        if forceResults ||
            normalizedQuery.count >= 3 ||
            results.contains(where: { $0.title.localizedCaseInsensitiveCompare(normalizedQuery) == .orderedSame }) {
            return .results
        }

        return .suggestions
    }

    private func matchingItems(for normalizedQuery: String) -> [HomeListTabItem] {
        guard !normalizedQuery.isEmpty else { return [] }
        return sourceItems.filter { item in
            item.title.localizedCaseInsensitiveContains(normalizedQuery)
        }
    }

    private func suggestedKeywords(for normalizedQuery: String) -> [String] {
        guard !normalizedQuery.isEmpty else { return [] }
        return sourceItems
            .map(\.title)
            .filter { $0.range(of: normalizedQuery, options: [.caseInsensitive, .anchored]) != nil }
            .uniqued()
    }

    private func loadItems(using repository: HomeListTabRepository) async {
        AppLog.enter(AppLog.searchViewModel, "SearchViewModel.loadItems")
        do {
            sourceItems = try await fetchAllItems(using: repository).uniqueByTitle()
            isLoading = false
            publish()
            AppLog.success(AppLog.searchViewModel, "SearchViewModel.loadItems", "sourceCount=\(sourceItems.count)")
        } catch {
            isLoading = false
            state = .loadFailed(message: error.searchMessage)
            AppLog.failure(AppLog.searchViewModel, "SearchViewModel.loadItems", error)
        }
    }

    private func fetchAllItems(using repository: HomeListTabRepository) async throws -> [HomeListTabItem] {
        var cursor: Int64?
        var allItems: [HomeListTabItem] = []
        var loadedPageCount = 0

        repeat {
            loadedPageCount += 1
            AppLog.enter(
                AppLog.searchViewModel,
                "SearchViewModel.fetchAllItems.page",
                "page=\(loadedPageCount) cursor=\(String(describing: cursor))"
            )
            let page = try await repository.items(
                request: HomeListTabPageRequest(
                    filters: .empty,
                    sortOption: .replacementDueSoon,
                    cursor: cursor,
                    size: pageSize
                )
            )
            allItems.append(contentsOf: page.items)
            AppLog.success(
                AppLog.searchViewModel,
                "SearchViewModel.fetchAllItems.page",
                "page=\(loadedPageCount) items=\(page.items.count) nextCursor=\(String(describing: page.nextCursor)) hasNext=\(page.hasNext)"
            )

            let previousCursor = cursor
            cursor = page.hasNext ? page.nextCursor : nil
            if cursor == previousCursor {
                AppLog.failure(
                    AppLog.searchViewModel,
                    "SearchViewModel.fetchAllItems.page",
                    SearchBootstrapError.repeatedCursor,
                    "cursor=\(String(describing: cursor))"
                )
                break
            }
        } while cursor != nil && loadedPageCount < maxBootstrapPageCount

        if cursor != nil {
            AppLog.success(
                AppLog.searchViewModel,
                "SearchViewModel.fetchAllItems",
                "stopped=maxBootstrapPageCount max=\(maxBootstrapPageCount) loadedItems=\(allItems.count)"
            )
        }

        return allItems
    }
}

private enum SearchBootstrapError: LocalizedError {
    case repeatedCursor

    var errorDescription: String? {
        switch self {
        case .repeatedCursor:
            return "검색 목록 페이지 cursor가 반복되어 로드를 중단했어요."
        }
    }
}

enum SearchViewState: Equatable {
    case loading
    case loadFailed(message: String)
    case success(SearchViewData)
}

struct SearchViewData: Equatable {
    let query: String
    let recentKeywords: [String]
    let suggestedKeywords: [String]
    let results: [HomeListTabItem]
    let displayMode: SearchDisplayMode
}

enum SearchDisplayMode: Equatable {
    case emptyRecent
    case recentKeywords
    case suggestions
    case results
    case noResult
}

private extension Array where Element == HomeListTabItem {
    func uniqueByTitle() -> [HomeListTabItem] {
        var seenTitles = Set<String>()
        return filter { item in
            seenTitles.insert(item.title).inserted
        }
    }
}

private extension Array where Element == String {
    mutating func moveToFront(_ keyword: String) {
        let trimmedKeyword = keyword.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedKeyword.isEmpty else { return }
        removeAll { $0 == trimmedKeyword }
        insert(trimmedKeyword, at: 0)
    }

    func uniqued() -> [String] {
        var seen = Set<String>()
        return filter { seen.insert($0).inserted }
    }
}

private extension Error {
    var searchMessage: String {
        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        }

        return "검색할 소모품 목록을 불러오지 못했어요."
    }
}
