package com.obrit.feature.register.screen.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

internal data class FocusBringIntoView(
    val requester: BringIntoViewRequester,
    val onFocus: (Boolean) -> Unit,
    val onSize: (IntSize) -> Unit,
)

// 포커스 시 IME 위로 필드가 스크롤되어 올라오도록 BringIntoView를 캡슐화.
@Composable
internal fun rememberFocusBringIntoView(): FocusBringIntoView {
    val requester = remember { BringIntoViewRequester() }
    var isFocused by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(isFocused, imeBottomPx, size) {
        if (isFocused && size.height > 0) {
            requester.bringIntoView(
                Rect(
                    left = 0f,
                    top = 0f,
                    right = size.width.toFloat(),
                    bottom = size.height + imeBottomPx.toFloat(),
                ),
            )
        }
    }
    return FocusBringIntoView(
        requester = requester,
        onFocus = { isFocused = it },
        onSize = { size = it },
    )
}
