import SwiftUI

private enum HomeStatusOverviewLayoutMetrics {
    static let cardHeight: CGFloat = 96 // 내 상태 현황 카드 높이
    static let cardPadding = OBRitSpacing.s6
    static let contentSpacing = OBRitSpacing.s6
    static let metricValueSpacing = OBRitSpacing.s4
    static var meterHeight: CGFloat {
        max(0, cardHeight - cardPadding * 2)
    }
    static let meterBarHeight: CGFloat = 18
    static let meterDividerWidth = OBRitSpacing.s1
    static let meterDividerStep = OBRitSpacing.s1 + OBRitSpacing.px
    static let meterDividerLeadingOffset = -(OBRitSpacing.s0_5 + OBRitSpacing.px / 2)
    static let indicatorHeight = OBRitSpacing.s6
    static var markerHeight: CGFloat {
        OBRitTypography.small.lineHeight + OBRitSpacing.s0_5 + indicatorHeight
    }
    static let averageMarkerTopOffset = OBRitSpacing.s5 + OBRitSpacing.s0_5
    static var markerOverlayHeight: CGFloat {
        markerHeight + averageMarkerTopOffset
    }
    static let ownMarkerWidth = OBRitSpacing.s11
    static let averageMarkerWidth = OBRitSpacing.s10
}

struct HomeStatusOverviewCard: View {
    let summary: HomeSummary

    var body: some View {
        HStack(spacing: HomeStatusOverviewLayoutMetrics.contentSpacing) {
            HomeStatusOverviewMetrics(summary: summary)
                .frame(height: HomeStatusOverviewLayoutMetrics.meterHeight, alignment: .center)
                .fixedSize(horizontal: true, vertical: false)

            HomeStatusOverviewMeter(summary: summary)
                .frame(maxWidth: .infinity)
                .frame(height: HomeStatusOverviewLayoutMetrics.meterHeight)
                .layoutPriority(1)
        }
        .padding(HomeStatusOverviewLayoutMetrics.cardPadding)
        .frame(maxWidth: .infinity, minHeight: HomeStatusOverviewLayoutMetrics.cardHeight)
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
        HStack(spacing: HomeStatusOverviewLayoutMetrics.metricValueSpacing) {
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
                    .frame(height: HomeStatusOverviewLayoutMetrics.meterBarHeight)

                ZStack(alignment: .topLeading) {
                    HomeStatusOverviewMeterMarker(title: "내 상태", titlePosition: .top)
                        .frame(width: HomeStatusOverviewLayoutMetrics.ownMarkerWidth, height: HomeStatusOverviewLayoutMetrics.markerHeight)
                        .offset(
                            x: markerOffsetX(
                                for: summary.ownStatusPercent,
                                markerWidth: HomeStatusOverviewLayoutMetrics.ownMarkerWidth,
                                in: width
                            )
                        )

                    HomeStatusOverviewMeterMarker(title: "평균", titlePosition: .bottom)
                        .frame(width: HomeStatusOverviewLayoutMetrics.averageMarkerWidth, height: HomeStatusOverviewLayoutMetrics.markerHeight)
                        .offset(
                            x: markerOffsetX(
                                for: summary.averageStatusPercent,
                                markerWidth: HomeStatusOverviewLayoutMetrics.averageMarkerWidth,
                                in: width
                            ),
                            y: HomeStatusOverviewLayoutMetrics.averageMarkerTopOffset
                        )
                }
                .frame(width: width, height: HomeStatusOverviewLayoutMetrics.markerOverlayHeight, alignment: .topLeading)
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
        let firstDividerCenterX = HomeStatusOverviewLayoutMetrics.meterDividerLeadingOffset + HomeStatusOverviewLayoutMetrics.meterDividerWidth / 2
        let dividerIndex = ((width * percent - firstDividerCenterX) / HomeStatusOverviewLayoutMetrics.meterDividerStep).rounded()
        let centerX = firstDividerCenterX + dividerIndex * HomeStatusOverviewLayoutMetrics.meterDividerStep
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
                .frame(width: OBRitSpacing.s1, height: HomeStatusOverviewLayoutMetrics.indicatorHeight)
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
                            width: HomeStatusOverviewLayoutMetrics.meterDividerWidth,
                            height: HomeStatusOverviewLayoutMetrics.meterBarHeight
                        )
                        .offset(x: dividerX(at: index))
                }
            }
            .frame(width: width, height: HomeStatusOverviewLayoutMetrics.meterBarHeight)
        }
    }

    private func dividerCount(for width: CGFloat) -> Int {
        let visibleWidth = width - HomeStatusOverviewLayoutMetrics.meterDividerLeadingOffset
        return max(0, Int(ceil(visibleWidth / HomeStatusOverviewLayoutMetrics.meterDividerStep)) + 1)
    }

    private func dividerX(at index: Int) -> CGFloat {
        HomeStatusOverviewLayoutMetrics.meterDividerLeadingOffset
            + CGFloat(index) * HomeStatusOverviewLayoutMetrics.meterDividerStep
    }
}
