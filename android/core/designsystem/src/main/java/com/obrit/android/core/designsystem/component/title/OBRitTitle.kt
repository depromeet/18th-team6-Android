package com.obrit.android.core.designsystem.component.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class OBRitTitleSize {
    Large,
    Medium,
    Small,
}

enum class OBRitTitleType {
    Default,
    WithTag,
    TextOnly,
}

@Composable
@Suppress("LongParameterList")
fun OBRitTitle(
    title: String,
    modifier: Modifier = Modifier,
    size: OBRitTitleSize = OBRitTitleSize.Large,
    type: OBRitTitleType = OBRitTitleType.Default,
    description: String? = null,
    tagText: String? = null,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val titleStyle = obritTitleTextStyle(typography = typography, size = size)
    val descriptionStyle = obritTitleDescriptionStyle(typography = typography, size = size)

    when (type) {
        OBRitTitleType.Default -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(obritTitleContentGap(size = size)),
            ) {
                Text(
                    text = title,
                    style = titleStyle.copy(color = colors.common00),
                    maxLines = OBRIT_TITLE_TITLE_MAX_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!description.isNullOrBlank()) {
                    Text(
                        text = description,
                        style = descriptionStyle.copy(color = colors.gray300),
                        maxLines = OBRIT_TITLE_DESCRIPTION_MAX_LINES,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        OBRitTitleType.WithTag -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(obritTitleContentGap(size = size)),
            ) {
                Text(
                    text = title,
                    style = titleStyle.copy(color = colors.common00),
                    maxLines = OBRIT_TITLE_TITLE_MAX_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(obritTitleTagGap(size = size)),
                ) {
                    OBRitTitleTag(
                        text = tagText.orEmpty(),
                        size = size,
                    )
                    if (!description.isNullOrBlank()) {
                        Text(
                            text = description,
                            style = descriptionStyle.copy(color = colors.gray300),
                            maxLines = OBRIT_TITLE_DESCRIPTION_MAX_LINES,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        OBRitTitleType.TextOnly -> {
            Text(
                text = title,
                modifier = modifier,
                style = titleStyle.copy(color = colors.common00),
                maxLines = OBRIT_TITLE_TITLE_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun OBRitTitleTag(
    text: String,
    size: OBRitTitleSize,
) {
    if (text.isBlank()) return

    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Box(
        modifier =
            Modifier
                .clip(OBRitTitleTagShape)
                .background(colors.gray50)
                .heightIn(min = obritTitleTagMinHeight(size = size))
                .padding(
                    horizontal = obritTitleTagHorizontalPadding(size = size),
                    vertical = obritTitleTagVerticalPadding(size = size),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                typography.xs.copy(
                    color = colors.gray750,
                    fontWeight = FontWeight.SemiBold,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun obritTitleTextStyle(
    typography: com.obrit.android.core.designsystem.theme.OBRitTypography,
    size: OBRitTitleSize,
) = when (size) {
    OBRitTitleSize.Large -> typography.xl6
    OBRitTitleSize.Medium -> typography.xl4
    OBRitTitleSize.Small -> typography.xl2
}.copy(fontWeight = FontWeight.Bold)

private fun obritTitleDescriptionStyle(
    typography: com.obrit.android.core.designsystem.theme.OBRitTypography,
    size: OBRitTitleSize,
) = when (size) {
    OBRitTitleSize.Large -> typography.base
    OBRitTitleSize.Medium -> typography.s
    OBRitTitleSize.Small -> typography.xs
}.copy(fontWeight = FontWeight.Medium)

@Preview(
    name = "OBRitTitle Large Default",
    showBackground = true,
)
@Composable
private fun OBRitTitleLargeDefaultPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            description = "설명을 입력해주세요. 최대 두 줄까지 입력 가능합니다.\n설명을 입력해주세요. 최대 두 줄까지 입력 가능합니다.",
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.Default,
        )
    }
}

@Preview(
    name = "OBRitTitle Medium Default",
    showBackground = true,
)
@Composable
private fun OBRitTitleMediumDefaultPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            description = "설명을 입력해주세요. 최대 두 줄까지 입력 가능합니다.\n설명을 입력해주세요. 최대 두 줄까지 입력 가능합니다.",
            size = OBRitTitleSize.Medium,
            type = OBRitTitleType.Default,
        )
    }
}

@Preview(
    name = "OBRitTitle Small Default",
    showBackground = true,
)
@Composable
private fun OBRitTitleSmallDefaultPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            description = "설명을 입력해주세요. 최대 두 줄까지 입력 가능합니다.\n설명을 입력해주세요. 최대 두 줄까지 입력 가능합니다.",
            size = OBRitTitleSize.Small,
            type = OBRitTitleType.Default,
        )
    }
}

@Preview(
    name = "OBRitTitle Large WithTag",
    showBackground = true,
)
@Composable
private fun OBRitTitleLargeWithTagPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            description = "설명을 입력해주세요.",
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.WithTag,
            tagText = "Text",
        )
    }
}

@Preview(
    name = "OBRitTitle Medium WithTag",
    showBackground = true,
)
@Composable
private fun OBRitTitleMediumWithTagPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            description = "설명을 입력해주세요.",
            size = OBRitTitleSize.Medium,
            type = OBRitTitleType.WithTag,
            tagText = "Text",
        )
    }
}

@Preview(
    name = "OBRitTitle Small WithTag",
    showBackground = true,
)
@Composable
private fun OBRitTitleSmallWithTagPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            description = "설명을 입력해주세요.",
            size = OBRitTitleSize.Small,
            type = OBRitTitleType.WithTag,
            tagText = "Text",
        )
    }
}

@Preview(
    name = "OBRitTitle Large TextOnly",
    showBackground = true,
)
@Composable
private fun OBRitTitleLargeTextOnlyPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.TextOnly,
        )
    }
}

@Preview(
    name = "OBRitTitle Medium TextOnly",
    showBackground = true,
)
@Composable
private fun OBRitTitleMediumTextOnlyPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            size = OBRitTitleSize.Medium,
            type = OBRitTitleType.TextOnly,
        )
    }
}

@Preview(
    name = "OBRitTitle Small TextOnly",
    showBackground = true,
)
@Composable
private fun OBRitTitleSmallTextOnlyPreview() {
    OBRitTitlePreviewContainer {
        OBRitTitle(
            title = "제목을 입력해주세요",
            size = OBRitTitleSize.Small,
            type = OBRitTitleType.TextOnly,
        )
    }
}

@Composable
private fun OBRitTitlePreviewContainer(content: @Composable () -> Unit) {
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

private fun obritTitleContentGap(size: OBRitTitleSize) =
    when (size) {
        OBRitTitleSize.Large -> AtomSpacing.S3.dp
        OBRitTitleSize.Medium -> AtomSpacing.S3.dp
        OBRitTitleSize.Small -> AtomSpacing.S1.dp
    }

private fun obritTitleTagGap(size: OBRitTitleSize) =
    when (size) {
        OBRitTitleSize.Large -> AtomSpacing.S2.dp
        OBRitTitleSize.Medium -> AtomSpacing.S1_5.dp
        OBRitTitleSize.Small -> AtomSpacing.S1.dp
    }

private fun obritTitleTagMinHeight(size: OBRitTitleSize) =
    when (size) {
        OBRitTitleSize.Large -> OBRitTitleTagLargeMinHeight
        OBRitTitleSize.Medium -> OBRitTitleTagMediumMinHeight
        OBRitTitleSize.Small -> OBRitTitleTagSmallMinHeight
    }

private fun obritTitleTagHorizontalPadding(size: OBRitTitleSize) =
    when (size) {
        OBRitTitleSize.Large -> OBRitTitleTagLargeHorizontalPadding
        OBRitTitleSize.Medium -> OBRitTitleTagMediumHorizontalPadding
        OBRitTitleSize.Small -> OBRitTitleTagSmallHorizontalPadding
    }

private fun obritTitleTagVerticalPadding(size: OBRitTitleSize) =
    when (size) {
        OBRitTitleSize.Large -> OBRitTitleTagLargeVerticalPadding
        OBRitTitleSize.Medium -> OBRitTitleTagMediumVerticalPadding
        OBRitTitleSize.Small -> OBRitTitleTagSmallVerticalPadding
    }

private val OBRitTitleTagShape = RoundedCornerShape(AtomRadius.ExtraSmall.dp)
private val OBRitTitleTagLargeMinHeight = AtomSpacing.S6.dp
private val OBRitTitleTagMediumMinHeight = AtomSpacing.S5.dp
private val OBRitTitleTagSmallMinHeight = AtomSpacing.S4.dp
private val OBRitTitleTagLargeHorizontalPadding = AtomSpacing.S2.dp
private val OBRitTitleTagMediumHorizontalPadding = AtomSpacing.S1_5.dp
private val OBRitTitleTagSmallHorizontalPadding = AtomSpacing.S1.dp
private val OBRitTitleTagLargeVerticalPadding = AtomSpacing.S1.dp
private val OBRitTitleTagMediumVerticalPadding = AtomSpacing.S0_5.dp
private val OBRitTitleTagSmallVerticalPadding = AtomSpacing.Px.dp
private const val OBRIT_TITLE_TITLE_MAX_LINES = 2
private const val OBRIT_TITLE_DESCRIPTION_MAX_LINES = 2
