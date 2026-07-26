package com.morseverse.feature.home.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.morseverse.core.designsystem.theme.*

// ═══════════════════════════════════════════════════════════════════
// HOME SCREEN — Nothing OS Aesthetic
// Pure black OLED, monochrome + red accent, geometric & minimal
// ═══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToTree: () -> Unit,
    onNavigateToTranslator: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToStory: () -> Unit,
    onNavigateToHam: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCharacter: (String) -> Unit,
    onNavigateToPracticeSession: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val profile by viewModel.userProfile.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val weakCharacters by viewModel.weakCharacters.collectAsState()
    val recentSessions by viewModel.recentSessions.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "MORSEVERSE",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    // XP Badge — Nothing-style: outlined, sharp corners
                    Surface(
                        shape = RoundedCornerShape(0.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                        modifier = Modifier.clickable { onNavigateToAchievements() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = NothingRed,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${profile.totalXp}",
                                style = MaterialTheme.typography.labelLarge,
                                color = DarkOnBackground,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = DarkOnSurfaceVariant
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ─── STREAK & DAILY GOAL CARD ──────────────────────────────
            item {
                StreakGoalCard(
                    streak = profile.currentStreak,
                    dailyMinutes = dailyStats.totalPracticeMinutes,
                    goalMinutes = profile.dailyGoalMinutes,
                    dailyGoalMet = dailyStats.dailyGoalMet
                )
            }

            // ─── QUICK STATS ROW ───────────────────────────────────────
            item {
                QuickStatsRow(
                    accuracy = profile.currentAccuracy,
                    wpm = profile.currentWpm.toFloat(),
                    totalChars = profile.totalCharacters,
                    sessions = profile.totalSessions
                )
            }

            // ─── CONTINUE LESSON ───────────────────────────────────────
            item {
                ContinueLessonCard(
                    onClick = { onNavigateToPracticeSession("CHARACTER") }
                )
            }

            // ─── QUICK ACTIONS ─────────────────────────────────────────
            item {
                QuickActionsSection(
                    onPractice = { onNavigateToPractice() },
                    onTranslate = { onNavigateToTranslator() },
                    onTree = { onNavigateToTree() },
                    onStory = { onNavigateToStory() }
                )
            }

            // ─── WEAK CHARACTERS ───────────────────────────────────────
            if (weakCharacters.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "WEAK CHARACTERS",
                        subtitle = "Focus on these to improve",
                        icon = Icons.Filled.Warning,
                        iconColor = NothingRed
                    )
                }

                item {
                    WeakCharactersRow(
                        characters = weakCharacters.take(8),
                        onCharacterClick = { onNavigateToCharacter(it) },
                        onPracticeAll = { onNavigateToPracticeSession("WEAK") }
                    )
                }
            }

            // ─── TODAY'S CHALLENGE ─────────────────────────────────────
            item {
                TodayChallengeCard(
                    onClick = { onNavigateToPracticeSession("DAILY") }
                )
            }

            // ─── FEATURE CARDS ─────────────────────────────────────────
            item {
                SectionHeader(
                    title = "EXPLORE",
                    subtitle = "Discover all features",
                    icon = Icons.Filled.Explore,
                    iconColor = DarkOnSurfaceVariant
                )
            }

            item {
                FeatureCardsGrid(
                    onLearn = onNavigateToLearn,
                    onStatistics = onNavigateToStatistics,
                    onHam = onNavigateToHam,
                    onAchievements = onNavigateToAchievements
                )
            }

            // ─── RECENT ACTIVITY ───────────────────────────────────────
            if (recentSessions.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "RECENT ACTIVITY",
                        subtitle = "Your latest sessions",
                        icon = Icons.Filled.History,
                        iconColor = DarkOnSurfaceVariant
                    )
                }

                items(recentSessions.take(5)) { session ->
                    RecentSessionCard(
                        mode = session.mode.displayName,
                        accuracy = session.accuracy,
                        wpm = session.averageWpm,
                        xpEarned = session.xpEarned,
                        timestamp = session.startTime
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// COMPONENTS — Nothing OS Aesthetic
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun StreakGoalCard(
    streak: Int,
    dailyMinutes: Int,
    goalMinutes: Int,
    dailyGoalMet: Boolean
) {
    val progress = (dailyMinutes.toFloat() / goalMinutes).coerceIn(0f, 1f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(0.dp), // Nothing: sharp corners
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                1.dp,
                                if (streak > 0) NothingRed else DarkOutline,
                                RoundedCornerShape(0.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${streak}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (streak > 0) NothingRed else DarkOnSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column {
                        Text(
                            "STREAK",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkOnSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "$streak days",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkOnBackground
                        )
                    }
                }

                // Daily Goal
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "$dailyMinutes / $goalMinutes MIN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = DarkOnBackground
                    )
                    Text(
                        if (dailyGoalMet) "COMPLETED ✓" else "DAILY GOAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (dailyGoalMet) MorseGreen else DarkOnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Progress Bar — Nothing-style: thin, red accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(DarkOutline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(if (dailyGoalMet) MorseGreen else NothingRed)
                )
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    accuracy: Float,
    wpm: Float,
    totalChars: Int,
    sessions: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = "${(accuracy * 100).toInt()}%",
            label = "ACCURACY",
            color = DarkOnBackground
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = "${wpm.toInt()}",
            label = "WPM",
            color = NothingRed
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = formatNumber(totalChars),
            label = "CHARS",
            color = DarkOnBackground
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = "$sessions",
            label = "SESSIONS",
            color = DarkOnBackground
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = DarkOnSurfaceVariant,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun ContinueLessonCard(onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = RoundedCornerShape(0.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            NothingRed.copy(alpha = borderAlpha)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, NothingRed, RoundedCornerShape(0.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = NothingRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "CONTINUE LEARNING",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = DarkOnBackground
                )
                Text(
                    "Pick up where you left off",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkOnSurfaceVariant
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = DarkOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onPractice: () -> Unit,
    onTranslate: () -> Unit,
    onTree: () -> Unit,
    onStory: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.FitnessCenter,
            label = "PRACTICE",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onPractice()
            }
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Translate,
            label = "TRANSLATE",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTranslate()
            }
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.AccountTree,
            label = "TREE",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTree()
            }
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Rocket,
            label = "STORY",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onStory()
            }
        )
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = DarkOnBackground,
                modifier = Modifier.size(22.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = DarkOnSurfaceVariant,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = DarkOnBackground
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = DarkOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeakCharactersRow(
    characters: List<String>,
    onCharacterClick: (String) -> Unit,
    onPracticeAll: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(characters) { char ->
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onCharacterClick(char)
                    },
                shape = RoundedCornerShape(0.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, NothingRed.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        char,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NothingRed,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPracticeAll()
                    },
                shape = RoundedCornerShape(0.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = "Practice all",
                        tint = DarkOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayChallengeCard(onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = RoundedCornerShape(0.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, NothingRed, RoundedCornerShape(0.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = NothingRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "TODAY'S CHALLENGE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = DarkOnBackground
                )
                Text(
                    "Decode 10 words at 25 WPM",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkOnSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(0.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, NothingRed)
            ) {
                Text(
                    "+50 XP",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = NothingRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun FeatureCardsGrid(
    onLearn: () -> Unit,
    onStatistics: () -> Unit,
    onHam: () -> Unit,
    onAchievements: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.School,
                title = "LEARNING",
                subtitle = "Koch, Farnsworth, Traditional",
                accentColor = DarkOnBackground,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onLearn()
                }
            )
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.BarChart,
                title = "STATISTICS",
                subtitle = "Track your progress",
                accentColor = NothingRed,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onStatistics()
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.CellTower,
                title = "HAM RADIO",
                subtitle = "Q Codes, Phonetic, more",
                accentColor = DarkOnBackground,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onHam()
                }
            )
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.EmojiEvents,
                title = "ACHIEVEMENTS",
                subtitle = "Unlock rewards",
                accentColor = NothingRed,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onAchievements()
                }
            )
        }
    }
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Box {
            // Top accent line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = DarkOnBackground
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecentSessionCard(
    mode: String,
    accuracy: Float,
    wpm: Float,
    xpEarned: Int,
    timestamp: Long
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(0.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, DarkOutline, RoundedCornerShape(0.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.FitnessCenter,
                    contentDescription = null,
                    tint = DarkOnSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    mode.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = DarkOnBackground
                )
                Text(
                    "${(accuracy * 100).toInt()}% accuracy • ${wpm.toInt()} WPM",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkOnSurfaceVariant
                )
            }

            Text(
                "+$xpEarned XP",
                style = MaterialTheme.typography.labelLarge,
                color = NothingRed,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// HELPERS
// ═══════════════════════════════════════════════════════════════════

private fun formatNumber(n: Int): String {
    return when {
        n >= 1_000_000 -> "${"%.1f".format(n / 1_000_000.0)}M"
        n >= 1_000 -> "${"%.1f".format(n / 1_000.0)}K"
        else -> "$n"
    }
}
