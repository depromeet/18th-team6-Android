package com.obrit.feature.register.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.radiobutton.OBRitRadioButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun CategoryListItem(
    name: String,
    iconUrl: String,
    addedCount: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .clickable(onClick = onClick)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(CATEGORY_ITEM_IMAGE_SIZE)
                    .clip(CircleShape)
                    .background(colors.gray750),
        ) {
            AsyncImage(
                model = iconUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        CategoryListItemText(
            name = name,
            addedCount = addedCount,
            modifier = Modifier.weight(1f),
        )
        OBRitRadioButton(selected = selected, onClick = onClick)
    }
}

@Composable
private fun CategoryListItemText(
    name: String,
    addedCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        Text(
            text = name,
            style = typography.xl.copy(color = colors.common00, fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = CATEGORY_SHEET_ADDED_COUNT_LABEL,
                style = typography.s.copy(color = colors.gray400, fontWeight = FontWeight.Medium),
            )
            Text(
                text = CATEGORY_SHEET_COUNT_FORMAT.format(addedCount),
                style = typography.s.copy(color = colors.common00, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
internal fun CategoryEmptyStateCard(
    onDirectRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S5.dp),
    ) {
        CategoryEmptyStateText()
        CategoryEmptyStateButton(onClick = onDirectRegisterClick)
    }
}

@Composable
private fun CategoryEmptyStateText() {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        Text(
            text = CATEGORY_SHEET_EMPTY_TITLE,
            style = typography.xl3.copy(fontWeight = FontWeight.Bold, color = colors.common00),
            textAlign = TextAlign.Center,
        )
        Text(
            text = CATEGORY_SHEET_EMPTY_DESCRIPTION,
            style =
                typography.base.copy(
                    fontWeight = FontWeight.Medium,
                    color = colors.gray300.copy(alpha = CATEGORY_EMPTY_DESCRIPTION_ALPHA),
                ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CategoryEmptyStateButton(onClick: () -> Unit) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .background(colors.common00)
                .clickable(onClick = onClick)
                .padding(horizontal = AtomSpacing.S3.dp, vertical = AtomSpacing.S2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
    ) {
        Text(
            text = CATEGORY_SHEET_EMPTY_BUTTON_LABEL,
            style = typography.base.copy(fontWeight = FontWeight.SemiBold, color = colors.common1000),
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colors.common1000,
            modifier = Modifier.size(CATEGORY_EMPTY_BUTTON_ICON_SIZE),
        )
    }
}

private const val CATEGORY_SHEET_ADDED_COUNT_LABEL = "추가된 소모품"
private const val CATEGORY_SHEET_EMPTY_TITLE = "소모품 검색 결과가 없어요!"
private const val CATEGORY_SHEET_EMPTY_DESCRIPTION = "하단 버튼을 통해 직접 소모품 종류를 등록할 수 있어요."
private const val CATEGORY_SHEET_EMPTY_BUTTON_LABEL = "직접 등록하기"
private const val CATEGORY_EMPTY_DESCRIPTION_ALPHA = 0.64f

private val CATEGORY_ITEM_IMAGE_SIZE = 52.dp
private val CATEGORY_EMPTY_BUTTON_ICON_SIZE = AtomSpacing.S4.dp
