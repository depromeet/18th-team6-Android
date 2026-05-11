package com.obrit.android.core.designsystem.component.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class BadgeType {
    WarningFilled,
    Gray750Filled,
    WarningWhitebgFilled,
    Red250Filled,
    Red800Filled,
}

@Composable
fun OBRitBadge(
    text: String,
    modifier: Modifier = Modifier,
    type: BadgeType = BadgeType.WarningFilled,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Text(
        text = text,
        modifier =
            modifier
                .clip(OBRitBadgeShape)
                .background(badgeContainerColor(colors = colors, type = type))
                .padding(horizontal = AtomSpacing.S2.dp, vertical = AtomSpacing.S1.dp),
        style = typography.xs.copy(fontWeight = FontWeight.Bold),
        color = badgeContentColor(colors = colors, type = type),
    )
}

internal fun badgeContainerColor(
    colors: OBRitColor,
    type: BadgeType,
): Color =
    when (type) {
        BadgeType.WarningFilled -> colors.red300
        BadgeType.Gray750Filled -> colors.gray750
        BadgeType.WarningWhitebgFilled -> colors.common00
        BadgeType.Red250Filled -> colors.red250
        BadgeType.Red800Filled -> colors.red800
    }

internal fun badgeContentColor(
    colors: OBRitColor,
    type: BadgeType,
): Color =
    when (type) {
        BadgeType.WarningFilled -> colors.common00
        BadgeType.Gray750Filled -> colors.common00
        BadgeType.WarningWhitebgFilled -> colors.red300
        BadgeType.Red250Filled -> colors.common00
        BadgeType.Red800Filled -> colors.red300
    }

private val OBRitBadgeShape = RoundedCornerShape(AtomRadius.Small.dp)

@Preview(
    name = "OBRitBadge WarningFilled",
    showBackground = true,
)
@Composable
private fun OBRitBadgeWarningFilledPreview() {
    OBRitBadgePreviewContainer {
        OBRitBadge(text = "Text", type = BadgeType.WarningFilled)
    }
}

@Preview(
    name = "OBRitBadge Gray750Filled",
    showBackground = true,
)
@Composable
private fun OBRitBadgeGray750FilledPreview() {
    OBRitBadgePreviewContainer {
        OBRitBadge(text = "Text", type = BadgeType.Gray750Filled)
    }
}

@Preview(
    name = "OBRitBadge WarningWhitebgFilled",
    showBackground = true,
)
@Composable
private fun OBRitBadgeWarningWhitebgFilledPreview() {
    OBRitBadgePreviewContainer {
        OBRitBadge(text = "Text", type = BadgeType.WarningWhitebgFilled)
    }
}

@Preview(
    name = "OBRitBadge Red250Filled",
    showBackground = true,
)
@Composable
private fun OBRitBadgeRed250FilledPreview() {
    OBRitBadgePreviewContainer {
        OBRitBadge(text = "Text", type = BadgeType.Red250Filled)
    }
}

@Preview(
    name = "OBRitBadge Red800Filled",
    showBackground = true,
)
@Composable
private fun OBRitBadgeRed800FilledPreview() {
    OBRitBadgePreviewContainer {
        OBRitBadge(text = "Text", type = BadgeType.Red800Filled)
    }
}

@Composable
private fun OBRitBadgePreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current
        Box(
            modifier =
                Modifier
                    .background(colors.gray200)
                    .padding(AtomSpacing.S5.dp),
        ) {
            content()
        }
    }
}
