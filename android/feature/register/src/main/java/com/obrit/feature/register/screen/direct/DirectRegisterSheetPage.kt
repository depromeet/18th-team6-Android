package com.obrit.feature.register.screen.direct

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.screen.common.ItemNameField
import com.obrit.feature.register.viewmodel.DirectRegisterSideEffect
import com.obrit.feature.register.viewmodel.DirectRegisterUiState
import com.obrit.feature.register.viewmodel.DirectRegisterViewModel
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun DirectRegisterSheetPage(
    initialName: String,
    onBack: () -> Unit,
    onRegister: (Category) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DirectRegisterViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onOpen(initialName)
    }

    DirectRegisterSheetContent(
        state = state,
        action =
            DirectRegisterSheetAction(
                onNameChange = viewModel::onNameChange,
                onSubmit = viewModel::onSubmit,
                onBack = onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DirectRegisterSideEffect.OnRegistered -> onRegister(sideEffect.category)
        }
    }
}

private data class DirectRegisterSheetAction(
    val onNameChange: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
)

@Composable
private fun DirectRegisterSheetContent(
    state: DirectRegisterUiState,
    action: DirectRegisterSheetAction,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding(),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S6.dp),
    ) {
        DirectRegisterSheetHeader(onBack = action.onBack)
        ItemNameField(
            value = state.name,
            error = state.nameError,
            placeholder = DIRECT_REGISTER_NAME_PLACEHOLDER,
            headerGap = AtomSpacing.S3.dp,
            onValueChange = action.onNameChange,
        )
        Spacer(modifier = Modifier.weight(1f))
        OBRitLargeFilledButton(
            onClick = action.onSubmit,
            enabled = state.isSubmitEnabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = DIRECT_REGISTER_SUBMIT_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun DirectRegisterSheetHeader(onBack: () -> Unit) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_topbar_arrow_left),
            contentDescription = DIRECT_REGISTER_BACK_DESCRIPTION,
            tint = colors.common00,
            modifier =
                Modifier
                    .size(AtomSpacing.S7.dp)
                    .clickable(onClick = onBack),
        )
        Text(
            text = DIRECT_REGISTER_TITLE,
            style = typography.xl5.copy(color = colors.common00, fontWeight = FontWeight.SemiBold),
        )
    }
}

private const val DIRECT_REGISTER_TITLE = "직접 등록하기"
private const val DIRECT_REGISTER_BACK_DESCRIPTION = "소모품 목록으로 돌아가기"
private const val DIRECT_REGISTER_NAME_PLACEHOLDER = "이름을 입력해주세요"
private const val DIRECT_REGISTER_SUBMIT_LABEL = "추가하기"

@Preview(name = "DirectRegisterSheet Empty", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun DirectRegisterSheetEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        DirectRegisterSheetContent(
            state = DirectRegisterUiState(),
            action = DirectRegisterSheetAction(onNameChange = {}, onSubmit = {}, onBack = {}),
        )
    }
}
