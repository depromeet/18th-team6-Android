import SwiftUI

struct ItemDetailReplacementHistoryChart: View {
    let item: ItemDetailDisplayData

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                Text("교체 주기 기록")
                    .obritTextStyle(OBRitTypography.s2xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)

                HStack(spacing: 0) {
                    Text("교체 기록은 ")
                        .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                    Text("최근 5회")
                        .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.green450)
                    Text("까지 제공해요")
                        .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                }
                .lineLimit(1)
                .minimumScaleFactor(0.8)
            }

            VStack(spacing: OBRitSpacing.s4) {
                GeometryReader { proxy in
                    chartBars(width: proxy.size.width)
                }
                .frame(height: ItemDetailHistoryChartMetrics.height)

                Rectangle()
                    .fill(OBRitColors.gray800)
                    .frame(height: 1)

                HStack(spacing: OBRitSpacing.s1) {
                    Text("평균 교체 주기")
                        .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultTertiary)
                    Text(item.replacementHistoryAverageText)
                        .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)
                }
                .frame(maxWidth: .infinity)
            }
        }
        .padding(.top, ItemDetailLayout.cardTopPadding)
        .padding(.horizontal, ItemDetailLayout.cardPadding)
        .padding(.bottom, ItemDetailLayout.cardBottomPadding)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(OBRitColors.backgroundDefaultSecondary)
        .clipShape(RoundedRectangle(cornerRadius: ItemDetailLayout.cardCornerRadius))
        .overlay(
            RoundedRectangle(cornerRadius: ItemDetailLayout.cardCornerRadius)
                .stroke(OBRitColors.gray800, lineWidth: ItemDetailLayout.cardBorderWidth)
        )
    }

    private func chartBars(width: CGFloat) -> some View {
        let entries = Array(item.replacementHistory.prefix(5))
        let count = max(entries.count, 1)
        let spacing = count > 1
            ? max(0, (width - ItemDetailHistoryChartMetrics.barWidth * CGFloat(count)) / CGFloat(count - 1))
            : 0

        return HStack(alignment: .bottom, spacing: spacing) {
            ForEach(entries) { entry in
                ItemDetailHistoryBar(entry: entry, itemStatus: item.status)
                    .frame(width: ItemDetailHistoryChartMetrics.barWidth)
            }
        }
        .frame(width: width, height: ItemDetailHistoryChartMetrics.height, alignment: .bottom)
    }
}

private struct ItemDetailHistoryBar: View {
    let entry: ItemDetailReplacementHistoryEntry
    let itemStatus: ItemDetailStatus

    var body: some View {
        VStack(spacing: OBRitSpacing.s1) {
            Text(entry.daysText)
                .lineLimit(1)
                .minimumScaleFactor(0.75)
                .obritTextStyle(
                    OBRitTypography.xs,
                    weight: OBRitFontWeight.semiBold,
                    color: entry.isCurrent ? itemStatus.chartCurrentColor : OBRitColors.textDefaultTertiary
                )

            ZStack(alignment: .bottom) {
                RoundedRectangle(cornerRadius: OBRitRadius.small)
                    .fill(entry.isCurrent ? itemStatus.chartCurrentColor : itemStatus.chartTrackColor)

                if !entry.isCurrent {
                    RoundedRectangle(cornerRadius: OBRitRadius.small)
                        .fill(itemStatus.chartBarColor)
                        .frame(height: 86 * entry.ratio)
                        .clipShape(ItemDetailBottomRoundedRectangle(radius: OBRitRadius.small))
                }
            }
            .frame(height: 86)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))

            Text(entry.dateText)
                .lineLimit(1)
                .minimumScaleFactor(0.75)
                .obritTextStyle(
                    OBRitTypography.xs,
                    weight: OBRitFontWeight.semiBold,
                    color: entry.isCurrent ? itemStatus.chartCurrentColor : OBRitColors.textDefaultTertiary
                )
        }
    }
}

private struct ItemDetailBottomRoundedRectangle: Shape {
    let radius: CGFloat

    func path(in rect: CGRect) -> Path {
        let cornerRadius = min(radius, rect.width / 2, rect.height / 2)
        var path = Path()
        path.move(to: CGPoint(x: rect.minX, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - cornerRadius))
        path.addQuadCurve(
            to: CGPoint(x: rect.maxX - cornerRadius, y: rect.maxY),
            control: CGPoint(x: rect.maxX, y: rect.maxY)
        )
        path.addLine(to: CGPoint(x: rect.minX + cornerRadius, y: rect.maxY))
        path.addQuadCurve(
            to: CGPoint(x: rect.minX, y: rect.maxY - cornerRadius),
            control: CGPoint(x: rect.minX, y: rect.maxY)
        )
        path.closeSubpath()
        return path
    }
}

private enum ItemDetailHistoryChartMetrics {
    static let height: CGFloat = 130
    static let barWidth: CGFloat = 58
}
