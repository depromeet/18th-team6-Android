@file:Suppress("LongMethod", "MagicNumber")

package com.obrit.feature.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.feature.detail.viewmodel.DetailColorTone
import com.obrit.feature.detail.viewmodel.DetailSpareStatus
import com.obrit.feature.detail.viewmodel.DetailUiState

@Composable
internal fun DetailSpareCountSection(
    state: DetailUiState.ConsumableSuccess,
    modifier: Modifier = Modifier,
) {
    DetailSpareCountSection(
        itemName = state.spareAreaItemName,
        representativeImageUrl = state.representativeImageUrl,
        spareStatus = state.spareStatus,
        modifier = modifier,
    )
}

@Composable
internal fun DetailSpareCountSection(
    itemName: String,
    representativeImageUrl: String?,
    spareStatus: DetailSpareStatus,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val accentColor = spareStatus.colorTone.toAccentColor(colors)

    DetailCardBackground(
        modifier =
            modifier
                .fillMaxWidth()
                .height(SPARE_COUNT_CARD_HEIGHT),
        contentPadding =
            PaddingValues(
                start = SPARE_COUNT_HORIZONTAL_PADDING,
                top = SPARE_COUNT_TOP_PADDING,
                end = SPARE_COUNT_HORIZONTAL_PADDING,
                bottom = SPARE_COUNT_BOTTOM_PADDING,
            ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(SPARE_COUNT_TITLE_BOTTOM_SPACING),
        ) {
            Text(
                text = "여분 수량",
                style = typography.xl2.copy(fontWeight = FontWeight.Bold),
                color = colors.common00,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SPARE_COUNT_ROW_ITEM_SPACING),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DetailRepresentativeImage(
                    itemName = itemName,
                    representativeImageUrl = representativeImageUrl,
                    modifier = Modifier.size(SPARE_COUNT_IMAGE_BACKGROUND_SIZE),
                )

                Text(
                    text = itemName,
                    modifier = Modifier.weight(1f),
                    style = typography.xl.copy(fontWeight = FontWeight.Bold),
                    color = colors.common00,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                SpareCountText(
                    count = spareStatus.count,
                    color = accentColor,
                )
            }
        }
    }
}

@Composable
private fun DetailRepresentativeImage(
    itemName: String,
    representativeImageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val imageDescription =
        if (representativeImageUrl.isNullOrBlank()) {
            "${itemName.ifBlank { "소모품" }} 대표 이미지 자리"
        } else {
            "${itemName.ifBlank { "소모품" }} 대표 이미지"
        }

    Box(
        modifier =
            modifier
                .clip(CircleShape)
                .background(colors.gray750)
                .clearAndSetSemantics {
                    contentDescription = imageDescription
                },
        contentAlignment = Alignment.Center,
    ) {
        DetailRemoteImage(
            imageUrl = representativeImageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun SpareCountText(
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = count.toString(),
            style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            color = color,
            maxLines = 1,
        )
        Text(
            text = "개",
            style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            color = color,
            maxLines = 1,
        )
    }
}

private fun DetailColorTone.toAccentColor(colors: OBRitColor): Color =
    when (this) {
        DetailColorTone.BRAND -> colors.green300
        DetailColorTone.WARNING -> colors.red300
    }

private val SPARE_COUNT_CARD_HEIGHT = 138.dp
private val SPARE_COUNT_HORIZONTAL_PADDING = 20.dp
private val SPARE_COUNT_TOP_PADDING = 24.dp
private val SPARE_COUNT_BOTTOM_PADDING = 17.dp
private val SPARE_COUNT_TITLE_BOTTOM_SPACING = 15.dp
private val SPARE_COUNT_ROW_ITEM_SPACING = 16.dp
private val SPARE_COUNT_IMAGE_BACKGROUND_SIZE = 52.dp
