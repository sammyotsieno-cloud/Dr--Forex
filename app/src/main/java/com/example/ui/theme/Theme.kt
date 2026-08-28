package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

private val DarkPurpleColorScheme = darkColorScheme(
    primary = PurpleAccent,
    onPrimary = Color.White,
    primaryContainer = Purple900,
    onPrimaryContainer = Purple100,
    secondary = CyanNeon,
    onSecondary = DarkPurpleBackground,
    secondaryContainer = DarkPurpleSurfaceVariant,
    onSecondaryContainer = Color.White,
    tertiary = AmberNeon,
    onTertiary = DarkPurpleBackground,
    background = DarkPurpleBackground,
    onBackground = Purple50,
    surface = DarkPurpleSurface,
    onSurface = Purple50,
    surfaceVariant = DarkPurpleSurfaceVariant,
    onSurfaceVariant = Purple100,
    outline = DarkPurpleBorder,
    outlineVariant = DarkPurpleHighlight,
    error = RoseNeon,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = Purple100,
    onPrimaryContainer = Purple900,
    secondary = CyanNeon,
    onSecondary = Color.White,
    secondaryContainer = Purple50,
    onSecondaryContainer = DarkPurpleSurface,
    tertiary = AmberNeon,
    onTertiary = Color.White,
    background = Color(0xFFFBF8FF),
    onBackground = Color(0xFF1E1035),
    surface = Color.White,
    onSurface = Color(0xFF1E1035),
    surfaceVariant = Color(0xFFF3E8FF),
    onSurfaceVariant = Color(0xFF4C2882),
    outline = Color(0xFFE9D5FF),
    outlineVariant = Color(0xFFF3E8FF),
    error = PolishRose,
    onError = Color.White
)

@Composable
fun DrForexTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colorScheme = if (darkTheme) DarkPurpleColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


