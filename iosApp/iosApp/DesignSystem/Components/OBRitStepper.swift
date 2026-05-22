import SwiftUI
import Shared

public enum OBRitStepperSize {
    case small
    case large
}

public struct OBRitStepper: View {
    private let valueText: String
    private let size: OBRitStepperSize
    private let isMinimum: Bool
    private let onDecrement: (() -> Void)?
    private let onIncrement: (() -> Void)?

    public init(
        valueText: String = "N",
        size: OBRitStepperSize = .small,
        isMinimum: Bool = false,
        onDecrement: (() -> Void)? = nil,
        onIncrement: (() -> Void)? = nil
    ) {
        self.valueText = valueText
        self.size = size
        self.isMinimum = isMinimum
        self.onDecrement = onDecrement
        self.onIncrement = onIncrement
    }

    public init(
        value: Int,
        size: OBRitStepperSize = .small,
        minimumValue: Int = 0,
        onDecrement: (() -> Void)? = nil,
        onIncrement: (() -> Void)? = nil
    ) {
        self.valueText = "\(value)"
        self.size = size
        self.isMinimum = value <= minimumValue
        self.onDecrement = onDecrement
        self.onIncrement = onIncrement
    }

    public var body: some View {
        switch size {
        case .small:
            smallStepper
        case .large:
            largeStepper
        }
    }

    private var smallStepper: some View {
        HStack(spacing: OBRitStepperMetrics.smallGap) {
            stepperButton(symbol: .minus, size: .small, disabled: isMinimum, action: onDecrement)
            smallValue
            stepperButton(symbol: .plus, size: .small, disabled: false, action: onIncrement)
        }
        .padding(.vertical, OBRitSpacing.s1)
        .background(OBRitColors.gray750)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
    }

    private var smallValue: some View {
        Text(valueText)
            .lineLimit(1)
            .minimumScaleFactor(0.7)
            .obritTextStyle(OBRitStepperTypography.lg, weight: AtomFontWeight.shared.Bold, color: OBRitColors.common00)
            .frame(width: OBRitStepperMetrics.smallControlSize, height: OBRitStepperMetrics.smallControlSize)
            .background(OBRitColors.gray600)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
    }

    private var largeStepper: some View {
        HStack(spacing: OBRitSpacing.s8) {
            stepperButton(symbol: .minus, size: .large, disabled: isMinimum, action: onDecrement)
            Text(valueText)
                .lineLimit(1)
                .minimumScaleFactor(0.7)
                .obritTextStyle(OBRitStepperTypography.xl7, weight: AtomFontWeight.shared.Bold, color: OBRitColors.common00)
                .frame(minWidth: 21)
            stepperButton(symbol: .plus, size: .large, disabled: false, action: onIncrement)
        }
        .frame(width: OBRitStepperMetrics.largeWidth)
    }

    private func stepperButton(
        symbol: OBRitStepperSymbol,
        size: OBRitStepperSize,
        disabled: Bool,
        action: (() -> Void)?
    ) -> some View {
        Button {
            guard !disabled else { return }
            action?()
        } label: {
            symbol
                .stroke(disabled ? OBRitColors.gray600 : OBRitColors.common00, style: StrokeStyle(lineWidth: 2, lineCap: .round))
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
                .frame(width: buttonSize(for: size), height: buttonSize(for: size))
                .background(buttonBackground(for: size))
                .clipShape(RoundedRectangle(cornerRadius: buttonRadius(for: size)))
                .contentShape(RoundedRectangle(cornerRadius: buttonRadius(for: size)))
        }
        .buttonStyle(.plain)
        .disabled(disabled)
        .accessibilityLabel(symbol.accessibilityLabel)
    }

    private func buttonSize(for size: OBRitStepperSize) -> CGFloat {
        switch size {
        case .small:
            return OBRitStepperMetrics.smallControlSize
        case .large:
            return OBRitStepperMetrics.largeButtonSize
        }
    }

    private func buttonRadius(for size: OBRitStepperSize) -> CGFloat {
        switch size {
        case .small:
            return OBRitRadius.small
        case .large:
            return OBRitRadius.large
        }
    }

    private func buttonBackground(for size: OBRitStepperSize) -> Color {
        switch size {
        case .small:
            return Color.clear
        case .large:
            return OBRitColors.gray800
        }
    }
}

private enum OBRitStepperTypography {
    static let lg = OBRitTypography.TextToken(
        size: CGFloat(AtomText.Lg.shared.FontSize),
        lineHeight: CGFloat(AtomText.Lg.shared.LineHeight)
    )
    static let xl7 = OBRitTypography.TextToken(
        size: CGFloat(AtomText.S7xl.shared.FontSize),
        lineHeight: CGFloat(AtomText.S7xl.shared.LineHeight)
    )
}

private enum OBRitStepperMetrics {
    static let smallGap: CGFloat = OBRitSpacing.s1_5
    static let smallControlSize: CGFloat = 36
    static let largeWidth: CGFloat = 197
    static let largeButtonSize: CGFloat = 56
}

private enum OBRitStepperSymbol: Shape, Equatable {
    case minus
    case plus

    var accessibilityLabel: Text {
        switch self {
        case .minus:
            return Text("decrease")
        case .plus:
            return Text("increase")
        }
    }

    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.minX + rect.width * 0.2, y: rect.midY))
        path.addLine(to: CGPoint(x: rect.maxX - rect.width * 0.2, y: rect.midY))
        if self == .plus {
            path.move(to: CGPoint(x: rect.midX, y: rect.minY + rect.height * 0.2))
            path.addLine(to: CGPoint(x: rect.midX, y: rect.maxY - rect.height * 0.2))
        }
        return path
    }
}
