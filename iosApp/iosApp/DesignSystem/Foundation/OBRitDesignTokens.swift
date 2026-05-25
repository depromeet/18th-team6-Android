import Shared
import SwiftUI

public enum OBRitColors {
    public static let common00 = color(AtomColors.Common.shared.S00)
    public static let common1000 = color(AtomColors.Common.shared.S1000)
    public static let commonBlack00_80 = color(AtomColors.CommonOpacityCommonBlack.shared.S00_80)
    public static let commonBlack00_60 = color(AtomColors.CommonOpacityCommonBlack.shared.S00_60)
    public static let commonBlack00_40 = color(AtomColors.CommonOpacityCommonBlack.shared.S00_40)
    public static let commonBlack00_20 = color(AtomColors.CommonOpacityCommonBlack.shared.S00_20)
    public static let commonWhite00_80 = color(AtomColors.CommonOpacityCommonWhite.shared.S00_80)
    public static let commonWhite00_60 = color(AtomColors.CommonOpacityCommonWhite.shared.S00_60)
    public static let commonWhite00_40 = color(AtomColors.CommonOpacityCommonWhite.shared.S00_40)
    public static let commonWhite00_20 = color(AtomColors.CommonOpacityCommonWhite.shared.S00_20)
    public static let backgroundDefaultDimDefault = color(SemanticColors.BackgroundDefault.shared.DimDefault)
    public static let gray50 = color(AtomColors.Gray.shared.S50)
    public static let gray100 = color(AtomColors.Gray.shared.S100)
    public static let gray150 = color(AtomColors.Gray.shared.S150)
    public static let gray200 = color(AtomColors.Gray.shared.S200)
    public static let gray250 = color(AtomColors.Gray.shared.S250)
    public static let gray300 = color(AtomColors.Gray.shared.S300)
    public static let gray400 = color(AtomColors.Gray.shared.S400)
    public static let gray450 = color(AtomColors.Gray.shared.S450)
    public static let gray500 = color(AtomColors.Gray.shared.S500)
    public static let gray600 = color(AtomColors.Gray.shared.S600)
    public static let gray700 = color(AtomColors.Gray.shared.S700)
    public static let gray750 = color(AtomColors.Gray.shared.S750)
    public static let gray800 = color(AtomColors.Gray.shared.S800)
    public static let gray850 = color(AtomColors.Gray.shared.S850)
    public static let gray900 = color(AtomColors.Gray.shared.S900)
    public static let grayOpacity700 = color(AtomColors.GrayOpacity.shared.S700)
    public static let grayOpacity750 = color(AtomColors.GrayOpacity.shared.S750)
    public static let grayOpacity800 = color(AtomColors.GrayOpacity.shared.S800)
    public static let grayOpacity850 = color(AtomColors.GrayOpacity.shared.S850)
    public static let grayOpacity900 = color(AtomColors.GrayOpacity.shared.S900)
    public static let green50 = color(AtomColors.Green.shared.S50)
    public static let green100 = color(AtomColors.Green.shared.S100)
    public static let green150 = color(AtomColors.Green.shared.S150)
    public static let green200 = color(AtomColors.Green.shared.S200)
    public static let green250 = color(AtomColors.Green.shared.S250)
    public static let red100 = color(AtomColors.Red.shared.S100)
    public static let red250 = color(AtomColors.Red.shared.S250)
    public static let green300 = color(AtomColors.Green.shared.S300)
    public static let green400 = color(AtomColors.Green.shared.S400)
    public static let green450 = color(AtomColors.Green.shared.S450)
    public static let green500 = color(AtomColors.Green.shared.S500)
    public static let green600 = color(AtomColors.Green.shared.S600)
    public static let green700 = color(AtomColors.Green.shared.S700)
    public static let green750 = color(AtomColors.Green.shared.S750)
    public static let green800 = color(AtomColors.Green.shared.S800)
    public static let green850 = color(AtomColors.Green.shared.S850)
    public static let green900 = color(AtomColors.Green.shared.S900)
    public static let red50 = color(AtomColors.Red.shared.S50)
    public static let red150 = color(AtomColors.Red.shared.S150)
    public static let red200 = color(AtomColors.Red.shared.S200)
    public static let red300 = color(AtomColors.Red.shared.S300)
    public static let red400 = color(AtomColors.Red.shared.S400)
    public static let red450 = color(AtomColors.Red.shared.S450)
    public static let red500 = color(AtomColors.Red.shared.S500)
    public static let red600 = color(AtomColors.Red.shared.S600)
    public static let red700 = color(AtomColors.Red.shared.S700)
    public static let red750 = color(AtomColors.Red.shared.S750)
    public static let red800 = color(AtomColors.Red.shared.S800)
    public static let red850 = color(AtomColors.Red.shared.S850)
    public static let red900 = color(AtomColors.Red.shared.S900)
    public static let yellow50 = color(AtomColors.Yellow.shared.S50)
    public static let yellow100 = color(AtomColors.Yellow.shared.S100)
    public static let yellow150 = color(AtomColors.Yellow.shared.S150)
    public static let yellow200 = color(AtomColors.Yellow.shared.S200)
    public static let yellow250 = color(AtomColors.Yellow.shared.S250)
    public static let yellow300 = color(AtomColors.Yellow.shared.S300)
    public static let yellow400 = color(AtomColors.Yellow.shared.S400)
    public static let yellow450 = color(AtomColors.Yellow.shared.S450)
    public static let yellow500 = color(AtomColors.Yellow.shared.S500)
    public static let yellow600 = color(AtomColors.Yellow.shared.S600)
    public static let yellow700 = color(AtomColors.Yellow.shared.S700)
    public static let yellow750 = color(AtomColors.Yellow.shared.S750)
    public static let yellow800 = color(AtomColors.Yellow.shared.S800)
    public static let yellow850 = color(AtomColors.Yellow.shared.S850)
    public static let yellow900 = color(AtomColors.Yellow.shared.S900)
    public static let blue50 = color(AtomColors.Blue.shared.S50)
    public static let blue100 = color(AtomColors.Blue.shared.S100)
    public static let blue150 = color(AtomColors.Blue.shared.S150)
    public static let blue200 = color(AtomColors.Blue.shared.S200)
    public static let blue250 = color(AtomColors.Blue.shared.S250)
    public static let blue300 = color(AtomColors.Blue.shared.S300)
    public static let blue400 = color(AtomColors.Blue.shared.S400)
    public static let blue450 = color(AtomColors.Blue.shared.S450)
    public static let blue500 = color(AtomColors.Blue.shared.S500)
    public static let blue600 = color(AtomColors.Blue.shared.S600)
    public static let blue700 = color(AtomColors.Blue.shared.S700)
    public static let blue750 = color(AtomColors.Blue.shared.S750)
    public static let blue800 = color(AtomColors.Blue.shared.S800)
    public static let blue850 = color(AtomColors.Blue.shared.S850)
    public static let blue900 = color(AtomColors.Blue.shared.S900)
    public static let backgroundDefaultDefault = color(SemanticColors.BackgroundDefault.shared.Default)
    public static let backgroundDefaultDefaultHover = color(SemanticColors.BackgroundDefault.shared.DefaultHover)
    public static let backgroundDefaultSecondary = color(SemanticColors.BackgroundDefault.shared.Secondary)
    public static let backgroundDefaultSecondaryHover = color(SemanticColors.BackgroundDefault.shared.SecondaryHover)
    public static let backgroundDefaultTertiary = color(SemanticColors.BackgroundDefault.shared.Tertiary)
    public static let backgroundDefaultLightGrayDefault = color(SemanticColors.BackgroundDefault.shared.LightGrayDefault)
    public static let backgroundDefaultLightGraySecondary = color(SemanticColors.BackgroundDefault.shared.LightGraySecondary)
    public static let backgroundDefaultLightGrayTertiary = color(SemanticColors.BackgroundDefault.shared.LightGrayTertiary)
    public static let backgroundDefaultMiddleGrayDefault = color(SemanticColors.BackgroundDefault.shared.MiddleGrayDefault)
    public static let backgroundDefaultMiddleGraySecondary = color(SemanticColors.BackgroundDefault.shared.MiddleGraySecondary)
    public static let backgroundDefaultMiddleGrayTertiary = color(SemanticColors.BackgroundDefault.shared.MiddleGrayTertiary)
    public static let backgroundDefaultDimSecondary = color(SemanticColors.BackgroundDefault.shared.DimSecondary)
    public static let backgroundDefaultDimTertiary = color(SemanticColors.BackgroundDefault.shared.DimTertiary)
    public static let backgroundWarningDefault = color(SemanticColors.BackgroundWarning.shared.Default)
    public static let backgroundWarningHover = color(SemanticColors.BackgroundWarning.shared.Hover)
    public static let backgroundWarningSecondary = color(SemanticColors.BackgroundWarning.shared.Secondary)
    public static let backgroundWarningSecondaryHover = color(SemanticColors.BackgroundWarning.shared.SecondaryHover)
    public static let backgroundPositiveDefault = color(SemanticColors.BackgroundPositive.shared.Default)
    public static let backgroundPositiveHover = color(SemanticColors.BackgroundPositive.shared.Hover)
    public static let backgroundPositiveSecondary = color(SemanticColors.BackgroundPositive.shared.Secondary)
    public static let backgroundPositiveSecondaryHover = color(SemanticColors.BackgroundPositive.shared.SecondaryHover)
    public static let backgroundDisabledDefault = color(SemanticColors.BackgroundDisabled.shared.Default)
    public static let textDefaultDefault = color(SemanticColors.TextDefault.shared.Default)
    public static let textDefaultSecondary = color(SemanticColors.TextDefault.shared.Secondary)
    public static let textDefaultTertiary = color(SemanticColors.TextDefault.shared.Tertiary)
    public static let textDefaultDarkDefault = color(SemanticColors.TextDefault.shared.DarkDefault)
    public static let textDefaultDarkSecondary = color(SemanticColors.TextDefault.shared.DarkSecondary)
    public static let textDefaultDarkTertiary = color(SemanticColors.TextDefault.shared.DarkTertiary)
    public static let textWarningDefault = color(SemanticColors.TextWarning.shared.Default)
    public static let textWarningSecondary = color(SemanticColors.TextWarning.shared.Secondary)
    public static let textWarningTertiary = color(SemanticColors.TextWarning.shared.Tertiary)
    public static let textPositiveDefault = color(SemanticColors.TextPositive.shared.Default)
    public static let textPositiveSecondary = color(SemanticColors.TextPositive.shared.Secondary)
    public static let textPositiveTertiary = color(SemanticColors.TextPositive.shared.Tertiary)
    public static let textDisabledDefault = color(SemanticColors.TextDisabled.shared.Default)
    public static let textDisabledOnDisabled = color(SemanticColors.TextDisabled.shared.OnDisabled)
    public static let borderDefaultDefault = color(SemanticColors.BorderDefault.shared.Default)
    public static let borderDefaultSecondary = color(SemanticColors.BorderDefault.shared.Secondary)
    public static let borderDefaultTertiary = color(SemanticColors.BorderDefault.shared.Tertiary)
    public static let borderWarningDefault = color(SemanticColors.BorderWarning.shared.Default)
    public static let borderWarningSecondary = color(SemanticColors.BorderWarning.shared.Secondary)
    public static let borderWarningTertiary = color(SemanticColors.BorderWarning.shared.Tertiary)
    public static let borderPositiveDefault = color(SemanticColors.BorderPositive.shared.Default)
    public static let borderPositiveSecondary = color(SemanticColors.BorderPositive.shared.Secondary)
    public static let borderPositiveTertiary = color(SemanticColors.BorderPositive.shared.Tertiary)
    public static let borderDisabledDefault = color(SemanticColors.BorderDisabled.shared.Default)
    public static let iconDefaultDefault = color(SemanticColors.IconDefault.shared.Default)
    public static let iconDefaultSecondary = color(SemanticColors.IconDefault.shared.Secondary)
    public static let iconDefaultTertiary = color(SemanticColors.IconDefault.shared.Tertiary)
    public static let iconWarningDefault = color(SemanticColors.IconWarning.shared.Default)
    public static let iconWarningSecondary = color(SemanticColors.IconWarning.shared.Secondary)
    public static let iconWarningTertiary = color(SemanticColors.IconWarning.shared.Tertiary)
    public static let iconPositiveDefault = color(SemanticColors.IconPositive.shared.Default)
    public static let iconPositiveSecondary = color(SemanticColors.IconPositive.shared.Secondary)
    public static let iconPositiveTertiary = color(SemanticColors.IconPositive.shared.Tertiary)
    public static let iconDisabledDefault = color(SemanticColors.IconDisabled.shared.Default)
    public static let iconDisabledOnDisabled = color(SemanticColors.IconDisabled.shared.OnDisabled)
    public static let surfaceDefaultDefaultDark = color(SemanticColors.SurfaceDefault.shared.DefaultDark)
    public static let surfaceDefaultDefaultLight = color(SemanticColors.SurfaceDefault.shared.DefaultLight)
    public static let surfaceDefaultDefaultHoverLight = color(SemanticColors.SurfaceDefault.shared.DefaultHoverLight)

    public static func color(_ argb: Int64) -> Color {
        let value = UInt64(bitPattern: argb)
        let alpha = Double((value >> 24) & 0xFF) / 255.0
        let red = Double((value >> 16) & 0xFF) / 255.0
        let green = Double((value >> 8) & 0xFF) / 255.0
        let blue = Double(value & 0xFF) / 255.0
        return Color(.sRGB, red: red, green: green, blue: blue, opacity: alpha)
    }
}

public enum OBRitSpacing {
    public static let s0: CGFloat = 0
    public static let px: CGFloat = 1
    public static let s0_5: CGFloat = 2
    public static let s1: CGFloat = 4
    public static let s1_5: CGFloat = 6
    public static let s2: CGFloat = 8
    public static let s2_5: CGFloat = 10
    public static let s3: CGFloat = 12
    public static let s4: CGFloat = 16
    public static let s5: CGFloat = 20
    public static let s6: CGFloat = 24
    public static let s7: CGFloat = 28
    public static let s8: CGFloat = 32
    public static let s9: CGFloat = 36
    public static let s10: CGFloat = 40
    public static let s11: CGFloat = 44
    public static let s12: CGFloat = 48
    public static let s14: CGFloat = 56
    public static let s16: CGFloat = 64
    public static let s20: CGFloat = 80
    public static let s24: CGFloat = 96
    public static let s28: CGFloat = 112
    public static let s32: CGFloat = 128
    public static let s36: CGFloat = 144
    public static let s40: CGFloat = 160
}

public enum OBRitRadius {
    public static let extraSmall: CGFloat = 4
    public static let small: CGFloat = 8
    public static let middle: CGFloat = 10
    public static let large: CGFloat = 12
    public static let extraLarge: CGFloat = 16
    public static let bottomSheet: CGFloat = 28
    public static let exception: CGFloat = 1000
}

public enum OBRitFontFamily {
    public static let fontSans = "Pretendard"
}

public enum OBRitFontWeight {
    public static let regular: Int32 = 400
    public static let medium: Int32 = 500
    public static let semiBold: Int32 = 600
    public static let bold: Int32 = 700
}

public enum OBRitTypography {
    public struct TextToken {
        public let size: CGFloat
        public let lineHeight: CGFloat
    }

    private static let letterSpacingPercent: CGFloat = -3

    public static let s2xs = TextToken(size: 10, lineHeight: 16)
    public static let xs = TextToken(size: 12, lineHeight: 18)
    public static let small = TextToken(size: 13, lineHeight: 20)
    public static let base = TextToken(size: 14, lineHeight: 22)
    public static let lg = TextToken(size: 15, lineHeight: 24)
    public static let xl = TextToken(size: 16, lineHeight: 24)
    public static let s2xl = TextToken(size: 18, lineHeight: 24)
    public static let s3xl = TextToken(size: 20, lineHeight: 30)
    public static let s4xl = TextToken(size: 22, lineHeight: 32)
    public static let s5xl = TextToken(size: 24, lineHeight: 36)
    public static let s6xl = TextToken(size: 28, lineHeight: 40)
    public static let s7xl = TextToken(size: 32, lineHeight: 44)
    public static let s = small

    public static func font(_ token: TextToken, weight: Int32 = OBRitFontWeight.regular) -> Font {
        Font.custom(fontName(for: weight), size: token.size)
    }

    public static func fontName(for weight: Int32) -> String {
        switch weight {
        case OBRitFontWeight.bold:
            return "Pretendard-Bold"
        case OBRitFontWeight.semiBold:
            return "Pretendard-SemiBold"
        case OBRitFontWeight.medium:
            return "Pretendard-Medium"
        default:
            return "Pretendard-Regular"
        }
    }

    public static func letterSpacing(for token: TextToken) -> CGFloat {
        token.size * letterSpacingPercent / 100
    }
}

public enum OBRitEffects {
    public static let backgroundBlur0: CGFloat = 0
    public static let backgroundBlur4: CGFloat = 4
    public static let backgroundBlur8: CGFloat = 8
    public static let backgroundBlur12: CGFloat = 12
    public static let backgroundBlur16: CGFloat = 16
}

public struct OBRitTextStyle: ViewModifier {
    let token: OBRitTypography.TextToken
    let weight: Int32
    let color: Color

    public func body(content: Content) -> some View {
        content
            .font(OBRitTypography.font(token, weight: weight))
            .tracking(OBRitTypography.letterSpacing(for: token))
            .lineSpacing(max(0, token.lineHeight - token.size))
            .foregroundStyle(color)
    }
}

public extension View {
    func obritTextStyle(
        _ token: OBRitTypography.TextToken,
        weight: Int32 = OBRitFontWeight.regular,
        color: Color
    ) -> some View {
        modifier(OBRitTextStyle(token: token, weight: weight, color: color))
    }
}
