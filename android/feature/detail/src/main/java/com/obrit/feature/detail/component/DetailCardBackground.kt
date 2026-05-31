package com.obrit.feature.detail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor

@Composable
internal fun DetailCardBackground(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LocalOBRitColor.current

    Box(
        modifier =
            modifier
                .clip(DETAIL_CARD_BACKGROUND_SHAPE)
                .background(colors.gray850)
                .border(
                    border = BorderStroke(width = 1.dp, color = colors.gray800),
                    shape = DETAIL_CARD_BACKGROUND_SHAPE,
                ).padding(contentPadding),
        content = content,
    )
}

private val DETAIL_CARD_BACKGROUND_SHAPE = RoundedCornerShape(16.dp)
