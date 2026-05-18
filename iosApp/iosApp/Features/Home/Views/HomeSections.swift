import SwiftUI

private enum HomeLayoutMetrics {
    static let headerHeight: CGFloat = 139
    static let inventoryTopPadding = OBRitSpacing.s3
    static let inventorySummarySpacing = OBRitSpacing.s7
    static let summaryCardHeight: CGFloat = 94
    static let summaryCardPadding = OBRitSpacing.s6 - OBRitSpacing.px
    static let summaryMetricWidth: CGFloat = 74
    static let summaryGraphWidth: CGFloat = 219
    static let averageMarkerX = OBRitSpacing.s16 + OBRitSpacing.s1 + OBRitSpacing.px
    static let ratioLabelHorizontalOffset = OBRitSpacing.s1_5
    static let ratioLabelOffsetY = -(OBRitSpacing.s16 + OBRitSpacing.s1 + OBRitSpacing.px)
}

struct HomeStatusSection: View {
    let summary: HomeSummary

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            VStack(alignment: .leading, spacing: 0) {
                Text("오늘의 소모품 관리")
                    .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    .frame(height: OBRitTypography.s5xl.lineHeight, alignment: .center)
                HStack(spacing: 0) {
                    Text("상태는 ")
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    Text(summary.status)
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.textWarningDefault)
                    Text("예요")
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                }
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
        .frame(height: HomeLayoutMetrics.headerHeight, alignment: .topLeading)
    }
}

private struct HomeStatusPair: View {
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: OBRitSpacing.s1) {
            Text(label)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            Text(value)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
        }
    }
}

struct HomeInventorySection: View {
    let summary: HomeSummary
    let items: [HomeConsumableItem]

    var body: some View {
        VStack(spacing: HomeLayoutMetrics.inventorySummarySpacing) {
            // GlassBall 섹션
            HStack(alignment: .center, spacing: OBRitSpacing.s2) {
                HomeRatioLabel(value: summary.positiveRatio, label: "양호", color: OBRitColors.textPositiveDefault, alignment: .trailing)
                    .padding(.top, OBRitSpacing.s8)
                    .offset(x: HomeLayoutMetrics.ratioLabelHorizontalOffset, y: HomeLayoutMetrics.ratioLabelOffsetY)

                HomeGlassBall(
                    normalRatio: Double(summary.positiveRatio) / 100,
                    warningRatio: Double(summary.warningRatio) / 100,
                    interiorItems: items.map(\.orbInteriorItem)
                )
                .frame(width: HomeOrbMetrics.outerDiameter, height: HomeOrbMetrics.outerDiameter)

                HomeRatioLabel(value: summary.warningRatio, label: "경고", color: OBRitColors.textWarningDefault, alignment: .leading)
                    .padding(.top, OBRitSpacing.s8)
                    .offset(x: -HomeLayoutMetrics.ratioLabelHorizontalOffset, y: HomeLayoutMetrics.ratioLabelOffsetY)
            }
            .frame(maxWidth: .infinity)

            HomeSummaryCard(summary: summary)
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.top, HomeLayoutMetrics.inventoryTopPadding)
        .padding(.bottom, OBRitSpacing.s4)
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
        .frame(width: OBRitSpacing.s14, alignment: alignment == .leading ? .leading : .trailing)
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

private struct HomeSummaryCard: View {
    let summary: HomeSummary

    var body: some View {
        HStack(spacing: HomeLayoutMetrics.summaryCardPadding) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                HomeMetricRow(title: "내 소모품", value: "\(summary.totalCount)", rowColor: OBRitColors.common00)
                HomeMetricRow(title: "교체 위험", value: "\(summary.warningCount)", rowColor: OBRitColors.textWarningDefault)
            }
            .frame(width: HomeLayoutMetrics.summaryMetricWidth, alignment: .leading)

            HomeStatusMeterGraph()
            .frame(width: HomeLayoutMetrics.summaryGraphWidth, height: OBRitSpacing.s12)
        }
        .padding(.horizontal, HomeLayoutMetrics.summaryCardPadding)
        .padding(.vertical, HomeLayoutMetrics.summaryCardPadding)
        .frame(maxWidth: .infinity, minHeight: HomeLayoutMetrics.summaryCardHeight, maxHeight: HomeLayoutMetrics.summaryCardHeight)
        .background(Color(red: 16 / 255, green: 18 / 255, blue: 19 / 255))
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
    }
}

private struct HomeMetricRow: View {
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
        .frame(width: HomeLayoutMetrics.summaryMetricWidth, alignment: .leading)
    }
}

private struct HomeStatusMeterGraph: View {
    var body: some View {
        ZStack(alignment: .topLeading) {
            HomeSparkBars()
                .frame(width: HomeLayoutMetrics.summaryGraphWidth, height: OBRitSpacing.s4 + OBRitSpacing.s0_5)
                .offset(y: OBRitSpacing.s3 + OBRitSpacing.s0_5)

            HomeMeterMarker(title: "내 상태", titlePosition: .top)
                .frame(width: OBRitSpacing.s11, height: OBRitSpacing.s11 + OBRitSpacing.s1_5)
                .offset(x: OBRitSpacing.s4 + OBRitSpacing.px, y: -OBRitSpacing.s2_5)

            HomeMeterMarker(title: "평균", titlePosition: .bottom)
                .frame(width: OBRitSpacing.s7, height: OBRitSpacing.s11 + OBRitSpacing.s1_5)
                .offset(x: HomeLayoutMetrics.averageMarkerX, y: OBRitSpacing.s3)
        }
        .frame(width: HomeLayoutMetrics.summaryGraphWidth, height: OBRitSpacing.s12)
    }
}

private struct HomeMeterMarker: View {
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
                .frame(width: OBRitSpacing.s1, height: OBRitSpacing.s6)
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

private struct HomeSparkBars: View {
    private let tickCount = 44

    var body: some View {
        HStack(alignment: .center, spacing: OBRitSpacing.px) {
            ForEach(0..<tickCount, id: \.self) { index in
                Capsule()
                    .fill(barColor(at: index))
                    .frame(width: OBRitSpacing.s1, height: OBRitSpacing.s4 + OBRitSpacing.s0_5)
            }
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }

    private func barColor(at index: Int) -> Color {
        let progress = Double(index) / Double(tickCount - 1)
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
                HStack(spacing: OBRitSpacing.s3) {
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

private struct HomeWarningList: View {
    let items: [HomeConsumableItem]
    let selectedSort: HomeWarningSort
    let onSelectSort: (HomeWarningSort) -> Void
    let onShowList: () -> Void
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s3) {
            HStack {
                Menu {
                    ForEach(HomeWarningSort.allCases, id: \.self) { sort in
                        Button(sort.title) {
                            onSelectSort(sort)
                        }
                    }
                } label: {
                    HStack(spacing: OBRitSpacing.s1) {
                        Text(selectedSort.title)
                        Image(systemName: "chevron.down")
                            .font(.system(size: OBRitSpacing.s2 + OBRitSpacing.s0_5, weight: .bold))
                    }
                    .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    .padding(.horizontal, OBRitSpacing.s3)
                    .padding(.vertical, OBRitSpacing.s2)
                    .background(OBRitColors.backgroundDefaultSecondary)
                    .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
                }

                Text("미리보기")
                    .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                Spacer()
            }

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
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: OBRitSpacing.s0_5) {
                Text("\(item.daysInUse)일")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                Text("째 사용중")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            }

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
