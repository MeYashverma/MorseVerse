package com.morseverse.feature.practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.morseverse.core.designsystem.theme.*
import com.morseverse.core.domain.models.PracticeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeSessionScreen(
    mode: String,
    onNavigateBack: () -> Unit,
    viewModel: PracticeSessionViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mode) {
        viewModel.startSession(PracticeMode.valueOf(mode))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        PracticeMode.valueOf(mode).displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.endSession()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                },
                actions = {
                    // Progress indicator
                    Text(
                        "${uiState.currentIndex + 1}/${uiState.totalItems}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MorseCyan)
                }
            }
            uiState.isComplete -> {
                SessionCompleteContent(
                    modifier = Modifier.padding(padding),
                    state = uiState,
                    onDone = {
                        viewModel.endSession()
                        onNavigateBack()
                    },
                    onRetry = { viewModel.startSession(PracticeMode.valueOf(mode)) }
                )
            }
            else -> {
                PracticeContent(
                    modifier = Modifier.padding(padding),
                    state = uiState,
                    onAnswer = { answer ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.submitAnswer(answer)
                    },
                    onPlayAudio = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.playCurrentAudio()
                    },
                    onSkip = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.skipCurrent()
                    },
                    onHint = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.showHint()
                    }
                )
            }
        }
    }
}

@Composable
private fun PracticeContent(
    modifier: Modifier,
    state: PracticeUiState,
    onAnswer: (String) -> Unit,
    onPlayAudio: () -> Unit,
    onSkip: () -> Unit,
    onHint: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { (state.currentIndex.toFloat() / state.totalItems).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MorseCyan,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatChip(label = "Accuracy", value = "${(state.accuracy * 100).toInt()}%")
            StatChip(label = "Streak", value = "${state.streak}")
            StatChip(label = "WPM", value = "${state.wpm}")
        }

        Spacer(Modifier.weight(1f))

        // Current character display
        AnimatedContent(
            targetState = state.currentMorse,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(300)
                ) togetherWith fadeOut(animationSpec = tween(200))
            },
            label = "morse_transition"
        ) { morse ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Morse code display
                Text(
                    morse.replace(".", "·").replace("-", "—"),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp
                    ),
                    color = MorseCyan,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                // Play audio button
                Surface(
                    onClick = onPlayAudio,
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = MorseCyan.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.VolumeUp,
                            contentDescription = "Play audio",
                            tint = MorseCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Feedback animation
        AnimatedVisibility(
            visible = state.lastResult != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            state.lastResult?.let { result ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (result.isCorrect) MorseGreen.copy(alpha = 0.1f) else MorseRed.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            if (result.isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                            contentDescription = null,
                            tint = if (result.isCorrect) MorseGreen else MorseRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            if (result.isCorrect) "Correct!" else "Answer: ${result.expected}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (result.isCorrect) MorseGreen else MorseRed
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Answer input - multiple choice buttons
        if (state.choices.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Split choices into rows of 4
                state.choices.chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { choice ->
                            ChoiceButton(
                                text = choice,
                                modifier = Modifier.weight(1f),
                                isCorrect = state.lastResult?.let { it.isCorrect && choice == it.expected } == true,
                                isWrong = state.lastResult?.let { !it.isCorrect && choice == it.given } == true,
                                onClick = { onAnswer(choice) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Bottom actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onHint,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Lightbulb, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Hint")
            }
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.SkipNext, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Skip")
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ChoiceButton(
    text: String,
    modifier: Modifier = Modifier,
    isCorrect: Boolean = false,
    isWrong: Boolean = false,
    onClick: () -> Unit
) {
    val containerColor = when {
        isCorrect -> MorseGreen.copy(alpha = 0.15f)
        isWrong -> MorseRed.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val borderColor = when {
        isCorrect -> MorseGreen
        isWrong -> MorseRed
        else -> Color.Transparent
    }

    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = if (borderColor != Color.Transparent) {
            androidx.compose.foundation.BorderStroke(2.dp, borderColor)
        } else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = when {
                    isCorrect -> MorseGreen
                    isWrong -> MorseRed
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@Composable
private fun SessionCompleteContent(
    modifier: Modifier,
    state: PracticeUiState,
    onDone: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Completion animation
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MorseGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MorseGreen,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Session Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Great job! Here's how you did:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        // Results
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ResultStat("Accuracy", "${(state.accuracy * 100).toInt()}%", MorseGreen)
            ResultStat("XP Earned", "+${state.xpEarned}", MorseAmber)
            ResultStat("Best Streak", "${state.bestStreak}", MorseCyan)
        }

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MorseCyan)
        ) {
            Icon(Icons.Filled.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text("Practice Again", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Done", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ResultStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// UI State
data class PracticeUiState(
    val isLoading: Boolean = true,
    val isComplete: Boolean = false,
    val currentIndex: Int = 0,
    val totalItems: Int = 0,
    val currentMorse: String = "",
    val choices: List<String> = emptyList(),
    val accuracy: Float = 0f,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val wpm: Int = 20,
    val xpEarned: Int = 0,
    val lastResult: AnswerResult? = null
)

data class AnswerResult(
    val given: String,
    val expected: String,
    val isCorrect: Boolean,
    val reactionTimeMs: Long = 0
)
