@file:Suppress("TooManyFunctions")

package com.obrit.android.core.designsystem.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class OBRitCardLevel { L1, L2, L3, L4, L5, L6 }

@Composable
@Suppress("LongMethod", "LongParameterList")
fun OBRitCardGrid(
    level: OBRitCardLevel,
    title: String,
    stockCount: Int,
    daysLabel: String,
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit = {},
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier =
            modifier
                .size(GridCardSize)
                .clip(OBRitCardShape)
                .background(cardContainerColor(colors = colors, level = level))
                .padding(AtomSpacing.S4.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2_5.dp),
    ) {
        CardImageBox(
            modifier =
                Modifier
                    .weight(1f)
                    .aspectRatio(1f),
            backgroundColor = colors.gray800,
            content = image,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp)) {
            Text(
                text = title,
                style = typography.xl.copy(fontWeight = FontWeight.Bold),
                color = colors.common00,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${stockCount}개",
                    style = typography.xs.copy(fontWeight = FontWeight.Bold),
                    color = gridStockCountColor(colors = colors, level = level),
                )
                Text(
                    text = "남음",
                    style = typography.xs.copy(fontWeight = FontWeight.Medium),
                    color = gridStockSuffixColor(colors = colors, level = level),
                )
            }
        }
        CardBadge(
            text = daysLabel,
            containerColor = gridBadgeContainerColor(colors = colors, level = level),
            contentColor = gridBadgeContentColor(colors = colors, level = level),
            textStyle = typography.xs.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
@Suppress("LongMethod", "LongParameterList")
fun OBRitCardList(
    level: OBRitCardLevel,
    title: String,
    daysInUseLabel: String,
    replaceLabel: String,
    sparesLabel: String,
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit = {},
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val badgeStyle = typography.xs.copy(fontWeight = FontWeight.Bold)

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(OBRitCardShape)
                .background(cardContainerColor(colors = colors, level = level))
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CardImageBox(
            modifier = Modifier.size(ListImageSize),
            backgroundColor = colors.gray800,
            content = image,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
        ) {
            Text(
                text = title,
                style = typography.xl.copy(fontWeight = FontWeight.Bold),
                color = colors.common00,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = daysInUseLabel,
                    style = typography.s.copy(fontWeight = FontWeight.Bold),
                    color = colors.common00,
                )
                Text(
                    text = "째 사용중",
                    style = typography.s.copy(fontWeight = FontWeight.Medium),
                    color = listInUseSuffixColor(colors = colors, level = level),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp)) {
            CardBadge(
                text = replaceLabel,
                containerColor = listFirstBadgeContainerColor(colors = colors, level = level),
                contentColor = listFirstBadgeContentColor(colors = colors, level = level),
                textStyle = badgeStyle,
            )
            CardBadge(
                text = sparesLabel,
                containerColor = listSecondBadgeContainerColor(colors = colors, level = level),
                contentColor = colors.common00,
                textStyle = badgeStyle,
            )
        }
    }
}

@Composable
private fun CardImageBox(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .clip(CircleShape)
                .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun CardBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    textStyle: TextStyle,
) {
    Text(
        text = text,
        modifier =
            Modifier
                .clip(OBRitCardBadgeShape)
                .background(containerColor)
                .padding(horizontal = AtomSpacing.S2.dp, vertical = AtomSpacing.S1.dp),
        style = textStyle,
        color = contentColor,
    )
}

private fun cardContainerColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color =
    when (level) {
        OBRitCardLevel.L1 -> colors.red300
        OBRitCardLevel.L2, OBRitCardLevel.L3 -> colors.red900
        OBRitCardLevel.L4, OBRitCardLevel.L5, OBRitCardLevel.L6 -> colors.gray850
    }

private fun gridStockCountColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color =
    when (level) {
        OBRitCardLevel.L2, OBRitCardLevel.L5 -> colors.red300
        else -> colors.common00
    }

private fun gridStockSuffixColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color = if (level == OBRitCardLevel.L1) colors.red100 else colors.gray300

private fun gridBadgeContainerColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color =
    when (level) {
        OBRitCardLevel.L1, OBRitCardLevel.L4 -> colors.common00
        OBRitCardLevel.L2, OBRitCardLevel.L3 -> colors.red300
        OBRitCardLevel.L5, OBRitCardLevel.L6 -> colors.gray750
    }

private fun gridBadgeContentColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color =
    when (level) {
        OBRitCardLevel.L1, OBRitCardLevel.L4 -> colors.red300
        else -> colors.common00
    }

private fun listInUseSuffixColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color = if (level == OBRitCardLevel.L1) colors.red100 else colors.gray300

private fun listFirstBadgeContainerColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color =
    when (level) {
        OBRitCardLevel.L1 -> colors.red250
        OBRitCardLevel.L2 -> colors.common00
        OBRitCardLevel.L3, OBRitCardLevel.L4 -> colors.red300
        OBRitCardLevel.L5, OBRitCardLevel.L6 -> colors.gray750
    }

private fun listFirstBadgeContentColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color = if (level == OBRitCardLevel.L2) colors.red300 else colors.common00

private fun listSecondBadgeContainerColor(
    colors: OBRitColor,
    level: OBRitCardLevel,
): Color =
    when (level) {
        OBRitCardLevel.L1 -> colors.red250
        OBRitCardLevel.L2, OBRitCardLevel.L5 -> colors.red300
        OBRitCardLevel.L3, OBRitCardLevel.L4, OBRitCardLevel.L6 -> colors.gray750
    }

private val OBRitCardShape = RoundedCornerShape(AtomRadius.ExtraLarge.dp)
private val OBRitCardBadgeShape = RoundedCornerShape(AtomRadius.Small.dp)
private val GridCardSize = 160.dp
private val ListImageSize = 48.dp

@Preview(name = "OBRitCardGrid L1", showBackground = true)
@Composable
private fun OBRitCardGridL1Preview() {
    OBRitCardPreviewContainer {
        OBRitCardGrid(level = OBRitCardLevel.L1, title = "필터", stockCount = 0, daysLabel = "D-day")
    }
}

@Preview(name = "OBRitCardGrid L2", showBackground = true)
@Composable
private fun OBRitCardGridL2Preview() {
    OBRitCardPreviewContainer {
        OBRitCardGrid(level = OBRitCardLevel.L2, title = "필터", stockCount = 0, daysLabel = "D-3")
    }
}

@Preview(name = "OBRitCardGrid L3", showBackground = true)
@Composable
private fun OBRitCardGridL3Preview() {
    OBRitCardPreviewContainer {
        OBRitCardGrid(level = OBRitCardLevel.L3, title = "필터", stockCount = 5, daysLabel = "D-day")
    }
}

@Preview(name = "OBRitCardGrid L4", showBackground = true)
@Composable
private fun OBRitCardGridL4Preview() {
    OBRitCardPreviewContainer {
        OBRitCardGrid(level = OBRitCardLevel.L4, title = "필터", stockCount = 5, daysLabel = "D-3")
    }
}

@Preview(name = "OBRitCardGrid L5", showBackground = true)
@Composable
private fun OBRitCardGridL5Preview() {
    OBRitCardPreviewContainer {
        OBRitCardGrid(level = OBRitCardLevel.L5, title = "필터", stockCount = 0, daysLabel = "D-7")
    }
}

@Preview(name = "OBRitCardGrid L6", showBackground = true)
@Composable
private fun OBRitCardGridL6Preview() {
    OBRitCardPreviewContainer {
        OBRitCardGrid(level = OBRitCardLevel.L6, title = "필터", stockCount = 5, daysLabel = "D-7")
    }
}

@Preview(name = "OBRitCardList L1", showBackground = true, widthDp = 412)
@Composable
private fun OBRitCardListL1Preview() {
    OBRitCardPreviewContainer {
        OBRitCardList(
            level = OBRitCardLevel.L1,
            title = "필터",
            daysInUseLabel = "30일",
            replaceLabel = "교체 D+2",
            sparesLabel = "여분 5개",
        )
    }
}

@Preview(name = "OBRitCardList L2", showBackground = true, widthDp = 412)
@Composable
private fun OBRitCardListL2Preview() {
    OBRitCardPreviewContainer {
        OBRitCardList(
            level = OBRitCardLevel.L2,
            title = "필터",
            daysInUseLabel = "27일",
            replaceLabel = "교체 D-3",
            sparesLabel = "여분 0개",
        )
    }
}

@Preview(name = "OBRitCardList L3", showBackground = true, widthDp = 412)
@Composable
private fun OBRitCardListL3Preview() {
    OBRitCardPreviewContainer {
        OBRitCardList(
            level = OBRitCardLevel.L3,
            title = "필터",
            daysInUseLabel = "30일",
            replaceLabel = "교체 D+2",
            sparesLabel = "여분 5개",
        )
    }
}

@Preview(name = "OBRitCardList L4", showBackground = true, widthDp = 412)
@Composable
private fun OBRitCardListL4Preview() {
    OBRitCardPreviewContainer {
        OBRitCardList(
            level = OBRitCardLevel.L4,
            title = "필터",
            daysInUseLabel = "27일",
            replaceLabel = "교체 D-3",
            sparesLabel = "여분 5개",
        )
    }
}

@Preview(name = "OBRitCardList L5", showBackground = true, widthDp = 412)
@Composable
private fun OBRitCardListL5Preview() {
    OBRitCardPreviewContainer {
        OBRitCardList(
            level = OBRitCardLevel.L5,
            title = "필터",
            daysInUseLabel = "20일",
            replaceLabel = "교체 D-7",
            sparesLabel = "여분 0개",
        )
    }
}

@Preview(name = "OBRitCardList L6", showBackground = true, widthDp = 412)
@Composable
private fun OBRitCardListL6Preview() {
    OBRitCardPreviewContainer {
        OBRitCardList(
            level = OBRitCardLevel.L6,
            title = "필터",
            daysInUseLabel = "20일",
            replaceLabel = "교체 D-7",
            sparesLabel = "여분 5개",
        )
    }
}

@Composable
private fun OBRitCardPreviewContainer(content: @Composable () -> Unit) {
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
