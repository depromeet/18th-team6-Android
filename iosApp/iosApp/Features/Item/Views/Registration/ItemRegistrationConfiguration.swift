import SwiftUI

enum ItemRegistrationLayout {
    static let fieldHeight = OBRitSpacing.s14
    static let itemThumbnailSize: CGFloat = 52
    static let imageOptionSize: CGFloat = 60
    static let imageGridSpacing: CGFloat = 18
    static let imageGridColumnCount = 5
    static let kindSheetMaxContentHeight: CGFloat = 766
    static let kindNoResultHeight: CGFloat = 278
    static let sheetBottomButtonReservedHeight: CGFloat = 76
    static let bottomSheetDimOpacity: CGFloat = 0.8
    static let bottomSheetAnimationDuration: Double = 0.24
    static let bottomSheetMaximumSafeAreaPadding = OBRitSpacing.s12
    static let topBarHeight = OBRitSpacing.s14
    static let bottomSheetTopAreaHeight: CGFloat = 85
    static let minimumVisibleHeight: CGFloat = 0
    static let zeroSpacing: CGFloat = 0
    static let spacerMinimumLength: CGFloat = 0
    static let requiredMarkerTopPadding: CGFloat = 1
    static let dropdownMenuZIndex: Double = 1
    static let expandedControlZIndex: Double = 10
    static let defaultZIndex: Double = 0
    static let singleLineLimit = 1
    static let searchFieldBorderWidth: CGFloat = 1.4
    static let selectedImageBorderWidth: CGFloat = 2
    static let secondaryTextOpacity: CGFloat = 0.64
    static let radioBorderWidth: CGFloat = 1.5
    static let radioOuterSize: CGFloat = 21
    static let transparentOpacity: CGFloat = 0
    static let completeBadgeWidth: CGFloat = 245
    static let completeBadgeHeight: CGFloat = 255
    static let completeContentBottomPaddingRatio: CGFloat = 0.07
    static let designWidth: CGFloat = 412

    static var imageGridColumns: [GridItem] {
        Array(
            repeating: GridItem(.fixed(imageOptionSize), spacing: imageGridSpacing, alignment: .leading),
            count: imageGridColumnCount
        )
    }

    static var bottomSheetHeaderHeight: CGFloat {
        OBRitSpacing.s1 + OBRitSpacing.s8 + OBRitSpacing.s2_5
    }

    static var bottomSheetTopPadding: CGFloat {
        bottomSheetTopAreaHeight - topBarHeight
    }

    static func horizontalPadding(for width: CGFloat) -> CGFloat {
        let horizontalInsetRatio = OBRitSpacing.s5 / designWidth
        return max(OBRitSpacing.s4, min(OBRitSpacing.s7, width * horizontalInsetRatio))
    }
}

enum ItemRegistrationConfig {
    static let itemNameMaxLength = 15
    static let kindNameMaxLength = 15
    static let quantityMinimum = 1
    static let defaultQuantity = 1
    static let quantityMaximum = 99
    static let emptyIDBase = 0
    static let nextIDIncrement = 1
    static let newKindInitialAddedCount = 0
    static let newKindInsertionIndex = 0
}

enum ItemRegistrationText {
    static let space = " "
    static let countUnit = "개"

    static func maxLengthHelper(_ maxLength: Int) -> String {
        "\(maxLength)자 이내로 입력해주세요"
    }

    static func characterCount(current: Int, maximum: Int) -> String {
        "\(min(current, maximum))/\(maximum)"
    }

    static func quantityText(prefix: String, quantity: Int) -> String {
        "\(prefix) \(quantity)\(countUnit)"
    }

    static func addedCountText(_ count: Int) -> String {
        "추가된 소모품 \(count)\(countUnit)"
    }

    static func countText(_ count: Int) -> String {
        "\(count)\(countUnit)"
    }
}

enum ItemRegistrationAsset {
    static let searchSymbol = "magnifyingglass"
    static let fallbackItemImage = "item_razor"
    static let completeBadge = "item_registration_complete_badge"
}

#if DEBUG
enum ItemRegistrationDebugConfig {
    static let stateEnvironmentKey = "OBRIT_MANUAL_REGISTRATION_STATE"
    static let filledState = "filled"
    static let kindSheetState = "kindSheet"
    static let dateSheetState = "dateSheet"
    static let directKindState = "directKind"
    static let completeState = "complete"
    static let fallbackItemName = "면도기"
}
#endif
