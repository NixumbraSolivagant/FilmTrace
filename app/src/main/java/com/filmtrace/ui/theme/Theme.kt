package com.filmtrace.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = WarmAmber,
    onPrimary = DarkBackground,
    primaryContainer = WarmAmber.copy(alpha = 0.18f),
    onPrimaryContainer = WarmAmber,
    secondary = WarmOrange,
    onSecondary = DarkBackground,
    secondaryContainer = WarmOrange.copy(alpha = 0.18f),
    onSecondaryContainer = WarmOrange,
    tertiary = StatusStock,
    background = DarkBackground,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = MutedText,
    surfaceContainer = DarkSurfaceVariant,
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = DarkSurfaceHigh,
    outline = MutedText.copy(alpha = 0.4f),
    outlineVariant = MutedText.copy(alpha = 0.18f)
)

private val LightColorScheme = lightColorScheme(
    primary = WarmAmber,
    onPrimary = LightTextOnLight,
    primaryContainer = WarmAmber.copy(alpha = 0.15f),
    secondary = WarmOrange,
    background = LightBackground,
    onBackground = LightTextOnLight,
    surface = LightSurface,
    onSurface = LightTextOnLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4A4540)
)

@Composable
fun FilmTraceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
