@file:Suppress("LongMethod", "MagicNumber")

package com.obrit.feature.detail.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
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
            "${itemName.ifBlank { "소모품" }} 대표 이미지 준비 중"
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
        ) {
            SpareToothbrushPlaceholder(modifier = Modifier.size(SPARE_COUNT_TOOTHBRUSH_SIZE))
        }
    }
}

@Composable
private fun SpareToothbrushPlaceholder(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

        rotate(degrees = -42f, pivot = center) {
            drawLine(
                brush =
                    Brush.linearGradient(
                        colors =
                            listOf(
                                colors.blue50,
                                colors.blue200,
                                colors.blue300,
                            ),
                    ),
                start = Offset(canvasWidth * 0.12f, canvasHeight * 0.66f),
                end = Offset(canvasWidth * 0.68f, canvasHeight * 0.66f),
                strokeWidth = canvasHeight * 0.2f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = colors.common00.copy(alpha = 0.55f),
                start = Offset(canvasWidth * 0.18f, canvasHeight * 0.6f),
                end = Offset(canvasWidth * 0.57f, canvasHeight * 0.6f),
                strokeWidth = canvasHeight * 0.05f,
                cap = StrokeCap.Round,
            )
            drawRoundRect(
                brush =
                    Brush.linearGradient(
                        colors =
                            listOf(
                                colors.blue100,
                                colors.blue250,
                            ),
                    ),
                topLeft = Offset(canvasWidth * 0.62f, canvasHeight * 0.56f),
                size = Size(canvasWidth * 0.24f, canvasHeight * 0.16f),
                cornerRadius =
                    CornerRadius(
                        x = canvasHeight * 0.08f,
                        y = canvasHeight * 0.08f,
                    ),
            )

            repeat(TOOTHBRUSH_BRISTLE_COLUMN_COUNT) { column ->
                repeat(TOOTHBRUSH_BRISTLE_ROW_COUNT) { row ->
                    drawRoundRect(
                        color = colors.common00.copy(alpha = 0.82f - row * 0.14f),
                        topLeft =
                            Offset(
                                x = canvasWidth * (0.66f + column * 0.045f),
                                y = canvasHeight * (0.31f + row * 0.075f),
                            ),
                        size = Size(canvasWidth * 0.04f, canvasHeight * 0.19f),
                        cornerRadius =
                            CornerRadius(
                                x = canvasHeight * 0.025f,
                                y = canvasHeight * 0.025f,
                            ),
                    )
                }
            }
        }
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
private val SPARE_COUNT_TOOTHBRUSH_SIZE = 34.dp
private const val TOOTHBRUSH_BRISTLE_COLUMN_COUNT = 4
private const val TOOTHBRUSH_BRISTLE_ROW_COUNT = 3
