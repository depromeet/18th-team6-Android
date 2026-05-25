package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateIosDesignTokensTask : DefaultTask() {
    @get:InputDirectory
    abstract val tokenRoot: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val root = tokenRoot.asFile.get()
        val atomColorFile = root.resolve("atom/color/AtomColors.kt")
        val semanticColorFile = root.resolve("semantic/SemanticColors.kt")
        val spacingFile = root.resolve("atom/spacing/AtomSpacing.kt")
        val radiusFile = root.resolve("atom/radius/AtomRadius.kt")
        val typographyFile = root.resolve("atom/typography/AtomTypography.kt")
        val textFile = root.resolve("atom/typography/AtomText.kt")
        val effectsFile = root.resolve("atom/effect/AtomEffects.kt")

        val atomColors = parseObjectConstants(atomColorFile)
        val semanticColorRefs = parseObjectConstants(semanticColorFile)
        val spacing = parseObjectConstants(spacingFile)
        val radius = parseObjectConstants(radiusFile)
        val typography = parseObjectConstants(typographyFile, dropRootObject = false)
        val effects = parseObjectConstants(effectsFile)
        val rawTextTokens = parseObjectConstants(textFile)
        val letterSpacing = rawTextTokens.getValue("LetterSpacing")

        fun atomColor(path: String): String = atomColors.getValue(path)
        fun semanticColor(path: String): String = resolveColor(semanticColorRefs.getValue(path), atomColors)
        fun number(value: String): String = value.removeSuffix("f")

        val colorTokens = listOf(
            "common00" to atomColor("Common.S00"),
            "common100" to atomColor("Common.S1000"),
            "commonBlack00_80" to atomColor("CommonOpacity.CommonBlack.S00_80"),
            "commonBlack00_60" to atomColor("CommonOpacity.CommonBlack.S00_60"),
            "commonBlack00_40" to atomColor("CommonOpacity.CommonBlack.S00_40"),
            "commonBlack00_20" to atomColor("CommonOpacity.CommonBlack.S00_20"),
            "commonWhite00_80" to atomColor("CommonOpacity.CommonWhite.S00_80"),
            "commonWhite00_60" to atomColor("CommonOpacity.CommonWhite.S00_60"),
            "commonWhite00_40" to atomColor("CommonOpacity.CommonWhite.S00_40"),
            "commonWhite00_20" to atomColor("CommonOpacity.CommonWhite.S00_20"),
            "backgroundDefaultDimDefault" to semanticColor("Background.Default.DimDefault"),
            "gray50" to atomColor("Gray.S50"),
            "gray100" to atomColor("Gray.S100"),
            "gray150" to atomColor("Gray.S150"),
            "gray200" to atomColor("Gray.S200"),
            "gray250" to atomColor("Gray.S250"),
            "gray300" to atomColor("Gray.S300"),
            "gray400" to atomColor("Gray.S400"),
            "gray450" to atomColor("Gray.S450"),
            "gray500" to atomColor("Gray.S500"),
            "gray600" to atomColor("Gray.S600"),
            "gray700" to atomColor("Gray.S700"),
            "gray750" to atomColor("Gray.S750"),
            "gray800" to atomColor("Gray.S800"),
            "gray850" to atomColor("Gray.S850"),
            "gray900" to atomColor("Gray.S900"),
            "grayOpacity700" to atomColor("GrayOpacity.S700"),
            "grayOpacity750" to atomColor("GrayOpacity.S750"),
            "grayOpacity800" to atomColor("GrayOpacity.S800"),
            "grayOpacity850" to atomColor("GrayOpacity.S850"),
            "grayOpacity900" to atomColor("GrayOpacity.S900"),
            "green50" to atomColor("Green.S50"),
            "green100" to atomColor("Green.S100"),
            "green150" to atomColor("Green.S150"),
            "green200" to atomColor("Green.S200"),
            "green250" to atomColor("Green.S250"),
            "red100" to atomColor("Red.S100"),
            "red250" to atomColor("Red.S250"),
            "green300" to atomColor("Green.S300"),
            "green400" to atomColor("Green.S400"),
            "green450" to atomColor("Green.S450"),
            "green500" to atomColor("Green.S500"),
            "green600" to atomColor("Green.S600"),
            "green700" to atomColor("Green.S700"),
            "green750" to atomColor("Green.S750"),
            "green800" to atomColor("Green.S800"),
            "green850" to atomColor("Green.S850"),
            "green900" to atomColor("Green.S900"),
            "red50" to atomColor("Red.S50"),
            "red150" to atomColor("Red.S150"),
            "red200" to atomColor("Red.S200"),
            "red300" to atomColor("Red.S300"),
            "red400" to atomColor("Red.S400"),
            "red450" to atomColor("Red.S450"),
            "red500" to atomColor("Red.S500"),
            "red600" to atomColor("Red.S600"),
            "red700" to atomColor("Red.S700"),
            "red750" to atomColor("Red.S750"),
            "red800" to atomColor("Red.S800"),
            "red850" to atomColor("Red.S850"),
            "red900" to atomColor("Red.S900"),
            "yellow50" to atomColor("Yellow.S50"),
            "yellow100" to atomColor("Yellow.S100"),
            "yellow150" to atomColor("Yellow.S150"),
            "yellow200" to atomColor("Yellow.S200"),
            "yellow250" to atomColor("Yellow.S250"),
            "yellow300" to atomColor("Yellow.S300"),
            "yellow400" to atomColor("Yellow.S400"),
            "yellow450" to atomColor("Yellow.S450"),
            "yellow500" to atomColor("Yellow.S500"),
            "yellow600" to atomColor("Yellow.S600"),
            "yellow700" to atomColor("Yellow.S700"),
            "yellow750" to atomColor("Yellow.S750"),
            "yellow800" to atomColor("Yellow.S800"),
            "yellow850" to atomColor("Yellow.S850"),
            "yellow900" to atomColor("Yellow.S900"),
            "blue50" to atomColor("Blue.S50"),
            "blue100" to atomColor("Blue.S100"),
            "blue150" to atomColor("Blue.S150"),
            "blue200" to atomColor("Blue.S200"),
            "blue250" to atomColor("Blue.S250"),
            "blue300" to atomColor("Blue.S300"),
            "blue400" to atomColor("Blue.S400"),
            "blue450" to atomColor("Blue.S450"),
            "blue500" to atomColor("Blue.S500"),
            "blue600" to atomColor("Blue.S600"),
            "blue700" to atomColor("Blue.S700"),
            "blue750" to atomColor("Blue.S750"),
            "blue800" to atomColor("Blue.S800"),
            "blue850" to atomColor("Blue.S850"),
            "blue900" to atomColor("Blue.S900"),
            "backgroundDefaultDefault" to semanticColor("Background.Default.Default"),
            "backgroundDefaultDefaultHover" to semanticColor("Background.Default.DefaultHover"),
            "backgroundDefaultSecondary" to semanticColor("Background.Default.Secondary"),
            "backgroundDefaultSecondaryHover" to semanticColor("Background.Default.SecondaryHover"),
            "backgroundDefaultTertiary" to semanticColor("Background.Default.Tertiary"),
            "backgroundDefaultLightGrayDefault" to semanticColor("Background.Default.LightGrayDefault"),
            "backgroundDefaultLightGraySecondary" to semanticColor("Background.Default.LightGraySecondary"),
            "backgroundDefaultLightGrayTertiary" to semanticColor("Background.Default.LightGrayTertiary"),
            "backgroundDefaultMiddleGrayDefault" to semanticColor("Background.Default.MiddleGrayDefault"),
            "backgroundDefaultMiddleGraySecondary" to semanticColor("Background.Default.MiddleGraySecondary"),
            "backgroundDefaultMiddleGrayTertiary" to semanticColor("Background.Default.MiddleGrayTertiary"),
            "backgroundDefaultDimSecondary" to semanticColor("Background.Default.DimSecondary"),
            "backgroundDefaultDimTertiary" to semanticColor("Background.Default.DimTertiary"),
            "backgroundWarningDefault" to semanticColor("Background.Warning.Default"),
            "backgroundWarningHover" to semanticColor("Background.Warning.Hover"),
            "backgroundWarningSecondary" to semanticColor("Background.Warning.Secondary"),
            "backgroundWarningSecondaryHover" to semanticColor("Background.Warning.SecondaryHover"),
            "backgroundPositiveDefault" to semanticColor("Background.Positive.Default"),
            "backgroundPositiveHover" to semanticColor("Background.Positive.Hover"),
            "backgroundPositiveSecondary" to semanticColor("Background.Positive.Secondary"),
            "backgroundPositiveSecondaryHover" to semanticColor("Background.Positive.SecondaryHover"),
            "backgroundDisabledDefault" to semanticColor("Background.Disabled.Default"),
            "textDefaultDefault" to semanticColor("Text.Default.Default"),
            "textDefaultSecondary" to semanticColor("Text.Default.Secondary"),
            "textDefaultTertiary" to semanticColor("Text.Default.Tertiary"),
            "textDefaultDarkDefault" to semanticColor("Text.Default.DarkDefault"),
            "textDefaultDarkSecondary" to semanticColor("Text.Default.DarkSecondary"),
            "textDefaultDarkTertiary" to semanticColor("Text.Default.DarkTertiary"),
            "textWarningDefault" to semanticColor("Text.Warning.Default"),
            "textWarningSecondary" to semanticColor("Text.Warning.Secondary"),
            "textWarningTertiary" to semanticColor("Text.Warning.Tertiary"),
            "textPositiveDefault" to semanticColor("Text.Positive.Default"),
            "textPositiveSecondary" to semanticColor("Text.Positive.Secondary"),
            "textPositiveTertiary" to semanticColor("Text.Positive.Tertiary"),
            "textDisabledDefault" to semanticColor("Text.Disabled.Default"),
            "textDisabledOnDisabled" to semanticColor("Text.Disabled.OnDisabled"),
            "borderDefaultDefault" to semanticColor("Border.Default.Default"),
            "borderDefaultSecondary" to semanticColor("Border.Default.Secondary"),
            "borderDefaultTertiary" to semanticColor("Border.Default.Tertiary"),
            "borderWarningDefault" to semanticColor("Border.Warning.Default"),
            "borderWarningSecondary" to semanticColor("Border.Warning.Secondary"),
            "borderWarningTertiary" to semanticColor("Border.Warning.Tertiary"),
            "borderPositiveDefault" to semanticColor("Border.Positive.Default"),
            "borderPositiveSecondary" to semanticColor("Border.Positive.Secondary"),
            "borderPositiveTertiary" to semanticColor("Border.Positive.Tertiary"),
            "borderDisabledDefault" to semanticColor("Border.Disabled.Default"),
            "iconDefaultDefault" to semanticColor("Icon.Default.Default"),
            "iconDefaultSecondary" to semanticColor("Icon.Default.Secondary"),
            "iconDefaultTertiary" to semanticColor("Icon.Default.Tertiary"),
            "iconWarningDefault" to semanticColor("Icon.Warning.Default"),
            "iconWarningSecondary" to semanticColor("Icon.Warning.Secondary"),
            "iconWarningTertiary" to semanticColor("Icon.Warning.Tertiary"),
            "iconPositiveDefault" to semanticColor("Icon.Positive.Default"),
            "iconPositiveSecondary" to semanticColor("Icon.Positive.Secondary"),
            "iconPositiveTertiary" to semanticColor("Icon.Positive.Tertiary"),
            "iconDisabledDefault" to semanticColor("Icon.Disabled.Default"),
            "iconDisabledOnDisabled" to semanticColor("Icon.Disabled.OnDisabled"),
            "surfaceDefaultDefaultDark" to semanticColor("Surface.Default.DefaultDark"),
            "surfaceDefaultDefaultLight" to semanticColor("Surface.Default.DefaultLight"),
            "surfaceDefaultDefaultHoverLight" to semanticColor("Surface.Default.DefaultHoverLight"),
        )

        val spacingTokens = listOf(
            "s0" to spacing.getValue("S0"),
            "px" to spacing.getValue("Px"),
            "s0_5" to spacing.getValue("S0_5"),
            "s1" to spacing.getValue("S1"),
            "s1_5" to spacing.getValue("S1_5"),
            "s2" to spacing.getValue("S2"),
            "s2_5" to spacing.getValue("S2_5"),
            "s3" to spacing.getValue("S3"),
            "s4" to spacing.getValue("S4"),
            "s5" to spacing.getValue("S5"),
            "s6" to spacing.getValue("S6"),
            "s7" to spacing.getValue("S7"),
            "s8" to spacing.getValue("S8"),
            "s9" to spacing.getValue("S9"),
            "s10" to spacing.getValue("S10"),
            "s11" to spacing.getValue("S11"),
            "s12" to spacing.getValue("S12"),
            "s14" to spacing.getValue("S14"),
            "s16" to spacing.getValue("S16"),
            "s20" to spacing.getValue("S20"),
            "s24" to spacing.getValue("S24"),
            "s28" to spacing.getValue("S28"),
            "s32" to spacing.getValue("S32"),
            "s36" to spacing.getValue("S36"),
            "s40" to spacing.getValue("S40"),
        )

        val radiusTokens = listOf(
            "extraSmall" to radius.getValue("ExtraSmall"),
            "small" to radius.getValue("Small"),
            "middle" to radius.getValue("Middle"),
            "large" to radius.getValue("Large"),
            "extraLarge" to radius.getValue("ExtraLarge"),
            "bottomSheet" to radius.getValue("BottomSheet"),
            "exception" to radius.getValue("Exception"),
        )

        val fontWeights = listOf(
            "regular" to typography.getValue("AtomFontWeight.Regular"),
            "medium" to typography.getValue("AtomFontWeight.Medium"),
            "semiBold" to typography.getValue("AtomFontWeight.SemiBold"),
            "bold" to typography.getValue("AtomFontWeight.Bold"),
        )

        val textTokenNames = listOf(
            "s2xs" to "S2xs",
            "xs" to "Xs",
            "small" to "S",
            "base" to "Base",
            "lg" to "Lg",
            "xl" to "Xl",
            "s2xl" to "S2xl",
            "s3xl" to "S3xl",
            "s4xl" to "S4xl",
            "s5xl" to "S5xl",
            "s6xl" to "S6xl",
            "s7xl" to "S7xl",
        )
        val textTokens = textTokenNames.associate { (_, kotlinName) ->
            kotlinName to (
                rawTextTokens.getValue("$kotlinName.FontSize") to
                    rawTextTokens.getValue("$kotlinName.LineHeight")
                )
        }

        val effectTokens = listOf(
            "backgroundBlur0" to effects.getValue("BackgroundBlur.Blur0"),
            "backgroundBlur4" to effects.getValue("BackgroundBlur.Blur4"),
            "backgroundBlur8" to effects.getValue("BackgroundBlur.Blur8"),
            "backgroundBlur12" to effects.getValue("BackgroundBlur.Blur12"),
            "backgroundBlur16" to effects.getValue("BackgroundBlur.Blur16"),
        )
        val fontFamily = typography.getValue("AtomFontFamily.FontSans").trim('"')

        val generated = buildString {
            appendLine("import SwiftUI")
            appendLine()
            appendLine("// Generated by ./gradlew generateIosDesignTokens from shared/design-system tokens.")
            appendLine("// Do not edit token values directly; update the Kotlin token source instead.")
            appendLine()
            appendLine("public enum OBRitColors {")
            colorTokens.forEach { (name, value) ->
                appendLine("    public static let $name = color($value)")
            }
            appendLine()
            appendLine("    public static func color(_ argb: Int64) -> Color {")
            appendLine("        let value = UInt64(bitPattern: argb)")
            appendLine("        let alpha = Double((value >> 24) & 0xFF) / 255.0")
            appendLine("        let red = Double((value >> 16) & 0xFF) / 255.0")
            appendLine("        let green = Double((value >> 8) & 0xFF) / 255.0")
            appendLine("        let blue = Double(value & 0xFF) / 255.0")
            appendLine("        return Color(.sRGB, red: red, green: green, blue: blue, opacity: alpha)")
            appendLine("    }")
            appendLine("}")
            appendLine()
            appendLine("public enum OBRitSpacing {")
            spacingTokens.forEach { (name, value) ->
                appendLine("    public static let $name: CGFloat = ${number(value)}")
            }
            appendLine("}")
            appendLine()
            appendLine("public enum OBRitRadius {")
            radiusTokens.forEach { (name, value) ->
                appendLine("    public static let $name: CGFloat = ${number(value)}")
            }
            appendLine("}")
            appendLine()
            appendLine("public enum OBRitFontFamily {")
            appendLine("    public static let fontSans = \"$fontFamily\"")
            appendLine("}")
            appendLine()
            appendLine("public enum OBRitFontWeight {")
            fontWeights.forEach { (name, value) ->
                appendLine("    public static let $name: Int32 = ${number(value)}")
            }
            appendLine("}")
            appendLine()
            appendLine("public enum OBRitTypography {")
            appendLine("    public struct TextToken {")
            appendLine("        public let size: CGFloat")
            appendLine("        public let lineHeight: CGFloat")
            appendLine("    }")
            appendLine()
            appendLine("    private static let letterSpacingPercent: CGFloat = ${number(letterSpacing)}")
            appendLine()
            textTokenNames.forEach { (swiftName, kotlinName) ->
                val (size, lineHeight) = textTokens.getValue(kotlinName)
                appendLine("    public static let $swiftName = TextToken(size: ${number(size)}, lineHeight: ${number(lineHeight)})")
            }
            appendLine("    public static let s = small")
            appendLine()
            appendLine("    public static func font(_ token: TextToken, weight: Int32 = OBRitFontWeight.regular) -> Font {")
            appendLine("        Font.custom(fontName(for: weight), size: token.size)")
            appendLine("    }")
            appendLine()
            appendLine("    public static func fontName(for weight: Int32) -> String {")
            appendLine("        switch weight {")
            appendLine("        case OBRitFontWeight.bold:")
            appendLine("            return \"Pretendard-Bold\"")
            appendLine("        case OBRitFontWeight.semiBold:")
            appendLine("            return \"Pretendard-SemiBold\"")
            appendLine("        case OBRitFontWeight.medium:")
            appendLine("            return \"Pretendard-Medium\"")
            appendLine("        default:")
            appendLine("            return \"Pretendard-Regular\"")
            appendLine("        }")
            appendLine("    }")
            appendLine()
            appendLine("    public static func letterSpacing(for token: TextToken) -> CGFloat {")
            appendLine("        token.size * letterSpacingPercent / 100")
            appendLine("    }")
            appendLine("}")
            appendLine()
            appendLine("public enum OBRitEffects {")
            effectTokens.forEach { (name, value) ->
                appendLine("    public static let $name: CGFloat = ${number(value)}")
            }
            appendLine("}")
            appendLine()
            appendLine("public struct OBRitTextStyle: ViewModifier {")
            appendLine("    let token: OBRitTypography.TextToken")
            appendLine("    let weight: Int32")
            appendLine("    let color: Color")
            appendLine()
            appendLine("    public func body(content: Content) -> some View {")
            appendLine("        content")
            appendLine("            .font(OBRitTypography.font(token, weight: weight))")
            appendLine("            .tracking(OBRitTypography.letterSpacing(for: token))")
            appendLine("            .lineSpacing(max(0, token.lineHeight - token.size))")
            appendLine("            .foregroundStyle(color)")
            appendLine("    }")
            appendLine("}")
            appendLine()
            appendLine("public extension View {")
            appendLine("    func obritTextStyle(")
            appendLine("        _ token: OBRitTypography.TextToken,")
            appendLine("        weight: Int32 = OBRitFontWeight.regular,")
            appendLine("        color: Color")
            appendLine("    ) -> some View {")
            appendLine("        modifier(OBRitTextStyle(token: token, weight: weight, color: color))")
            appendLine("    }")
            appendLine("}")
        }

        outputFile.asFile.get().apply {
            parentFile.mkdirs()
            writeText(generated)
        }
    }

    private fun parseObjectConstants(file: File, dropRootObject: Boolean = true): Map<String, String> {
        val constants = linkedMapOf<String, String>()
        val stack = mutableListOf<String>()
        val objectRegex = Regex("""^\s*object\s+([A-Za-z0-9_]+)\s*\{""")
        val constRegex = Regex("""^\s*const\s+val\s+([A-Za-z0-9_]+)(?::\s*[A-Za-z0-9_.<>]+)?\s*=\s*([^\s]+)""")

        file.forEachLine { line ->
            objectRegex.find(line)?.let { match ->
                stack += match.groupValues[1]
            }

            constRegex.find(line)?.let { match ->
                val objectPath = if (dropRootObject) stack.drop(1) else stack
                val key = (objectPath + match.groupValues[1]).joinToString(".")
                constants[key] = match.groupValues[2]
            }

            repeat(line.count { it == '}' }) {
                if (stack.isNotEmpty()) {
                    stack.removeAt(stack.lastIndex)
                }
            }
        }

        return constants
    }

    private fun resolveColor(expression: String, atomColors: Map<String, String>): String {
        if (expression.startsWith("0x")) {
            return expression
        }

        val atomPath = expression.removePrefix("AtomColors.").removeSuffix(",")
        return atomColors.getValue(atomPath)
    }
}
