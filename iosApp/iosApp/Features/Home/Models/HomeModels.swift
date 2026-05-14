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
    var statusFilters: Set<HomeStatusFilter> = []

    var riskRank: Int {
        cardLevel.homeRiskRank
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
