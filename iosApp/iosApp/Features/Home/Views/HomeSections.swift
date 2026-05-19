import SwiftUI

private enum HomeLayoutMetrics {
    static let inventoryTopPadding = OBRitSpacing.s3
    static let inventorySummarySpacing = OBRitSpacing.s7
    static let dashboardCardHeight: CGFloat = 96 // 내 상태 현황 카드 높이
    static let dashboardCardPadding = OBRitSpacing.s6
    static let dashboardContentSpacing = OBRitSpacing.s6
    static let dashboardMetricValueSpacing = OBRitSpacing.s4
    static var dashboardMeterHeight: CGFloat {
        max(0, dashboardCardHeight - dashboardCardPadding * 2)
    }
    static let dashboardMeterBarHeight: CGFloat = 18
    static let dashboardMeterDividerWidth = OBRitSpacing.s1
    static let dashboardMeterDividerStep = OBRitSpacing.s1 + OBRitSpacing.px
    static let dashboardMeterDividerLeadingOffset = -(OBRitSpacing.s0_5 + OBRitSpacing.px / 2)
    static let dashboardIndicatorHeight = OBRitSpacing.s6
    static var dashboardMarkerHeight: CGFloat {
        OBRitTypography.small.lineHeight + OBRitSpacing.s0_5 + dashboardIndicatorHeight
    }
    static let dashboardAverageMarkerTopOffset = OBRitSpacing.s5 + OBRitSpacing.s0_5
    static var dashboardMarkerOverlayHeight: CGFloat {
        dashboardMarkerHeight + dashboardAverageMarkerTopOffset
    }
    static let ratioLabelWidth = OBRitSpacing.s14
    static let ratioLabelOffsetY = -(OBRitSpacing.s16 + OBRitSpacing.s1 + OBRitSpacing.px)
    static let dashboardOwnMarkerWidth = OBRitSpacing.s11
    static let dashboardAverageMarkerWidth = OBRitSpacing.s10
}

struct HomeStatusSection: View {
    static let height: CGFloat = 139

    let summary: HomeSummary

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            VStack(alignment: .leading, spacing: 0) {
                Text("오늘의 소모품 관리")
                    .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    .lineLimit(1)
                    .minimumScaleFactor(0.88)
                    .frame(height: OBRitTypography.s5xl.lineHeight, alignment: .center)
                HStack(spacing: 0) {
                    Text("상태는 ")
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    Text(summary.status)
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: statusColor)
                    Text("예요")
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                }
                .lineLimit(1)
                .minimumScaleFactor(0.88)
                .frame(height: OBRitTypography.s5xl.lineHeight, alignment: .center)
            }

            HStack(spacing: OBRitSpacing.s3) {
                HomeStatusPair(label: "교체 관리", value: summary.replacementStatus)
                HomeStatusPair(label: "여분 관리", value: summary.stockStatus)
            }
            .frame(height: OBRitTypography.base.lineHeight, alignment: .center)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(OBRitSpacing.s5)
        .frame(height: Self.height, alignment: .topLeading)
    }

    private var statusColor: Color {
        summary.status == "완벽" || summary.status == "양호" ? OBRitColors.textPositiveDefault : OBRitColors.textWarningDefault
    }
}

private struct HomeStatusPair: View {
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: OBRitSpacing.s1) {
            Text(label)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                .lineLimit(1)
            Text(value)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .lineLimit(1)
        }
    }
}

struct HomeInventorySection: View {
    let summary: HomeSummary
    let items: [HomeConsumableItem]

    var body: some View {
        VStack(spacing: HomeLayoutMetrics.inventorySummarySpacing) {
            HomeOrbOverview(summary: summary, items: items)

            HomeStatusOverviewCard(summary: summary)
                .padding(.horizontal, OBRitSpacing.s5)
                .frame(maxWidth: .infinity, alignment: .center)
        }
        .padding(.top, HomeLayoutMetrics.inventoryTopPadding)
        .padding(.bottom, OBRitSpacing.s4)
    }
}

private struct HomeOrbOverview: View {
    let summary: HomeSummary
    let items: [HomeConsumableItem]

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = OBRitSpacing.s5
            let contentWidth = max(0, geometry.size.width - horizontalPadding * 2)
            let reservedLabelWidth = HomeLayoutMetrics.ratioLabelWidth * 2
            let reservedSpacing = OBRitSpacing.s2 * 2
            let availableOrbWidth = contentWidth - reservedLabelWidth - reservedSpacing
            let orbScale = max(0, min(1, availableOrbWidth / HomeOrbMetrics.outerDiameter))
            let orbSize = HomeOrbMetrics.outerDiameter * orbScale
            let labelOffsetY = HomeLayoutMetrics.ratioLabelOffsetY * orbScale

            HStack(alignment: .center, spacing: OBRitSpacing.s2) {
                HomeRatioLabel(
                    value: summary.positiveRatio,
                    label: "양호",
                    color: OBRitColors.textPositiveDefault,
                    alignment: .trailing
                )
                .padding(.top, OBRitSpacing.s8 * orbScale)
                .offset(y: labelOffsetY)

                HomeGlassBall(
                    normalRatio: Double(summary.positiveRatio) / 100,
                    warningRatio: Double(summary.warningRatio) / 100,
                    interiorItems: items.map(\.orbInteriorItem)
                )
                .scaleEffect(orbScale)
                .frame(width: orbSize, height: orbSize)

                HomeRatioLabel(
                    value: summary.warningRatio,
                    label: "경고",
                    color: OBRitColors.textWarningDefault,
                    alignment: .leading
                )
                .padding(.top, OBRitSpacing.s8 * orbScale)
                .offset(y: labelOffsetY)
            }
            .padding(.horizontal, horizontalPadding)
            .frame(width: geometry.size.width, height: geometry.size.height)
        }
        .frame(height: HomeOrbMetrics.outerDiameter)
    }
}

private extension HomeConsumableItem {
    var orbInteriorItem: HomeOrbInteriorItem {
        HomeOrbInteriorItem(
            id: id,
            assetName: orbAssetName,
            weight: 0.92 + CGFloat(max(1, 7 - riskRank)) * 0.055
        )
    }
}

private struct HomeRatioLabel: View {
    let value: Int
    let label: String
    let color: Color
    let alignment: HorizontalAlignment

    var body: some View {
        VStack(alignment: alignment, spacing: 0) {
            Text("\(value)%")
                .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.bold, color: color)
            Text(label)
                .obritTextStyle(OBRitTypography.xl, color: color)
        }
        .frame(width: HomeLayoutMetrics.ratioLabelWidth, alignment: alignment == .leading ? .leading : .trailing)
        .background {
            RoundedRectangle(cornerRadius: HomeRatioLabelMetrics.backgroundCornerRadius)
                .fill(color.opacity(HomeRatioLabelMetrics.backgroundOpacity))
                .frame(
                    width: HomeRatioLabelMetrics.backgroundWidth,
                    height: HomeRatioLabelMetrics.backgroundHeight
                )
                .blur(radius: HomeRatioLabelMetrics.backgroundBlurRadius)
        }
    }
}

private enum HomeRatioLabelMetrics {
    static let backgroundWidth: CGFloat = 291
    static let backgroundHeight: CGFloat = 292
    static let backgroundCornerRadius: CGFloat = 9999
    static let backgroundBlurRadius: CGFloat = 48
    static let backgroundOpacity: Double = 0.10
}

private struct HomeStatusOverviewCard: View {
    let summary: HomeSummary

    var body: some View {
        HStack(spacing: HomeLayoutMetrics.dashboardContentSpacing) {
            HomeStatusOverviewMetrics(summary: summary)
                .frame(height: HomeLayoutMetrics.dashboardMeterHeight, alignment: .center)
                .fixedSize(horizontal: true, vertical: false)

            HomeStatusOverviewMeter(summary: summary)
                .frame(maxWidth: .infinity)
                .frame(height: HomeLayoutMetrics.dashboardMeterHeight)
                .layoutPriority(1)
        }
        .padding(HomeLayoutMetrics.dashboardCardPadding)
        .frame(maxWidth: .infinity, minHeight: HomeLayoutMetrics.dashboardCardHeight)
        .background(OBRitColors.common100)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        .accessibilityElement(children: .contain)
        .accessibilityLabel("내 상태 현황")
    }
}

private struct HomeStatusOverviewMetrics: View {
    let summary: HomeSummary

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
            HomeStatusOverviewMetricRow(title: "내 소모품", value: "\(summary.totalCount)", rowColor: OBRitColors.common00)
            HomeStatusOverviewMetricRow(title: "교체 필요", value: "\(summary.warningCount)", rowColor: replacementRequiredColor)
        }
        .fixedSize(horizontal: true, vertical: false)
    }

    // 내 상태 현황: 교체 필요가 0개일 경우, 교체 필요 영역 텍스트 컬러가 Gray로 변경.
    private var replacementRequiredColor: Color {
        summary.warningCount == 0 ? OBRitColors.textDefaultSecondary : OBRitColors.textWarningDefault
    }
}

private struct HomeStatusOverviewMetricRow: View {
    let title: String
    let value: String
    let rowColor: Color

    var body: some View {
        HStack(spacing: HomeLayoutMetrics.dashboardMetricValueSpacing) {
            Text(title)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: rowColor)
                .lineLimit(1)
            Text(value)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: rowColor)
                .lineLimit(1)
        }
        .frame(height: OBRitTypography.base.lineHeight)
        .fixedSize(horizontal: true, vertical: false)
    }
}

private struct HomeStatusOverviewMeter: View {
    let summary: HomeSummary

    var body: some View {
        GeometryReader { geometry in
            let width = geometry.size.width

            ZStack(alignment: .center) {
                HomeStatusOverviewMeterBars()
                    .frame(width: width)
                    .frame(height: HomeLayoutMetrics.dashboardMeterBarHeight)

                ZStack(alignment: .topLeading) {
                    HomeStatusOverviewMeterMarker(title: "내 상태", titlePosition: .top)
                        .frame(width: HomeLayoutMetrics.dashboardOwnMarkerWidth, height: HomeLayoutMetrics.dashboardMarkerHeight)
                        .offset(x: markerOffsetX(for: summary.ownStatusPercent, markerWidth: HomeLayoutMetrics.dashboardOwnMarkerWidth, in: width))

                    HomeStatusOverviewMeterMarker(title: "평균", titlePosition: .bottom)
                        .frame(width: HomeLayoutMetrics.dashboardAverageMarkerWidth, height: HomeLayoutMetrics.dashboardMarkerHeight)
                        .offset(
                            x: markerOffsetX(for: summary.averageStatusPercent, markerWidth: HomeLayoutMetrics.dashboardAverageMarkerWidth, in: width),
                            y: HomeLayoutMetrics.dashboardAverageMarkerTopOffset
                        )
                }
                .frame(width: width, height: HomeLayoutMetrics.dashboardMarkerOverlayHeight, alignment: .topLeading)
            }
            .frame(width: width, height: geometry.size.height)
            .frame(width: geometry.size.width, height: geometry.size.height, alignment: .center)
        }
    }

    private func markerOffsetX(for percent: Double, markerWidth: CGFloat, in width: CGFloat) -> CGFloat {
        let centeredX = dividerCenterX(for: percent, in: width) - markerWidth / 2
        return min(max(0, centeredX), max(0, width - markerWidth))
    }

    private func dividerCenterX(for percent: Double, in width: CGFloat) -> CGFloat {
        let firstDividerCenterX = HomeLayoutMetrics.dashboardMeterDividerLeadingOffset + HomeLayoutMetrics.dashboardMeterDividerWidth / 2
        let dividerIndex = ((width * percent - firstDividerCenterX) / HomeLayoutMetrics.dashboardMeterDividerStep).rounded()
        let centerX = firstDividerCenterX + dividerIndex * HomeLayoutMetrics.dashboardMeterDividerStep
        return min(max(0, centerX), width)
    }
}

private struct HomeStatusOverviewMeterMarker: View {
    enum TitlePosition {
        case top
        case bottom
    }

    let title: String
    let titlePosition: TitlePosition

    var body: some View {
        VStack(spacing: OBRitSpacing.s0_5) {
            if titlePosition == .top {
                label
            }
            Capsule()
                .fill(OBRitColors.common00)
                .frame(width: OBRitSpacing.s1, height: HomeLayoutMetrics.dashboardIndicatorHeight)
                .shadow(color: Color.black.opacity(0.24), radius: OBRitSpacing.s0_5, x: 0, y: 0)
            if titlePosition == .bottom {
                label
            }
        }
        .frame(maxWidth: .infinity)
    }

    private var label: some View {
        Text(title)
            .obritTextStyle(OBRitTypography.small, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
            .frame(height: OBRitTypography.small.lineHeight)
            .lineLimit(1)
    }
}

private struct HomeStatusOverviewMeterBars: View {
    var body: some View {
        GeometryReader { geometry in
            let width = geometry.size.width
            let dividerCount = dividerCount(for: width)

            ZStack(alignment: .leading) {
                Rectangle()
                    .fill(
                        LinearGradient(
                            colors: [
                                OBRitColors.backgroundWarningDefault,
                                OBRitColors.backgroundPositiveDefault
                            ],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )

                ForEach(0..<dividerCount, id: \.self) { index in
                    // 검은색 divider
                    RoundedRectangle(cornerRadius: 0)
                        .fill(OBRitColors.common100)
                        .frame(
                            width: HomeLayoutMetrics.dashboardMeterDividerWidth,
                            height: HomeLayoutMetrics.dashboardMeterBarHeight
                        )
                        .offset(x: dividerX(at: index))
                }
            }
            .frame(width: width, height: HomeLayoutMetrics.dashboardMeterBarHeight)
        }
    }

    private func dividerCount(for width: CGFloat) -> Int {
        let visibleWidth = width - HomeLayoutMetrics.dashboardMeterDividerLeadingOffset
        return max(0, Int(ceil(visibleWidth / HomeLayoutMetrics.dashboardMeterDividerStep)) + 1)
    }

    private func dividerX(at index: Int) -> CGFloat {
        HomeLayoutMetrics.dashboardMeterDividerLeadingOffset
            + CGFloat(index) * HomeLayoutMetrics.dashboardMeterDividerStep
    }
}

struct HomeWarningSection: View {
    let items: [HomeConsumableItem]
    let selectedFilter: HomeStatusFilter
    let filterCounts: [HomeStatusFilter: Int]
    let selectedSort: HomeWarningSort
    let onSelectFilter: (HomeStatusFilter) -> Void
    let onSelectSort: (HomeWarningSort) -> Void
    let onShowList: () -> Void
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
            HomeSegmentedFilter(
                selectedFilter: selectedFilter,
                filterCounts: filterCounts,
                onSelect: onSelectFilter
            )

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: OBRitSpacing.s2) {
                    ForEach(items) { item in
                        Button {
                            onSelect(item.id)
                        } label: {
                            OBRitCardGrid(
                                level: item.cardLevel,
                                title: item.title,
                                stockCount: item.stockCount,
                                daysLabel: item.dDayLabel
                            ) {
                                ConsumableDotImage(color: item.imageColor)
                                    .padding(OBRitSpacing.s3)
                            }
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, OBRitSpacing.s5)
            }

            HomeWarningList(
                items: items,
                selectedSort: selectedSort,
                onSelectSort: onSelectSort,
                onShowList: onShowList,
                onSelect: onSelect
            )
        }
        .padding(.top, OBRitSpacing.s5)
        .padding(.bottom, OBRitSpacing.s6)
    }
}

private struct HomeSegmentedFilter: View {
    let selectedFilter: HomeStatusFilter
    let filterCounts: [HomeStatusFilter: Int]
    let onSelect: (HomeStatusFilter) -> Void

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: OBRitSpacing.s2) {
                ForEach(HomeStatusFilter.allCases, id: \.self) { filter in
                    OBRitChip(
                        text: filter.title,
                        selected: selectedFilter == filter,
                        number: filterCounts[filter, default: 0]
                    ) {
                        onSelect(filter)
                    }
                }
            }
            .padding(.horizontal, OBRitSpacing.s5)
        }
    }
}

private struct HomeWarningList: View {
    @State private var sortMenuPresented = false

    let items: [HomeConsumableItem]
    let selectedSort: HomeWarningSort
    let onSelectSort: (HomeWarningSort) -> Void
    let onShowList: () -> Void
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s4) {
            sortHeader

            VStack(spacing: OBRitSpacing.s2) {
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
                            ConsumableDotImage(color: item.imageColor)
                                .padding(OBRitSpacing.s2)
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
        }
        .padding(.horizontal, OBRitSpacing.s5)
    }

    private var sortHeader: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
            HStack {
                OBRitFilledTextButton(
                    text: selectedSort.title,
                    size: .small,
                    color: .gray,
                    action: {
                        sortMenuPresented.toggle()
                    },
                    trailingIcon: { contentColor in
                        OBRitIcon(kind: .chevronDown, color: contentColor)
                    }
                )

                Text("미리보기")
                    .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                Spacer()
            }

            if sortMenuPresented {
                OBRitDropdownMenu(
                    items: HomeWarningSort.allCases.map(\.title),
                    selectedIndex: selectedSortIndex,
                    itemSize: .small
                ) { index in
                    onSelectSort(HomeWarningSort.allCases[index])
                    sortMenuPresented = false
                }
            }
        }
    }

    private var selectedSortIndex: Int? {
        HomeWarningSort.allCases.firstIndex(of: selectedSort)
    }
}

struct HomeUsageListSection: View {
    let items: [HomeConsumableItem]
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            Text("사용 현황")
                .obritTextStyle(OBRitTypography.small, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .padding(.horizontal, OBRitSpacing.s5)

            LazyVStack(spacing: 0) {
                ForEach(items) { item in
                    Button {
                        onSelect(item.id)
                    } label: {
                        HomeUsageRow(item: item)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, OBRitSpacing.s5)
        }
    }
}

private struct HomeUsageRow: View {
    let item: HomeConsumableItem

    var body: some View {
        HStack(spacing: OBRitSpacing.s4) {
            ConsumableDotImage(color: item.imageColor)
                .frame(width: OBRitSpacing.s8, height: OBRitSpacing.s8)

            Text(item.title)
                .lineLimit(1)
                .minimumScaleFactor(0.86)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .frame(maxWidth: .infinity, alignment: .leading)
                .layoutPriority(1)

            HStack(spacing: OBRitSpacing.s0_5) {
                Text("\(item.daysInUse)일")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                Text("째 사용중")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            }
            .lineLimit(1)
            .minimumScaleFactor(0.86)

            Image(systemName: "chevron.right")
                .font(.system(size: OBRitSpacing.s3, weight: .bold))
                .foregroundStyle(OBRitColors.iconDefaultSecondary)
        }
        .padding(.vertical, OBRitSpacing.s4)
        .contentShape(Rectangle())
    }
}

private struct ConsumableDotImage: View {
    let color: Color

    var body: some View {
        Circle()
            .fill(OBRitColors.backgroundDefaultSecondary)
    }
}
