import SwiftUI

enum ItemDetailLayout {
    static let designWidth: CGFloat = 412
    static let horizontalPadding = OBRitSpacing.s5
    static let sectionSpacing = OBRitSpacing.s7
    static let cardPadding = OBRitSpacing.s5
    static let cardTopPadding = OBRitSpacing.s6
    static let cardBottomPadding = OBRitSpacing.s4
    static let cardCornerRadius = OBRitRadius.extraLarge
    static let cardBorderWidth: CGFloat = 1
    static let heroDiameterRatio: CGFloat = 230 / designWidth
    static let heroMaxDiameter: CGFloat = 230
    static let heroVerticalPadding = OBRitSpacing.s5
    static let heroRingWidthRatio: CGFloat = 12 / 230
    static let dateSummaryHeight: CGFloat = 81
    static let actionBarButtonHeight: CGFloat = 60
}

enum ItemDetailStatus {
    case good
    case warning

    var accentColor: Color {
        switch self {
        case .good:
            return OBRitColors.green300
        case .warning:
            return OBRitColors.textWarningDefault
        }
    }

    var subduedAccentColor: Color {
        switch self {
        case .good:
            return OBRitColors.green600
        case .warning:
            return OBRitColors.red600
        }
    }

    var badgeBackgroundColor: Color {
        switch self {
        case .good:
            return OBRitColors.green800
        case .warning:
            return OBRitColors.red800
        }
    }

    var heroFillColor: Color {
        switch self {
        case .good:
            return OBRitColors.green850
        case .warning:
            return OBRitColors.red850
        }
    }

    var heroTrackColor: Color {
        switch self {
        case .good:
            return OBRitColors.green800
        case .warning:
            return OBRitColors.red800
        }
    }

    var chartTrackColor: Color {
        OBRitColors.green850
    }

    var chartBarColor: Color {
        OBRitColors.green800
    }

    var chartCurrentColor: Color {
        OBRitColors.green300
    }
}

struct ItemDetailDisplayData: Identifiable {
    let id: Int
    let title: String
    let imageAssetName: String
    let status: ItemDetailStatus
    let heroProgress: Double
    let lastReplacementDateText: String
    let nextReplacementDateText: String
    let replacementDayBadgeText: String
    let stockCount: Int
    let averageReplacementDaysText: String
    let recommendedReplacementDaysText: String
    let currentUsageDaysText: String
    let currentStatusBadgeText: String?
    let replacementHistory: [ItemDetailReplacementHistoryEntry]
    let replacementHistoryAverageText: String
}

struct ItemDetailReplacementHistoryEntry: Identifiable {
    let id: String
    let daysText: String
    let dateText: String
    let ratio: Double
    let isCurrent: Bool

    init(
        id: String = UUID().uuidString,
        daysText: String,
        dateText: String,
        ratio: Double,
        isCurrent: Bool = false
    ) {
        self.id = id
        self.daysText = daysText
        self.dateText = dateText
        self.ratio = min(max(ratio, 0), 1)
        self.isCurrent = isCurrent
    }
}

struct ItemDetailViewAction {
    let onBack: () -> Void
    let onMore: () -> Void
    let onManageStock: () -> Void
    let onCompleteReplacement: () -> Void

    static let noop = ItemDetailViewAction(
        onBack: {},
        onMore: {},
        onManageStock: {},
        onCompleteReplacement: {}
    )
}

enum ItemDetailPreviewData {
    static let good = ItemDetailDisplayData(
        id: 1,
        title: "칫솔",
        imageAssetName: "home_orb_toothbrush",
        status: .good,
        heroProgress: 0.82,
        lastReplacementDateText: "5월 1일",
        nextReplacementDateText: "6월 1일",
        replacementDayBadgeText: "D-7",
        stockCount: 3,
        averageReplacementDaysText: "34일",
        recommendedReplacementDaysText: "30일",
        currentUsageDaysText: "23일째",
        currentStatusBadgeText: nil,
        replacementHistory: [
            ItemDetailReplacementHistoryEntry(id: "history-1", daysText: "29일", dateText: "01/01", ratio: 0.90),
            ItemDetailReplacementHistoryEntry(id: "history-2", daysText: "31일", dateText: "01/31", ratio: 0.70),
            ItemDetailReplacementHistoryEntry(id: "history-3", daysText: "32일", dateText: "03/03", ratio: 0.78),
            ItemDetailReplacementHistoryEntry(id: "history-4", daysText: "38일", dateText: "04/10", ratio: 0.92),
            ItemDetailReplacementHistoryEntry(id: "history-current", daysText: "23일", dateText: "현재", ratio: 1, isCurrent: true)
        ],
        replacementHistoryAverageText: "33.8일"
    )

    static let warning = ItemDetailDisplayData(
        id: 2,
        title: "칫솔",
        imageAssetName: "home_orb_toothbrush",
        status: .warning,
        heroProgress: 0.90,
        lastReplacementDateText: "5월 1일",
        nextReplacementDateText: "5월 21일",
        replacementDayBadgeText: "D+2",
        stockCount: 0,
        averageReplacementDaysText: "34일",
        recommendedReplacementDaysText: "30일",
        currentUsageDaysText: "32일째",
        currentStatusBadgeText: "D+2",
        replacementHistory: [
            ItemDetailReplacementHistoryEntry(id: "history-1", daysText: "31일", dateText: "01/01", ratio: 0.90),
            ItemDetailReplacementHistoryEntry(id: "history-2", daysText: "29일", dateText: "01/31", ratio: 0.70),
            ItemDetailReplacementHistoryEntry(id: "history-3", daysText: "34일", dateText: "03/03", ratio: 0.78),
            ItemDetailReplacementHistoryEntry(id: "history-4", daysText: "37일", dateText: "04/10", ratio: 0.92),
            ItemDetailReplacementHistoryEntry(id: "history-current", daysText: "32일", dateText: "현재", ratio: 1, isCurrent: true)
        ],
        replacementHistoryAverageText: "33.8일"
    )
}
