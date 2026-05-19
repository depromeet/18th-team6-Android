import SwiftUI

struct HomeDashboard {
    let summary: HomeSummary
    let warningItems: [HomeConsumableItem]
    let usageItems: [HomeConsumableItem]
}

struct HomeSummary {
    let status: String
    let replacementStatus: String
    let stockStatus: String
    let positiveRatio: Int
    let warningRatio: Int
    let totalCount: Int
    let warningCount: Int
    let history: [Double]
    private let ownStatusPercentValue: Double
    private let averageStatusPercentValue: Double

    init(
        status: String,
        replacementStatus: String,
        stockStatus: String,
        positiveRatio: Int,
        warningRatio: Int,
        totalCount: Int,
        warningCount: Int,
        history: [Double],
        ownStatusPercent: Double? = nil,
        averageStatusPercent: Double? = nil
    ) {
        self.status = status
        self.replacementStatus = replacementStatus
        self.stockStatus = stockStatus
        self.positiveRatio = positiveRatio
        self.warningRatio = warningRatio
        self.totalCount = totalCount
        self.warningCount = warningCount
        self.history = history
        self.ownStatusPercentValue = Self.normalizedPercent(ownStatusPercent ?? Double(positiveRatio) / 100)
        self.averageStatusPercentValue = Self.normalizedPercent(
            averageStatusPercent ?? Self.averagePercent(from: history, fallback: self.ownStatusPercentValue)
        )
    }

    init(items: [HomeConsumableItem], averageStatusHistory: [Double] = []) {
        let metrics = HomeStatusMetrics(items: items)
        let ownStatusPercent = metrics.ownStatusPercent
        let positiveRatio = Int((ownStatusPercent * 100).rounded())

        self.init(
            status: metrics.overallStatus.title,
            replacementStatus: metrics.replacementStatus.title,
            stockStatus: metrics.spareStatus.title,
            positiveRatio: positiveRatio,
            warningRatio: 100 - positiveRatio,
            totalCount: metrics.totalCount,
            warningCount: metrics.replacementDangerCount,
            history: averageStatusHistory,
            ownStatusPercent: ownStatusPercent,
            averageStatusPercent: Self.averagePercent(
                from: averageStatusHistory,
                fallback: metrics.replacementAveragePercent
            )
        )
    }
}

extension HomeSummary {
    // 평균/내 상태의 백분율은 해당 기준을 참조합니다.
    var ownStatusPercent: Double {
        ownStatusPercentValue
    }

    var averageStatusPercent: Double {
        averageStatusPercentValue
    }

    fileprivate static func averagePercent(from values: [Double], fallback: Double) -> Double {
        guard !values.isEmpty else { return fallback }
        return normalizedPercent(values.reduce(0, +) / Double(values.count))
    }

    fileprivate static func normalizedPercent(_ value: Double) -> Double {
        min(max(value, 0), 1)
    }
}

struct HomeConsumableItem: Identifiable {
    let id: Int
    let title: String
    let daysInUse: Int
    let stockCount: Int
    let dDayLabel: String
    let replaceLabel: String
    let sparesLabel: String
    let cardLevel: OBRitCardLevel
    let imageColor: Color
    let orbAssetName: String
    var statusFilters: Set<HomeStatusFilter> = []

    var riskRank: Int {
        cardLevel.homeRiskRank
    }
}

struct HomeOrbInteriorItem: Identifiable, Hashable {
    let id: Int
    let assetName: String
    let weight: CGFloat

    init(id: Int, assetName: String, weight: CGFloat = HomeOrbVisualConfig.defaultItemWeight) {
        self.id = id
        self.assetName = assetName
        self.weight = min(
            max(weight, HomeOrbVisualConfig.minimumItemWeight),
            HomeOrbVisualConfig.maximumWeightForSizing
        )
    }

    init(id: Int, assetName: String, riskRank: Int) {
        self.init(
            id: id,
            assetName: assetName,
            weight: Self.weight(forRiskRank: riskRank)
        )
    }

    static func weight(forRiskRank riskRank: Int) -> CGFloat {
        HomeOrbVisualConfig.itemWeightBase +
            CGFloat(max(1, HomeOrbVisualConfig.riskRankUpperBound - riskRank)) * HomeOrbVisualConfig.itemWeightRiskScale
    }
}

extension HomeConsumableItem {
    var orbInteriorItem: HomeOrbInteriorItem {
        HomeOrbInteriorItem(
            id: id,
            assetName: orbAssetName,
            riskRank: riskRank
        )
    }
}

enum HomeStatusFilter: CaseIterable, Hashable {
    case replacementDanger
    case spareShortage
    case replacementWarning

    var title: String {
        switch self {
        case .replacementDanger:
            return "교체 위험"
        case .spareShortage:
            return "여분 부족"
        case .replacementWarning:
            return "교체 경고"
        }
    }
}

enum HomeWarningSort: CaseIterable, Hashable {
    case replacementRisk
    case longestUse

    var title: String {
        switch self {
        case .replacementRisk:
            return "교체 위험순"
        case .longestUse:
            return "오래 사용한 순"
        }
    }
}

private extension OBRitCardLevel {
    var homeRiskRank: Int {
        switch self {
        case .l1:
            return 1
        case .l2:
            return 2
        case .l3:
            return 3
        case .l4:
            return 4
        case .l5:
            return 5
        case .l6:
            return 6
        }
    }
}

private struct HomeStatusMetrics {
    let totalCount: Int
    let replacementScoreAverage: Double
    let replacementDangerRatio: Double
    let replacementDangerCount: Int
    let spareMissingRatio: Double

    init(items: [HomeConsumableItem]) {
        totalCount = items.count

        guard !items.isEmpty else {
            replacementScoreAverage = 0
            replacementDangerRatio = 0
            replacementDangerCount = 0
            spareMissingRatio = 0
            return
        }

        let replacementScores = items.map(\.replacementScore)
        replacementDangerCount = replacementScores.filter { $0 == 0 }.count
        replacementScoreAverage = Double(replacementScores.reduce(0, +)) / Double(items.count)
        replacementDangerRatio = Double(replacementDangerCount) / Double(items.count)
        spareMissingRatio = Double(items.filter { $0.stockCount == 0 }.count) / Double(items.count)
    }

    var replacementAveragePercent: Double {
        min(replacementScoreAverage / 2, 1)
    }

    var ownStatusPercent: Double {
        min(replacementAveragePercent, 1 - replacementDangerRatio)
    }

    var replacementStatus: HomeStatusLevel {
        if replacementDangerRatio == 0 {
            return replacementScoreAverage >= 1.8 ? .good : .warning
        }

        if replacementDangerRatio <= 0.30 && replacementScoreAverage >= 1.0 {
            return .warning
        }

        return .danger
    }

    var spareStatus: HomeStatusLevel {
        if spareMissingRatio == 0 {
            return .good
        }

        return spareMissingRatio <= 0.30 ? .warning : .danger
    }

    var overallStatus: HomeOverallStatus {
        switch (replacementStatus, spareStatus) {
        case (.good, .good):
            return .perfect
        case (.good, .warning), (.warning, .good):
            return .good
        case (.good, .danger), (.warning, .warning), (.danger, .good):
            return .warning
        case (.warning, .danger), (.danger, .warning), (.danger, .danger):
            return .danger
        }
    }
}

private enum HomeStatusLevel {
    case good
    case warning
    case danger

    var title: String {
        switch self {
        case .good:
            return "양호"
        case .warning:
            return "경고"
        case .danger:
            return "위험"
        }
    }
}

private enum HomeOverallStatus {
    case perfect
    case good
    case warning
    case danger

    var title: String {
        switch self {
        case .perfect:
            return "완벽"
        case .good:
            return "양호"
        case .warning:
            return "경고"
        case .danger:
            return "위험"
        }
    }
}

private extension HomeConsumableItem {
    var replacementScore: Int {
        if dDayLabel.localizedCaseInsensitiveContains("day")
            || dDayLabel.hasPrefix("D+")
            || dDayLabel == "D-0" {
            return 0
        }

        guard dDayLabel.hasPrefix("D-"),
              let days = Int(dDayLabel.dropFirst(2)) else {
            return 0
        }

        return days >= 4 ? 2 : 1
    }
}
