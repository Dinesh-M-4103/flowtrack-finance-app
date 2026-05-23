package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryNeon,
    secondary = PrimaryCyan,
    tertiary = PurpleNeon,
    background = DarkBg,
    surface = CardDark,
    onPrimary = Color(0xFF080D1A),
    onSecondary = Color(0xFF080D1A),
    onTertiary = Color.White,
    onBackground = TextWhite,
    onSurface = TextWhite,
    outline = DarkBorder,
    error = NeonRed
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0F172A),
    secondary = Color(0xFF007A87),
    tertiary = PurpleNeon,
    background = LightBg,
    surface = LightCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark,
    outline = LightBorder,
    error = Color(0xFFE11D48)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
