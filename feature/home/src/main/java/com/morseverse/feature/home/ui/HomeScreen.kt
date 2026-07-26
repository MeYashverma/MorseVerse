package com.morseverse.feature.home.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.morseverse.core.designsystem.theme.*

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
                    Column {
                        Text(
                            "MorseVerse",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // XP Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MorseAmber.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { onNavigateToAchievements() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = MorseAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "${profile.totalXp}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MorseAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                        title = "Weak Characters",
                        subtitle = "Focus on these to improve",
                        icon = Icons.Filled.Warning,
                        iconColor = MorseAmber
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
                    title = "Explore",
                    subtitle = "Discover all features",
                    icon = Icons.Filled.Explore,
                    iconColor = MorseCyan
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
                        title = "Recent Activity",
                        subtitle = "Your latest sessions",
                        icon = Icons.Filled.History,
                        iconColor = MorseViolet
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
// COMPONENTS
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun StreakGoalCard(
    streak: Int,
    dailyMinutes: Int,
    goalMinutes: Int,
    dailyGoalMet: Boolean
) {
    val progress = (dailyMinutes.toFloat() / goalMinutes).coerceIn(0f, 1f)
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                            .clip(CircleShape)
                            .background(
                                if (streak > 0) MorseAmber.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "🔥",
                            fontSize = 24.sp
                        )
                    }
                    Column {
                        Text(
                            "$streak",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (streak > 0) MorseAmber else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "day streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Daily Goal
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "$dailyMinutes / $goalMinutes min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (dailyGoalMet) "Goal completed! ✓" else "Daily goal",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dailyGoalMet) MorseGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (dailyGoalMet) GradientGreenToCyan else GradientCyanToViolet
                            )
                        )
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
            label = "Accuracy",
            icon = Icons.Filled.GpsFixed,
            color = MorseGreen
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = "${wpm.toInt()}",
            label = "WPM",
            icon = Icons.Filled.Speed,
            color = MorseCyan
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = formatNumber(totalChars),
            label = "Characters",
            icon = Icons.Filled.TextFields,
            color = MorseAmber
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = "$sessions",
            label = "Sessions",
            icon = Icons.Filled.History,
            color = MorseViolet
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContinueLessonCard(onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MorseCyan.copy(alpha = glowAlpha),
                                MorseCyan.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = MorseCyan,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Continue Learning",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Pick up where you left off",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            label = "Practice",
            color = MorseCyan,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onPractice()
            }
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Translate,
            label = "Translate",
            color = MorseAmber,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTranslate()
            }
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.AccountTree,
            label = "Tree",
            color = MorseGreen,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTree()
            }
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Rocket,
            label = "Story",
            color = MorseViolet,
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
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = color,
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
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    .size(56.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onCharacterClick(char)
                    },
                shape = RoundedCornerShape(16.dp),
                color = MorseRed.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MorseRed.copy(alpha = 0.3f)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        char,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MorseRed
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPracticeAll()
                    },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = "Practice all",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
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
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MorseAmber.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = MorseAmber,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Today's Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Decode 10 words at 25 WPM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MorseAmber.copy(alpha = 0.15f)
            ) {
                Text(
                    "+50 XP",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MorseAmber,
                    fontWeight = FontWeight.Bold
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
                title = "Learning",
                subtitle = "Koch, Farnsworth, Traditional",
                gradientColors = GradientCyanToViolet,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onLearn()
                }
            )
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.BarChart,
                title = "Statistics",
                subtitle = "Track your progress",
                gradientColors = GradientGreenToCyan,
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
                title = "Ham Radio",
                subtitle = "Q Codes, Phonetic, more",
                gradientColors = GradientAmberToRed,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onHam()
                }
            )
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.EmojiEvents,
                title = "Achievements",
                subtitle = "Unlock rewards",
                gradientColors = GradientVioletToPink,
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
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Box {
            // Gradient accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Brush.horizontalGradient(gradientColors))
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
                    tint = gradientColors.first(),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MorseViolet.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.FitnessCenter,
                    contentDescription = null,
                    tint = MorseViolet,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    mode,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${(accuracy * 100).toInt()}% accuracy • ${wpm.toInt()} WPM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                "+$xpEarned XP",
                style = MaterialTheme.typography.labelLarge,
                color = MorseAmber,
                fontWeight = FontWeight.Bold
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
