import SwiftUI

private enum HomePreviewLayoutMetrics {
    static let horizontalPadding = OBRitSpacing.s5
    static let verticalPadding = OBRitSpacing.s5
    static let contentSpacing = OBRitSpacing.s4
    static let cardSpacing = OBRitSpacing.s2
    static let headerHeight = OBRitSpacing.s9 + OBRitSpacing.s0_5
    static let moreButtonHeight: CGFloat = 46
    static let headerTitleSpacing = OBRitSpacing.s3
    static let bottomSheetTopMargin = OBRitSpacing.s5
    static let bottomSheetHeaderHeight = OBRitSpacing.s1 + OBRitSpacing.s8 + OBRitSpacing.s2_5
}

struct HomePreviewSection: View {
    let items: [HomeConsumableItem]
    let selectedSort: HomeWarningSort
    let onOpenSortSheet: () -> Void
    let onShowList: () -> Void
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(spacing: HomePreviewLayoutMetrics.contentSpacing) {
            sortHeader

            VStack(spacing: HomePreviewLayoutMetrics.cardSpacing) {
                ForEach(items.prefix(3)) { item in
                    Button {
                        onSelect(item.id)
                    } label: {
                        OBRitCardList(
                            level: item.cardLevel,
                            title: item.title,
                            daysInUseLabel: "\(item.daysInUse)일",
                            replaceLabel: item.replaceLabel,
                            sparesLabel: item.sparesLabel
                        ) {
                            Image(item.imageAssetName)
                                .resizable()
                                .scaledToFit()
                        }
                    }
                    .buttonStyle(.plain)
                }
            }

            OBRitFilledTextButton(
                text: "더보기",
                size: .middle,
                color: .gray,
                fillsWidth: true,
                action: onShowList
            )
            .frame(height: HomePreviewLayoutMetrics.moreButtonHeight)
        }
        .padding(.horizontal, HomePreviewLayoutMetrics.horizontalPadding)
        .padding(.vertical, HomePreviewLayoutMetrics.verticalPadding)
    }

    private var sortHeader: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
            HStack(spacing: HomePreviewLayoutMetrics.headerTitleSpacing) {
                HomePreviewSortButton(
                    text: selectedSort.title,
                    action: onOpenSortSheet
                )

                Text("미리보기")
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                Spacer()
            }
            .frame(height: HomePreviewLayoutMetrics.headerHeight)
        }
    }
}

private struct HomePreviewSortButton: View {
    let text: String
    let action: () -> Void

    var body: some View {
        OBRitDropdown(
            value: text,
            variant: .chip,
            onClick: action
        )
    }
}

struct HomePreviewSortBottomSheetOverlay: View {
    let selectedSort: HomeWarningSort
    let onDismiss: () -> Void
    let onSelectSort: (HomeWarningSort) -> Void

    var body: some View {
        GeometryReader { geometry in
            let bottomPadding = geometry.safeAreaInsets.bottom

            ZStack(alignment: .bottom) {
                OBRitColors.backgroundDefaultDimDefault
                    .ignoresSafeArea()
                    .onTapGesture(perform: onDismiss)

                OBRitBottomSheet(
                    contentHeight: contentHeight(in: geometry, bottomPadding: bottomPadding),
                    bottomPadding: bottomPadding
                ) {
                    ScrollView(showsIndicators: false) {
                        VStack(spacing: 0) {
                            ForEach(HomeWarningSort.allCases, id: \.self) { sort in
                                Button {
                                    onSelectSort(sort)
                                } label: {
                                    HStack {
                                        Text(sort.title)
                                            .obritTextStyle(
                                                OBRitTypography.xl,
                                                weight: OBRitFontWeight.bold,
                                                color: OBRitColors.common00
                                            )
                                        Spacer(minLength: 0)
                                        if sort == selectedSort {
                                            Image(systemName: "checkmark")
                                                .font(.system(size: OBRitSpacing.s5, weight: .bold))
                                                .foregroundStyle(OBRitColors.green300)
                                        }
                                    }
                                    .frame(height: OBRitSpacing.s14)
                                    .contentShape(Rectangle())
                                }
                                .buttonStyle(.plain)
                            }
                        }
                        .frame(maxWidth: .infinity, alignment: .top)
                    }
                }
                .ignoresSafeArea(.container, edges: .bottom)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottom)
            .ignoresSafeArea(.container, edges: .bottom)
        }
        .transition(.opacity)
    }

    private func contentHeight(in geometry: GeometryProxy, bottomPadding: CGFloat) -> CGFloat {
        let preferredHeight = OBRitSpacing.s14 * CGFloat(HomeWarningSort.allCases.count)
        let availableHeight = geometry.size.height
            - geometry.safeAreaInsets.top
            - HomePreviewLayoutMetrics.bottomSheetTopMargin
            - HomePreviewLayoutMetrics.bottomSheetHeaderHeight
            - bottomPadding
        return min(preferredHeight, max(0, availableHeight))
    }
}
