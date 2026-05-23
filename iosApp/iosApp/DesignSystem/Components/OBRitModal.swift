import SwiftUI
import Shared

public enum OBRitModalButtonCount: Sendable {
    case one
    case two
}

public enum OBRitModalImageSize: Sendable {
    case small
    case large

    var dimension: CGFloat {
        switch self {
        case .small:
            return 68
        case .large:
            return 100
        }
    }
}

public struct OBRitModal<ImageContent: View>: View {
    private let title: String
    private let description: String
    private let buttonCount: OBRitModalButtonCount
    private let imageSize: OBRitModalImageSize
    private let showsImage: Bool
    private let primaryTitle: String
    private let secondaryTitle: String
    private let onPrimaryClick: () -> Void
    private let onSecondaryClick: () -> Void
    private let image: () -> ImageContent

    public init(
        title: String,
        description: String,
        buttonCount: OBRitModalButtonCount = .two,
        imageSize: OBRitModalImageSize = .small,
        showsImage: Bool = true,
        primaryTitle: String = "CTA",
        secondaryTitle: String = "CTA",
        onPrimaryClick: @escaping () -> Void,
        onSecondaryClick: @escaping () -> Void = {},
        @ViewBuilder image: @escaping () -> ImageContent
    ) {
        self.title = title
        self.description = description
        self.buttonCount = buttonCount
        self.imageSize = imageSize
        self.showsImage = showsImage
        self.primaryTitle = primaryTitle
        self.secondaryTitle = secondaryTitle
        self.onPrimaryClick = onPrimaryClick
        self.onSecondaryClick = onSecondaryClick
        self.image = image
    }

    public var body: some View {
        VStack(spacing: OBRitSpacing.s2_5) {
            if showsImage {
                image()
                    .frame(width: imageSize.dimension, height: imageSize.dimension)
            }

            VStack(spacing: OBRitSpacing.s4) {
                VStack(spacing: OBRitSpacing.s2) {
                    Text(title)
                        .lineLimit(2)
                        .multilineTextAlignment(.center)
                        .obritTextStyle(OBRitModalMetrics.titleTextToken, weight: AtomFontWeight.shared.Bold, color: OBRitColors.common00)
                        .frame(maxWidth: .infinity)

                    Text(description)
                        .lineLimit(2)
                        .multilineTextAlignment(.center)
                        .obritTextStyle(OBRitTypography.base, weight: AtomFontWeight.shared.Medium, color: OBRitColors.gray300)
                        .frame(maxWidth: .infinity)
                }
                .frame(width: OBRitModalMetrics.contentWidth)

                buttonRow
                    .frame(width: OBRitModalMetrics.contentWidth)
            }
            .frame(width: OBRitModalMetrics.contentWidth)
        }
        .padding(.horizontal, OBRitSpacing.s7)
        .padding(.vertical, OBRitSpacing.s6)
        .frame(width: OBRitModalMetrics.modalWidth)
        .background(OBRitColors.gray800)
        .clipShape(RoundedRectangle(cornerRadius: OBRitModalMetrics.cornerRadius))
    }

    @ViewBuilder
    private var buttonRow: some View {
        switch buttonCount {
        case .one:
            OBRitModalButton(
                title: primaryTitle,
                style: .primary,
                action: onPrimaryClick
            )
        case .two:
            HStack(spacing: OBRitSpacing.s3) {
                OBRitModalButton(
                    title: secondaryTitle,
                    style: .secondary,
                    action: onSecondaryClick
                )
                .frame(width: OBRitModalMetrics.secondaryButtonWidth)

                OBRitModalButton(
                    title: primaryTitle,
                    style: .primary,
                    action: onPrimaryClick
                )
            }
        }
    }

}

private enum OBRitModalMetrics {
    static let modalWidth: CGFloat = 370
    static let contentWidth: CGFloat = 314
    static let secondaryButtonWidth: CGFloat = 82
    static let cornerRadius: CGFloat = 16
    static let titleTextToken = OBRitTypography.TextToken(size: 20, lineHeight: 30)
}

public extension OBRitModal where ImageContent == OBRitModalPlaceholderImage {
    init(
        title: String,
        description: String,
        buttonCount: OBRitModalButtonCount = .two,
        imageSize: OBRitModalImageSize = .small,
        showsImage: Bool = true,
        primaryTitle: String = "CTA",
        secondaryTitle: String = "CTA",
        onPrimaryClick: @escaping () -> Void,
        onSecondaryClick: @escaping () -> Void = {}
    ) {
        self.init(
            title: title,
            description: description,
            buttonCount: buttonCount,
            imageSize: imageSize,
            showsImage: showsImage,
            primaryTitle: primaryTitle,
            secondaryTitle: secondaryTitle,
            onPrimaryClick: onPrimaryClick,
            onSecondaryClick: onSecondaryClick
        ) {
            OBRitModalPlaceholderImage(size: imageSize)
        }
    }
}

public struct OBRitModalPlaceholderImage: View {
    private let size: OBRitModalImageSize

    public init(size: OBRitModalImageSize = .small) {
        self.size = size
    }

    public var body: some View {
        ZStack {
            Color(red: 230 / 255, green: 236 / 255, blue: 239 / 255)
            VStack(spacing: 3) {
                Circle()
                    .fill(Color(red: 201 / 255, green: 210 / 255, blue: 219 / 255))
                    .frame(width: 18, height: 18)

                HStack(alignment: .top, spacing: 2) {
                    TriangleShape()
                        .fill(Color(red: 201 / 255, green: 210 / 255, blue: 219 / 255))
                        .frame(width: 22, height: 22)
                    Rectangle()
                        .fill(Color(red: 201 / 255, green: 210 / 255, blue: 219 / 255))
                        .frame(width: 17, height: 17)
                }
                .frame(width: 41)
            }
            .frame(width: 41)
        }
        .frame(width: size.dimension, height: size.dimension)
    }
}

private enum OBRitModalButtonStyle {
    case primary
    case secondary
}

private struct OBRitModalButton: View {
    let title: String
    let style: OBRitModalButtonStyle
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .lineLimit(1)
                .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.SemiBold, color: contentColor)
                .frame(maxWidth: .infinity)
                .frame(height: 46)
                .padding(.horizontal, OBRitSpacing.s5)
                .background(backgroundColor)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
                .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        }
        .buttonStyle(.plain)
    }

    private var backgroundColor: Color {
        switch style {
        case .primary:
            return OBRitColors.green300
        case .secondary:
            return OBRitColors.gray750
        }
    }

    private var contentColor: Color {
        switch style {
        case .primary:
            return OBRitColors.common100
        case .secondary:
            return OBRitColors.gray300
        }
    }
}

private struct TriangleShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.midX, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.maxY))
        path.closeSubpath()
        return path
    }
}

struct OBRitModal_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: OBRitSpacing.s5) {
            OBRitModal(
                title: "Title Text\n최대 두 줄까지 작성 가능합니다.",
                description: "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
                buttonCount: .two,
                imageSize: .small,
                onPrimaryClick: {}
            )
            OBRitModal(
                title: "Title Text\n최대 두 줄까지 작성 가능합니다.",
                description: "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
                buttonCount: .one,
                imageSize: .large,
                onPrimaryClick: {}
            )
        }
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.gray900)
        .previewLayout(.sizeThatFits)
    }
}
