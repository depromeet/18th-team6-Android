package com.obrit.feature.register.screen.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun FieldSectionHeader(label: String) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style =
                typography.xl2.copy(
                    color = colors.common00,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
        Essential()
    }
}

@Composable
private fun Essential() {
    Icon(
        painter = painterResource(id = R.drawable.ic_essential),
        contentDescription = ESSENTIAL_CONTENT_DESCRIPTION,
        modifier = Modifier.size(ESSENTIAL_ICON_SIZE),
        tint = Color.Unspecified,
    )
}

private const val ESSENTIAL_CONTENT_DESCRIPTION = "필수 항목"
private val ESSENTIAL_ICON_SIZE = AtomSpacing.S6.dp
