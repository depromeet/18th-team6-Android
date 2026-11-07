import Foundation

@main
private struct StatusWidgetStateTests {
    static func state(_ ratio: Double) -> StatusWidgetState {
        StatusWidgetState(dangerCount: 2, warningCount: 0, goodRatio: ratio)
    }

    static func main() {
        precondition(StatusWidgetState.columns == 10 && StatusWidgetState.rows == 3)
        precondition(StatusWidgetState.totalDots == 30)
        for (ratio, good, filled) in [(0.7, true, 21), (0.6, true, 18), (0.4, false, 18), (0.2, false, 24)] {
            let value = state(ratio)
            precondition(value.isGood == good && value.filledDotCount == filled, "PRD example: \(ratio)")
        }
        precondition(state(0.499).isGood == false && state(0.499).filledDotCount == 15)
        precondition(state(0.5).isGood && state(0.5).filledDotCount == 15)
        precondition(state(0.501).isGood && state(0.501).filledDotCount == 15)
        precondition(state(0.49999).goodRatio == 0.49999, "Keep original ratio")
        precondition(state(0.55).filledDotCount == 17, "Round a half upward")
        precondition(state(0.45).filledDotCount == 17)
        precondition(state(1).isGood && state(1).filledDotCount == 30)
        precondition(!state(0).isGood && state(0).filledDotCount == 30)
        precondition(state(-0.1).goodRatio == 0 && state(1.1).goodRatio == 1)
        precondition(state(.nan).filledDotCount <= 30)

        for step in 0...1000 {
            let value = state(Double(step) / 1000)
            var actualFilled = 0
            for row in 0..<StatusWidgetState.rows {
                for column in 0..<StatusWidgetState.columns {
                    if value.isFilled(rowFromTop: row, column: column) { actualFilled += 1 }
                }
            }
            precondition(actualFilled == value.filledDotCount)
        }
        let green = state(0.7)
        precondition(green.isFilled(rowFromTop: 0, column: 0))
        precondition(!green.isFilled(rowFromTop: 0, column: 1))
        precondition(green.isFilled(rowFromTop: 1, column: 9))
        precondition(green.isFilled(rowFromTop: 2, column: 9))
        let orange = state(0.4)
        precondition(!orange.isFilled(rowFromTop: 0, column: 0))
        precondition(orange.isFilled(rowFromTop: 1, column: 7))
        precondition(!orange.isFilled(rowFromTop: 1, column: 8))
        precondition(orange.isFilled(rowFromTop: 2, column: 9))
        precondition(!orange.isFilled(rowFromTop: -1, column: 0))
        precondition(!orange.isFilled(rowFromTop: 0, column: 10))
        print("PASS: 30 dots, PRD examples, 50% threshold, rounding, bottom-up fill, bounded state")
    }
}
