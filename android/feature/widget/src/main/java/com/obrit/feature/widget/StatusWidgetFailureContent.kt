package com.obrit.feature.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import com.obrit.android.feature.widget.R

/** 저장된 성공 데이터가 없을 때 표시하는 실패 화면. */
@Composable
internal fun StatusWidgetFailureContent(
    retryAction: Action,
    modifier: GlanceModifier = GlanceModifier,
) {
    StatusWidgetNoticeContent(
        iconRes = R.drawable.ic_widget_warning_filled,
        message = "정보를 불러오지 못했어요",
        retryAction = retryAction,
        modifier = modifier,
    )
}
