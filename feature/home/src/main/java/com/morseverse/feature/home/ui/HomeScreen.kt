package com.morseverse.feature.home.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
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
                    Text("morseverse", style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Normal, letterSpacing = 4.sp, color = DarkOnBackground)
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(0.dp), color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray700),
                        modifier = Modifier.clickable { onNavigateToAchievements() }
                    ) {
                        Row(
                            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("xp", style = MaterialTheme.typography.labelSmall,
                                color = NothingGray500, letterSpacing = 2.sp)
                            Text("${profile.totalXp}", style = MaterialTheme.typography.labelLarge,
                                color = DarkOnBackground, fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.Monospace)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(Icons.Outlined.Settings, "Settings", tint = NothingGray500)
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(bottom = 24.dp)) {
            item { StreakGoalCard(profile.currentStreak, dailyStats.totalPracticeMinutes, profile.dailyGoalMinutes, dailyStats.dailyGoalMet) }
            item { QuickStatsRow(profile.currentAccuracy, profile.currentWpm.toFloat(), profile.totalCharacters, profile.totalSessions) }
            item { ContinueLessonCard { onNavigateToPracticeSession("CHARACTER") } }
            item { QuickActionsSection(onNavigateToPractice, onNavigateToTranslator, onNavigateToTree, onNavigateToStory) }
            if (weakCharacters.isNotEmpty()) {
                item { SectionHeader("weak characters", "focus on these", Icons.Filled.Warning, NothingRed) }
                item { WeakCharactersRow(weakCharacters.take(8), onNavigateToCharacter) { onNavigateToPracticeSession("WEAK") } }
            }
            item { TodayChallengeCard { onNavigateToPracticeSession("DAILY") } }
            item { SectionHeader("explore", "discover all features", Icons.Filled.Explore, NothingGray500) }
            item { FeatureCardsGrid(onNavigateToLearn, onNavigateToStatistics, onNavigateToHam, onNavigateToAchievements) }
            if (recentSessions.isNotEmpty()) {
                item { SectionHeader("recent activity", "your latest sessions", Icons.Filled.History, NothingGray500) }
                items(recentSessions.take(5)) { s ->
                    RecentSessionCard(s.mode.displayName, s.accuracy, s.averageWpm, s.xpEarned, s.startTime)
                }
            }
        }
    }
}

@Composable
private fun StreakGoalCard(streak: Int, dailyMinutes: Int, goalMinutes: Int, dailyGoalMet: Boolean) {
    val progress = (dailyMinutes.toFloat() / goalMinutes).coerceIn(0f, 1f)
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        shape = RoundedCornerShape(0.dp), color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(44.dp).border(0.5.dp,
                            if (streak > 0) NothingRed.copy(alpha = 0.5f) else NothingGray800,
                            RoundedCornerShape(0.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$streak", style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Normal,
                            color = if (streak > 0) NothingRed else NothingGray500,
                            fontFamily = FontFamily.Monospace)
                    }
                    Column {
                        Text("streak", style = MaterialTheme.typography.labelSmall, color = NothingGray500, letterSpacing = 2.sp)
                        Text("$streak days", style = MaterialTheme.typography.bodyMedium, color = DarkOnBackground)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$dailyMinutes / $goalMinutes min", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal, fontFamily = FontFamily.Monospace, color = DarkOnBackground)
                    Text(if (dailyGoalMet) "completed ✓" else "daily goal", style = MaterialTheme.typography.labelSmall,
                        color = if (dailyGoalMet) MorseGreen else NothingGray500, letterSpacing = 1.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(1.5.dp).background(NothingGray800)) {
                Box(Modifier.fillMaxWidth(progress).fillMaxHeight()
                    .background(if (dailyGoalMet) MorseGreen.copy(alpha = 0.7f) else NothingGray400))
            }
        }
    }
}

@Composable
private fun QuickStatsRow(accuracy: Float, wpm: Float, totalChars: Int, sessions: Int) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(Modifier.weight(1f), "${(accuracy * 100).toInt()}%", "accuracy")
        StatCard(Modifier.weight(1f), "${wpm.toInt()}", "wpm")
        StatCard(Modifier.weight(1f), formatNumber(totalChars), "chars")
        StatCard(Modifier.weight(1f), "$sessions", "sessions")
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String) {
    Surface(
        modifier = modifier, shape = RoundedCornerShape(0.dp), color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Normal,
                color = DarkOnBackground, fontFamily = FontFamily.Monospace)
            Text(label, style = MaterialTheme.typography.labelSmall, color = NothingGray500, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun ContinueLessonCard(onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val borderAlpha by infiniteTransition.animateFloat(0.2f, 0.6f,
        infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "ba")

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onClick() },
        shape = RoundedCornerShape(0.dp), color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingRed.copy(alpha = borderAlpha))
    ) {
        Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).border(0.5.dp, NothingGray700, RoundedCornerShape(0.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.PlayArrow, null, tint = NothingGray400, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("continue learning", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal, letterSpacing = 1.sp, color = DarkOnBackground)
                Text("pick up where you left off", style = MaterialTheme.typography.bodySmall, color = NothingGray500)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = NothingGray600)
        }
    }
}

@Composable
private fun QuickActionsSection(onPractice: () -> Unit, onTranslate: () -> Unit, onTree: () -> Unit, onStory: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        QuickActionButton(Modifier.weight(1f), Icons.Filled.FitnessCenter, "practice") { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onPractice() }
        QuickActionButton(Modifier.weight(1f), Icons.Filled.Translate, "translate") { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onTranslate() }
        QuickActionButton(Modifier.weight(1f), Icons.Filled.AccountTree, "tree") { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onTree() }
        QuickActionButton(Modifier.weight(1f), Icons.Filled.Rocket, "story") { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onStory() }
    }
}

@Composable
private fun QuickActionButton(modifier: Modifier, icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(modifier.clickable(onClick = onClick), shape = RoundedCornerShape(0.dp), color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, label, tint = NothingGray400, modifier = Modifier.size(20.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = NothingGray500, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String, icon: ImageVector, iconColor: Color) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Normal, letterSpacing = 2.sp, color = DarkOnBackground)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = NothingGray500)
        }
    }
}

@Composable
private fun WeakCharactersRow(characters: List<String>, onCharacterClick: (String) -> Unit, onPracticeAll: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(characters) { char ->
            Surface(Modifier.size(48.dp).clickable { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onCharacterClick(char) },
                shape = RoundedCornerShape(0.dp), color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingRed.copy(alpha = 0.3f))) {
                Box(contentAlignment = Alignment.Center) {
                    Text(char, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Normal,
                        color = NothingRed.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace)
                }
            }
        }
        item {
            Surface(Modifier.size(48.dp).clickable { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onPracticeAll() },
                shape = RoundedCornerShape(0.dp), color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.ArrowForward, "Practice all", tint = NothingGray500, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TodayChallengeCard(onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onClick() },
        shape = RoundedCornerShape(0.dp), color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800)
    ) {
        Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).border(0.5.dp, NothingGray700, RoundedCornerShape(0.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.EmojiEvents, null, tint = NothingGray400, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("today's challenge", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal, letterSpacing = 1.sp, color = DarkOnBackground)
                Text("decode 10 words at 25 wpm", style = MaterialTheme.typography.bodySmall, color = NothingGray500)
            }
            Surface(shape = RoundedCornerShape(0.dp), color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray700)) {
                Text("+50 xp", Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium, color = NothingGray400, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun FeatureCardsGrid(onLearn: () -> Unit, onStatistics: () -> Unit, onHam: () -> Unit, onAchievements: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FeatureCard(Modifier.weight(1f), Icons.Filled.School, "learning", "koch, farnsworth, traditional") { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onLearn() }
            FeatureCard(Modifier.weight(1f), Icons.Filled.BarChart, "statistics", "track your progress") { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onStatistics() }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FeatureCard(Modifier.weight(1f), Icons.Filled.CellTower, "ham radio", "q codes, phonetic, more") { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onHam() }
            FeatureCard(Modifier.weight(1f), Icons.Filled.EmojiEvents, "achievements", "unlock rewards") { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onAchievements() }
        }
    }
}

@Composable
private fun FeatureCard(modifier: Modifier, icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(modifier.height(100.dp).clickable(onClick = onClick), shape = RoundedCornerShape(0.dp),
        color = Color.Transparent, border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800)) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = NothingGray400, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Normal, letterSpacing = 1.sp, color = DarkOnBackground)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = NothingGray500)
        }
    }
}

@Composable
private fun RecentSessionCard(mode: String, accuracy: Float, wpm: Float, xpEarned: Int, timestamp: Long) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 3.dp),
        shape = RoundedCornerShape(0.dp), color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800.copy(alpha = 0.5f))) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(32.dp).border(0.5.dp, NothingGray800, RoundedCornerShape(0.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.FitnessCenter, null, tint = NothingGray500, modifier = Modifier.size(14.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(mode.lowercase(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Normal, letterSpacing = 1.sp, color = DarkOnBackground)
                Text("${(accuracy * 100).toInt()}% accuracy · ${wpm.toInt()} wpm", style = MaterialTheme.typography.bodySmall, color = NothingGray500)
            }
            Text("+$xpEarned xp", style = MaterialTheme.typography.labelMedium, color = NothingGray400, fontFamily = FontFamily.Monospace)
        }
    }
}

private fun formatNumber(n: Int): String = when {
    n >= 1_000_000 -> "${"%.1f".format(n / 1_000_000.0)}m"
    n >= 1_000 -> "${"%.1f".format(n / 1_000.0)}k"
    else -> "$n"
}
