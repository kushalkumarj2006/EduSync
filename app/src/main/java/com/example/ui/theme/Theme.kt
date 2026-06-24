package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    secondary = AccentOrangeDark,
    tertiary = SuccessGreen,
    background = BackgroundDark,
    surface = SurfaceCardDark,
    onBackground = TextLightSlate,
    onSurface = TextLightSlate,
    onPrimary = BackgroundDark,
    onSecondary = BackgroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = AccentOrange,
    tertiary = SuccessGreen,
    background = BackgroundLight,
    surface = SurfaceCardLight,
    onBackground = TextDeepSlate,
    onSurface = TextDeepSlate,
    onPrimary = SurfaceCardLight,
    onSecondary = SurfaceCardLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic colors to enforce the beautiful cohesive brand palette (Blue + Orange)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
