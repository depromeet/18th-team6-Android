package com.obrit.feature.register.screen.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun CategoryCountTitle(
    prefix: String,
    count: Int,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val text =
        buildAnnotatedString {
            withStyle(SpanStyle(color = colors.common00)) { append(prefix) }
            withStyle(SpanStyle(color = colors.green300)) {
                append(CATEGORY_SHEET_COUNT_FORMAT.format(count))
            }
        }
    Text(
        text = text,
        style = typography.xl3.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
internal fun CategoryConfirmButton(
    isEmptyResult: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    OBRitLargeFilledButton(
        onClick = onClick,
        enabled = enabled,
        colors = OBRitButtonDefaults.positiveButtonColors(),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = CATEGORY_CTA_BOTTOM_PADDING),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
        ) {
            if (isEmptyResult) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_button_exclamation_circle),
                    contentDescription = null,
                    tint = LocalOBRitColor.current.common1000,
                    modifier = Modifier.size(CATEGORY_CTA_ICON_SIZE),
                )
            }
            Text(
                text = CATEGORY_SHEET_CTA_LABEL,
                style =
                    LocalOBRitTypography.current.xl.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
            )
        }
    }
}

private const val CATEGORY_SHEET_CTA_LABEL = "소모품 종류 선택하기"
private val CATEGORY_CTA_ICON_SIZE = AtomSpacing.S6.dp
// CTA 아래 ~ 시트 바닥 총 40dp. OBRitBottomSheet 자체가 pb=20을 가지므로 여기서 20dp만 추가.
private val CATEGORY_CTA_BOTTOM_PADDING = AtomSpacing.S5.dp
