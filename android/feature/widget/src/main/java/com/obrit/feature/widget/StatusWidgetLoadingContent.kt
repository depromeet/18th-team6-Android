package com.obrit.feature.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import com.obrit.android.feature.widget.R

@Composable
internal fun StatusWidgetLoadingContent(modifier: GlanceModifier = GlanceModifier) {
    StatusWidgetNoticeContent(
        iconRes = R.drawable.ic_widget_warning_filled,
        message = "정보를 불러오는 중이에요",
        modifier = modifier,
        isLoading = true,
    )
}
