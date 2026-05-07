package com.obrit.android.core.designsystem.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.color.AtomColors
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OBRitModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = OBRitBottomSheetScrimColor,
        dragHandle = null,
        tonalElevation = 0.dp,
    ) {
        OBRitBottomSheet(content = content)
    }
}

@Composable
fun OBRitBottomSheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalOBRitColor.current

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(OBRitBottomSheetShape)
                .background(colors.gray900)
                .padding(bottom = OBRitBottomSheetBottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(OBRitBottomSheetHeaderGap),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(OBRitBottomSheetHeaderPadding),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .width(OBRitBottomSheetHandleWidth)
                        .height(OBRitBottomSheetHandleHeight)
                        .clip(OBRitBottomSheetHandleShape)
                        .background(colors.gray250.copy(alpha = OBRitBottomSheetHandleAlpha)),
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = OBRitBottomSheetHorizontalPadding),
            content = content,
        )
    }
}

private val OBRitBottomSheetHeaderPadding = AtomSpacing.S4.dp
private val OBRitBottomSheetHeaderGap = AtomSpacing.S2_5.dp
private val OBRitBottomSheetHorizontalPadding = AtomSpacing.S5.dp
private val OBRitBottomSheetBottomPadding = AtomSpacing.S5.dp
private val OBRitBottomSheetHandleWidth = AtomSpacing.S8.dp
private val OBRitBottomSheetHandleHeight = AtomSpacing.S1.dp
private val OBRitBottomSheetShape =
    RoundedCornerShape(
        topStart = AtomRadius.BottomSheet.dp,
        topEnd = AtomRadius.BottomSheet.dp,
    )
private val OBRitBottomSheetHandleShape = RoundedCornerShape(AtomSpacing.S0_5.dp)
private val OBRitBottomSheetScrimColor = Color(AtomColors.CommonOpacity.CommonBlack.S00_60)
private const val OBRitBottomSheetHandleAlpha = 0.4f

@Preview(
    name = "OBRitBottomSheet",
    showBackground = true,
    backgroundColor = 0xFF101213,
)
@Composable
private fun OBRitBottomSheetPreview() {
    OBRitBottomSheetPreviewContainer {
        OBRitBottomSheet(content = {})
    }
}

@Composable
private fun OBRitBottomSheetPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier =
                Modifier
                    .background(OBRitBottomSheetScrimColor)
                    .padding(top = AtomSpacing.S20.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            content()
        }
    }
}
