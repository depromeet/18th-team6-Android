import SwiftUI

public enum OBRitFloatingActionButtonIcon {
    case close
    case plus

    var symbolName: String {
        switch self {
        case .close:
            return "xmark"
        case .plus:
            return "plus"
        }
    }
}

public struct OBRitFloatingActionMenuItem: Identifiable {
    public let id: String
    public let title: String
    public let action: () -> Void

    public init(
        id: String,
        title: String,
        action: @escaping () -> Void
    ) {
        self.id = id
        self.title = title
        self.action = action
    }
}

public struct OBRitFloatingActionMenu: View {
    @Binding private var isPresented: Bool

    private let items: [OBRitFloatingActionMenuItem]
    private let accessibilityLabel: String

    public init(
        isPresented: Binding<Bool>,
        items: [OBRitFloatingActionMenuItem],
        accessibilityLabel: String
    ) {
        _isPresented = isPresented
        self.items = items
        self.accessibilityLabel = accessibilityLabel
    }

    public var body: some View {
        VStack(alignment: .trailing, spacing: OBRitSpacing.s2) {
            if isPresented {
                VStack(spacing: 0) {
                    ForEach(items) { item in
                        Button {
                            isPresented = false
                            item.action()
                        } label: {
                            Text(item.title)
                                .lineLimit(1)
                                .obritTextStyle(
                                    OBRitTypography.xl,
                                    weight: OBRitFontWeight.bold,
                                    color: OBRitColors.textDefaultDarkDefault
                                )
                                .frame(height: OBRitFloatingActionMenuMetrics.itemHeight)
                                .padding(.horizontal, OBRitSpacing.s5)
                                .frame(minWidth: OBRitFloatingActionMenuMetrics.minimumMenuWidth)
                                .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.vertical, OBRitSpacing.s2)
                .background(OBRitColors.backgroundDefaultLightGrayDefault)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
            }

            OBRitFloatingActionButton(
                icon: .plus,
                iconRotation: isPresented ? .degrees(45) : .zero,
                accessibilityLabel: accessibilityLabel,
                backgroundColor: isPresented ? OBRitColors.backgroundDefaultMiddleGraySecondary : OBRitColors.common00,
                contentColor: isPresented ? OBRitColors.common00 : OBRitColors.gray900
            ) {
                withAnimation(OBRitFloatingActionMenuMetrics.toggleAnimation) {
                    isPresented.toggle()
                }
            }
        }
        .frame(minWidth: OBRitFloatingActionMenuMetrics.minimumMenuWidth, alignment: .trailing)
    }
}

public struct OBRitFloatingActionButton: View {
    private let icon: OBRitFloatingActionButtonIcon
    private let iconRotation: Angle
    private let accessibilityLabel: String
    private let backgroundColor: Color
    private let contentColor: Color
    private let action: () -> Void

    public init(
        icon: OBRitFloatingActionButtonIcon = .plus,
        iconRotation: Angle = .zero,
        accessibilityLabel: String,
        backgroundColor: Color = OBRitColors.common00,
        contentColor: Color = OBRitColors.gray900,
        action: @escaping () -> Void
    ) {
        self.icon = icon
        self.iconRotation = iconRotation
        self.accessibilityLabel = accessibilityLabel
        self.backgroundColor = backgroundColor
        self.contentColor = contentColor
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            Image(systemName: icon.symbolName)
                .font(.system(size: OBRitSpacing.s5, weight: .bold))
                .foregroundStyle(contentColor)
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
                .rotationEffect(iconRotation)
                .padding(OBRitSpacing.s4)
                .background(backgroundColor)
                .clipShape(Circle())
                .shadow(color: Color.black.opacity(0.24), radius: OBRitSpacing.s6, x: 0, y: OBRitSpacing.s4)
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityLabel)
        .animation(OBRitFloatingActionMenuMetrics.toggleAnimation, value: iconRotation)
        .animation(OBRitFloatingActionMenuMetrics.toggleAnimation, value: backgroundColor)
    }
}

private enum OBRitFloatingActionMenuMetrics {
    static let minimumMenuWidth: CGFloat = 120
    static let itemHeight: CGFloat = 48
    static let toggleAnimation = Animation.spring(response: 0.28, dampingFraction: 0.78)
}
