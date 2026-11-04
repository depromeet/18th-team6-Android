import SwiftUI

/// A bounded horizontal number selector with a centered value and neighboring choices.
public struct OBRitNumberSelector: View {
    @State private var centeredValue: Int?

    private let value: Int
    private let range: ClosedRange<Int>
    private let isEnabled: Bool
    private let onValueChange: (Int) -> Void

    public init(
        value: Int,
        range: ClosedRange<Int> = 0...99,
        isEnabled: Bool = true,
        onValueChange: @escaping (Int) -> Void
    ) {
        let clampedValue = min(max(value, range.lowerBound), range.upperBound)
        _centeredValue = State(initialValue: clampedValue)
        self.value = clampedValue
        self.range = range
        self.isEnabled = isEnabled
        self.onValueChange = onValueChange
    }

    public var body: some View {
        HStack(spacing: 0) {
            stepButton(symbol: "minus", increment: -1, disabled: value == range.lowerBound)
            GeometryReader { geometry in
                numberStrip(width: geometry.size.width)
            }
            stepButton(symbol: "plus", increment: 1, disabled: value == range.upperBound)
        }
        .padding(OBRitSpacing.s1_5)
        .frame(height: OBRitSpacing.s14)
        .background(OBRitColors.backgroundDefaultDefaultHover)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        .onChange(of: value) { _, newValue in
            guard centeredValue != newValue else { return }
            centeredValue = newValue
        }
        .onChange(of: centeredValue) { _, newValue in
            guard isEnabled, let newValue, range.contains(newValue), value != newValue else { return }
            onValueChange(newValue)
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("수량")
        .accessibilityValue("\(value)")
        .accessibilityAdjustableAction { direction in
            switch direction {
            case .increment: select(value + 1)
            case .decrement: select(value - 1)
            @unknown default: break
            }
        }
    }

    private func numberStrip(width: CGFloat) -> some View {
        let cellWidth = width / NumberSelectorMetrics.visibleCount
        return ScrollView(.horizontal) {
            LazyHStack(spacing: 0) {
                ForEach(Array(range), id: \.self) { number in
                    numberButton(number, width: cellWidth)
                        .id(number)
                }
            }
            .scrollTargetLayout()
        }
        .contentMargins(.horizontal, max(0, (width - cellWidth) / 2), for: .scrollContent)
        .scrollIndicators(.hidden)
        .scrollTargetBehavior(.viewAligned)
        .scrollPosition(id: $centeredValue, anchor: .center)
        .scrollDisabled(!isEnabled)
    }

    private func numberButton(_ number: Int, width: CGFloat) -> some View {
        Button {
            select(number)
        } label: {
            Text("\(number)")
                .monospacedDigit()
                .obritTextStyle(
                    OBRitTypography.lg,
                    weight: number == value ? OBRitFontWeight.bold : OBRitFontWeight.medium,
                    color: color(for: number)
                )
                .frame(width: min(OBRitSpacing.s9, width), height: OBRitSpacing.s9)
                .background(number == value ? OBRitColors.gray700 : .clear)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
                .frame(width: width, height: OBRitSpacing.s11)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(!isEnabled)
    }

    private func stepButton(symbol: String, increment: Int, disabled: Bool) -> some View {
        Button {
            select(value + increment)
        } label: {
            Image("number_selector_\(symbol)")
                .renderingMode(.template)
                .foregroundStyle(disabled || !isEnabled ? OBRitColors.gray600 : OBRitColors.common00)
                .frame(width: OBRitSpacing.s7, height: OBRitSpacing.s7)
                .frame(width: OBRitSpacing.s11, height: OBRitSpacing.s11)
                .background(OBRitColors.gray900)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
        }
        .buttonStyle(.plain)
        .disabled(disabled || !isEnabled)
        .accessibilityLabel(increment > 0 ? "수량 늘리기" : "수량 줄이기")
    }

    private func color(for number: Int) -> Color {
        switch abs(number - value) {
        case 0: OBRitColors.common00
        case 1: OBRitColors.gray600
        case 2: OBRitColors.gray700
        default: OBRitColors.gray750
        }
    }

    private func select(_ number: Int) {
        guard isEnabled, range.contains(number), number != value else { return }
        onValueChange(number)
        withAnimation(.easeOut(duration: NumberSelectorMetrics.animationDuration)) {
            centeredValue = number
        }
    }
}

private enum NumberSelectorMetrics {
    static let visibleCount: CGFloat = 7
    static let animationDuration: Double = 0.16
}

#Preview {
    NumberSelectorPreview()
}

private struct NumberSelectorPreview: View {
    @State private var quantity = 0

    var body: some View {
        VStack(spacing: OBRitSpacing.s6) {
            OBRitNumberSelector(value: quantity) { quantity = $0 }
            OBRitNumberSelector(value: 4, onValueChange: { _ in })
            OBRitNumberSelector(value: 99, onValueChange: { _ in })
        }
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultDefault)
    }
}
