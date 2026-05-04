package com.obrit.android.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.obrit.android.core.designsystem.R
import com.obrit.obrit.shared.designsystem.tokens.atom.typography.AtomText

val LocalOBRitTypography =
    staticCompositionLocalOf {
        OBRitTypography()
    }

@Immutable
data class OBRitTypography(
    val xs2: TextStyle =
        obritTextStyle(
            fontSize = AtomText.S2xs.FontSize,
            lineHeight = AtomText.S2xs.LineHeight,
        ),
    val xs: TextStyle =
        obritTextStyle(
            fontSize = AtomText.Xs.FontSize,
            lineHeight = AtomText.Xs.LineHeight,
        ),
    val s: TextStyle =
        obritTextStyle(
            fontSize = AtomText.S.FontSize,
            lineHeight = AtomText.S.LineHeight,
        ),
    val base: TextStyle =
        obritTextStyle(
            fontSize = AtomText.Base.FontSize,
            lineHeight = AtomText.Base.LineHeight,
        ),
    val lg: TextStyle =
        obritTextStyle(
            fontSize = AtomText.Lg.FontSize,
            lineHeight = AtomText.Lg.LineHeight,
        ),
    val xl: TextStyle =
        obritTextStyle(
            fontSize = AtomText.Xl.FontSize,
            lineHeight = AtomText.Xl.LineHeight,
        ),
    val xl2: TextStyle =
        obritTextStyle(
            fontSize = AtomText.S2xl.FontSize,
            lineHeight = AtomText.S2xl.LineHeight,
        ),
    val xl3: TextStyle =
        obritTextStyle(
            fontSize = AtomText.S3xl.FontSize,
            lineHeight = AtomText.S3xl.LineHeight,
        ),
    val xl4: TextStyle =
        obritTextStyle(
            fontSize = AtomText.S4xl.FontSize,
            lineHeight = AtomText.S4xl.LineHeight,
        ),
    val xl5: TextStyle =
        obritTextStyle(
            fontSize = AtomText.S5xl.FontSize,
            lineHeight = AtomText.S5xl.LineHeight,
        ),
    val xl6: TextStyle =
        obritTextStyle(
            fontSize = AtomText.S6xl.FontSize,
            lineHeight = AtomText.S6xl.LineHeight,
        ),
    val xl7: TextStyle =
        obritTextStyle(
            fontSize = AtomText.S7xl.FontSize,
            lineHeight = AtomText.S7xl.LineHeight,
        ),
)

private fun obritTextStyle(
    fontSize: Float,
    lineHeight: Float,
): TextStyle =
    TextStyle(
        fontFamily = Pretendard,
        letterSpacing = AtomText.LetterSpacing.sp,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
    )

private val Pretendard =
    FontFamily(
        Font(resId = R.font.pretendard_bold, weight = FontWeight.Bold),
        Font(resId = R.font.pretendard_semibold, weight = FontWeight.SemiBold),
        Font(resId = R.font.pretendard_medium, weight = FontWeight.Medium),
        Font(resId = R.font.pretendard_regular, weight = FontWeight.Normal),
    )
