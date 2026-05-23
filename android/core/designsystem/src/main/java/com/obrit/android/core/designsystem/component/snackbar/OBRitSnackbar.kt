package com.obrit.android.core.designsystem.component.snackbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
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
    val iconRes = snackbarIconRes(icon = icon)
    val hasIcon = iconRes != null

    Row(
        modifier =
            modifier
                .clip(OBRitSnackbarShape)
                .background(LocalOBRitColor.current.gray800)
                .border(
                    border =
                        BorderStroke(
                            width = 1.4.dp,
                            color = LocalOBRitColor.current.gray750,
                        ),
                    shape = OBRitSnackbarShape,
                ).padding(snackbarContentPadding(hasIcon = hasIcon)),
        verticalAlignment = Alignment.Top,
    ) {
        if (iconRes != null) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(AtomSpacing.S5.dp),
                tint = Color.Unspecified,
            )
        }

        Text(
            text = message,
            modifier = if (hasIcon) Modifier.padding(start = 14.dp) else Modifier,
            style = textStyle.copy(color = LocalOBRitColor.current.common00),
        )
    }
}

private fun snackbarIconRes(icon: SnackbarIcon): Int? =
    when (icon) {
        SnackbarIcon.None -> null
        SnackbarIcon.Default -> R.drawable.ic_snackbar_question_circle
        SnackbarIcon.Error -> R.drawable.ic_snackbar_exclamation_circle
        SnackbarIcon.Success -> R.drawable.ic_snackbar_check_circle
    }

private fun snackbarContentPadding(hasIcon: Boolean): PaddingValues =
    PaddingValues(
        start = if (hasIcon) 14.dp else 20.dp,
        top = if (hasIcon) 10.dp else 8.dp,
        end = 20.dp,
        bottom = if (hasIcon) 10.dp else 8.dp,
    )

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
