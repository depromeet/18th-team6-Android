import SwiftUI

struct SearchView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var viewModel: SearchViewModel
    @State private var didRequestInitialFocus = false
    @FocusState private var isSearchFocused: Bool

    let onBack: (() -> Void)?
    let onSelectItem: (Int) -> Void

    @MainActor
    init(
        onBack: (() -> Void)? = nil,
        onSelectItem: @escaping (Int) -> Void
    ) {
        self.init(
            viewModelFactory: { SearchViewModel() },
            onBack: onBack,
            onSelectItem: onSelectItem
        )
    }

    @MainActor
    init(
        viewModelFactory: @MainActor @escaping () -> SearchViewModel,
        onBack: (() -> Void)? = nil,
        onSelectItem: @escaping (Int) -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        self.onBack = onBack
        self.onSelectItem = onSelectItem
    }

    @MainActor
    init(
        viewModel: SearchViewModel,
        onBack: (() -> Void)? = nil,
        onSelectItem: @escaping (Int) -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.onBack = onBack
        self.onSelectItem = onSelectItem
    }

    var body: some View {
        SearchContentView(
            state: viewModel.state,
            query: Binding(
                get: { viewModel.currentQuery },
                set: viewModel.updateQuery
            ),
            searchFocus: $isSearchFocused,
            action: SearchViewAction(
                onBack: handleBack,
                onSelectKeyword: viewModel.selectKeyword,
                onRemoveRecentKeyword: viewModel.removeRecentKeyword,
                onSubmitSearch: viewModel.submitSearch,
                onRetry: viewModel.retry,
                onSelectItem: onSelectItem
            )
        )
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
        .onAppear {
            AppLog.enter(AppLog.searchViewModel, "SearchView.onAppear")
        }
        .task {
            viewModel.loadIfNeeded()
            await requestInitialFocusIfReady()
        }
        .onChange(of: viewModel.canRequestInitialFocus) { _, _ in
            Task {
                await requestInitialFocusIfReady()
            }
        }
        .onDisappear {
            AppLog.success(AppLog.searchViewModel, "SearchView.onDisappear")
        }
    }

    private func handleBack() {
        if let onBack {
            onBack()
        } else {
            dismiss()
        }
    }

    @MainActor
    private func requestInitialFocusIfReady() async {
        guard viewModel.canRequestInitialFocus, !didRequestInitialFocus else { return }

        didRequestInitialFocus = true
        AppLog.enter(AppLog.searchViewModel, "SearchView.initialFocus")
        try? await Task.sleep(for: .milliseconds(300))
        guard !Task.isCancelled else {
            didRequestInitialFocus = false
            return
        }
        isSearchFocused = true
        AppLog.success(AppLog.searchViewModel, "SearchView.initialFocus")
    }
}

struct SearchViewAction {
    let onBack: () -> Void
    let onSelectKeyword: (String) -> Void
    let onRemoveRecentKeyword: (String) -> Void
    let onSubmitSearch: () -> Void
    let onRetry: () -> Void
    let onSelectItem: (Int) -> Void
}

private struct SearchContentView: View {
    let state: SearchViewState
    @Binding var query: String
    let searchFocus: FocusState<Bool>.Binding
    let action: SearchViewAction

    var body: some View {
        VStack(spacing: 0) {
            OBRitSearchTopBar(
                query: $query,
                searchFocus: searchFocus,
                backgroundColor: false,
                onBackClick: action.onBack,
                onSubmit: action.onSubmitSearch
            )

            switch state {
            case .loading:
                SearchLoadingView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
            case let .loadFailed(message):
                SearchLoadFailedView(message: message, onRetry: action.onRetry)
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
            case let .success(viewData):
                SearchBodyView(
                    viewData: viewData,
                    action: action
                )
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
            }
        }
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
    }
}

private struct SearchLoadingView: View {
    var body: some View {
        VStack(spacing: OBRitSpacing.s3) {
            ProgressView()
                .tint(OBRitColors.green300)
                .accessibilityLabel("검색 목록 불러오는 중")

            Text("소모품 목록을 불러오는 중이에요")
                .multilineTextAlignment(.center)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.gray500)
        }
        .frame(maxWidth: .infinity, alignment: .top)
        .padding(.top, SearchMetrics.emptyMessageTopPadding)
        .padding(.horizontal, OBRitSpacing.s5)
    }
}

private struct SearchLoadFailedView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s4) {
            SearchEmptyMessageView(
                title: message,
                description: "잠시 후 다시 시도해주세요"
            )

            OBRitFilledTextButton(text: "다시 시도", size: .middle, action: onRetry)
        }
        .frame(maxWidth: .infinity, alignment: .top)
    }
}

private struct SearchBodyView: View {
    let viewData: SearchViewData
    let action: SearchViewAction

    var body: some View {
        switch viewData.displayMode {
        case .emptyRecent:
            SearchEmptyMessageView(
                title: "어떤 소모품을 찾아드릴까요?",
                description: "지금 떠오르는 소모품을 검색해 보세요"
            )
        case .recentKeywords:
            RecentKeywordListView(
                keywords: viewData.recentKeywords,
                onSelect: action.onSelectKeyword,
                onRemove: action.onRemoveRecentKeyword
            )
        case .suggestions:
            SearchSuggestionListView(
                query: viewData.query.trimmingCharacters(in: .whitespacesAndNewlines),
                keywords: viewData.suggestedKeywords,
                onSelect: action.onSelectKeyword
            )
        case .results:
            SearchResultListView(
                items: viewData.results,
                onSelectItem: action.onSelectItem
            )
        case .noResult:
            SearchEmptyMessageView(
                title: "소모품 검색 결과가 없어요!",
                description: "소모품 이름을 검색해 보세요"
            )
        }
    }
}

private struct SearchEmptyMessageView: View {
    let title: String
    let description: String

    var body: some View {
        VStack(spacing: OBRitSpacing.s2) {
            Text(title)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
            Text(description)
                .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.medium, color: OBRitColors.gray500)
        }
        .frame(maxWidth: .infinity, alignment: .top)
        .padding(.top, SearchMetrics.emptyMessageTopPadding)
        .padding(.horizontal, OBRitSpacing.s5)
    }
}

private struct RecentKeywordListView: View {
    let keywords: [String]
    let onSelect: (String) -> Void
    let onRemove: (String) -> Void

    var body: some View {
        VStack(spacing: 0) {
            ForEach(keywords, id: \.self) { keyword in
                RecentKeywordRow(
                    keyword: keyword,
                    onSelect: onSelect,
                    onRemove: onRemove
                )
            }
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.top, OBRitSpacing.s4)
    }
}

private struct RecentKeywordRow: View {
    let keyword: String
    let onSelect: (String) -> Void
    let onRemove: (String) -> Void

    var body: some View {
        HStack(spacing: OBRitSpacing.s2) {
            Button {
                onSelect(keyword)
            } label: {
                Text(keyword)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.common00)
            }
            .buttonStyle(.plain)

            Button {
                onRemove(keyword)
            } label: {
                Image(systemName: "xmark")
                    .font(.system(size: OBRitSpacing.s3, weight: .regular))
                    .foregroundStyle(OBRitColors.gray400)
                    .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
            }
            .buttonStyle(.plain)
            .accessibilityLabel("\(keyword) 최근 검색어 삭제")
        }
        .frame(height: SearchMetrics.recentKeywordRowHeight)
    }
}

private struct SearchSuggestionListView: View {
    let query: String
    let keywords: [String]
    let onSelect: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            ForEach(keywords, id: \.self) { keyword in
                Button {
                    onSelect(keyword)
                } label: {
                    Text(highlightedKeyword(keyword))
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                        .padding(.vertical, OBRitSpacing.s2)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.top, OBRitSpacing.s4)
    }

    private func highlightedKeyword(_ keyword: String) -> AttributedString {
        var attributedKeyword = AttributedString(keyword)

        if let range = attributedKeyword.range(of: query, options: [.caseInsensitive]) {
            attributedKeyword[range].foregroundColor = OBRitColors.green300
        }

        return attributedKeyword
    }
}

private struct SearchResultListView: View {
    let items: [HomeListTabItem]
    let onSelectItem: (Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("검색 결과")
                .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.medium, color: OBRitColors.common00)
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.top, OBRitSpacing.s4)
                .padding(.bottom, OBRitSpacing.s3)

            ScrollView(showsIndicators: false) {
                LazyVStack(spacing: OBRitSpacing.s2) {
                    ForEach(items) { item in
                        Button {
                            onSelectItem(item.id)
                        } label: {
                            OBRitCardList(
                                level: item.cardLevel,
                                title: item.title,
                                daysInUseLabel: item.daysInUseLabel,
                                replaceLabel: item.replaceLabel,
                                sparesLabel: item.sparesLabel
                            ) {
                                Image(item.assetName)
                                    .resizable()
                                    .scaledToFit()
                                    .padding(OBRitSpacing.s2)
                            }
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.bottom, OBRitSpacing.s8)
            }
        }
    }
}

private enum SearchMetrics {
    static let emptyMessageTopPadding: CGFloat = 204
    static let recentKeywordRowHeight: CGFloat = 46
}

private struct SearchContentPreview: View {
    let state: SearchViewState
    @State private var query: String
    @FocusState private var isSearchFocused: Bool

    init(state: SearchViewState, query: String) {
        self.state = state
        _query = State(initialValue: query)
    }

    var body: some View {
        SearchContentView(
            state: state,
            query: $query,
            searchFocus: $isSearchFocused,
            action: SearchViewAction(
                onBack: {},
                onSelectKeyword: { _ in },
                onRemoveRecentKeyword: { _ in },
                onSubmitSearch: {},
                onRetry: {},
                onSelectItem: { _ in }
            )
        )
    }
}

#Preview("Search - Recent") {
    SearchContentPreview(
        state: .success(
            SearchViewData(
                query: "",
                recentKeywords: ["샤워기", "수세미", "정수기 필터", "수건"],
                suggestedKeywords: [],
                results: [],
                displayMode: .recentKeywords
            )
        ),
        query: ""
    )
}

#Preview("Search - Results") {
    SearchContentPreview(
        state: .success(
            SearchViewData(
                query: "샤워기",
                recentKeywords: [],
                suggestedKeywords: [],
                results: Array(HomeListTabSampleData.items.prefix(2)),
                displayMode: .results
            )
        ),
        query: "샤워기"
    )
}
