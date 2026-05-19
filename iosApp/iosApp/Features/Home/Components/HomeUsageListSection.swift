import SwiftUI

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
            HomeConsumableImage(color: item.imageColor)
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
