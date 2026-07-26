package com.morseverse.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════
// MORSEVERSE COLOR SYSTEM — Authentic Nothing OS
// Warm monochrome, dot-matrix feel, strategic red accent,
// generous whitespace, subtle and refined
// ═══════════════════════════════════════════════════════════════════

// ── Nothing OS Signature ───────────────────────────────────────────
val NothingRed = Color(0xFFE74C3C)           // Softer red, not harsh #FF0000
val NothingRedSubtle = Color(0x40E74C3C)     // Very subtle red tint
val NothingRedDim = Color(0xFFB33A2E)

// NothingOS warm whites and grays (the KEY difference)
val NothingWhite = Color(0xFFF0EDE8)         // Warm off-white, not pure white
val NothingWhitePure = Color(0xFFF5F2ED)
val NothingCream = Color(0xFFE8E4DD)

// ── Primary - Muted Cyan ──────────────────────────────────────────
val MorseCyan = Color(0xFF5BC0BE)
val MorseCyanLight = Color(0xFF8DD4D2)
val MorseCyanDark = Color(0xFF3A9E9C)
val MorseCyanContainer = Color(0xFF1A2A2A)
val MorseCyanOnContainer = Color(0xFFB3E8E7)

// ── Secondary - Warm Amber ────────────────────────────────────────
val MorseAmber = Color(0xFFD4A574)
val MorseAmberLight = Color(0xFFE8C9A0)
val MorseAmberDark = Color(0xFFB08050)
val MorseAmberContainer = Color(0xFF2A2015)
val MorseAmberOnContainer = Color(0xFFF0DCC8)

// ── Tertiary - Muted Violet ───────────────────────────────────────
val MorseViolet = Color(0xFF9B8EC4)
val MorseVioletLight = Color(0xFFBBB0DC)
val MorseVioletDark = Color(0xFF7B6EA4)
val MorseVioletContainer = Color(0xFF1E1A2A)
val MorseVioletOnContainer = Color(0xFFD8D0EC)

// ── Success ───────────────────────────────────────────────────────
val MorseGreen = Color(0xFF7BC47F)
val MorseGreenLight = Color(0xFFA8DBAB)
val MorseGreenDark = Color(0xFF5AA45E)
val MorseGreenContainer = Color(0xFF152A15)
val MorseGreenOnContainer = Color(0xFFC8E8C9)

// ── Error ─────────────────────────────────────────────────────────
val MorseRed = Color(0xFFE07070)
val MorseRedLight = Color(0xFFECA0A0)
val MorseRedDark = Color(0xFFC05050)
val MorseRedContainer = Color(0xFF2A1515)
val MorseRedOnContainer = Color(0xFFF0D0D0)

// ── Warning ───────────────────────────────────────────────────────
val MorseYellow = Color(0xFFE8C864)
val MorseYellowContainer = Color(0xFF2A2510)

// ── Dark Theme — NothingOS warm dark ──────────────────────────────
val DarkBackground = Color(0xFF0A0A08)         // Slightly warm black
val DarkSurface = Color(0xFF121210)
val DarkSurfaceVariant = Color(0xFF1A1A18)
val DarkSurfaceElevated = Color(0xFF1E1E1C)
val DarkSurfaceCard = Color(0xFF141412)
val DarkOnBackground = Color(0xFFD8D4CE)       // Warm off-white text
val DarkOnSurface = Color(0xFFD0CCC6)
val DarkOnSurfaceVariant = Color(0xFF8A8680)   // Warm gray
val DarkOutline = Color(0xFF2A2824)            // Subtle warm border
val DarkOutlineVariant = Color(0xFF1E1C18)

// ── NothingOS Gray Scale (warm-tinted) ────────────────────────────
val NothingGray50 = Color(0xFFF5F2ED)
val NothingGray100 = Color(0xFFE8E4DD)
val NothingGray200 = Color(0xFFD4D0C8)
val NothingGray300 = Color(0xFFB8B4AC)
val NothingGray400 = Color(0xFF94908A)
val NothingGray500 = Color(0xFF706C66)
val NothingGray600 = Color(0xFF504C48)
val NothingGray700 = Color(0xFF383632)
val NothingGray800 = Color(0xFF242220)
val NothingGray900 = Color(0xFF141210)

// ── Light Theme ───────────────────────────────────────────────────
val LightBackground = Color(0xFFF5F2ED)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0EDE8)
val LightSurfaceElevated = Color(0xFFFFFFFF)
val LightSurfaceCard = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1A1814)
val LightOnSurface = Color(0xFF1A1814)
val LightOnSurfaceVariant = Color(0xFF6B6760)
val LightOutline = Color(0xFFD4D0C8)
val LightOutlineVariant = Color(0xFFE8E4DD)

// ── AMOLED Theme ──────────────────────────────────────────────────
val AmoledBackground = Color(0xFF000000)
val AmoledSurface = Color(0xFF080808)
val AmoledSurfaceVariant = Color(0xFF0E0E0E)
val AmoledSurfaceElevated = Color(0xFF141414)
val AmoledSurfaceCard = Color(0xFF0A0A0A)

// ── Mastery Level Colors (Nothing-style: grayscale + subtle accent)
val MasteryNovice = Color(0xFF484640)
val MasteryApprentice = Color(0xFF606058)
val MasteryJourneyman = Color(0xFF808078)
val MasteryExpert = Color(0xFFA8A8A0)
val MasteryMaster = Color(0xFFD0CCC6)
val MasteryGrandmaster = Color(0xFFE74C3C)     // Red = mastered

// ── Heatmap Colors ────────────────────────────────────────────────
val HeatmapEmpty = Color(0xFF141210)
val HeatmapLevel1 = Color(0xFF1E1C18)
val HeatmapLevel2 = Color(0xFF302E28)
val HeatmapLevel3 = Color(0xFF504E48)
val HeatmapLevel4 = Color(0xFFE74C3C)

// ── Chart Colors ──────────────────────────────────────────────────
val ChartColors = listOf(
    Color(0xFFD8D4CE),
    Color(0xFFE74C3C),
    Color(0xFFB0ACA6),
    Color(0xFF808078),
    Color(0xFF5BC0BE),
    Color(0xFFD4A574),
    Color(0xFF9B8EC4),
    Color(0xFF7BC47F),
)

// ── Gradient Colors ───────────────────────────────────────────────
val GradientCyanToViolet = listOf(MorseCyan, MorseViolet)
val GradientAmberToRed = listOf(MorseAmber, MorseRed)
val GradientGreenToCyan = listOf(MorseGreen, MorseCyan)
val GradientVioletToPink = listOf(MorseViolet, Color(0xFFD4A0B0))

val GradientNothingDark = listOf(Color(0xFF141210), Color(0xFF0A0A08))
val GradientNothingSubtle = listOf(Color(0xFF1E1C18), Color(0xFF121210))
val GradientNothingRed = listOf(NothingRed.copy(alpha = 0.15f), Color.Transparent)

// ── Tree-Specific Colors ──────────────────────────────────────────
val TreeNodeDot = Color(0xFFB0ACA6)           // Warm gray for dit
val TreeNodeDash = Color(0xFFE74C3C)          // Red for dah
val TreeNodeInactive = Color(0xFF2A2824)
val TreeEdgeColor = Color(0xFF2A2824)
val TreePathHighlight = Color(0xFFE74C3C)
val TreePathGlow = Color(0x20E74C3C)
val TreeCorrectGreen = Color(0xFF7BC47F)
val TreeWrongRed = Color(0xFFE07070)
