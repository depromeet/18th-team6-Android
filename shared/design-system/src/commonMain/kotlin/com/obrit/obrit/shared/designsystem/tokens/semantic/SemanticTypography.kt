package com.obrit.obrit.shared.designsystem.tokens.semantic

import com.obrit.obrit.shared.designsystem.tokens.atom.typography.AtomFontFamily
import com.obrit.obrit.shared.designsystem.tokens.atom.typography.AtomFontSize
import com.obrit.obrit.shared.designsystem.tokens.atom.typography.AtomFontWeight
import com.obrit.obrit.shared.designsystem.tokens.atom.typography.AtomLineHeight

// 플랫폼 중립 Typography 모델. Compose TextStyle 등 변환은 android 어댑터가 담당.
data class TypographyToken(
    val fontFamily: String,
    val fontWeight: Int,
    val fontSize: Float,
    val lineHeight: Float,
    val letterSpacing: Float,
)

object SemanticTypography {
    object TextXl {
        val Medium = TypographyToken(
            fontFamily = AtomFontFamily.Sans,
            fontWeight = AtomFontWeight.Medium,
            fontSize = AtomFontSize.Xl,
            lineHeight = AtomLineHeight.Lg,
            letterSpacing = -3f,
        )
    }

    object TextS {
        val Medium = TypographyToken(
            fontFamily = AtomFontFamily.Sans,
            fontWeight = AtomFontWeight.Medium,
            fontSize = AtomFontSize.S,
            lineHeight = AtomLineHeight.S,
            letterSpacing = -3f,
        )
    }

    object TextBase {
        val Medium = TypographyToken(
            fontFamily = AtomFontFamily.Sans,
            fontWeight = AtomFontWeight.Medium,
            fontSize = AtomFontSize.Base,
            lineHeight = AtomLineHeight.Base,
            letterSpacing = -3f,
        )

        val SemiBold = TypographyToken(
            fontFamily = AtomFontFamily.Sans,
            fontWeight = AtomFontWeight.SemiBold,
            fontSize = AtomFontSize.Base,
            lineHeight = AtomLineHeight.Base,
            letterSpacing = -3f,
        )
    }

    // SSOT가 raw 값으로 등록한 시리즈. Atom 매핑 확인 필요.
    object Head {
        val Head5SB24 = TypographyToken(
            fontFamily = "Pretendard",
            fontWeight = 600,
            fontSize = 24f,
            lineHeight = 1.30f,
            letterSpacing = -3f,
        )
    }

    object Caption {
        val Caption1M13 = TypographyToken(
            fontFamily = "Pretendard",
            fontWeight = 500,
            fontSize = 13f,
            lineHeight = 1.40f,
            letterSpacing = -4f,
        )
    }
}
