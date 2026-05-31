@file:Suppress("LongParameterList")

package com.obrit.feature.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme

@Composable
internal fun DetailCtaSection(
    onSpareManagementRequest: () -> Unit,
    onReplaceCompleteClick: () -> Unit,
    isSpareManagementEnabled: Boolean,
    isReplaceCtaEnabled: Boolean,
    isReplaceProcessing: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DETAIL_CTA_BUTTON_GAP),
    ) {
        DetailCtaButton(
            text = SPARE_MANAGEMENT_CTA_TEXT,
            onClick = onSpareManagementRequest,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = colors.gray800,
                    contentColor = colors.gray150,
                    disabledContainerColor = colors.gray800,
                    disabledContentColor = colors.gray700,
                ),
            modifier = Modifier.weight(1f),
            enabled = isSpareManagementEnabled && !isReplaceProcessing,
        )
        DetailCtaButton(
            text = REPLACE_COMPLETE_CTA_TEXT,
            onClick = onReplaceCompleteClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = colors.green300,
                    contentColor = colors.common1000,
                    disabledContainerColor = colors.green800,
                    disabledContentColor = colors.common1000,
                ),
            modifier = Modifier.weight(1f),
            enabled = isReplaceCtaEnabled && !isReplaceProcessing,
        )
    }
}

@Composable
private fun DetailCtaButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Button(
        onClick = onClick,
        modifier = modifier.height(DETAIL_CTA_BUTTON_HEIGHT),
        enabled = enabled,
        shape = RoundedCornerShape(DETAIL_CTA_BUTTON_RADIUS),
        colors = colors,
        elevation = null,
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = text,
            style = typography.xl.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(name = "DetailCtaSection", showBackground = true)
@Composable
private fun DetailCtaSectionPreview() {
    DetailCtaSectionPreviewContainer {
        DetailCtaSection(
            onSpareManagementRequest = {},
            onReplaceCompleteClick = {},
            isSpareManagementEnabled = true,
            isReplaceCtaEnabled = true,
            isReplaceProcessing = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(name = "DetailCtaSection Disabled", showBackground = true)
@Composable
private fun DetailCtaSectionDisabledPreview() {
    DetailCtaSectionPreviewContainer {
        DetailCtaSection(
            onSpareManagementRequest = {},
            onReplaceCompleteClick = {},
            isSpareManagementEnabled = false,
            isReplaceCtaEnabled = false,
            isReplaceProcessing = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DetailCtaSectionPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current

        Box(
            modifier =
                Modifier
                    .width(DETAIL_CTA_PREVIEW_WIDTH)
                    .height(DETAIL_CTA_PREVIEW_HEIGHT)
                    .background(colors.gray900)
                    .padding(
                        horizontal = DETAIL_CTA_PREVIEW_HORIZONTAL_PADDING,
                        vertical = DETAIL_CTA_PREVIEW_VERTICAL_PADDING,
                    ),
        ) {
            content()
        }
    }
}

private val DETAIL_CTA_BUTTON_HEIGHT = 60.dp
private val DETAIL_CTA_BUTTON_RADIUS = 12.dp
private val DETAIL_CTA_BUTTON_GAP = 12.dp
private val DETAIL_CTA_PREVIEW_WIDTH = 412.dp
private val DETAIL_CTA_PREVIEW_HEIGHT = 92.dp
private val DETAIL_CTA_PREVIEW_HORIZONTAL_PADDING = 20.dp
private val DETAIL_CTA_PREVIEW_VERTICAL_PADDING = 16.dp
private const val SPARE_MANAGEMENT_CTA_TEXT = "여분 관리"
private const val REPLACE_COMPLETE_CTA_TEXT = "교체 완료"
