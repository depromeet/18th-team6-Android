package com.obrit.feature.detail.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.obrit.feature.detail.viewmodel.DetailUiState

@Composable
internal fun DetailScreenSuccessContent(
    state: DetailUiState.Success,
    action: DetailScreenAction,
    modifier: Modifier = Modifier,
) {
    val agent = state.agent

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = action.onBackClick) {
            Text(text = "Back")
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = agent.name,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = agent.type.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = agent.description,
            style = MaterialTheme.typography.bodyLarge,
        )

        DetailInfoRow(
            label = "ID",
            value = agent.id.toString(),
            modifier = Modifier.fillMaxWidth(),
        )
        DetailInfoRow(
            label = "Created",
            value = agent.timestamp.toString(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
        )
    }
}
