import SwiftUI

struct HomeContentView: View {
    @State private var isPreviewSortSheetPresented = false
    @State private var isFabMenuPresented = false

    let dashboard: HomeDashboard
    let selectedStatusFilter: HomeStatusFilter
    let statusFilterCounts: [HomeStatusFilter: Int]
    let selectedWarningSort: HomeWarningSort
    let visibleQuickItems: [HomeItemItem]
    let visibleWarningItems: [HomeItemItem]
    let onBottomSheetVisibleChange: (Bool) -> Void
    let action: HomeViewAction

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                ScrollView(showsIndicators: false) {
                    let homeTabMinHeight = max(
                        0,
                        geometry.size.height -
                            geometry.safeAreaInsets.top -
                            OBRitSpacing.s14 -
                            OBRitSpacing.s32
                    )

                    VStack(spacing: 0) {
                        Color.clear.frame(height: geometry.safeAreaInsets.top)
                        OBRitHomeTopBar(
                            backgroundColor: false,
                            onSearchClick: action.onSearch,
                            onNotificationClick: action.onNotification,
                            onProfileClick: action.onProfile
                        )
                        HomeTab(
                            dashboard: dashboard,
                            selectedStatusFilter: selectedStatusFilter,
                            statusFilterCounts: statusFilterCounts,
                            selectedWarningSort: selectedWarningSort,
                            quickItems: visibleQuickItems,
                            previewItems: visibleWarningItems,
                            emptyStateMinHeight: homeTabMinHeight,
                            onOpenPreviewSortSheet: {
                                isPreviewSortSheetPresented = true
                            },
                            action: action
                        )
                    }
                    .padding(.bottom, OBRitSpacing.s32)
                }
                .ignoresSafeArea(edges: .top)

                if dashboard.hasRegisteredItems {
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

                if isPreviewSortSheetPresented {
                    HomePreviewSortBottomSheetOverlay(
                        selectedSort: selectedWarningSort,
                        onDismiss: {
                            isPreviewSortSheetPresented = false
                        },
                        onSelectSort: { sort in
                            action.onSelectWarningSort(sort)
                            isPreviewSortSheetPresented = false
                        }
                    )
                }
            }
            .background(OBRitColors.backgroundDefaultDefault)
        }
        .onChange(of: isPreviewSortSheetPresented) { _, isPresented in
            onBottomSheetVisibleChange(isPresented)
        }
        .onDisappear {
            onBottomSheetVisibleChange(false)
        }
    }
}

#Preview {
    HomeContentView(
        dashboard: HomeSampleData.dashboard,
        selectedStatusFilter: .replacementDanger,
        statusFilterCounts: [.replacementDanger: 3, .spareShortage: 3, .replacementWarning: 1],
        selectedWarningSort: .replacementRisk,
        visibleQuickItems: HomeSampleData.dashboard.warningItems
            .filter { $0.quickStatusFilters.contains(.replacementDanger) }
            .sorted { $0.replacementDday < $1.replacementDday },
        visibleWarningItems: HomeSampleData.dashboard.warningItems
            .filter { $0.quickStatusFilters.contains(.replacementDanger) }
            .sorted { $0.riskRank < $1.riskRank },
        onBottomSheetVisibleChange: { _ in },
        action: HomeViewAction(
            onSearch: {},
            onNotification: {},
            onProfile: {},
            onRegisterDirect: {},
            onShowList: {},
            onSelectItem: { _ in },
            onSelectStatusFilter: { _ in },
            onSelectWarningSort: { _ in }
        )
    )
}
