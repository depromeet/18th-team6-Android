import SwiftUI

public struct OBRitSlider: View {
    private let value: Double
    private let enabled: Bool
    private let valueRange: ClosedRange<Double>
    private let onValueChange: (Double) -> Void
    private let onValueChangeFinished: (() -> Void)?

    public init(
        value: Double,
        enabled: Bool = true,
        valueRange: ClosedRange<Double> = 0...1,
        onValueChange: @escaping (Double) -> Void,
        onValueChangeFinished: (() -> Void)? = nil
    ) {
        self.value = value
        self.enabled = enabled
        self.valueRange = valueRange
        self.onValueChange = onValueChange
        self.onValueChangeFinished = onValueChangeFinished
    }

    public var body: some View {
        GeometryReader { geometry in
            let fraction = normalizedFraction
            let trackWidth = max(0, geometry.size.width - OBRitSliderConstants.thumbSize)
            let thumbCenterX = OBRitSliderConstants.thumbSize / 2 + trackWidth * fraction
            let fillWidth = min(geometry.size.width, thumbCenterX + OBRitSliderConstants.trackHeight / 4)

            ZStack(alignment: .leading) {
                Capsule()
                    .fill(OBRitColors.gray800)
                    .frame(height: OBRitSliderConstants.trackHeight)

                Capsule()
                    .fill(OBRitColors.green300)
                    .frame(width: fillWidth, height: OBRitSliderConstants.trackHeight)

                Circle()
                    .fill(OBRitColors.green300)
                    .padding(OBRitSpacing.s1)
                    .background(Circle().fill(OBRitColors.common00))
                    .frame(width: OBRitSliderConstants.thumbSize, height: OBRitSliderConstants.thumbSize)
                    .position(x: thumbCenterX, y: geometry.size.height / 2)
            }
            .contentShape(Rectangle())
            .gesture(
                DragGesture(minimumDistance: 0)
                    .onChanged { gesture in
                        guard enabled else { return }
                        let location = min(max(gesture.location.x - OBRitSliderConstants.thumbSize / 2, 0), trackWidth)
                        onValueChange(valueRange.lowerBound + (valueRange.upperBound - valueRange.lowerBound) * location / max(trackWidth, 1))
                    }
                    .onEnded { _ in
                        guard enabled else { return }
                        onValueChangeFinished?()
                    }
            )
            .opacity(enabled ? 1 : 0.45)
        }
        .frame(height: OBRitSliderConstants.thumbSize)
        .accessibilityElement()
        .accessibilityLabel("Slider")
        .accessibilityValue("\(Int(normalizedFraction * 100))%")
        .accessibilityAdjustableAction { direction in
            guard enabled else { return }
            let step = (valueRange.upperBound - valueRange.lowerBound) / 20
            switch direction {
            case .increment:
                onValueChange(min(valueRange.upperBound, value + step))
            case .decrement:
                onValueChange(max(valueRange.lowerBound, value - step))
            @unknown default:
                break
            }
        }
    }

    private var normalizedFraction: Double {
        let span = valueRange.upperBound - valueRange.lowerBound
        guard span > 0 else { return 0 }
        return ((value - valueRange.lowerBound) / span).clamped(to: 0...1)
    }
}

private enum OBRitSliderConstants {
    static let thumbSize = OBRitSpacing.s5
    static let trackHeight: CGFloat = 4
}

private extension Comparable {
    func clamped(to range: ClosedRange<Self>) -> Self {
        min(max(self, range.lowerBound), range.upperBound)
    }
}
