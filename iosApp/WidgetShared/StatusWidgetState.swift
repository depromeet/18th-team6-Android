import Foundation

struct StatusWidgetState: Codable, Sendable {
    static let columns = 10
    static let rows = 3
    static let totalDots = columns * rows

    let dangerCount: Int
    let warningCount: Int
    let goodRatio: Double
    let isEmpty: Bool

    init(dangerCount: Int, warningCount: Int, goodRatio: Double, isEmpty: Bool = false) {
        self.isEmpty = isEmpty
        self.dangerCount = max(0, dangerCount)
        self.warningCount = max(0, warningCount)
        self.goodRatio = goodRatio.isFinite ? min(max(goodRatio, 0), 1) : 0
    }

    var isGood: Bool { goodRatio >= 0.5 }

    var filledDotCount: Int {
        let displayRatio = isGood ? goodRatio : 1 - goodRatio
        return Int((displayRatio * Double(Self.totalDots)).rounded(.toNearestOrAwayFromZero))
    }

    func isFilled(rowFromTop row: Int, column: Int) -> Bool {
        guard (0..<Self.rows).contains(row), (0..<Self.columns).contains(column) else { return false }
        let indexFromBottom = (Self.rows - 1 - row) * Self.columns + column
        return indexFromBottom < filledDotCount
    }
}
