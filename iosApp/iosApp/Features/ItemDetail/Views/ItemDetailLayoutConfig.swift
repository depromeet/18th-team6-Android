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
    static let heroImagePadding: CGFloat = 40
    static let dateSummaryVerticalPadding = OBRitSpacing.s2_5
    static let actionBarButtonSize = OBRitFilledButtonSize.large
    static let actionBarVerticalPadding = OBRitSpacing.s4
    static let actionBarAdditionalScrollPadding = OBRitSpacing.s4

    static var actionBarHeight: CGFloat {
        actionBarButtonSize.height + actionBarVerticalPadding * 2
    }
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

    func progressAccentColor(for progress: Double) -> Color {
        let clampedProgress = min(max(progress, 0), 1)
        let start = (red: 0.145, green: 0.937, blue: 0.804)
        let end = (red: 1.000, green: 0.349, blue: 0.133)

        return Color(
            .sRGB,
            red: start.red + (end.red - start.red) * clampedProgress,
            green: start.green + (end.green - start.green) * clampedProgress,
            blue: start.blue + (end.blue - start.blue) * clampedProgress,
            opacity: 1
        )
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
    let moreMenuItems: [ItemDetailMoreMenuItem]
    let onSelectMoreMenuItem: (ItemDetailMoreMenuItem) -> Void
    let onManageStock: () -> Void
    let onCompleteReplacement: () -> Void

    static let noop = ItemDetailViewAction(
        onBack: {},
        moreMenuItems: [.edit, .delete],
        onSelectMoreMenuItem: { _ in },
        onManageStock: {},
        onCompleteReplacement: {}
    )
}
