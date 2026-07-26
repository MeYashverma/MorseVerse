package com.morseverse.core.designsystem.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════════
// MORSEVERSE THEME SYSTEM
// Supports: Light, Dark, AMOLED, Material You
// ═══════════════════════════════════════════════════════════════════

enum class ThemeMode { LIGHT, DARK, AMOLED, MATERIAL_YOU }

private val DarkColorScheme = darkColorScheme(
    primary = MorseCyan,
    onPrimary = Color.Black,
    primaryContainer = MorseCyanContainer,
    onPrimaryContainer = MorseCyanOnContainer,
    secondary = MorseAmber,
    onSecondary = Color.Black,
    secondaryContainer = MorseAmberContainer,
    onSecondaryContainer = MorseAmberOnContainer,
    tertiary = MorseViolet,
    onTertiary = Color.Black,
    tertiaryContainer = MorseVioletContainer,
    onTertiaryContainer = MorseVioletOnContainer,
    error = MorseRed,
    onError = Color.Black,
    errorContainer = MorseRedContainer,
    onErrorContainer = MorseRedOnContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = Color(0xFFE5E5E5),
    inverseOnSurface = Color(0xFF1A1A1A),
    inversePrimary = MorseCyanDark,
    surfaceTint = MorseCyan
)

private val LightColorScheme = lightColorScheme(
    primary = MorseCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0F0FF),
    onPrimaryContainer = Color(0xFF001F2A),
    secondary = MorseAmberDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF0D0),
    onSecondaryContainer = Color(0xFF2A1D00),
    tertiary = MorseVioletDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8D5FF),
    onTertiaryContainer = Color(0xFF1A0D33),
    error = MorseRedDark,
    onError = Color.White,
    errorContainer = Color(0xFFFFD4D4),
    onErrorContainer = Color(0xFF2A0A0A),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    inverseSurface = Color(0xFF2A2A2A),
    inverseOnSurface = Color(0xFFF0F0F0),
    inversePrimary = MorseCyan,
    surfaceTint = MorseCyanDark
)

private val AmoledColorScheme = darkColorScheme(
    primary = MorseCyan,
    onPrimary = Color.Black,
    primaryContainer = MorseCyanContainer,
    onPrimaryContainer = MorseCyanOnContainer,
    secondary = MorseAmber,
    onSecondary = Color.Black,
    secondaryContainer = MorseAmberContainer,
    onSecondaryContainer = MorseAmberOnContainer,
    tertiary = MorseViolet,
    onTertiary = Color.Black,
    tertiaryContainer = MorseVioletContainer,
    onTertiaryContainer = MorseVioletOnContainer,
    error = MorseRed,
    onError = Color.Black,
    errorContainer = MorseRedContainer,
    onErrorContainer = MorseRedOnContainer,
    background = AmoledBackground,
    onBackground = DarkOnBackground,
    surface = AmoledSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = Color(0xFFE5E5E5),
    inverseOnSurface = Color.Black,
    inversePrimary = MorseCyanDark,
    surfaceTint = MorseCyan
)

@Composable
fun MorseVerseTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isSystemInDarkTheme()) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        themeMode == ThemeMode.LIGHT -> LightColorScheme
        themeMode == ThemeMode.AMOLED -> AmoledColorScheme
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = themeMode == ThemeMode.LIGHT
                isAppearanceLightNavigationBars = themeMode == ThemeMode.LIGHT
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MorseVerseTypography,
        content = content
    )
}

// Extension for easy access to custom colors
object MorseVerseColors {
    val cyan get() = MorseCyan
    val amber get() = MorseAmber
    val violet get() = MorseViolet
    val green get() = MorseGreen
    val red get() = MorseRed
    val yellow get() = MorseYellow

    val masteryNovice get() = MasteryNovice
    val masteryApprentice get() = MasteryApprentice
    val masteryJourneyman get() = MasteryJourneyman
    val masteryExpert get() = MasteryExpert
    val masteryMaster get() = MasteryMaster
    val masteryGrandmaster get() = MasteryGrandmaster

    val chartColors get() = ChartColors
}
