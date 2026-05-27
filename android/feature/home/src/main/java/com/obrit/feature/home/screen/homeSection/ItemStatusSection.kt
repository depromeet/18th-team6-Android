package com.obrit.feature.home.screen.homeSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.model.home.HomeOverallLevel
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.HomeStatusLevel

@Composable
internal fun ItemStatusSection(
    overallStatus: HomeOverallStatus,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val highlightWord = overallStatus.overall.displayText()
    val suffix = overallStatus.overall.titleSuffix()
    val highlightColor =
        when (overallStatus.overall) {
            HomeOverallLevel.PERFECT, HomeOverallLevel.GOOD -> colors.green300
            else -> colors.red300
        }

    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text =
                buildAnnotatedString {
                    withStyle(SpanStyle(color = colors.common00)) {
                        append("오늘의 소모품 관리\n상태는 ")
                    }
                    withStyle(SpanStyle(color = highlightColor)) {
                        append(highlightWord)
                    }
                    withStyle(SpanStyle(color = colors.common00)) {
                        append(suffix)
                    }
                },
            style = typography.xl5.copy(fontWeight = FontWeight.Bold),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ManagementStatusBox(label = "교체 관리", statusText = overallStatus.replacement.displayText())
            ManagementStatusBox(label = "여분 관리", statusText = overallStatus.spare.displayText())
        }
    }
}

@Composable
private fun ManagementStatusBox(
    label: String,
    statusText: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = colors.gray300,
        )
        Text(
            text = statusText,
            style = typography.base.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
        )
    }
}

private fun HomeOverallLevel.displayText(): String =
    when (this) {
        HomeOverallLevel.PERFECT -> "완벽"
        HomeOverallLevel.GOOD -> "양호"
        HomeOverallLevel.WARNING -> "경고"
        HomeOverallLevel.DANGER -> "위험"
        HomeOverallLevel.UNKNOWN -> "경고"
    }

private fun HomeOverallLevel.titleSuffix(): String =
    when (this) {
        HomeOverallLevel.PERFECT, HomeOverallLevel.GOOD -> "해요"
        HomeOverallLevel.WARNING -> "예요"
        HomeOverallLevel.DANGER -> "해요"
        HomeOverallLevel.UNKNOWN -> "예요"
    }

private fun HomeStatusLevel.displayText(): String =
    when (this) {
        HomeStatusLevel.GOOD -> "양호"
        HomeStatusLevel.WARNING -> "경고"
        HomeStatusLevel.DANGER -> "위험"
        HomeStatusLevel.UNKNOWN -> "경고"
    }

@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun ItemStatusSectionPreview() {
    OBRitTheme {
        ItemStatusSection(
            overallStatus =
                HomeOverallStatus(
                    overall = HomeOverallLevel.WARNING,
                    replacement = HomeStatusLevel.WARNING,
                    spare = HomeStatusLevel.WARNING,
                ),
        )
    }
}
