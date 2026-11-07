import SwiftUI

enum ItemDetailReplacementCompletionModalKind: Equatable {
    case nextReplacement
    case lowStock
}

struct ItemDetailReplacementCompletionModal: View {
    let kind: ItemDetailReplacementCompletionModalKind
    let itemName: String
    let itemImageURL: String
    let messageLines: [String]
    let summaryTitle: String
    let summaryValue: String
    let recordedAtText: String
    let onConfirm: () -> Void
    let onCancel: () -> Void

    init(
        kind: ItemDetailReplacementCompletionModalKind,
        itemName: String,
        itemImageURL: String = "",
        messageLines: [String],
        summaryTitle: String,
        summaryValue: String,
        recordedAtText: String,
        onConfirm: @escaping () -> Void,
        onCancel: @escaping () -> Void
    ) {
        self.kind = kind
        self.itemName = itemName
        self.itemImageURL = itemImageURL
        self.messageLines = messageLines
        self.summaryTitle = summaryTitle
        self.summaryValue = summaryValue
        self.recordedAtText = recordedAtText
        self.onConfirm = onConfirm
        self.onCancel = onCancel
    }

    var body: some View {
        VStack(spacing: ItemDetailReplacementModalMetrics.sectionSpacing) {
            VStack(spacing: ItemDetailReplacementModalMetrics.bodySpacing) {
                VStack(spacing: OBRitSpacing.s6) {
                    itemBadge
                    titleArea
                }

                VStack(spacing: OBRitSpacing.s3) {
                    summaryRow

                    Text(recordedAtText)
                        .lineLimit(1)
                        .minimumScaleFactor(0.8)
                        .frame(maxWidth: .infinity)
                        .obritTextStyle(
                            OBRitTypography.s2xs,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.textDefaultDarkTertiary
                        )
                }
            }

            VStack(spacing: OBRitSpacing.s2) {
                OBRitFilledTextButton(
                    text: "확인",
                    size: .middle,
                    color: .green,
                    fillsWidth: true,
                    action: onConfirm
                )

                Button(action: onCancel) {
                    Text("취소하기")
                        .underline()
                        .frame(maxWidth: .infinity)
                        .obritTextStyle(
                            OBRitTypography.s2xs,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.textDefaultTertiary
                        )
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.top, OBRitSpacing.s6)
        .padding(.bottom, OBRitSpacing.s4)
        .frame(width: ItemDetailReplacementModalMetrics.width)
        .background(OBRitColors.backgroundDefaultDefaultHover)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
    }

    private var itemBadge: some View {
        ZStack {
            Circle()
                .fill(OBRitColors.backgroundPositiveDefault)
                .shadow(
                    color: OBRitColors.green700.opacity(ItemDetailReplacementModalMetrics.glowOpacity),
                    radius: ItemDetailReplacementModalMetrics.glowRadius
                )

            OBRitRemoteImage(urlString: itemImageURL, contentMode: .fill)
                .frame(
                    width: ItemDetailReplacementModalMetrics.imageSize,
                    height: ItemDetailReplacementModalMetrics.imageSize
                )
                .clipShape(Circle())
        }
        .frame(
            width: ItemDetailReplacementModalMetrics.badgeSize,
            height: ItemDetailReplacementModalMetrics.badgeSize
        )
    }

    private var titleArea: some View {
        VStack(spacing: OBRitSpacing.s3) {
            Text("\(itemName) 교체 완료!")
                .lineLimit(1)
                .minimumScaleFactor(0.8)
                .obritTextStyle(
                    OBRitTypography.s5xl,
                    weight: OBRitFontWeight.bold,
                    color: OBRitColors.textDefaultDefault
                )

            VStack(spacing: OBRitSpacing.s0) {
                ForEach(Array(messageLines.prefix(2).enumerated()), id: \.offset) { _, line in
                    Text(line)
                        .lineLimit(1)
                        .minimumScaleFactor(0.8)
                }
            }
            .multilineTextAlignment(.center)
            .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultTertiary)
        }
        .frame(maxWidth: .infinity)
    }

    private var summaryRow: some View {
        HStack {
            HStack(spacing: OBRitSpacing.s1_5) {
                Image(kind.summaryIconAssetName)
                    .resizable()
                    .scaledToFit()
                    .frame(
                        width: ItemDetailReplacementModalMetrics.summaryIconFrame,
                        height: ItemDetailReplacementModalMetrics.summaryIconFrame
                    )

                Text(summaryTitle)
                    .lineLimit(1)
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.textDefaultTertiary
                    )
            }

            Spacer(minLength: OBRitSpacing.s2)

            Text(summaryValue)
                .lineLimit(1)
                .minimumScaleFactor(0.75)
                .obritTextStyle(
                    OBRitTypography.base,
                    weight: OBRitFontWeight.bold,
                    color: kind.summaryValueColor
                )
        }
        .frame(height: ItemDetailReplacementModalMetrics.summaryHeight)
        .padding(.horizontal, OBRitSpacing.s4)
        .background(OBRitColors.backgroundDefaultSecondary)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
    }
}

extension ItemDetailReplacementCompletionModal {
    init(
        itemName: String,
        itemImageURL: String = "",
        daysComparedToPrevious: Int,
        nextReplacementLabel: String,
        recordedAtText: String,
        onConfirm: @escaping () -> Void,
        onCancel: @escaping () -> Void
    ) {
        let comparisonText: String
        if daysComparedToPrevious < 0 {
            comparisonText = "지난번보다 \(abs(daysComparedToPrevious))일 빠르게 교체했어요."
        } else if daysComparedToPrevious > 0 {
            comparisonText = "지난번보다 \(daysComparedToPrevious)일 늦게 교체했어요."
        } else {
            comparisonText = "지난번과 같은 주기로 교체했어요."
        }

        self.init(
            kind: .nextReplacement,
            itemName: itemName,
            itemImageURL: itemImageURL,
            messageLines: [comparisonText, "교체 시기를 잘 지키고 있어요!"],
            summaryTitle: "다음 교체 예상일",
            summaryValue: nextReplacementLabel,
            recordedAtText: recordedAtText,
            onConfirm: onConfirm,
            onCancel: onCancel
        )
    }

    init(
        itemName: String,
        itemImageURL: String = "",
        remainingSpareQuantity: Int,
        recordedAtText: String,
        onConfirm: @escaping () -> Void,
        onCancel: @escaping () -> Void
    ) {
        self.init(
            kind: .lowStock,
            itemName: itemName,
            itemImageURL: itemImageURL,
            messageLines: ["\(itemName) 여분이 얼마 남지 않았어요!", "여분을 확인해주세요"],
            summaryTitle: "남은 여분 갯수",
            summaryValue: "\(remainingSpareQuantity) 개",
            recordedAtText: recordedAtText,
            onConfirm: onConfirm,
            onCancel: onCancel
        )
    }
}

private extension ItemDetailReplacementCompletionModalKind {
    var summaryIconAssetName: String {
        switch self {
        case .nextReplacement:
            return "item_detail_icon_calendar"
        case .lowStock:
            return "item_detail_icon_box"
        }
    }

    var summaryValueColor: Color {
        switch self {
        case .nextReplacement:
            return OBRitColors.textPositiveDefault
        case .lowStock:
            return OBRitColors.textWarningDefault
        }
    }
}

private enum ItemDetailReplacementModalMetrics {
    static let width: CGFloat = 333
    static let sectionSpacing: CGFloat = 20
    static let bodySpacing: CGFloat = 20
    static let badgeSize: CGFloat = 100
    static let imageSize: CGFloat = 66
    static let glowRadius: CGFloat = 25
    static let glowOpacity: CGFloat = 0.9
    static let summaryHeight: CGFloat = 48
    static let summaryIconFrame: CGFloat = 24
}

#Preview("Next Replacement") {
    ZStack {
        OBRitColors.gray900
        ItemDetailReplacementCompletionModal(
            itemName: "칫솔",
            daysComparedToPrevious: -2,
            nextReplacementLabel: "6월 22일(30일 후)",
            recordedAtText: "2026. 05. 23 오전 09:30 기록됨",
            onConfirm: {},
            onCancel: {}
        )
    }
}

#Preview("Low Stock") {
    ZStack {
        OBRitColors.gray900
        ItemDetailReplacementCompletionModal(
            itemName: "칫솔",
            remainingSpareQuantity: 1,
            recordedAtText: "2026. 05. 23 오전 09:30 기록됨",
            onConfirm: {},
            onCancel: {}
        )
    }
}
