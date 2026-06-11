package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.obrit.android.core.designsystem.component.topbar.OBRitHomeTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme

@Composable
internal fun HomeScreenLoadingContent(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        OBRitHomeTopBar(
            onSearchClick = {},
            onNotificationClick = {},
            onProfileClick = {},
            modifier = Modifier.statusBarsPadding(),
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "소모품 정보를 불러오고 있어요.\n잠시만 기다려주세요.",
                textAlign = TextAlign.Center,
                style = typography.base,
                color = colors.common00,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun HomeScreenLoadingContentPreview() {
    OBRitTheme {
        HomeScreenLoadingContent()
    }
}
