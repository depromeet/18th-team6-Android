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
            HStack(spacing: OBRitSpacing.s0_5) {
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
            return OBRitSpacing.s5
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

public struct OBRitFilledTextButton<LeadingIcon: View, TrailingIcon: View>: View {
    private let text: String
    private let size: OBRitFilledButtonSize
    private let color: OBRitFilledButtonColor
    private let enabled: Bool
    private let fillsWidth: Bool
    private let action: () -> Void
    private let hasLeadingIcon: Bool
    private let hasTrailingIcon: Bool
    private let leadingIcon: (Color) -> LeadingIcon
    private let trailingIcon: (Color) -> TrailingIcon

    public init(
        text: String,
        size: OBRitFilledButtonSize = .large,
        color: OBRitFilledButtonColor = .green,
        enabled: Bool = true,
        fillsWidth: Bool = false,
        action: @escaping () -> Void,
        @ViewBuilder leadingIcon: @escaping (Color) -> LeadingIcon,
        @ViewBuilder trailingIcon: @escaping (Color) -> TrailingIcon
    ) {
        self.text = text
        self.size = size
        self.color = color
        self.enabled = enabled
        self.fillsWidth = fillsWidth
        self.action = action
        self.hasLeadingIcon = true
        self.hasTrailingIcon = true
        self.leadingIcon = leadingIcon
        self.trailingIcon = trailingIcon
    }

    public var body: some View {
        OBRitFilledButton(
            size: size,
            color: color,
            enabled: enabled,
            fillsWidth: fillsWidth,
            action: action
        ) { contentColor in
            if hasLeadingIcon {
                leadingIcon(contentColor)
                    .frame(width: OBRitFilledButtonIconSize, height: OBRitFilledButtonIconSize)
            }
            Text(text)
                .lineLimit(1)
                .obritTextStyle(
                    size == .small ? OBRitTypography.base : OBRitTypography.xl,
                    weight: AtomFontWeight.shared.SemiBold,
                    color: contentColor
                )
            if hasTrailingIcon {
                trailingIcon(contentColor)
                    .frame(width: OBRitFilledButtonIconSize, height: OBRitFilledButtonIconSize)
            }
        }
    }
}

public extension OBRitFilledTextButton where LeadingIcon == EmptyView, TrailingIcon == EmptyView {
    init(
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
        self.hasLeadingIcon = false
        self.hasTrailingIcon = false
        self.leadingIcon = { _ in EmptyView() }
        self.trailingIcon = { _ in EmptyView() }
    }
}

public extension OBRitFilledTextButton where TrailingIcon == EmptyView {
    init(
        text: String,
        size: OBRitFilledButtonSize = .large,
        color: OBRitFilledButtonColor = .green,
        enabled: Bool = true,
        fillsWidth: Bool = false,
        action: @escaping () -> Void,
        @ViewBuilder leadingIcon: @escaping (Color) -> LeadingIcon
    ) {
        self.text = text
        self.size = size
        self.color = color
        self.enabled = enabled
        self.fillsWidth = fillsWidth
        self.action = action
        self.hasLeadingIcon = true
        self.hasTrailingIcon = false
        self.leadingIcon = leadingIcon
        self.trailingIcon = { _ in EmptyView() }
    }
}

public extension OBRitFilledTextButton where LeadingIcon == EmptyView {
    init(
        text: String,
        size: OBRitFilledButtonSize = .large,
        color: OBRitFilledButtonColor = .green,
        enabled: Bool = true,
        fillsWidth: Bool = false,
        action: @escaping () -> Void,
        @ViewBuilder trailingIcon: @escaping (Color) -> TrailingIcon
    ) {
        self.text = text
        self.size = size
        self.color = color
        self.enabled = enabled
        self.fillsWidth = fillsWidth
        self.action = action
        self.hasLeadingIcon = false
        self.hasTrailingIcon = true
        self.leadingIcon = { _ in EmptyView() }
        self.trailingIcon = trailingIcon
    }
}

private let OBRitFilledButtonIconSize: CGFloat = 16
