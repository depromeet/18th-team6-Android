package com.obrit.android.core.designsystem.component.gnb

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class OBRitGnbTab {
    Home,
    List,
}

@Composable
fun OBRitGnb(
    selectedTab: OBRitGnbTab,
    onTabSelect: (OBRitGnbTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Row(
        modifier =
            modifier
                .obritGnbShadow()
                .clip(OBRitGnbContainerShape)
                .background(colors.gray250)
                .padding(
                    horizontal = OBRitGnbHorizontalPadding,
                    vertical = OBRitGnbVerticalPadding,
                ),
    ) {
        OBRitGnbTabItem(
            icon = R.drawable.ic_gnb_home,
            iconSize = OBRitGnbHomeIconSize,
            isSelected = selectedTab == OBRitGnbTab.Home,
            selectedIconColor = colors.common100,
            unselectedIconColor = colors.common00,
            onClick = { onTabSelect(OBRitGnbTab.Home) },
        )
        OBRitGnbTabItem(
            icon = R.drawable.ic_gnb_list,
            iconSize = OBRitGnbListIconSize,
            isSelected = selectedTab == OBRitGnbTab.List,
            selectedIconColor = colors.common100,
            unselectedIconColor = colors.common00,
            onClick = { onTabSelect(OBRitGnbTab.List) },
        )
    }
}

@Composable
private fun OBRitGnbTabItem(
    @DrawableRes icon: Int,
    iconSize: Dp,
    isSelected: Boolean,
    selectedIconColor: Color,
    unselectedIconColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(OBRitGnbTabWidth, OBRitGnbTabHeight)
                .clip(OBRitGnbTabShape)
                .background(if (isSelected) Color.White else Color.Transparent)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = if (isSelected) selectedIconColor else unselectedIconColor,
        )
    }
}

private fun Modifier.obritGnbShadow(): Modifier =
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint =
                Paint().apply {
                    asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = android.graphics.Color.TRANSPARENT
                        setShadowLayer(
                            OBRitGnbShadowBlur.toPx(),
                            0f,
                            OBRitGnbShadowOffsetY.toPx(),
                            Color.Black.copy(alpha = OBRIT_GNB_SHADOW_ALPHA).toArgb(),
                        )
                    }
                }
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = OBRitGnbContainerRadius.toPx(),
                radiusY = OBRitGnbContainerRadius.toPx(),
                paint = paint,
            )
        }
    }

@Preview(
    name = "OBRitGnb Home Selected",
    showBackground = true,
    backgroundColor = 0xFF1D1B20,
)
@Composable
private fun OBRitGnbHomeSelectedPreview() {
    OBRitTheme(dynamicColor = false) {
        Box(modifier = Modifier.padding(AtomSpacing.S10.dp)) {
            OBRitGnb(
                selectedTab = OBRitGnbTab.Home,
                onTabSelect = {},
            )
        }
    }
}

@Preview(
    name = "OBRitGnb List Selected",
    showBackground = true,
    backgroundColor = 0xFF1D1B20,
)
@Composable
private fun OBRitGnbListSelectedPreview() {
    OBRitTheme(dynamicColor = false) {
        Box(modifier = Modifier.padding(AtomSpacing.S10.dp)) {
            OBRitGnb(
                selectedTab = OBRitGnbTab.List,
                onTabSelect = {},
            )
        }
    }
}

@Preview(
    name = "OBRitGnb Interactive",
    showBackground = true,
    backgroundColor = 0xFF1D1B20,
)
@Composable
private fun OBRitGnbInteractivePreview() {
    var selectedTab by remember { mutableStateOf(OBRitGnbTab.Home) }
    OBRitTheme(dynamicColor = false) {
        Box(modifier = Modifier.padding(AtomSpacing.S10.dp)) {
            OBRitGnb(
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
            )
        }
    }
}

private val OBRitGnbContainerRadius = 28.dp
private val OBRitGnbTabRadius = 24.dp
private val OBRitGnbContainerShape = RoundedCornerShape(OBRitGnbContainerRadius)
private val OBRitGnbTabShape = RoundedCornerShape(OBRitGnbTabRadius)
private val OBRitGnbTabWidth = 56.dp
private val OBRitGnbTabHeight = 48.dp
private val OBRitGnbHorizontalPadding = 5.dp
private val OBRitGnbVerticalPadding = AtomSpacing.S1.dp
private val OBRitGnbHomeIconSize = 20.dp
private val OBRitGnbListIconSize = 24.dp
private val OBRitGnbShadowBlur = 24.dp
private val OBRitGnbShadowOffsetY = 16.dp
private const val OBRIT_GNB_CONTAINER_ALPHA = 0.4f
private const val OBRIT_GNB_SHADOW_ALPHA = 0.24f
