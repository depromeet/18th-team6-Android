import SwiftUI

private enum HomeUsageListLayoutMetrics {
    static let sectionPadding = OBRitSpacing.s5
    static let contentSpacing = OBRitSpacing.s2
    static let rowHeight = OBRitSpacing.s16
    static let rowContentSpacing = OBRitSpacing.s4
    static let thumbnailSize = OBRitSpacing.s8
    static let trailingGroupSpacing = OBRitSpacing.s4
    static let usageTextSpacing = OBRitSpacing.s0_5
    static let chevronSize = OBRitSpacing.s4
}

struct HomeUsageListSection: View {
    let items: [HomeItemItem]
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: HomeUsageListLayoutMetrics.contentSpacing) {
            Text("사용 현황")
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .frame(maxWidth: .infinity, alignment: .leading)

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
        }
        .padding(HomeUsageListLayoutMetrics.sectionPadding)
    }
}

private struct HomeUsageRow: View {
    let item: HomeItemItem

    var body: some View {
        HStack(spacing: HomeUsageListLayoutMetrics.rowContentSpacing) {
            HStack(spacing: HomeUsageListLayoutMetrics.rowContentSpacing) {
                ZStack {
                    Circle()
                        .fill(OBRitColors.backgroundDefaultSecondary)
                    Image(item.imageAssetName)
                        .resizable()
                        .scaledToFit()
                }
                .frame(
                    width: HomeUsageListLayoutMetrics.thumbnailSize,
                    height: HomeUsageListLayoutMetrics.thumbnailSize
                )
                .clipShape(Circle())

                Text(item.title)
                    .lineLimit(1)
                    .minimumScaleFactor(0.86)
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .layoutPriority(1)
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: HomeUsageListLayoutMetrics.trailingGroupSpacing) {
                HStack(spacing: HomeUsageListLayoutMetrics.usageTextSpacing) {
                    Text("\(item.daysInUse)일")
                        .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    Text("째 사용중")
                        .obritTextStyle(
                            OBRitTypography.base,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.common00.opacity(0.64)
                        )
                }
                .lineLimit(1)
                .minimumScaleFactor(0.86)

                Image(systemName: "chevron.right")
                    .font(.system(size: HomeUsageListLayoutMetrics.chevronSize, weight: .medium))
                    .foregroundStyle(OBRitColors.iconDefaultSecondary)
                    .frame(
                        width: HomeUsageListLayoutMetrics.chevronSize,
                        height: HomeUsageListLayoutMetrics.chevronSize
                    )
            }
            .fixedSize(horizontal: true, vertical: false)
        }
        .frame(height: HomeUsageListLayoutMetrics.rowHeight)
        .contentShape(Rectangle())
    }
}
