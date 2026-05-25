import SwiftUI

struct ItemDetailStatusSummaryCard: View {
    let item: ItemDetailDisplayData

    var body: some View {
        VStack(alignment: .leading, spacing: 22) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                Text("교체 주기")
                    .obritTextStyle(OBRitTypography.s2xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)
                Text("전체적인 상태를 빠르게 확인해보세요!")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            }

            VStack(spacing: OBRitSpacing.s4) {
                summaryRow(title: "나의 평균 교체 주기", value: item.averageReplacementDaysText)
                divider
                summaryRow(title: "권장 교체 주기", value: item.recommendedReplacementDaysText)
                divider
                summaryRow(
                    title: "현재 사용 상태",
                    value: item.currentUsageDaysText,
                    badgeText: item.currentStatusBadgeText
                )
                divider
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

    private func summaryRow(title: String, value: String, badgeText: String? = nil) -> some View {
        HStack(spacing: OBRitSpacing.s3) {
            Text(title)
                .lineLimit(1)
                .minimumScaleFactor(0.82)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultTertiary)

            Spacer(minLength: OBRitSpacing.s3)

            HStack(spacing: OBRitSpacing.s1_5) {
                if let badgeText {
                    ItemDetailStatusBadge(text: badgeText, itemStatus: item.status)
                }

                Text(value)
                    .lineLimit(1)
                    .minimumScaleFactor(0.82)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)
            }
        }
    }

    private var divider: some View {
        Rectangle()
            .fill(OBRitColors.gray800)
            .frame(height: 1)
            .padding(.horizontal, -OBRitSpacing.s4)
    }
}
