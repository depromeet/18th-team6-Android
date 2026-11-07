package com.obrit.feature.register.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun CategorySectionHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
    onAddClick: (() -> Unit)? = null,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val titleStyle = typography.xl3.copy(fontWeight = FontWeight.SemiBold)
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(CATEGORY_SECTION_HEADER_HEIGHT)
                .background(
                    Brush.verticalGradient(
                        CATEGORY_HEADER_SOLID_STOP to colors.gray900,
                        1f to Color.Transparent,
                    ),
                ).padding(top = AtomSpacing.S1.dp, bottom = AtomSpacing.S3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        Text(text = title, style = titleStyle.copy(color = colors.common00))
        Text(text = count.toString(), style = titleStyle.copy(color = colors.green300))
        if (onAddClick != null) {
            CategoryAddButton(
                onClick = onAddClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CategoryAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = CATEGORY_SHEET_ADD_LABEL,
                style = typography.base.copy(color = colors.gray300, fontWeight = FontWeight.Medium),
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_stepper_plus),
                contentDescription = null,
                tint = colors.gray300,
                modifier = Modifier.size(AtomSpacing.S4.dp),
            )
        }
    }
}

@Composable
internal fun CategoryConfirmButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        CATEGORY_CTA_FADE_STOP to colors.gray900,
                    ),
                ).padding(top = AtomSpacing.S5.dp),
    ) {
        OBRitLargeFilledButton(
            onClick = onClick,
            enabled = enabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = CATEGORY_SHEET_CTA_LABEL,
                style = LocalOBRitTypography.current.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

// 입력 폼에 넣기만 하는 버튼이라 입력 폼의 '추가하기'와 구분한다.
private const val CATEGORY_SHEET_CTA_LABEL = "선택하기"
private const val CATEGORY_SHEET_ADD_LABEL = "추가하기"
private const val CATEGORY_HEADER_SOLID_STOP = 0.8f
private const val CATEGORY_CTA_FADE_STOP = 0.2f

private val CATEGORY_SECTION_HEADER_HEIGHT = 44.dp
