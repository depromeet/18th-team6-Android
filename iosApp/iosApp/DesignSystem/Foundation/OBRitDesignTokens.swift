import SwiftUI
import Shared

public enum OBRitColors {
    public static let common00 = color(AtomColors.Common.shared.S00)
    public static let common100 = color(AtomColors.Common.shared.S100)
    public static let gray50 = color(AtomColors.Gray.shared.S50)
    public static let gray100 = color(AtomColors.Gray.shared.S100)
    public static let gray250 = color(AtomColors.Gray.shared.S250)
    public static let gray300 = color(AtomColors.Gray.shared.S300)
    public static let gray450 = color(AtomColors.Gray.shared.S450)
    public static let gray600 = color(AtomColors.Gray.shared.S600)
    public static let gray700 = color(AtomColors.Gray.shared.S700)
    public static let gray750 = color(AtomColors.Gray.shared.S750)
    public static let gray800 = color(AtomColors.Gray.shared.S800)
    public static let gray900 = color(AtomColors.Gray.shared.S900)
    public static let green300 = color(AtomColors.Green.shared.S300)
    public static let green800 = color(AtomColors.Green.shared.S800)
    public static let red300 = color(AtomColors.Red.shared.S300)

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
    public static let px = CGFloat(AtomSpacing.shared.Px)
    public static let s1 = CGFloat(AtomSpacing.shared.S1)
    public static let s1_5 = CGFloat(AtomSpacing.shared.S1_5)
    public static let s2 = CGFloat(AtomSpacing.shared.S2)
    public static let s2_5 = CGFloat(AtomSpacing.shared.S2_5)
    public static let s3 = CGFloat(AtomSpacing.shared.S3)
    public static let s4 = CGFloat(AtomSpacing.shared.S4)
    public static let s5 = CGFloat(AtomSpacing.shared.S5)
    public static let s6 = CGFloat(AtomSpacing.shared.S6)
    public static let s14 = CGFloat(AtomSpacing.shared.S14)
}

public enum OBRitRadius {
    public static let small = CGFloat(AtomRadius.shared.Small)
    public static let middle = CGFloat(AtomRadius.shared.Middle)
    public static let large = CGFloat(AtomRadius.shared.Large)
}

public enum OBRitTypography {
    public struct TextToken {
        let size: CGFloat
        let lineHeight: CGFloat
    }

    public static let small = TextToken(size: CGFloat(AtomText.S.shared.FontSize), lineHeight: CGFloat(AtomText.S.shared.LineHeight))
    public static let base = TextToken(size: CGFloat(AtomText.Base.shared.FontSize), lineHeight: CGFloat(AtomText.Base.shared.LineHeight))
    public static let xl = TextToken(size: CGFloat(AtomText.Xl.shared.FontSize), lineHeight: CGFloat(AtomText.Xl.shared.LineHeight))

    public static func font(_ token: TextToken, weight: Int32 = AtomFontWeight.shared.Regular) -> Font {
        Font.custom(fontName(for: weight), size: token.size)
    }

    public static func fontName(for weight: Int32) -> String {
        switch weight {
        case AtomFontWeight.shared.Bold:
            return "Pretendard-Bold"
        case AtomFontWeight.shared.SemiBold:
            return "Pretendard-SemiBold"
        case AtomFontWeight.shared.Medium:
            return "Pretendard-Medium"
        default:
            return "Pretendard-Regular"
        }
    }

    public static func letterSpacing(for token: TextToken) -> CGFloat {
        token.size * CGFloat(AtomText.shared.LetterSpacing) / 100
    }
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
        weight: Int32 = AtomFontWeight.shared.Regular,
        color: Color
    ) -> some View {
        modifier(OBRitTextStyle(token: token, weight: weight, color: color))
    }
}
