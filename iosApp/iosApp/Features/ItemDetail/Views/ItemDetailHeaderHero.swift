import SwiftUI

struct ItemDetailHeaderHero: View {
    let item: ItemDetailDisplayData
    let availableWidth: CGFloat

    var body: some View {
        let diameter = min(
            availableWidth * ItemDetailLayout.heroDiameterRatio,
            ItemDetailLayout.heroMaxDiameter
        )
        let ringWidth = max(10, diameter * ItemDetailLayout.heroRingWidthRatio)
        let progressColor = item.status.progressAccentColor(for: item.heroProgress)

        ZStack {
            Circle()
                .fill(item.status.heroFillColor)
                .frame(width: diameter * 0.90, height: diameter * 0.90)

            Circle()
                .stroke(item.status.heroTrackColor, lineWidth: ringWidth)
                .frame(width: diameter - ringWidth, height: diameter - ringWidth)

            Circle()
                .trim(from: 0, to: item.heroProgress)
                .stroke(
                    progressColor,
                    style: StrokeStyle(lineWidth: ringWidth, lineCap: .round)
                )
                .rotationEffect(.degrees(-90))
                .frame(width: diameter - ringWidth, height: diameter - ringWidth)

            Image(item.imageAssetName)
                .resizable()
                .scaledToFit()
                .padding(ItemDetailLayout.heroImagePadding)
                .frame(width: diameter, height: diameter)
                .blendMode(.colorDodge)
                .shadow(color: progressColor.opacity(0.28), radius: 12, x: 0, y: 4)
        }
        .frame(maxWidth: .infinity)
        .frame(height: diameter + ItemDetailLayout.heroVerticalPadding * 2)
    }
}
