package com.depromeet.obrit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OBRitColorScheme = darkColorScheme(
    primary = OBRitPrimary,
    onPrimary = OBRitTextPrimary,
    secondary = OBRitSecondary,
    onSecondary = OBRitBackground,
    background = OBRitBackground,
    onBackground = OBRitTextPrimary,
    surface = OBRitSurface,
    onSurface = OBRitTextPrimary,
    surfaceVariant = OBRitSurfaceVariant,
    onSurfaceVariant = OBRitTextSecondary
)

@Composable
fun OBRitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OBRitColorScheme,
        typography = Typography,
        content = content
    )
}

