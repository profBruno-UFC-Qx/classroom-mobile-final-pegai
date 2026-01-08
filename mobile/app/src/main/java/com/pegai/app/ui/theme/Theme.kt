package com.pegai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Gradiente que se ajusta ao tema
@Composable
fun brandGradient(): Brush {
    return if (isSystemInDarkTheme()) {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFF073D5C), Color(0xFF1B8270)) // Tons mais sóbrios
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(BlueDark, CyanPegai)
        )
    }
}

private val DarkColorScheme = darkColorScheme(
    primary = BlueMedium,
    secondary = CyanPegai,
    background = GrayDark,
    surface = GraySurface,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = BlueMedium,
    secondary = CyanPegai,
    background = BackgroundPremium,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun PegaiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}