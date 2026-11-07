package com.obrit.feature.register.screen.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import com.obrit.android.core.designsystem.component.textfield.InputResultState
import com.obrit.android.core.designsystem.component.textfield.OBRitOutlinedTextField
import com.obrit.feature.register.viewmodel.ITEM_NAME_MAX_LENGTH
import com.obrit.feature.register.viewmodel.ItemNameError

@Composable
internal fun ItemNameField(
    value: String,
    error: ItemNameError?,
    placeholder: String,
    headerGap: Dp,
    onValueChange: (String) -> Unit,
) {
    val focus = rememberFocusBringIntoView()
    Column(
        verticalArrangement = Arrangement.spacedBy(headerGap),
        modifier =
            Modifier
                .bringIntoViewRequester(focus.requester)
                .onSizeChanged(focus.onSize),
    ) {
        FieldSectionHeader(label = ITEM_NAME_LABEL)
        OBRitOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            supportingTextEnabled = error != null,
            placeholder = placeholder,
            maxLength = ITEM_NAME_MAX_LENGTH,
            inputResultState = if (error != null) InputResultState.Error else InputResultState.Default,
            supportingText = error?.toMessage().orEmpty(),
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onFocusEvent { focus.onFocus(it.isFocused) },
        )
    }
}

internal fun ItemNameError.toMessage(): String =
    when (this) {
        ItemNameError.TooLong -> ITEM_NAME_TOO_LONG_MESSAGE
        ItemNameError.InvalidCharacter -> ITEM_NAME_INVALID_CHARACTER_MESSAGE
        ItemNameError.InvalidSpacing -> ITEM_NAME_INVALID_SPACING_MESSAGE
        ItemNameError.Duplicated -> ITEM_NAME_DUPLICATED_MESSAGE
    }

private const val ITEM_NAME_LABEL = "이름"
private const val ITEM_NAME_TOO_LONG_MESSAGE = "이름은 15자 이내로 입력해주세요"
private const val ITEM_NAME_INVALID_CHARACTER_MESSAGE = "한글 및 영문, 숫자만 입력 가능합니다."
private const val ITEM_NAME_INVALID_SPACING_MESSAGE = "공백은 단어 사이에 한 칸만 입력할 수 있어요"
private const val ITEM_NAME_DUPLICATED_MESSAGE = "이미 같은 이름의 소모품이 있어요"
