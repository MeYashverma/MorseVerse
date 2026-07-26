package com.morseverse.feature.story.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMission: (String) -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val missions by viewModel.missions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Story Mode",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Learn Morse through adventures",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Intro card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MorseViolet.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MorseViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Rocket,
                                contentDescription = null,
                                tint = MorseViolet,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Begin Your Journey",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MorseViolet
                            )
                            Text(
                                "Each mission teaches new characters through exciting stories!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Missions
            items(MorseCodeData.STORY_MISSIONS) { missionData ->
                val missionProgress = missions.find { it.id == missionData.id }

                StoryMissionCard(
                    title = missionData.title,
                    description = missionData.description,
                    characters = missionData.characters,
                    chaptersTotal = missionData.chapters.size,
                    chaptersCompleted = 0, // From progress
                    isUnlocked = missionProgress?.isUnlocked ?: (missionData == MorseCodeData.STORY_MISSIONS.first()),
                    isCompleted = missionProgress?.isCompleted ?: false,
                    progress = missionProgress?.progress ?: 0f,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToMission(missionData.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun StoryMissionCard(
    title: String,
    description: String,
    characters: List<String>,
    chaptersTotal: Int,
    chaptersCompleted: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    progress: Float,
    onClick: () -> Unit
) {
    val cardColor = when {
        isCompleted -> MorseGreen.copy(alpha = 0.05f)
        isUnlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = cardColor,
        border = if (isUnlocked) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isCompleted) MorseGreen.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        } else null
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> MorseGreen.copy(alpha = 0.2f)
                                isUnlocked -> MorseAmber.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when {
                            isCompleted -> Icons.Filled.CheckCircle
                            isUnlocked -> Icons.Filled.PlayArrow
                            else -> Icons.Filled.Lock
                        },
                        contentDescription = null,
                        tint = when {
                            isCompleted -> MorseGreen
                            isUnlocked -> MorseAmber
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Progress bar
            if (isUnlocked) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isCompleted) MorseGreen else MorseAmber,
                    trackColor = MaterialTheme.colorScheme.surface
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$chaptersCompleted / $chaptersTotal chapters",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // New characters
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        characters.take(4).forEach { char ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MorseCyan.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    char,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MorseCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
