import Foundation

@MainActor
final class SearchViewModel: ObservableObject {
    @Published private(set) var state: SearchViewState

    private let sourceItems: [HomeListTabItem]
    private var query = ""
    private var recentKeywords: [String]

    init(
        sourceItems: [HomeListTabItem] = HomeListTabSampleData.items,
        recentKeywords: [String] = ["샤워기", "수세미", "정수기 필터", "수건"]
    ) {
        self.sourceItems = sourceItems.uniqueByTitle()
        self.recentKeywords = recentKeywords
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

    func updateQuery(_ query: String) {
        self.query = query
        publish()
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
}

enum SearchViewState: Equatable {
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
