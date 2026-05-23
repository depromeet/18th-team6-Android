import SwiftUI

struct ItemDetailStockCard: View {
    let item: ItemDetailDisplayData

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
            Text("여분 수량")
                .obritTextStyle(OBRitTypography.s2xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)

            HStack(spacing: OBRitSpacing.s4) {
                ZStack {
                    Circle()
                        .fill(OBRitColors.gray750)
                    Image(item.imageAssetName)
                        .resizable()
                        .scaledToFit()
                        .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
                }
                .frame(width: 52, height: 52)

                Text(item.title)
                    .lineLimit(1)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)

                Spacer(minLength: OBRitSpacing.s3)

                Text("\(item.stockCount) 개")
                    .lineLimit(1)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: item.status.accentColor)
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
}
