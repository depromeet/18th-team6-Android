package com.obrit.feature.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import com.obrit.android.feature.widget.R

/** 조회에 성공했지만 등록된 소모품이 없을 때 표시하는 화면. */
@Composable
internal fun StatusWidgetEmptyContent(
    openHomeAction: Action?,
    modifier: GlanceModifier = GlanceModifier,
) {
    StatusWidgetNoticeContent(
        iconRes = R.drawable.ic_widget_add_lined,
        message = "등록된 소모품이 없어요",
        modifier = modifier.clickableIfPresent(openHomeAction),
    )
}
