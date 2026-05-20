@file:Suppress("MemberNameEqualsClassName")

package com.obrit.obrit.shared.designsystem.tokens.semantic

import com.obrit.obrit.shared.designsystem.tokens.atom.color.AtomColors

object SemanticColors {
    object Background {
        object Default {
            const val Default: Long = AtomColors.Gray.S900
            const val DefaultHover: Long = AtomColors.Gray.S800
            const val Secondary: Long = AtomColors.Gray.S850
            const val SecondaryHover: Long = AtomColors.Gray.S900
            const val Tertiary: Long = AtomColors.Gray.S750
            const val LightGrayDefault: Long = AtomColors.Gray.S50
            const val LightGraySecondary: Long = AtomColors.Gray.S100
            const val LightGrayTertiary: Long = AtomColors.Gray.S250
            const val MiddleGrayDefault: Long = AtomColors.Gray.S450
            const val MiddleGraySecondary: Long = AtomColors.Gray.S600
            const val MiddleGrayTertiary: Long = AtomColors.Gray.S700
            const val DimDefault: Long = AtomColors.CommonOpacity.CommonBlack.S00_80
            const val DimSecondary: Long = AtomColors.GrayOpacity.S800
            const val DimTertiary: Long = AtomColors.GrayOpacity.S700
        }

        object Warning {
            const val Default: Long = AtomColors.Red.S300
            const val Hover: Long = AtomColors.Red.S450
            const val Secondary: Long = AtomColors.Red.S500
            const val SecondaryHover: Long = AtomColors.Red.S600
        }

        object Positive {
            const val Default: Long = AtomColors.Green.S300
            const val Hover: Long = AtomColors.Green.S400
            const val Secondary: Long = AtomColors.Green.S450
            const val SecondaryHover: Long = AtomColors.Green.S500
        }

        object Disabled {
            const val Default: Long = AtomColors.Gray.S700
        }
    }

    object Text {
        object Default {
            const val Default: Long = AtomColors.Common.S00
            const val Secondary: Long = AtomColors.Gray.S300
            const val Tertiary: Long = AtomColors.Gray.S500
            const val DarkDefault: Long = AtomColors.Gray.S900
            const val DarkSecondary: Long = AtomColors.Gray.S750
            const val DarkTertiary: Long = AtomColors.Gray.S600
        }

        object Warning {
            const val Default: Long = AtomColors.Red.S300
            const val Secondary: Long = AtomColors.Red.S400
            const val Tertiary: Long = AtomColors.Red.S450
        }

        object Positive {
            const val Default: Long = AtomColors.Green.S300
            const val Secondary: Long = AtomColors.Green.S400
            const val Tertiary: Long = AtomColors.Green.S450
        }

        object Disabled {
            const val Default: Long = AtomColors.Gray.S700
            const val OnDisabled: Long = AtomColors.Gray.S250
        }
    }

    object Border {
        object Default {
            const val Default: Long = AtomColors.Gray.S750
            const val Secondary: Long = AtomColors.Gray.S600
            const val Tertiary: Long = AtomColors.Gray.S450
        }

        object Warning {
            const val Default: Long = AtomColors.Red.S300
            const val Secondary: Long = AtomColors.Red.S400
            const val Tertiary: Long = AtomColors.Red.S450
        }

        object Positive {
            const val Default: Long = AtomColors.Green.S300
            const val Secondary: Long = AtomColors.Green.S400
            const val Tertiary: Long = AtomColors.Green.S450
        }

        object Disabled {
            const val Default: Long = AtomColors.Gray.S700
        }
    }

    object Icon {
        object Default {
            const val Default: Long = AtomColors.Common.S00
            const val Secondary: Long = AtomColors.Gray.S50
            const val Tertiary: Long = AtomColors.Gray.S100
        }

        object Warning {
            const val Default: Long = AtomColors.Red.S300
            const val Secondary: Long = AtomColors.Red.S400
            const val Tertiary: Long = AtomColors.Red.S450
        }

        object Positive {
            const val Default: Long = AtomColors.Green.S300
            const val Secondary: Long = AtomColors.Green.S400
            const val Tertiary: Long = AtomColors.Green.S450
        }

        object Disabled {
            const val Default: Long = AtomColors.Gray.S600
            const val OnDisabled: Long = AtomColors.Gray.S250
        }
    }

    object Surface {
        object Default {
            const val DefaultDark: Long = AtomColors.Gray.S800
            const val DefaultLight: Long = AtomColors.Common.S00
            const val DefaultHoverLight: Long = AtomColors.CommonOpacity.CommonWhite.S00_40
        }
    }
}
