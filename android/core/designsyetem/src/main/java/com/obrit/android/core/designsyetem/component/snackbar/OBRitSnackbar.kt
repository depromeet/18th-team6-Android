package com.obrit.android.core.designsyetem.component.snackbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsyetem.R
import com.obrit.android.core.designsyetem.theme.LocalOBRitColor
import com.obrit.android.core.designsyetem.theme.LocalOBRitTypography
import com.obrit.android.core.designsyetem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class SnackbarIcon {
    None,
    Default,
    Error,
    Success,
}

@Composable
fun OBRitSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    icon: SnackbarIcon = SnackbarIcon.None,
    textStyle: TextStyle =
        LocalOBRitTypography.current.xl.copy(
            fontWeight = FontWeight.Medium,
        ),
) {
    val colors = LocalOBRitColor.current

    Row(
        modifier =
            modifier
                .clip(OBRitSnackbarShape)
                .background(colors.gray800)
                .border(
                    border =
                        BorderStroke(
                            width = OBRitSnackbarBorderWidth,
                            color = colors.gray750,
                        ),
                    shape = OBRitSnackbarShape,
                ).padding(
                    start = snackbarStartPadding(icon = icon),
                    top = snackbarVerticalPadding(icon = icon),
                    end = OBRitSnackbarEndPadding,
                    bottom = snackbarVerticalPadding(icon = icon),
                ),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(OBRitSnackbarContentGap),
    ) {
        if (icon != SnackbarIcon.None) {
            Icon(
                painter = painterResource(id = snackbarIconRes(icon = icon)),
                contentDescription = null,
                modifier = Modifier.size(OBRitSnackbarIconSize),
                tint = Color.Unspecified,
            )
        }

        Text(
            text = message,
            style = textStyle.copy(color = colors.common00),
        )
    }
}

@DrawableRes
private fun snackbarIconRes(icon: SnackbarIcon): Int =
    when (icon) {
        SnackbarIcon.None -> error("SnackbarIcon.None has no drawable resource.")
        SnackbarIcon.Default -> R.drawable.ic_snackbar_question_circle
        SnackbarIcon.Error -> R.drawable.ic_snackbar_exclamation_circle
        SnackbarIcon.Success -> R.drawable.ic_snackbar_check_circle
    }

private fun snackbarStartPadding(icon: SnackbarIcon) =
    if (icon == SnackbarIcon.None) {
        OBRitSnackbarNoIconHorizontalPadding
    } else {
        OBRitSnackbarIconStartPadding
    }

private fun snackbarVerticalPadding(icon: SnackbarIcon) =
    if (icon == SnackbarIcon.None) {
        OBRitSnackbarNoIconVerticalPadding
    } else {
        OBRitSnackbarIconVerticalPadding
    }

private val OBRitSnackbarBorderWidth = 1.4.dp
private val OBRitSnackbarIconStartPadding = 14.dp
private val OBRitSnackbarEndPadding = AtomSpacing.S5.dp
private val OBRitSnackbarNoIconHorizontalPadding = AtomSpacing.S5.dp
private val OBRitSnackbarIconVerticalPadding = AtomSpacing.S2_5.dp
private val OBRitSnackbarNoIconVerticalPadding = AtomSpacing.S2.dp
private val OBRitSnackbarContentGap = 14.dp
private val OBRitSnackbarIconSize = AtomSpacing.S5.dp
private val OBRitSnackbarShape = RoundedCornerShape(AtomRadius.Large.dp)
private const val OBRIT_SNACKBAR_PREVIEW_MESSAGE = "토스트 팝업 내용을 입력하세요."

@Preview(
    name = "OBRitSnackbar Default",
    showBackground = true,
)
@Composable
private fun OBRitSnackbarDefaultPreview() {
    OBRitSnackbarPreviewContainer {
        OBRitSnackbar(
            message = "$OBRIT_SNACKBAR_PREVIEW_MESSAGE\n$OBRIT_SNACKBAR_PREVIEW_MESSAGE",
            icon = SnackbarIcon.Default,
        )
    }
}

@Preview(
    name = "OBRitSnackbar Error",
    showBackground = true,
)
@Composable
private fun OBRitSnackbarErrorPreview() {
    OBRitSnackbarPreviewContainer {
        OBRitSnackbar(
            message = "$OBRIT_SNACKBAR_PREVIEW_MESSAGE\n$OBRIT_SNACKBAR_PREVIEW_MESSAGE",
            icon = SnackbarIcon.Error,
        )
    }
}

@Preview(
    name = "OBRitSnackbar Success",
    showBackground = true,
)
@Composable
private fun OBRitSnackbarSuccessPreview() {
    OBRitSnackbarPreviewContainer {
        OBRitSnackbar(
            message = "$OBRIT_SNACKBAR_PREVIEW_MESSAGE\n$OBRIT_SNACKBAR_PREVIEW_MESSAGE",
            icon = SnackbarIcon.Success,
        )
    }
}

@Preview(
    name = "OBRitSnackbar No Icon",
    showBackground = true,
)
@Composable
private fun OBRitSnackbarNoIconPreview() {
    OBRitSnackbarPreviewContainer {
        OBRitSnackbar(message = OBRIT_SNACKBAR_PREVIEW_MESSAGE)
    }
}

@Composable
private fun OBRitSnackbarPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current
        Box(
            modifier =
                Modifier
                    .background(colors.gray900)
                    .padding(AtomSpacing.S5.dp),
        ) {
            content()
        }
    }
}
