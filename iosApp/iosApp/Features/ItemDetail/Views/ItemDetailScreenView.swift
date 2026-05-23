import SwiftUI

struct ItemDetailScreenView: View {
    let item: ItemDetailDisplayData
    let action: ItemDetailViewAction

    init(
        item: ItemDetailDisplayData,
        action: ItemDetailViewAction = .noop
    ) {
        self.item = item
        self.action = action
    }

    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 0) {
                OBRitDepthTopBar(
                    title: item.title,
                    backgroundColor: false,
                    onBackClick: action.onBack,
                    onMoreClick: action.onMore
                )

                ScrollView(showsIndicators: false) {
                    VStack(spacing: 0) {
                        ItemDetailHeaderHero(item: item, availableWidth: geometry.size.width)

                        VStack(spacing: ItemDetailLayout.sectionSpacing) {
                            ItemDetailDateSummary(item: item)
                            ItemDetailStockCard(item: item)
                            ItemDetailStatusSummaryCard(item: item)
                            ItemDetailReplacementHistoryChart(item: item)
                        }
                        .padding(.horizontal, ItemDetailLayout.horizontalPadding)
                        .padding(.bottom, ItemDetailLayout.actionBarButtonHeight + geometry.safeAreaInsets.bottom + OBRitSpacing.s12)
                    }
                }
                .background(OBRitColors.backgroundDefaultDefault)
            }
            .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
            .safeAreaInset(edge: .bottom, spacing: 0) {
                ItemDetailBottomActionBar(
                    onManageStock: action.onManageStock,
                    onCompleteReplacement: action.onCompleteReplacement
                )
            }
        }
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }
}

#Preview("Item Detail - Good") {
    ItemDetailScreenView(item: ItemDetailPreviewData.good)
}

#Preview("Item Detail - Warning") {
    ItemDetailScreenView(item: ItemDetailPreviewData.warning)
}
