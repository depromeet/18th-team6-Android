package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography

@Composable
internal fun HomeScreenLoadingContent(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.gray900),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = colors.green300)
        Text(
            text = "소모품 정보를 불러오는 중이에요.\n잠시만 기다려주세요!",
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = colors.common00,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
