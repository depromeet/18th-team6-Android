import SwiftUI

private enum HomeLayoutMetrics {
    static let inventoryTopPadding = OBRitSpacing.s3
    static let inventorySummarySpacing = OBRitSpacing.s7
    static let dashboardCardHeight: CGFloat = 96
    static let dashboardCardPadding = OBRitSpacing.s6
    static let dashboardContentSpacing = OBRitSpacing.s6
    static let dashboardMetricsWidth: CGFloat = 76
    static let dashboardMeterWidth: CGFloat = 224
    static let dashboardMeterHeight: CGFloat = 48
    static let dashboardMeterBarTop: CGFloat = 15
    static let dashboardMeterBarHeight: CGFloat = 18
    static let dashboardMeterTickWidth = OBRitSpacing.s0_5
    static let dashboardMeterTickSpacing = OBRitSpacing.s0_5
    static let dashboardIndicatorHeight = OBRitSpacing.s6
    static let dashboardMarkerOverlayHeight: CGFloat = 68
    static let ratioLabelWidth = OBRitSpacing.s14
    static let ratioLabelHorizontalOffset: CGFloat = 0
    static let ratioLabelOffsetY = -(OBRitSpacing.s16 + OBRitSpacing.s1 + OBRitSpacing.px)
    static let dashboardOwnMarkerWidth = OBRitSpacing.s11
    static let dashboardAverageMarkerWidth = OBRitSpacing.s7
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
        summary.status == "양호" ? OBRitColors.textPositiveDefault : OBRitColors.textWarningDefault
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
                .offset(
                    x: HomeLayoutMetrics.ratioLabelHorizontalOffset,
                    y: labelOffsetY
                )

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
                .offset(
                    x: -HomeLayoutMetrics.ratioLabelHorizontalOffset,
                    y: labelOffsetY
                )
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

    var orbAssetName: String {
        if title.contains("디퓨저") || title.contains("샴푸") || title.contains("바디워시") {
            return "home_orb_diffuser"
        }

        if title.contains("필터") {
            return "home_orb_shower_filter"
        }

        if title.contains("칫솔") {
            return "home_orb_toothbrush"
        }

        if title.contains("수세미") || title.contains("지퍼백") || title.contains("쓰레기") {
            return "home_orb_sponge"
        }

        if title.contains("수건") || title.contains("키친타월") {
            return "home_orb_towel"
        }

        if title.contains("세제") {
            return "home_orb_detergent"
        }

        if title.contains("면도기") {
            return "home_orb_razor"
        }

        return fallbackOrbAssetName
    }

    var fallbackOrbAssetName: String {
        let assetNames = [
            "home_orb_detergent",
            "home_orb_sponge",
            "home_orb_toothbrush",
            "home_orb_diffuser",
            "home_orb_shower_filter",
            "home_orb_razor",
            "home_orb_towel"
        ]

        return assetNames[id % assetNames.count]
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

            HomeStatusOverviewMeter(summary: summary)
                .frame(maxWidth: .infinity, minHeight: HomeLayoutMetrics.dashboardMeterHeight)
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
        .frame(width: HomeLayoutMetrics.dashboardMetricsWidth, alignment: .leading)
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
        HStack(spacing: 0) {
            Text(title)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: rowColor)
                .lineLimit(1)
            Spacer(minLength: 0)
            Text(value)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: rowColor)
                .lineLimit(1)
        }
        .frame(height: OBRitTypography.base.lineHeight)
        .frame(width: HomeLayoutMetrics.dashboardMetricsWidth, alignment: .leading)
    }
}

private struct HomeStatusOverviewMeter: View {
    let summary: HomeSummary

    var body: some View {
        GeometryReader { geometry in
            let width = min(geometry.size.width, HomeLayoutMetrics.dashboardMeterWidth)

            ZStack(alignment: .topLeading) {
                HomeStatusOverviewMeterBars()
                    .frame(width: width)
                    .frame(height: HomeLayoutMetrics.dashboardMeterBarHeight)
                    .offset(y: HomeLayoutMetrics.dashboardMeterBarTop)

                ZStack(alignment: .topLeading) {
                    HomeStatusOverviewMeterMarker(title: "내 상태", titlePosition: .top)
                        .frame(width: HomeLayoutMetrics.dashboardOwnMarkerWidth, height: OBRitSpacing.s11 + OBRitSpacing.s1_5)
                        .offset(x: markerX(for: summary.ownStatusPercent, markerWidth: HomeLayoutMetrics.dashboardOwnMarkerWidth, in: width))

                    HomeStatusOverviewMeterMarker(title: "평균", titlePosition: .bottom)
                        .frame(width: HomeLayoutMetrics.dashboardAverageMarkerWidth, height: OBRitSpacing.s11 + OBRitSpacing.s1_5)
                        .offset(
                            x: markerX(for: summary.averageStatusPercent, markerWidth: HomeLayoutMetrics.dashboardAverageMarkerWidth, in: width),
                            y: OBRitSpacing.s5 + OBRitSpacing.s0_5
                        )
                }
                .frame(width: width, height: HomeLayoutMetrics.dashboardMarkerOverlayHeight, alignment: .topLeading)
                .offset(y: markerOverlayTopOffset)
            }
            .frame(width: width, height: geometry.size.height)
            .frame(width: geometry.size.width, height: geometry.size.height, alignment: .center)
        }
    }

    private var markerOverlayTopOffset: CGFloat {
        let barCenterY = HomeLayoutMetrics.dashboardMeterBarTop + HomeLayoutMetrics.dashboardMeterBarHeight / 2
        let topMarkerIndicatorCenterY = OBRitTypography.small.lineHeight + OBRitSpacing.s0_5 + HomeLayoutMetrics.dashboardIndicatorHeight / 2
        return barCenterY - topMarkerIndicatorCenterY
    }

    private func markerX(for percent: Double, markerWidth: CGFloat, in width: CGFloat) -> CGFloat {
        let centeredX = width * percent - markerWidth / 2
        return min(max(0, centeredX), max(0, width - markerWidth))
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
            let tickCount = tickCount(for: geometry.size.width)

            HStack(alignment: .center, spacing: HomeLayoutMetrics.dashboardMeterTickSpacing) {
                ForEach(0..<tickCount, id: \.self) { index in
                    Capsule()
                        .fill(barColor(at: index, tickCount: tickCount))
                        .frame(
                            width: HomeLayoutMetrics.dashboardMeterTickWidth,
                            height: HomeLayoutMetrics.dashboardMeterBarHeight
                        )
                }
            }
            .frame(maxWidth: .infinity, alignment: .center)
        }
    }

    private func tickCount(for width: CGFloat) -> Int {
        let step = HomeLayoutMetrics.dashboardMeterTickWidth + HomeLayoutMetrics.dashboardMeterTickSpacing
        return max(1, Int((width + HomeLayoutMetrics.dashboardMeterTickSpacing) / step))
    }

    private func barColor(at index: Int, tickCount: Int) -> Color {
        let progress = tickCount == 1 ? 1 : Double(index) / Double(tickCount - 1)
        return Color(
            red: 1.0 * (1 - progress) + (38 / 255) * progress,
            green: (89 / 255) * (1 - progress) + (239 / 255) * progress,
            blue: (34 / 255) * (1 - progress) + (205 / 255) * progress
        )
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
