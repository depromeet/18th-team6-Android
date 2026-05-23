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
                ItemDetailNativeMenuTopBar(
                    title: item.title,
                    action: action
                )
                .zIndex(1)

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
                        .padding(
                            .bottom,
                            ItemDetailLayout.actionBarHeight +
                                geometry.safeAreaInsets.bottom +
                                ItemDetailLayout.actionBarAdditionalScrollPadding
                        )
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

private struct ItemDetailNativeMenuTopBar: View {
    let title: String
    let action: ItemDetailViewAction

    @State private var isMoreMenuPresented = false

    var body: some View {
        ZStack {
            HStack {
                topBarIconButton(symbolName: "chevron.left", accessibilityLabel: "뒤로", action: action.onBack)

                Spacer()

                Button {
                    withAnimation(.easeOut(duration: 0.16)) {
                        isMoreMenuPresented.toggle()
                    }
                } label: {
                    Image(systemName: "ellipsis")
                        .font(.system(size: OBRitSpacing.s5, weight: .regular))
                        .foregroundStyle(OBRitColors.common00)
                        .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
                        .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
                        .rotationEffect(.degrees(90))
                }
                .buttonStyle(.plain)
                .accessibilityLabel("더보기")
            }
            .padding(.horizontal, OBRitSpacing.s3)

            Text(title)
                .lineLimit(1)
                .truncationMode(.tail)
                .frame(width: ItemDetailNativeMenuTopBarMetrics.titleWidth)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
        }
        .frame(maxWidth: .infinity)
        .frame(height: OBRitSpacing.s14)
        .overlay(alignment: .topTrailing) {
            if isMoreMenuPresented {
                ItemDetailMoreMenu(items: action.moreMenuItems) { item in
                    withAnimation(.easeOut(duration: 0.12)) {
                        isMoreMenuPresented = false
                    }
                    action.onSelectMoreMenuItem(item)
                }
                .padding(.trailing, OBRitSpacing.s3)
                .offset(y: OBRitSpacing.s12)
                .transition(.opacity.combined(with: .scale(scale: 0.96, anchor: .topTrailing)))
                .zIndex(2)
            }
        }
    }

    private func topBarIconButton(
        symbolName: String,
        accessibilityLabel: String,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            Image(systemName: symbolName)
                .font(.system(size: OBRitSpacing.s5, weight: .regular))
                .foregroundStyle(OBRitColors.common00)
                .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
                .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityLabel)
    }
}

private enum ItemDetailNativeMenuTopBarMetrics {
    static let titleWidth: CGFloat = 277
}
