import SwiftUI

private enum HomeInventoryLayoutMetrics {
    static let topPadding = OBRitSpacing.s3
    static let summarySpacing = OBRitSpacing.s7
    static let ratioLabelWidth = OBRitSpacing.s14
    static let ratioLabelOffsetY = -(OBRitSpacing.s16 + OBRitSpacing.s1 + OBRitSpacing.px)
}

struct HomeInventorySection: View {
    let summary: HomeSummary
    let items: [HomeItemItem]

    var body: some View {
        VStack(spacing: HomeInventoryLayoutMetrics.summarySpacing) {
            HomeOrbOverview(summary: summary, items: items)

            HomeStatusOverviewCard(summary: summary)
                .padding(.horizontal, OBRitSpacing.s5)
                .frame(maxWidth: .infinity, alignment: .center)
        }
        .padding(.top, HomeInventoryLayoutMetrics.topPadding)
        .padding(.bottom, OBRitSpacing.s4)
    }
}

private struct HomeOrbOverview: View {
    let summary: HomeSummary
    let items: [HomeItemItem]

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = OBRitSpacing.s5
            let contentWidth = max(0, geometry.size.width - horizontalPadding * 2)
            let reservedLabelWidth = HomeInventoryLayoutMetrics.ratioLabelWidth * 2
            let reservedSpacing = OBRitSpacing.s2 * 2
            let availableOrbWidth = contentWidth - reservedLabelWidth - reservedSpacing
            let orbScale = max(0, min(1, availableOrbWidth / HomeOrbMetrics.outerDiameter))
            let orbSize = HomeOrbMetrics.outerDiameter * orbScale
            let labelOffsetY = HomeInventoryLayoutMetrics.ratioLabelOffsetY * orbScale

            HStack(alignment: .center, spacing: OBRitSpacing.s2) {
                HomeRatioLabel(
                    value: summary.warningRatio,
                    label: "경고",
                    color: OBRitColors.textWarningDefault,
                    alignment: .trailing,
                    orbScale: orbScale
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
                    value: summary.positiveRatio,
                    label: "양호",
                    color: OBRitColors.textPositiveDefault,
                    alignment: .leading,
                    orbScale: orbScale
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

private struct HomeRatioLabel: View {
    let value: Int
    let label: String
    let color: Color
    let alignment: HorizontalAlignment
    let orbScale: CGFloat

    var body: some View {
        let backgroundSize = HomeRatioLabelMetrics.backgroundSize(orbScale: orbScale)
        let valueOffsetX = alignment == .trailing ? HomeRatioLabelMetrics.trailingValueOpticalCorrection : 0

        VStack(alignment: alignment, spacing: 0) {
            Text("\(value)%")
                .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.bold, color: color)
                .offset(x: valueOffsetX)
            Text(label)
                .obritTextStyle(OBRitTypography.xl, color: color)
        }
        .frame(width: HomeInventoryLayoutMetrics.ratioLabelWidth, alignment: alignment == .leading ? .leading : .trailing)
        .background {
            RoundedRectangle(cornerRadius: min(backgroundSize.width, backgroundSize.height) / 2)
                .fill(color.opacity(HomeRatioLabelMetrics.backgroundOpacity))
                .frame(
                    width: backgroundSize.width,
                    height: backgroundSize.height
                )
                .blur(radius: HomeRatioLabelMetrics.backgroundBlurRadius(orbScale: orbScale))
        }
    }
}

private enum HomeRatioLabelMetrics {
    static let trailingValueOpticalCorrection = OBRitSpacing.px
    static let backgroundWidthRatio: CGFloat = 291 / HomeOrbMetrics.outerDiameter
    static let backgroundHeightRatio: CGFloat = 292 / HomeOrbMetrics.outerDiameter
    static let backgroundBlurRadiusRatio: CGFloat = 48 / HomeOrbMetrics.outerDiameter
    static let backgroundOpacity: Double = 0.10

    static func backgroundSize(orbScale: CGFloat) -> CGSize {
        CGSize(
            width: HomeOrbMetrics.outerDiameter * backgroundWidthRatio * orbScale,
            height: HomeOrbMetrics.outerDiameter * backgroundHeightRatio * orbScale
        )
    }

    static func backgroundBlurRadius(orbScale: CGFloat) -> CGFloat {
        HomeOrbMetrics.outerDiameter * backgroundBlurRadiusRatio * orbScale
    }
}
