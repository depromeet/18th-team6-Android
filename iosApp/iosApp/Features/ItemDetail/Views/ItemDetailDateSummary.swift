import SwiftUI

struct ItemDetailDateSummary: View {
    let item: ItemDetailDisplayData

    var body: some View {
        ZStack {
            Rectangle()
                .fill(OBRitColors.gray700)
                .frame(width: 1, height: 61)

            HStack(spacing: 0) {
                summaryColumn(
                    title: "최근 교체일",
                    value: item.lastReplacementDateText,
                    titleColor: OBRitColors.textDefaultDarkTertiary,
                    valueColor: OBRitColors.textDefaultDefault
                )

                summaryColumn(
                    title: "다음 교체 예정일",
                    value: item.nextReplacementDateText,
                    titleColor: item.status.subduedAccentColor,
                    valueColor: item.status.accentColor,
                    badgeText: item.replacementDayBadgeText
                )
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, ItemDetailLayout.dateSummaryVerticalPadding)
        .background(OBRitColors.backgroundDefaultSecondary)
        .clipShape(RoundedRectangle(cornerRadius: ItemDetailLayout.cardCornerRadius))
    }

    private func summaryColumn(
        title: String,
        value: String,
        titleColor: Color,
        valueColor: Color,
        badgeText: String? = nil
    ) -> some View {
        VStack(spacing: 0) {
            Text(title)
                .lineLimit(1)
                .minimumScaleFactor(0.85)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: titleColor)

            HStack(spacing: OBRitSpacing.s2) {
                Text(value)
                    .lineLimit(1)
                    .minimumScaleFactor(0.78)
                    .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.bold, color: valueColor)

                if let badgeText {
                    ItemDetailStatusBadge(text: badgeText, itemStatus: item.status)
                }
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, OBRitSpacing.s3)
    }
}

struct ItemDetailStatusBadge: View {
    let text: String
    let itemStatus: ItemDetailStatus

    var body: some View {
        Text(text)
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: itemStatus.accentColor)
            .padding(.horizontal, OBRitSpacing.s2)
            .padding(.vertical, OBRitSpacing.s1)
            .background(itemStatus.badgeBackgroundColor)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
    }
}
