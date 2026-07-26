package com.morseverse.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════
// MORSEVERSE COLOR SYSTEM — Nothing OS Inspired
// OLED-first, monochrome + red accent, geometric & minimal
// ═══════════════════════════════════════════════════════════════════

// ── Nothing OS Accent: Red ──────────────────────────────────────────
val NothingRed = Color(0xFFFF0000)
val NothingRedDim = Color(0xFFCC0000)
val NothingRedContainer = Color(0xFF1A0000)
val NothingRedOnContainer = Color(0xFFFF6666)

// ── Primary - Electric Cyan (functional accent) ────────────────────
val MorseCyan = Color(0xFF00D4FF)
val MorseCyanLight = Color(0xFF66E5FF)
val MorseCyanDark = Color(0xFF0099BB)
val MorseCyanContainer = Color(0xFF001F2A)
val MorseCyanOnContainer = Color(0xFFB3ECFF)

// ── Secondary - Warm Amber ─────────────────────────────────────────
val MorseAmber = Color(0xFFFFB020)
val MorseAmberLight = Color(0xFFFFD680)
val MorseAmberDark = Color(0xFFCC8C1A)
val MorseAmberContainer = Color(0xFF2A1D00)
val MorseAmberOnContainer = Color(0xFFFFF0D0)

// ── Tertiary - Violet ──────────────────────────────────────────────
val MorseViolet = Color(0xFFB388FF)
val MorseVioletLight = Color(0xFFD4B8FF)
val MorseVioletDark = Color(0xFF8B5CF6)
val MorseVioletContainer = Color(0xFF1A0D33)
val MorseVioletOnContainer = Color(0xFFE8D5FF)

// ── Success ────────────────────────────────────────────────────────
val MorseGreen = Color(0xFF22C55E)
val MorseGreenLight = Color(0xFF86EFAC)
val MorseGreenDark = Color(0xFF16A34A)
val MorseGreenContainer = Color(0xFF0A2A15)
val MorseGreenOnContainer = Color(0xFFBBF7D0)

// ── Error ──────────────────────────────────────────────────────────
val MorseRed = Color(0xFFEF4444)
val MorseRedLight = Color(0xFFFCA5A5)
val MorseRedDark = Color(0xFFDC2626)
val MorseRedContainer = Color(0xFF2A0A0A)
val MorseRedOnContainer = Color(0xFFFFD4D4)

// ── Warning ────────────────────────────────────────────────────────
val MorseYellow = Color(0xFFFBBF24)
val MorseYellowContainer = Color(0xFF2A2000)

// ── Nothing OS Surface Colors — Dark Theme ─────────────────────────
val DarkBackground = Color(0xFF000000)         // Pure black OLED
val DarkSurface = Color(0xFF0A0A0A)
val DarkSurfaceVariant = Color(0xFF111111)
val DarkSurfaceElevated = Color(0xFF161616)
val DarkSurfaceCard = Color(0xFF0D0D0D)
val DarkOnBackground = Color(0xFFE5E5E5)
val DarkOnSurface = Color(0xFFE5E5E5)
val DarkOnSurfaceVariant = Color(0xFF7A7A7A)
val DarkOutline = Color(0xFF222222)
val DarkOutlineVariant = Color(0xFF1A1A1A)

// Nothing-style subtle grays
val NothingGray100 = Color(0xFFE5E5E5)
val NothingGray200 = Color(0xFFCCCCCC)
val NothingGray300 = Color(0xFFAAAAAA)
val NothingGray400 = Color(0xFF888888)
val NothingGray500 = Color(0xFF666666)
val NothingGray600 = Color(0xFF444444)
val NothingGray700 = Color(0xFF333333)
val NothingGray800 = Color(0xFF222222)
val NothingGray900 = Color(0xFF111111)

// ── Surface Colors — Light Theme ───────────────────────────────────
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

// ── AMOLED Theme — Pure black everything ───────────────────────────
val AmoledBackground = Color(0xFF000000)
val AmoledSurface = Color(0xFF000000)
val AmoledSurfaceVariant = Color(0xFF0A0A0A)
val AmoledSurfaceElevated = Color(0xFF111111)
val AmoledSurfaceCard = Color(0xFF080808)

// ── Mastery Level Colors (Nothing-inspired: mostly monochrome) ─────
val MasteryNovice = Color(0xFF444444)
val MasteryApprentice = Color(0xFF666666)
val MasteryJourneyman = Color(0xFF888888)
val MasteryExpert = Color(0xFFBBBBBB)
val MasteryMaster = Color(0xFFE5E5E5)
val MasteryGrandmaster = Color(0xFFFF0000)  // Red = mastered (Nothing accent)

// ── Heatmap Colors ─────────────────────────────────────────────────
val HeatmapEmpty = Color(0xFF111111)
val HeatmapLevel1 = Color(0xFF1A1A1A)
val HeatmapLevel2 = Color(0xFF333333)
val HeatmapLevel3 = Color(0xFF666666)
val HeatmapLevel4 = Color(0xFFFF0000)  // Red = peak activity

// ── Chart Colors (Nothing-inspired: monochrome + red) ──────────────
val ChartColors = listOf(
    Color(0xFFE5E5E5), // White
    Color(0xFFFF0000), // Red (accent)
    Color(0xFF999999), // Light gray
    Color(0xFF666666), // Medium gray
    Color(0xFF444444), // Dark gray
    Color(0xFFCCCCCC), // Off-white
    Color(0xFF333333), // Darker gray
    Color(0xFFBBBBBB), // Silver
)

// ── Gradient Colors ────────────────────────────────────────────────
val GradientCyanToViolet = listOf(MorseCyan, MorseViolet)
val GradientAmberToRed = listOf(MorseAmber, MorseRed)
val GradientGreenToCyan = listOf(MorseGreen, MorseCyan)
val GradientVioletToPink = listOf(MorseViolet, Color(0xFFF472B6))

// Nothing-style gradients (mostly monochrome)
val GradientNothingDark = listOf(Color(0xFF111111), Color(0xFF000000))
val GradientNothingSubtle = listOf(Color(0xFF1A1A1A), Color(0xFF0D0D0D))
val GradientNothingRed = listOf(NothingRed.copy(alpha = 0.3f), Color.Transparent)

// ── Tree-Specific Colors ───────────────────────────────────────────
val TreeNodeDot = Color(0xFFE5E5E5)        // White for dit nodes
val TreeNodeDash = Color(0xFFFF0000)       // Red for dah nodes
val TreeNodeInactive = Color(0xFF333333)
val TreeEdgeColor = Color(0xFF333333)
val TreePathHighlight = Color(0xFFFF0000)  // Red path
val TreePathGlow = Color(0x33FF0000)       // Subtle red glow
val TreeCorrectGreen = Color(0xFF22C55E)
val TreeWrongRed = Color(0xFFEF4444)
