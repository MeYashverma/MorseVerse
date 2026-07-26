package com.morseverse.feature.learn.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.morseverse.core.designsystem.theme.*
import com.morseverse.core.domain.models.LearningMethod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTree: () -> Unit,
    onNavigateToCharacter: (String) -> Unit,
    onNavigateToPractice: (String) -> Unit,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val selectedMethod by viewModel.selectedMethod.collectAsState()
    val kochProgress by viewModel.kochProgress.collectAsState()
    val characterGroups by viewModel.characterGroups.collectAsState()
    val currentLesson by viewModel.currentLesson.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Learn",
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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ─── LEARNING METHOD SELECTOR ──────────────────────────────
            item {
                Text(
                    "Learning Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(LearningMethod.entries) { method ->
                        MethodCard(
                            method = method,
                            isSelected = method == selectedMethod,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.selectMethod(method)
                            }
                        )
                    }
                }
            }

            // ─── CURRENT PROGRESS ──────────────────────────────────────
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Your Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                ProgressOverviewCard(
                    charactersLearned = kochProgress.count { it.isCompleted },
                    totalCharacters = 40,
                    currentLesson = currentLesson
                )
            }

            // ─── MORSE TREE QUICK ACCESS ───────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToTree()
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = MorseCyan.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Filled.AccountTree,
                            contentDescription = null,
                            tint = MorseCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Morse Binary Tree",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MorseCyan
                            )
                            Text(
                                "Interactive tree visualization",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = MorseCyan
                        )
                    }
                }
            }

            // ─── CHARACTER GROUPS / LESSONS ────────────────────────────
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    when (selectedMethod) {
                        LearningMethod.KOCH -> "Koch Lessons"
                        LearningMethod.TRADITIONAL -> "Character Groups"
                        LearningMethod.FARNSWORTH -> "Farnsworth Lessons"
                        else -> "Lessons"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            itemsIndexed(characterGroups) { index, group ->
                LessonCard(
                    lessonNumber = index + 1,
                    characters = group,
                    isCompleted = index < kochProgress.count { it.isCompleted },
                    isCurrent = index == kochProgress.count { it.isCompleted },
                    isLocked = index > kochProgress.count { it.isCompleted },
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.selectLesson(index)
                        onNavigateToPractice("CHARACTER")
                    }
                )
            }

            // ─── ALPHABET REFERENCE ────────────────────────────────────
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Full Alphabet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                AlphabetGrid(
                    onCharacterClick = { onNavigateToCharacter(it) }
                )
            }
        }
    }
}

@Composable
private fun MethodCard(
    method: LearningMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.width(180.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MorseCyan.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, MorseCyan.copy(alpha = 0.5f))
        } else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                when (method) {
                    LearningMethod.KOCH -> Icons.Filled.Speed
                    LearningMethod.FARNSWORTH -> Icons.Filled.Timer
                    LearningMethod.TRADITIONAL -> Icons.Filled.MenuBook
                    LearningMethod.ADAPTIVE -> Icons.Filled.AutoAwesome
                    LearningMethod.STORY -> Icons.Filled.Rocket
                },
                contentDescription = null,
                tint = if (isSelected) MorseCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                method.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MorseCyan else MaterialTheme.colorScheme.onSurface
            )
            Text(
                method.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ProgressOverviewCard(
    charactersLearned: Int,
    totalCharacters: Int,
    currentLesson: Int
) {
    val progress = charactersLearned.toFloat() / totalCharacters

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "$charactersLearned / $totalCharacters",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MorseCyan
                    )
                    Text(
                        "characters mastered",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Lesson $currentLesson",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MorseAmber
                    )
                    Text(
                        "current",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MorseCyan,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
private fun LessonCard(
    lessonNumber: Int,
    characters: List<String>,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCurrent -> MorseCyan.copy(alpha = 0.1f)
        isCompleted -> MorseGreen.copy(alpha = 0.05f)
        isLocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    val borderColor = when {
        isCurrent -> MorseCyan.copy(alpha = 0.3f)
        isCompleted -> MorseGreen.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(enabled = !isLocked, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = if (borderColor != Color.Transparent) {
            androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        } else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lesson number/status
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> MorseGreen.copy(alpha = 0.2f)
                            isCurrent -> MorseCyan.copy(alpha = 0.2f)
                            isLocked -> MaterialTheme.colorScheme.surface
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(Icons.Filled.Check, null, tint = MorseGreen, modifier = Modifier.size(20.dp))
                    isLocked -> Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    else -> Text("$lessonNumber", fontWeight = FontWeight.Bold, color = if (isCurrent) MorseCyan else MaterialTheme.colorScheme.onSurface)
                }
            }

            // Characters
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Lesson $lessonNumber",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    characters.forEach { char ->
                        Text(
                            char,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = when {
                                isCompleted -> MorseGreen
                                isCurrent -> MorseCyan
                                isLocked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            if (isCurrent) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MorseCyan),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Start", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun AlphabetGrid(onCharacterClick: (String) -> Unit) {
    val haptic = LocalHapticFeedback.current
    val alphabet = ('A'..'Z').toList()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        alphabet.chunked(7).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { char ->
                    val morse = com.morseverse.core.common.constants.MorseCodeData.INTERNATIONAL_MORSE[char.toString()] ?: ""
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onCharacterClick(char.toString())
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                char.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                morse.replace(".", "·").replace("-", "—"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
                // Fill remaining space
                repeat(7 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}
