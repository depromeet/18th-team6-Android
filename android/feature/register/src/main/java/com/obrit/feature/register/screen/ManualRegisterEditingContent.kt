package com.obrit.feature.register.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrit.feature.register.viewmodel.ManualRegisterUiState

@Composable
internal fun ManualRegisterEditingContent(
    state: ManualRegisterUiState.Editing,
    action: ManualRegisterScreenAction,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text(text = "수동 등록")
    }
}
