package com.morseverse.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════
// MORSEVERSE COLOR SYSTEM
// OLED-first, Nothing OS inspired, Material 3 Expressive
// ═══════════════════════════════════════════════════════════════════

// Primary - Electric Cyan (Nothing OS inspired)
val MorseCyan = Color(0xFF00D4FF)
val MorseCyanLight = Color(0xFF66E5FF)
val MorseCyanDark = Color(0xFF0099BB)
val MorseCyanContainer = Color(0xFF001F2A)
val MorseCyanOnContainer = Color(0xFFB3ECFF)

// Secondary - Warm Amber
val MorseAmber = Color(0xFFFFB020)
val MorseAmberLight = Color(0xFFFFD680)
val MorseAmberDark = Color(0xFFCC8C1A)
val MorseAmberContainer = Color(0xFF2A1D00)
val MorseAmberOnContainer = Color(0xFFFFF0D0)

// Tertiary - Violet
val MorseViolet = Color(0xFFB388FF)
val MorseVioletLight = Color(0xFFD4B8FF)
val MorseVioletDark = Color(0xFF8B5CF6)
val MorseVioletContainer = Color(0xFF1A0D33)
val MorseVioletOnContainer = Color(0xFFE8D5FF)

// Success
val MorseGreen = Color(0xFF22C55E)
val MorseGreenLight = Color(0xFF86EFAC)
val MorseGreenDark = Color(0xFF16A34A)
val MorseGreenContainer = Color(0xFF0A2A15)
val MorseGreenOnContainer = Color(0xFFBBF7D0)

// Error
val MorseRed = Color(0xFFEF4444)
val MorseRedLight = Color(0xFFFCA5A5)
val MorseRedDark = Color(0xFFDC2626)
val MorseRedContainer = Color(0xFF2A0A0A)
val MorseRedOnContainer = Color(0xFFFFD4D4)

// Warning
val MorseYellow = Color(0xFFFBBF24)
val MorseYellowContainer = Color(0xFF2A2000)

// Surface Colors - Dark Theme
val DarkBackground = Color(0xFF000000)     // Pure black for OLED
val DarkSurface = Color(0xFF0A0A0A)
val DarkSurfaceVariant = Color(0xFF141414)
val DarkSurfaceElevated = Color(0xFF1A1A1A)
val DarkSurfaceCard = Color(0xFF111111)
val DarkOnBackground = Color(0xFFE5E5E5)
val DarkOnSurface = Color(0xFFE5E5E5)
val DarkOnSurfaceVariant = Color(0xFF8A8A8A)
val DarkOutline = Color(0xFF2A2A2A)
val DarkOutlineVariant = Color(0xFF1F1F1F)

// Surface Colors - Light Theme
val LightBackground = Color(0xFFF8F9FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F3F5)
val LightSurfaceElevated = Color(0xFFFFFFFF)
val LightSurfaceCard = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1A1A1A)
val LightOnSurface = Color(0xFF1A1A1A)
val LightOnSurfaceVariant = Color(0xFF6B7280)
val LightOutline = Color(0xFFD1D5DB)
val LightOutlineVariant = Color(0xFFE5E7EB)

// AMOLED Theme - Pure black everything
val AmoledBackground = Color(0xFF000000)
val AmoledSurface = Color(0xFF000000)
val AmoledSurfaceVariant = Color(0xFF0A0A0A)
val AmoledSurfaceElevated = Color(0xFF111111)
val AmoledSurfaceCard = Color(0xFF080808)

// Mastery Level Colors
val MasteryNovice = Color(0xFF6B7280)
val MasteryApprentice = Color(0xFF3B82F6)
val MasteryJourneyman = Color(0xFF8B5CF6)
val MasteryExpert = Color(0xFFF59E0B)
val MasteryMaster = Color(0xFFEF4444)
val MasteryGrandmaster = Color(0xFFFFD700)

// Heatmap Colors
val HeatmapEmpty = Color(0xFF1A1A1A)
val HeatmapLevel1 = Color(0xFF0E4429)
val HeatmapLevel2 = Color(0xFF006D32)
val HeatmapLevel3 = Color(0xFF26A641)
val HeatmapLevel4 = Color(0xFF39D353)

// Chart Colors
val ChartColors = listOf(
    Color(0xFF00D4FF), // Cyan
    Color(0xFFFFB020), // Amber
    Color(0xFFB388FF), // Violet
    Color(0xFF22C55E), // Green
    Color(0xFFEF4444), // Red
    Color(0xFFF472B6), // Pink
    Color(0xFF67E8F9), // Light Cyan
    Color(0xFFFCD34D), // Yellow
)

// Gradient Colors
val GradientCyanToViolet = listOf(MorseCyan, MorseViolet)
val GradientAmberToRed = listOf(MorseAmber, MorseRed)
val GradientGreenToCyan = listOf(MorseGreen, MorseCyan)
val GradientVioletToPink = listOf(MorseViolet, Color(0xFFF472B6))
