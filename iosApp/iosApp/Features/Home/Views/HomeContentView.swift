import SwiftUI

struct HomeContentView: View {
    @State private var isPreviewSortSheetPresented = false

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
                    let topNavigationHeight = geometry.safeAreaInsets.top + OBRitSpacing.s14
                    let homeTabMinHeight = max(
                        0,
                        geometry.size.height -
                            topNavigationHeight -
                            OBRitSpacing.s32
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
                    .padding(.top, topNavigationHeight)
                    .padding(.bottom, OBRitSpacing.s32)
                }
                .ignoresSafeArea(edges: .top)

                VStack(spacing: 0) {
                    Color.clear.frame(height: geometry.safeAreaInsets.top)
                    OBRitHomeTopBar.transparent(
                        showNotificationButton: false,
                        onSearchClick: action.onSearch,
                        onNotificationClick: action.onNotification,
                        onProfileClick: action.onProfile,
                        onLogoEasterEgg: action.onLogoEasterEgg
                    )
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
                .ignoresSafeArea(edges: .top)

                if dashboard.hasRegisteredItems {
                    VStack {
                        Spacer(minLength: 0)
                        HStack {
                            Spacer(minLength: 0)
                            HomeRegistrationFloatingActionMenu(
                                onRegisterImage: action.onRegisterImage,
                                onRegisterDirect: action.onRegisterDirect
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
        dashboard: HomeDashboard.empty,
        selectedStatusFilter: .danger,
        statusFilterCounts: [:],
        selectedWarningSort: .lowStock,
        visibleQuickItems: [],
        visibleWarningItems: [],
        onBottomSheetVisibleChange: { _ in },
        action: HomeViewAction(
            onSearch: {},
            onNotification: {},
            onProfile: {},
            onLogoEasterEgg: {},
            onRegisterImage: {},
            onRegisterDirect: {},
            onShowList: {},
            onSelectItem: { _ in },
            onSelectStatusFilter: { _ in },
            onSelectWarningSort: { _ in }
        )
    )
}
