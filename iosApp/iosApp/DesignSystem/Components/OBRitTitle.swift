import SwiftUI

public enum OBRitTitleSize {
    case large
    case medium
    case small
}

public enum OBRitTitleType {
    case `default`
    case textOnly
    case withTag
}

public struct OBRitTitle: View {
    private let title: String
    private let description: String
    private let tagText: String
    private let size: OBRitTitleSize
    private let type: OBRitTitleType

    public init(
        title: String = "제목을 입력해주세요",
        description: String = "설명을 입력해주세요. 최대 두 줄까지 입력 가능합니다.",
        tagText: String = "Text",
        size: OBRitTitleSize = .large,
        type: OBRitTitleType = .default
    ) {
        self.title = title
        self.description = description
        self.tagText = tagText
        self.size = size
        self.type = type
    }

    public var body: some View {
        VStack(spacing: OBRitSpacing.s3) {
            content
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.vertical, OBRitSpacing.s4)
        .frame(maxWidth: .infinity)
    }

    @ViewBuilder
    private var content: some View {
        switch type {
        case .default:
            VStack(spacing: size == .small ? OBRitSpacing.s1 : OBRitSpacing.s3) {
                titleText
                    .multilineTextAlignment(.center)
                descriptionText
                    .lineLimit(2)
                    .multilineTextAlignment(.center)
            }
            .frame(maxWidth: .infinity)
        case .textOnly:
            titleText
                .multilineTextAlignment(.center)
                .frame(maxWidth: .infinity)
        case .withTag:
            VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                titleText
                HStack(spacing: OBRitSpacing.s2) {
                    OBRitTitleTag(text: tagText)
                    descriptionText
                        .lineLimit(1)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }

    private var titleText: some View {
        Text(title)
            .lineLimit(1)
            .obritTextStyle(titleToken, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)
    }

    private var descriptionText: some View {
        Text(description)
            .obritTextStyle(descriptionToken, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
    }

    private var titleToken: OBRitTypography.TextToken {
        switch size {
        case .large:
            return OBRitTypography.s6xl
        case .medium:
            return OBRitTypography.s4xl
        case .small:
            return OBRitTypography.TextToken(size: OBRitTypography.s2xl.size, lineHeight: OBRitTypography.lg.lineHeight)
        }
    }

    private var descriptionToken: OBRitTypography.TextToken {
        switch size {
        case .large:
            return OBRitTypography.xl
        case .medium:
            return OBRitTypography.lg
        case .small:
            return OBRitTypography.base
        }
    }
}

private struct OBRitTitleTag: View {
    let text: String

    var body: some View {
        Text(text)
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDarkSecondary)
            .padding(.horizontal, OBRitSpacing.s2)
            .padding(.vertical, OBRitSpacing.s1)
            .background(OBRitColors.backgroundDefaultLightGrayDefault)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraSmall))
    }
}
