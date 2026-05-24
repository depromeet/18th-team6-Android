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
    case normal
    case warning

    var accentColor: Color {
        switch self {
        case .normal:
            return OBRitColors.green300
        case .warning:
            return OBRitColors.textWarningDefault
        }
    }

    var subduedAccentColor: Color {
        switch self {
        case .normal:
            return OBRitColors.green600
        case .warning:
            return OBRitColors.red600
        }
    }

    var badgeBackgroundColor: Color {
        switch self {
        case .normal:
            return OBRitColors.green800
        case .warning:
            return OBRitColors.red800
        }
    }

    var heroFillColor: Color {
        switch self {
        case .normal:
            return OBRitColors.green850
        case .warning:
            return OBRitColors.red850
        }
    }

    var heroTrackColor: Color {
        switch self {
        case .normal:
            return OBRitColors.green800
        case .warning:
            return OBRitColors.red800
        }
    }

    var chartTrackColor: Color {
        switch self {
        case .normal:
            return OBRitColors.green850
        case .warning:
            return OBRitColors.red850
        }
    }

    var chartBarColor: Color {
        switch self {
        case .normal:
            return OBRitColors.green800
        case .warning:
            return OBRitColors.red800
        }
    }

    var chartCurrentColor: Color {
        accentColor
    }

    func progressAccentColor(for progress: Double) -> Color {
        let clampedProgress = min(max(progress, 0), 1)
        let colors = progressColorComponents

        return Color(
            .sRGB,
            red: colors.start.red + (colors.end.red - colors.start.red) * clampedProgress,
            green: colors.start.green + (colors.end.green - colors.start.green) * clampedProgress,
            blue: colors.start.blue + (colors.end.blue - colors.start.blue) * clampedProgress,
            opacity: 1
        )
    }

    private var progressColorComponents: (
        start: (red: Double, green: Double, blue: Double),
        end: (red: Double, green: Double, blue: Double)
    ) {
        switch self {
        case .normal:
            return (
                start: (red: 0.145, green: 0.937, blue: 0.804),
                end: (red: 0.137, green: 0.729, blue: 0.635)
            )
        case .warning:
            return (
                start: (red: 1.000, green: 0.478, blue: 0.306),
                end: (red: 1.000, green: 0.349, blue: 0.133)
            )
        }
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
