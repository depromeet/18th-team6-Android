import SwiftUI

private let obritCardListImageBackgroundColor = Color(red: 48.0 / 255.0, green: 51.0 / 255.0, blue: 62.0 / 255.0)

public enum OBRitCardLevel {
    case l1
    case l2
    case l3
    case l4
    case l5
    case l6
}

public struct OBRitCardGrid<ImageContent: View>: View {
    private let level: OBRitCardLevel
    private let title: String
    private let stockCount: Int
    private let daysLabel: String
    private let image: () -> ImageContent

    public init(
        level: OBRitCardLevel,
        title: String,
        stockCount: Int,
        daysLabel: String,
        @ViewBuilder image: @escaping () -> ImageContent
    ) {
        self.level = level
        self.title = title
        self.stockCount = stockCount
        self.daysLabel = daysLabel
        self.image = image
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2_5) {
            CardImageBox {
                image()
            }
            .frame(width: OBRitSpacing.s11, height: OBRitSpacing.s11, alignment: .leading)

            VStack(alignment: .leading, spacing: OBRitSpacing.s0_5) {
                Text(title)
                    .lineLimit(1)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                HStack(spacing: OBRitSpacing.s1) {
                    Text("\(stockCount)개")
                        .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: gridStockCountColor)
                    Text("남음")
                        .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.medium, color: gridStockSuffixColor)
                }
            }

            CardBadge(text: daysLabel, containerColor: gridBadgeContainerColor, contentColor: gridBadgeContentColor)
        }
        .padding(OBRitSpacing.s4)
        .frame(width: OBRitSpacing.s40, height: OBRitSpacing.s40, alignment: .topLeading)
        .background(cardContainerColor)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
    }

    private var cardContainerColor: Color {
        switch level {
        case .l1:
            return OBRitColors.red300
        case .l2, .l3:
            return OBRitColors.red900
        case .l4, .l5, .l6:
            return OBRitColors.gray850
        }
    }

    private var gridStockCountColor: Color {
        switch level {
        case .l2, .l5:
            return OBRitColors.red300
        default:
            return OBRitColors.common00
        }
    }

    private var gridStockSuffixColor: Color {
        level == .l1 ? OBRitColors.red100 : OBRitColors.gray300
    }

    private var gridBadgeContainerColor: Color {
        switch level {
        case .l1, .l4:
            return OBRitColors.common00
        case .l2, .l3:
            return OBRitColors.red300
        case .l5, .l6:
            return OBRitColors.gray750
        }
    }

    private var gridBadgeContentColor: Color {
        switch level {
        case .l1, .l4:
            return OBRitColors.red300
        default:
            return OBRitColors.common00
        }
    }
}

public extension OBRitCardGrid where ImageContent == EmptyView {
    init(
        level: OBRitCardLevel,
        title: String,
        stockCount: Int,
        daysLabel: String
    ) {
        self.init(level: level, title: title, stockCount: stockCount, daysLabel: daysLabel) {
            EmptyView()
        }
    }
}

public struct OBRitCardList<ImageContent: View>: View {
    private let level: OBRitCardLevel
    private let title: String
    private let daysInUseLabel: String
    private let replaceLabel: String
    private let sparesLabel: String
    private let image: () -> ImageContent

    public init(
        level: OBRitCardLevel,
        title: String,
        daysInUseLabel: String,
        replaceLabel: String,
        sparesLabel: String,
        @ViewBuilder image: @escaping () -> ImageContent
    ) {
        self.level = level
        self.title = title
        self.daysInUseLabel = daysInUseLabel
        self.replaceLabel = replaceLabel
        self.sparesLabel = sparesLabel
        self.image = image
    }

    public var body: some View {
        HStack(spacing: OBRitSpacing.s4) {
            CardImageBox(backgroundColor: obritCardListImageBackgroundColor) {
                image()
            }
            .frame(width: OBRitSpacing.s12, height: OBRitSpacing.s12)

            VStack(alignment: .leading, spacing: OBRitSpacing.s0_5) {
                Text(title)
                    .lineLimit(1)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                HStack(spacing: OBRitSpacing.s0_5) {
                    Text(daysInUseLabel)
                        .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    Text("째 사용중")
                        .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.medium, color: listInUseSuffixColor)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: OBRitSpacing.s1) {
                CardBadge(text: replaceLabel, containerColor: listFirstBadgeContainerColor, contentColor: listFirstBadgeContentColor)
                CardBadge(text: sparesLabel, containerColor: listSecondBadgeContainerColor, contentColor: OBRitColors.common00)
            }
            .fixedSize(horizontal: true, vertical: true)
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.vertical, OBRitSpacing.s4)
        .frame(maxWidth: .infinity)
        .background(cardContainerColor)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
    }

    private var cardContainerColor: Color {
        switch level {
        case .l1:
            return OBRitColors.red300
        case .l2, .l3:
            return OBRitColors.red900
        case .l4, .l5, .l6:
            return OBRitColors.gray850
        }
    }

    private var listInUseSuffixColor: Color {
        level == .l1 ? OBRitColors.red100 : OBRitColors.gray300
    }

    private var listFirstBadgeContainerColor: Color {
        switch level {
        case .l1:
            return OBRitColors.red250
        case .l2:
            return OBRitColors.common00
        case .l3, .l4:
            return OBRitColors.red300
        case .l5, .l6:
            return OBRitColors.gray750
        }
    }

    private var listFirstBadgeContentColor: Color {
        level == .l2 ? OBRitColors.red300 : OBRitColors.common00
    }

    private var listSecondBadgeContainerColor: Color {
        switch level {
        case .l1:
            return OBRitColors.red250
        case .l2, .l5:
            return OBRitColors.red300
        case .l3, .l4, .l6:
            return OBRitColors.gray750
        }
    }
}

public extension OBRitCardList where ImageContent == EmptyView {
    init(
        level: OBRitCardLevel,
        title: String,
        daysInUseLabel: String,
        replaceLabel: String,
        sparesLabel: String
    ) {
        self.init(
            level: level,
            title: title,
            daysInUseLabel: daysInUseLabel,
            replaceLabel: replaceLabel,
            sparesLabel: sparesLabel
        ) {
            EmptyView()
        }
    }
}

private struct CardImageBox<Content: View>: View {
    private let backgroundColor: Color
    private let content: () -> Content

    init(
        backgroundColor: Color = OBRitColors.gray800,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.backgroundColor = backgroundColor
        self.content = content
    }

    var body: some View {
        ZStack {
            Circle()
                .fill(backgroundColor)
            content()
        }
    }
}

private struct CardBadge: View {
    let text: String
    let containerColor: Color
    let contentColor: Color

    var body: some View {
        Text(text)
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: contentColor)
            .padding(.horizontal, OBRitSpacing.s2)
            .padding(.vertical, OBRitSpacing.s1)
            .background(containerColor)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
            .fixedSize(horizontal: true, vertical: true)
    }
}
