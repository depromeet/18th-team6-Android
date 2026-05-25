import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel: HomeViewModel
    @ObservedObject private var refreshCenter: AppRefreshCenter

    let onNavigateItem: (ItemRoute) -> Void
    let onShowListTab: () -> Void
    let onBottomSheetVisibleChange: (Bool) -> Void

    @MainActor
    init(
        viewModelFactory: @MainActor @escaping () -> HomeViewModel,
        refreshCenter: AppRefreshCenter = AppRefreshCenter(),
        onNavigateItem: @escaping (ItemRoute) -> Void,
        onShowListTab: @escaping () -> Void,
        onBottomSheetVisibleChange: @escaping (Bool) -> Void = { _ in }
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        self.refreshCenter = refreshCenter
        self.onNavigateItem = onNavigateItem
        self.onShowListTab = onShowListTab
        self.onBottomSheetVisibleChange = onBottomSheetVisibleChange
    }

    var body: some View {
        Group {
            switch viewModel.state {
            case .loading:
                HomeDataUnavailableContentView(
                    presentation: .loading,
                    action: homeAction
                )
            case let .loadFailed(message):
                HomeDataUnavailableContentView(
                    presentation: .loadFailed(message: message, onRetry: viewModel.retry),
                    action: homeAction
                )
            case let .success(dashboard):
                HomeContentView(
                    dashboard: dashboard,
                    selectedStatusFilter: viewModel.selectedStatusFilter,
                    statusFilterCounts: viewModel.statusFilterCounts,
                    selectedWarningSort: viewModel.selectedWarningSort,
                    visibleQuickItems: viewModel.visibleQuickItems,
                    visibleWarningItems: viewModel.visibleWarningItems,
                    onBottomSheetVisibleChange: onBottomSheetVisibleChange,
                    action: homeAction
                )
            }
        }
        .onChange(of: refreshCenter.itemRefreshToken) { _, _ in
            viewModel.refresh()
        }
    }

    private var homeAction: HomeViewAction {
        HomeViewAction(
            onSearch: { onNavigateItem(.search) },
            onNotification: {},
            onProfile: {},
            onRegisterDirect: { onNavigateItem(.itemRegistration) },
            onShowList: onShowListTab,
            onSelectItem: { onNavigateItem(.detail(itemId: $0)) },
            onSelectStatusFilter: viewModel.selectStatusFilter,
            onSelectWarningSort: viewModel.selectWarningSort
        )
    }
}

struct HomeViewAction {
    let onSearch: () -> Void
    let onNotification: () -> Void
    let onProfile: () -> Void
    let onRegisterDirect: () -> Void
    let onShowList: () -> Void
    let onSelectItem: (Int) -> Void
    let onSelectStatusFilter: (HomeStatusFilter) -> Void
    let onSelectWarningSort: (HomeWarningSort) -> Void
}

private struct HomeDataUnavailableContentView: View {
    enum Presentation {
        case loading
        case loadFailed(message: String, onRetry: () -> Void)
    }

    @State private var isFabMenuPresented = false

    let presentation: Presentation
    let action: HomeViewAction

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                VStack(spacing: 0) {
                    Color.clear.frame(height: geometry.safeAreaInsets.top)
                    OBRitHomeTopBar(
                        backgroundColor: false,
                        onSearchClick: action.onSearch,
                        onNotificationClick: action.onNotification,
                        onProfileClick: action.onProfile
                    )

                    dataStateMessage
                        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                        .padding(.horizontal, OBRitSpacing.s5)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
                .ignoresSafeArea(edges: .top)

                VStack {
                    Spacer(minLength: 0)
                    HStack {
                        Spacer(minLength: 0)
                        OBRitFloatingActionMenu(
                            isPresented: $isFabMenuPresented,
                            items: [
                                OBRitFloatingActionMenuItem(
                                    id: "itemRegistration",
                                    title: "직접 등록",
                                    action: action.onRegisterDirect
                                )
                            ],
                            accessibilityLabel: "소모품 등록"
                        )
                        .padding(.trailing, OBRitSpacing.s5)
                        .padding(.bottom, OBRitSpacing.s6)
                    }
                }
            }
            .background(OBRitColors.backgroundDefaultDefault)
        }
    }

    @ViewBuilder
    private var dataStateMessage: some View {
        switch presentation {
        case .loading:
            VStack(spacing: OBRitSpacing.s5) {
                ProgressView()
                    .tint(OBRitColors.green300)
                    .accessibilityLabel("홈 정보 불러오는 중")

                Text("홈 정보를 불러오는 중이에요")
                    .multilineTextAlignment(.center)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.textDefaultDefault
                    )
            }
        case let .loadFailed(message, onRetry):
            VStack(spacing: OBRitSpacing.s5) {
                Text(message)
                    .multilineTextAlignment(.center)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.textDefaultDefault
                    )

                OBRitFilledTextButton(text: "다시 시도", size: .middle, action: onRetry)
            }
        }
    }
}
