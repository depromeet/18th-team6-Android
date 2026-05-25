package com.obrit.feature.register.screen.manual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.stepper.OBRitStepper
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun QuantityCard(
    title: String,
    totalCount: Int,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
) {
    val colors = LocalOBRitColor.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .padding(AtomSpacing.S5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(QUANTITY_CARD_IMAGE_SIZE)
                    .clip(CircleShape)
                    .background(colors.gray750),
        )
        QuantityCardText(title = title, totalCount = totalCount, modifier = Modifier.weight(1f))
        OBRitStepper(value = quantity, onValueChange = onQuantityChange)
    }
}

@Composable
private fun QuantityCardText(
    title: String,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        Text(
            text = title,
            style = typography.xl.copy(color = colors.common00, fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
        Text(
            text = QUANTITY_TOTAL_FORMAT.format(totalCount),
            style = typography.s.copy(color = colors.gray500, fontWeight = FontWeight.Medium),
            maxLines = 1,
        )
    }
}

private const val QUANTITY_TOTAL_FORMAT = "전체 %d개"
private val QUANTITY_CARD_IMAGE_SIZE = 52.dp
