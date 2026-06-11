package com.obrit.feature.register.screen.manual

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.textfield.InputResultState
import com.obrit.android.core.designsystem.component.textfield.OBRitOutlinedTextField
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.feature.register.screen.common.FieldSectionHeader
import com.obrit.feature.register.screen.common.ReplacementPeriodDropdown
import com.obrit.feature.register.screen.common.rememberFocusBringIntoView
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.items.ReplacementPeriod

@Composable
internal fun CategoryField(
    value: String,
    onClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        FieldSectionHeader(label = MANUAL_REGISTER_CATEGORY_LABEL)
        SelectableField(
            placeholder = MANUAL_REGISTER_CATEGORY_PLACEHOLDER,
            value = value,
            trailingIconRes = R.drawable.ic_topbar_search,
            trailingIconSize = SELECTABLE_FIELD_SEARCH_ICON_SIZE,
            onClick = onClick,
        )
    }
}

@Composable
internal fun NameField(
    value: String,
    isDuplicateName: Boolean,
    onValueChange: (String) -> Unit,
) {
    val isOverLimit = value.length > MANUAL_REGISTER_NAME_MAX_LENGTH
    val isError = isOverLimit || isDuplicateName
    val supportingText =
        when {
            isOverLimit -> MANUAL_REGISTER_NAME_OVER_LIMIT_MESSAGE
            isDuplicateName -> MANUAL_REGISTER_NAME_DUPLICATE_MESSAGE
            else -> ""
        }
    val focus = rememberFocusBringIntoView()
    Column(
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        modifier =
            Modifier
                .bringIntoViewRequester(focus.requester)
                .onSizeChanged(focus.onSize),
    ) {
        FieldSectionHeader(label = MANUAL_REGISTER_NAME_LABEL)
        OBRitOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            supportingTextEnabled = isError,
            placeholder = MANUAL_REGISTER_NAME_PLACEHOLDER,
            maxLength = MANUAL_REGISTER_NAME_MAX_LENGTH,
            inputResultState = if (isError) InputResultState.Error else InputResultState.Default,
            supportingText = supportingText,
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onFocusEvent { focus.onFocus(it.isFocused) },
        )
    }
}

@Composable
internal fun LastReplaceDateField(
    selected: ReplacementPeriod?,
    onChange: (ReplacementPeriod?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        FieldSectionHeader(label = MANUAL_REGISTER_LAST_REPLACE_DATE_LABEL)
        ReplacementPeriodDropdown(
            selected = selected,
            onChange = onChange,
            placeholder = MANUAL_REGISTER_LAST_REPLACE_DATE_PLACEHOLDER,
        )
    }
}

@Composable
internal fun QuantityField(
    title: String,
    iconUrl: String,
    quantity: Int,
    totalCount: Int,
    onQuantityChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        FieldSectionHeader(label = MANUAL_REGISTER_QUANTITY_LABEL)
        QuantityCard(
            title = title.ifEmpty { MANUAL_REGISTER_QUANTITY_TITLE_PLACEHOLDER },
            iconUrl = iconUrl,
            totalCount = totalCount,
            quantity = quantity,
            onQuantityChange = onQuantityChange,
        )
        InfoNote(text = MANUAL_REGISTER_QUANTITY_HELP)
    }
}

@Composable
private fun SelectableField(
    placeholder: String,
    value: String,
    @DrawableRes trailingIconRes: Int,
    trailingIconSize: Dp,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val isEmpty = value.isEmpty()
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = SELECTABLE_FIELD_MIN_HEIGHT)
                .clip(SELECTABLE_FIELD_SHAPE)
                .background(colors.gray800)
                .clickable(onClick = onClick)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        Text(
            text = if (isEmpty) placeholder else value,
            style =
                typography.xl.copy(
                    color = if (isEmpty) colors.gray700 else colors.common00,
                    fontWeight = FontWeight.Medium,
                ),
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        Icon(
            painter = painterResource(id = trailingIconRes),
            contentDescription = null,
            modifier = Modifier.size(trailingIconSize),
            tint = Color.Unspecified,
        )
    }
}

@Composable
private fun InfoNote(text: String) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1_5.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier.size(INFO_NOTE_ICON_SIZE),
            tint = Color.Unspecified,
        )
        Text(
            text = text,
            style =
                typography.base.copy(
                    color = colors.gray500,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
    }
}

private const val MANUAL_REGISTER_CATEGORY_LABEL = "소모품 종류"
private const val MANUAL_REGISTER_CATEGORY_PLACEHOLDER = "소모품 종류를 선택해주세요"
private const val MANUAL_REGISTER_NAME_LABEL = "소모품 명"
private const val MANUAL_REGISTER_NAME_PLACEHOLDER = "구분을 위한 이름을 입력해주세요"
private const val MANUAL_REGISTER_NAME_MAX_LENGTH = 15
private const val MANUAL_REGISTER_NAME_OVER_LIMIT_MESSAGE = "소모품 명은 15자 이내로 입력해주세요"
private const val MANUAL_REGISTER_NAME_DUPLICATE_MESSAGE = "중복된 이름입니다. 다른 이름으로 입력해주세요."
private const val MANUAL_REGISTER_LAST_REPLACE_DATE_LABEL = "마지막 교체 일자"
private const val MANUAL_REGISTER_LAST_REPLACE_DATE_PLACEHOLDER = "마지막 교체 일자를 등록해주세요"
private const val MANUAL_REGISTER_QUANTITY_LABEL = "등록할 수량"
private const val MANUAL_REGISTER_QUANTITY_TITLE_PLACEHOLDER = "-"
private const val MANUAL_REGISTER_QUANTITY_HELP = "소모품의 전체 수량은 추후 수정할 수 있어요."

private val SELECTABLE_FIELD_MIN_HEIGHT = AtomSpacing.S14.dp
private val SELECTABLE_FIELD_SHAPE = RoundedCornerShape(AtomRadius.Middle.dp)
private val SELECTABLE_FIELD_SEARCH_ICON_SIZE = AtomSpacing.S6.dp
private val INFO_NOTE_ICON_SIZE = AtomSpacing.S4.dp
