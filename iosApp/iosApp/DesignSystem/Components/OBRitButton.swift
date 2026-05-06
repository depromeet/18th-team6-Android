import SwiftUI
import Shared

public enum OBRitFilledButtonSize {
    case large
    case middle
    case small
}

public enum OBRitFilledButtonColor {
    case green
    case gray
    case white
}

public struct OBRitFilledButton<Content: View>: View {
    private let size: OBRitFilledButtonSize
    private let color: OBRitFilledButtonColor
    private let enabled: Bool
    private let fillsWidth: Bool
    private let action: () -> Void
    private let content: (Color) -> Content

    public init(
        size: OBRitFilledButtonSize = .large,
        color: OBRitFilledButtonColor = .green,
        enabled: Bool = true,
        fillsWidth: Bool = false,
        action: @escaping () -> Void,
        @ViewBuilder content: @escaping (Color) -> Content
    ) {
        self.size = size
        self.color = color
        self.enabled = enabled
        self.fillsWidth = fillsWidth
        self.action = action
        self.content = content
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: OBRitSpacing.s2) {
                content(contentColor)
            }
            .frame(minHeight: height)
            .padding(.horizontal, horizontalPadding)
            .frame(maxWidth: fillsWidth ? .infinity : nil)
            .background(containerColor)
            .clipShape(RoundedRectangle(cornerRadius: radius))
            .contentShape(RoundedRectangle(cornerRadius: radius))
        }
        .buttonStyle(.plain)
        .disabled(!enabled)
    }

    var textToken: OBRitTypography.TextToken {
        size == .small ? OBRitTypography.base : OBRitTypography.xl
    }

    private var height: CGFloat {
        switch size {
        case .large:
            return OBRitSpacing.s14
        case .middle:
            return 46
        case .small:
            return 38
        }
    }

    private var horizontalPadding: CGFloat {
        switch size {
        case .large:
            return OBRitSpacing.s6
        case .middle:
            return 22
        case .small:
            return 14
        }
    }

    private var radius: CGFloat {
        switch size {
        case .large:
            return OBRitRadius.large
        case .middle:
            return OBRitRadius.middle
        case .small:
            return OBRitRadius.small
        }
    }

    private var containerColor: Color {
        switch color {
        case .green:
            return enabled ? OBRitColors.green300 : OBRitColors.green800
        case .gray:
            return OBRitColors.gray800
        case .white:
            return enabled ? OBRitColors.common00 : OBRitColors.gray600
        }
    }

    private var contentColor: Color {
        switch color {
        case .green:
            return OBRitColors.common100
        case .gray:
            return enabled ? OBRitColors.common00 : OBRitColors.gray700
        case .white:
            return enabled ? OBRitColors.common100 : OBRitColors.color(0xFF24242A)
        }
    }
}

public struct OBRitFilledTextButton: View {
    private let text: String
    private let size: OBRitFilledButtonSize
    private let color: OBRitFilledButtonColor
    private let enabled: Bool
    private let fillsWidth: Bool
    private let action: () -> Void

    public init(
        text: String,
        size: OBRitFilledButtonSize = .large,
        color: OBRitFilledButtonColor = .green,
        enabled: Bool = true,
        fillsWidth: Bool = false,
        action: @escaping () -> Void
    ) {
        self.text = text
        self.size = size
        self.color = color
        self.enabled = enabled
        self.fillsWidth = fillsWidth
        self.action = action
    }

    public var body: some View {
        OBRitFilledButton(
            size: size,
            color: color,
            enabled: enabled,
            fillsWidth: fillsWidth,
            action: action
        ) { contentColor in
            Text(text)
                .lineLimit(1)
                .obritTextStyle(
                    size == .small ? OBRitTypography.base : OBRitTypography.xl,
                    weight: AtomFontWeight.shared.Bold,
                    color: contentColor
                )
        }
    }
}
