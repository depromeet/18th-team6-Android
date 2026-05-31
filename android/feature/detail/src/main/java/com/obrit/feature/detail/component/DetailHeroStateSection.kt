@file:Suppress("MagicNumber", "TooManyFunctions", "UnusedParameter")

package com.obrit.feature.detail.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.detail.viewmodel.DetailColorTone
import com.obrit.feature.detail.viewmodel.DetailStatusGrade
import com.obrit.feature.detail.viewmodel.DetailUiState

@Composable
internal fun DetailHeroStateSection(
    state: DetailUiState.ConsumableSuccess,
    modifier: Modifier = Modifier,
) {
    DetailHeroStateContent(
        contentState = state.toHeroStateContentState(),
        modifier = modifier,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun DetailHeroStateSection(
    itemName: String,
    categoryName: String,
    representativeImageUrl: String?,
    currentUsageDays: Int,
    recommendedReplacementIntervalDays: Int,
    progressRawRatio: Double,
    progressDisplayRatio: Double,
    statusGrade: DetailStatusGrade,
    colorTone: DetailColorTone,
    dDayLabel: String,
    modifier: Modifier = Modifier,
) {
    DetailHeroStateContent(
        contentState =
            DetailHeroStateContentState(
                itemName = itemName,
                representativeImageUrl = representativeImageUrl,
                progressRawRatio = progressRawRatio,
                progressDisplayRatio = progressDisplayRatio,
                statusGrade = statusGrade,
                colorTone = colorTone,
            ),
        modifier = modifier,
    )
}

@Composable
private fun DetailHeroStateContent(
    contentState: DetailHeroStateContentState,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val heroSize = maxWidth.coerceAtMost(DETAIL_HERO_PREVIEW_WIDTH)

        Box(
            modifier =
                Modifier
                    .size(heroSize)
                    .aspectRatio(DETAIL_HERO_ASPECT_RATIO),
            contentAlignment = Alignment.Center,
        ) {
            DetailProgressRing(
                progressRawRatio = contentState.progressRawRatio,
                progressDisplayRatio = contentState.progressDisplayRatio,
                statusGrade = contentState.statusGrade,
                colorTone = contentState.colorTone,
                modifier = Modifier.fillMaxSize(),
            )
            DetailConsumableImagePlaceholder(
                itemName = contentState.itemName,
                imageUrl = contentState.representativeImageUrl,
                modifier = Modifier.size(heroSize * DETAIL_HERO_IMAGE_SIZE_RATIO),
            )
        }
    }
}

@Composable
private fun DetailProgressRing(
    progressRawRatio: Double,
    progressDisplayRatio: Double,
    statusGrade: DetailStatusGrade,
    colorTone: DetailColorTone,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val palette =
        detailRingPalette(
            colors = colors,
            progressRawRatio = progressRawRatio,
            statusGrade = statusGrade,
            colorTone = colorTone,
        )
    val progress = progressDisplayRatio.coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO).toFloat()

    Canvas(
        modifier =
            modifier.semantics {
                contentDescription = "Replacement progress ${(progress * PERCENT_SCALE).toInt()} percent"
            },
    ) {
        drawHeroRing(
            progress = progress,
            palette = palette,
        )
    }
}

@Composable
private fun DetailConsumableImagePlaceholder(
    itemName: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val imageDescription = itemName.heroImageDescription(imageUrl)

    DetailRemoteImage(
        imageUrl = imageUrl,
        contentDescription = null,
        modifier =
            modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = imageDescription
                },
        contentScale = ContentScale.FillBounds,
    )
}

private fun detailRingPalette(
    colors: OBRitColor,
    progressRawRatio: Double,
    statusGrade: DetailStatusGrade,
    colorTone: DetailColorTone,
): DetailRingPalette {
    val pressure = progressRawRatio.coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO).toFloat()
    val isWarningState =
        colorTone == DetailColorTone.WARNING ||
            statusGrade == DetailStatusGrade.WARNING ||
            statusGrade == DetailStatusGrade.DANGER

    return if (isWarningState) {
        DetailRingPalette(
            centerColor = Color(0xFF4A2720),
            progressColors =
                listOf(
                    Color(0xFF773421),
                    Color(0xFFD24D21),
                ),
            gradientStartRatio = Offset(135f / DETAIL_HERO_PREVIEW_WIDTH_VALUE, 58.5f / DETAIL_HERO_PREVIEW_HEIGHT_VALUE),
            gradientEndRatio = Offset(210.5f / DETAIL_HERO_PREVIEW_WIDTH_VALUE, 26.5f / DETAIL_HERO_PREVIEW_HEIGHT_VALUE),
        )
    } else {
        DetailRingPalette(
            centerColor = Color(0xFF1F3B3A),
            progressColors =
                listOf(
                    Color(0xFF20665D),
                    pressure.toBrandEndColor(colors),
                ),
            gradientStartRatio = Offset(279.5f / DETAIL_HERO_PREVIEW_WIDTH_VALUE, 218.5f / DETAIL_HERO_PREVIEW_HEIGHT_VALUE),
            gradientEndRatio = Offset(210.5f / DETAIL_HERO_PREVIEW_WIDTH_VALUE, 26.5f / DETAIL_HERO_PREVIEW_HEIGHT_VALUE),
        )
    }
}

private fun DrawScope.drawHeroRing(
    progress: Float,
    palette: DetailRingPalette,
) {
    val minDimension = size.minDimension
    if (minDimension <= 0f) return

    val centerRadius = minDimension * DETAIL_HERO_CENTER_RADIUS_RATIO
    val strokeWidth = minOf(DETAIL_HERO_RING_STROKE_WIDTH.toPx(), centerRadius * 0.2f)
    val arcDiameter = (centerRadius * 2f - strokeWidth).coerceAtLeast(0f)
    if (arcDiameter <= 0f) return

    val topLeft =
        Offset(
            x = (size.width - arcDiameter) / 2f,
            y = (size.height - arcDiameter) / 2f,
        )
    val arcSize = Size(width = arcDiameter, height = arcDiameter)

    drawRingCenter(
        radius = centerRadius,
        palette = palette,
    )
    drawRingProgress(
        progress = progress,
        strokeWidth = strokeWidth,
        topLeft = topLeft,
        arcSize = arcSize,
        palette = palette,
    )
}

private fun DrawScope.drawRingCenter(
    radius: Float,
    palette: DetailRingPalette,
) {
    drawCircle(
        color = palette.centerColor,
        radius = radius,
    )
}

private fun DrawScope.drawRingProgress(
    progress: Float,
    strokeWidth: Float,
    topLeft: Offset,
    arcSize: Size,
    palette: DetailRingPalette,
) {
    val renderedProgress = progress.coerceAtMost(MAX_RENDERED_PROGRESS_RATIO)

    if (renderedProgress > MIN_PROGRESS_RATIO.toFloat()) {
        drawArc(
            brush =
                Brush.linearGradient(
                    colors = palette.progressColors,
                    start =
                        Offset(
                            x = size.width * palette.gradientStartRatio.x,
                            y = size.height * palette.gradientStartRatio.y,
                        ),
                    end =
                        Offset(
                            x = size.width * palette.gradientEndRatio.x,
                            y = size.height * palette.gradientEndRatio.y,
                        ),
                ),
            startAngle = START_ANGLE,
            sweepAngle = FULL_SWEEP_ANGLE * renderedProgress,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
    }
}

private fun Float.toBrandEndColor(colors: OBRitColor): Color =
    when {
        this < 0.5f -> Color(0xFF23BAA2)
        this < 0.8f -> colors.green300
        else -> Color(0xFF23BAA2)
    }

private fun DetailUiState.ConsumableSuccess.toHeroStateContentState(): DetailHeroStateContentState =
    DetailHeroStateContentState(
        itemName = itemName,
        representativeImageUrl = representativeImageUrl,
        progressRawRatio = progressRawRatio,
        progressDisplayRatio = progressDisplayRatio,
        statusGrade = statusGrade,
        colorTone = colorTone,
    )

private fun String.heroImageDescription(imageUrl: String?): String =
    if (imageUrl.isNullOrBlank()) {
        "$this image placeholder"
    } else {
        "$this representative image"
    }

private data class DetailHeroStateContentState(
    val itemName: String,
    val representativeImageUrl: String?,
    val progressRawRatio: Double,
    val progressDisplayRatio: Double,
    val statusGrade: DetailStatusGrade,
    val colorTone: DetailColorTone,
)

private data class DetailRingPalette(
    val centerColor: Color,
    val progressColors: List<Color>,
    val gradientStartRatio: Offset,
    val gradientEndRatio: Offset,
)

private val DETAIL_HERO_PREVIEW_WIDTH = DETAIL_HERO_PREVIEW_WIDTH_VALUE.dp
private val DETAIL_HERO_RING_STROKE_WIDTH = 16.dp
private const val DETAIL_HERO_PREVIEW_WIDTH_VALUE = 412f
private const val DETAIL_HERO_PREVIEW_HEIGHT_VALUE = 270f
private const val DETAIL_HERO_ASPECT_RATIO = DETAIL_HERO_PREVIEW_WIDTH_VALUE / DETAIL_HERO_PREVIEW_HEIGHT_VALUE
private const val DETAIL_HERO_CENTER_RADIUS_RATIO = 115f / DETAIL_HERO_PREVIEW_HEIGHT_VALUE
private const val DETAIL_HERO_IMAGE_SIZE_RATIO = 148f / DETAIL_HERO_PREVIEW_WIDTH_VALUE
private const val MIN_PROGRESS_RATIO = 0.0
private const val MAX_PROGRESS_RATIO = 1.0
private const val MAX_RENDERED_PROGRESS_RATIO = 0.875f
private const val PERCENT_SCALE = 100
private const val START_ANGLE = -90f
private const val FULL_SWEEP_ANGLE = 360f

@Preview(name = "DetailHeroStateSection Brand", widthDp = 412, heightDp = 270)
@Composable
private fun DetailHeroStateSectionBrandPreview() {
    OBRitTheme(dynamicColor = false) {
        DetailHeroStateContent(
            contentState =
                DetailHeroStateContentState(
                    itemName = "Toothbrush",
                    representativeImageUrl = null,
                    progressRawRatio = 0.385,
                    progressDisplayRatio = 0.385,
                    statusGrade = DetailStatusGrade.GOOD,
                    colorTone = DetailColorTone.BRAND,
                ),
        )
    }
}

@Preview(name = "DetailHeroStateSection Warning", widthDp = 412, heightDp = 270)
@Composable
private fun DetailHeroStateSectionWarningPreview() {
    OBRitTheme(dynamicColor = false) {
        DetailHeroStateContent(
            contentState =
                DetailHeroStateContentState(
                    itemName = "Toothbrush",
                    representativeImageUrl = null,
                    progressRawRatio = 0.875,
                    progressDisplayRatio = 0.875,
                    statusGrade = DetailStatusGrade.DANGER,
                    colorTone = DetailColorTone.WARNING,
                ),
        )
    }
}
