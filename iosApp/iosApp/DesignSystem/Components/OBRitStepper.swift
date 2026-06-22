import SwiftUI

public enum OBRitStepperSize {
    case small
    case large
}

public struct OBRitStepper: View {
    @State private var editableValueText: String
    @FocusState private var isValueFocused: Bool

    private let valueText: String
    private let minimumValue: Int
    private let maximumValue: Int?
    private let size: OBRitStepperSize
    private let isMinimum: Bool
    private let isEnabled: Bool
    private let onDecrement: (() -> Void)?
    private let onIncrement: (() -> Void)?
    private let onValueChange: ((Int) -> Void)?

    public init(
        valueText: String = "N",
        size: OBRitStepperSize = .small,
        isMinimum: Bool = false,
        isEnabled: Bool = true,
        onDecrement: (() -> Void)? = nil,
        onIncrement: (() -> Void)? = nil
    ) {
        _editableValueText = State(initialValue: valueText)
        self.valueText = valueText
        self.minimumValue = 0
        self.maximumValue = nil
        self.size = size
        self.isMinimum = isMinimum
        self.isEnabled = isEnabled
        self.onDecrement = onDecrement
        self.onIncrement = onIncrement
        self.onValueChange = nil
    }

    public init(
        value: Int,
        size: OBRitStepperSize = .small,
        minimumValue: Int = 0,
        maximumValue: Int? = nil,
        isEnabled: Bool = true,
        onDecrement: (() -> Void)? = nil,
        onIncrement: (() -> Void)? = nil,
        onValueChange: ((Int) -> Void)? = nil
    ) {
        let valueText = "\(value)"
        _editableValueText = State(initialValue: valueText)
        self.valueText = valueText
        self.minimumValue = minimumValue
        self.maximumValue = maximumValue
        self.size = size
        self.isMinimum = value <= minimumValue
        self.isEnabled = isEnabled
        self.onDecrement = onDecrement
        self.onIncrement = onIncrement
        self.onValueChange = onValueChange
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
            stepperButton(symbol: .minus, size: .small, disabled: !isEnabled || isMinimum, action: onDecrement)
            valueView(
                typography: OBRitStepperTypography.lg,
                frame: CGSize(
                    width: OBRitStepperMetrics.smallControlSize,
                    height: OBRitStepperMetrics.smallControlSize
                )
            )
            .background(OBRitColors.gray600)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
            stepperButton(symbol: .plus, size: .small, disabled: !isEnabled, action: onIncrement)
        }
        .padding(.vertical, OBRitSpacing.s1)
        .background(OBRitColors.gray750)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
    }

    private var largeStepper: some View {
        HStack(spacing: OBRitSpacing.s8) {
            stepperButton(symbol: .minus, size: .large, disabled: !isEnabled || isMinimum, action: onDecrement)
            valueView(
                typography: OBRitStepperTypography.xl7,
                frame: CGSize(width: OBRitStepperMetrics.largeValueMinWidth, height: OBRitStepperMetrics.largeButtonSize)
            )
            stepperButton(symbol: .plus, size: .large, disabled: !isEnabled, action: onIncrement)
        }
        .frame(width: OBRitStepperMetrics.largeWidth)
    }

    @ViewBuilder
    private func valueView(
        typography: OBRitTypography.TextToken,
        frame: CGSize
    ) -> some View {
        if onValueChange == nil {
            Text(valueText)
                .lineLimit(1)
                .minimumScaleFactor(0.7)
                .obritTextStyle(typography, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .frame(minWidth: frame.width, minHeight: frame.height)
        } else {
            TextField("", text: $editableValueText)
                .keyboardType(.numberPad)
                .multilineTextAlignment(.center)
                .focused($isValueFocused)
                .disabled(!isEnabled)
                .lineLimit(1)
                .minimumScaleFactor(0.7)
                .tint(OBRitColors.common00)
                .obritTextStyle(typography, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .frame(width: frame.width, height: frame.height)
                .contentShape(Rectangle())
                .accessibilityLabel(Text("quantity"))
                .onChange(of: valueText) { _, newValue in
                    guard newValue != editableValueText else { return }
                    editableValueText = newValue
                }
                .onChange(of: editableValueText) { _, newValue in
                    updateValue(from: newValue)
                }
                .onChange(of: isValueFocused) { _, isFocused in
                    guard !isFocused, editableValueText.isEmpty else { return }
                    editableValueText = valueText
                }
        }
    }

    private func updateValue(from text: String) {
        let digits = text.filter(\.isNumber)
        guard digits == text else {
            editableValueText = digits
            return
        }

        guard let parsedValue = Int(digits) else { return }
        let clampedValue = clamp(parsedValue)
        let clampedText = "\(clampedValue)"

        if clampedText != digits {
            editableValueText = clampedText
        }

        if clampedText != valueText {
            onValueChange?(clampedValue)
        }
    }

    private func clamp(_ value: Int) -> Int {
        let upperBoundedValue = maximumValue.map { min(value, $0) } ?? value
        return max(upperBoundedValue, minimumValue)
    }

    private func stepperButton(
        symbol: OBRitStepperSymbol,
        size: OBRitStepperSize,
        disabled: Bool,
        action: (() -> Void)?
    ) -> some View {
        Button {
            guard !disabled else { return }
            let shouldRestoreValueFocus = isValueFocused && onValueChange != nil
            action?()
            if shouldRestoreValueFocus {
                restoreValueFocus()
            }
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

    private func restoreValueFocus() {
        Task { @MainActor in
            await Task.yield()
            isValueFocused = true
        }
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
        size: OBRitTypography.lg.size,
        lineHeight: OBRitTypography.lg.lineHeight
    )
    static let xl7 = OBRitTypography.TextToken(
        size: OBRitTypography.s7xl.size,
        lineHeight: OBRitTypography.s7xl.lineHeight
    )
}

private enum OBRitStepperMetrics {
    static let smallGap: CGFloat = OBRitSpacing.s1_5
    static let smallControlSize: CGFloat = 36
    static let largeWidth: CGFloat = 197
    static let largeButtonSize: CGFloat = 56
    static let largeValueMinWidth: CGFloat = 21
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
