package com.obrit.feature.home.screen.section

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

@Composable
internal fun ConsumableStatusSection(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

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
                    withStyle(SpanStyle(color = colors.red300)) {
                        append("경고")
                    }
                    withStyle(SpanStyle(color = colors.common00)) {
                        append("예요")
                    }
                },
            style = typography.xl5.copy(fontWeight = FontWeight.Bold),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ManagementStatusBox(label = "교체 관리", statusText = "경고")
            ManagementStatusBox(label = "여분 관리", statusText = "경고")
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

@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun ConsumableStatusSectionPreview() {
    OBRitTheme {
        ConsumableStatusSection()
    }
}
