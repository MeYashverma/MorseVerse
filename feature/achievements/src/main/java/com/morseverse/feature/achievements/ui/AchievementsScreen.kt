package com.morseverse.feature.achievements.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morseverse.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onNavigateBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val achievements = listOf(
        AchievementData("First Steps", "Complete your first lesson", "🎯", false, 0.3f, "MILESTONE"),
        AchievementData("Alphabet Master", "Learn all 26 letters", "📝", false, 0.5f, "MASTER"),
        AchievementData("Speed Demon", "Reach 30 WPM", "⚡", false, 0.2f, "SPEED"),
        AchievementData("Perfect 10", "Get 10 correct in a row", "🔥", true, 1.0f, "ACCURACY"),
        AchievementData("Dedicated", "Practice 7 days in a row", "📅", false, 0.7f, "STREAK"),
        AchievementData("Explorer", "Try all practice modes", "🧭", false, 0.4f, "EXPLORER"),
        AchievementData("Night Owl", "Practice after midnight", "🦉", false, 0f, "SPECIAL"),
        AchievementData("Early Bird", "Practice before 7 AM", "🐦", false, 0f, "SPECIAL"),
        AchievementData("Centurion", "Complete 100 sessions", "💯", false, 0.1f, "MILESTONE"),
        AchievementData("Word Master", "Decode 50 words correctly", "📖", false, 0.3f, "MASTER")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Achievements",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Progress summary
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MorseAmber.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MorseAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "🏆",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Column {
                            Text(
                                "${achievements.count { it.isUnlocked }} / ${achievements.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MorseAmber
                            )
                            Text(
                                "achievements unlocked",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Achievement cards
            items(achievements) { achievement ->
                AchievementCard(achievement = achievement)
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: AchievementData) {
    val backgroundColor = if (achievement.isUnlocked) {
        MorseGreen.copy(alpha = 0.05f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = if (achievement.isUnlocked) {
            androidx.compose.foundation.BorderStroke(1.dp, MorseGreen.copy(alpha = 0.3f))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) MorseGreen.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    achievement.icon,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { achievement.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (achievement.isUnlocked) MorseGreen else MorseCyan,
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }

            if (achievement.isUnlocked) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = MorseGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

data class AchievementData(
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val progress: Float,
    val category: String
)
