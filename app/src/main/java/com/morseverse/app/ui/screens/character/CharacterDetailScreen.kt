package com.morseverse.app.ui.screens.character

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.designsystem.theme.*
import com.morseverse.core.domain.models.CharacterProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    character: String,
    onNavigateBack: () -> Unit,
    onPractice: () -> Unit,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val progress by viewModel.progress.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    LaunchedEffect(character) {
        viewModel.loadCharacter(character)
    }

    val morse = MorseCodeData.INTERNATIONAL_MORSE[character.uppercase()] ?: ""
    val memoryTip = MorseCodeData.MEMORY_TIPS[character.uppercase()] ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Character Detail") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Character display
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MorseCyan.copy(alpha = 0.2f),
                                MorseCyan.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    character.uppercase(),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                    fontWeight = FontWeight.Bold,
                    color = MorseCyan
                )
            }

            Spacer(Modifier.height(16.dp))

            // Morse code
            Text(
                morse.replace(".", "·").replace("-", "—"),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 6.sp
                ),
                color = MorseAmber
            )

            Spacer(Modifier.height(8.dp))

            // Standard timing representation
            Text(
                morse.map { if (it == '.') "·" else "—" }.joinToString(" "),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Mastery progress
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Mastery",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${progress.level.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MorseCyan
                        )
                        Text(
                            "${(progress.mastery * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress.mastery },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = progressToColor(progress.mastery),
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    Spacer(Modifier.height(16.dp))

                    // Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CharacterStat("Accuracy", "${(progress.accuracy * 100).toInt()}%")
                        CharacterStat("Attempts", "${progress.totalAttempts}")
                        CharacterStat("Streak", "${progress.streak}")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Memory tip
            if (memoryTip.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MorseViolet.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = MorseViolet,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            memoryTip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.playAudio(character)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) MorseRed else MorseCyan
                    )
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                        null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isPlaying) "Stop" else "Play Audio")
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPractice()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MorseAmber)
                ) {
                    Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Practice")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Related characters
            Text(
                "Related Characters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            val relatedChars = getRelatedCharacters(character.uppercase())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                relatedChars.forEach { related ->
                    val relatedMorse = MorseCodeData.INTERNATIONAL_MORSE[related] ?: ""
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                related,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                relatedMorse.replace(".", "·").replace("-", "—"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterStat(label: String, value: String) {
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

private fun progressToColor(progress: Float): Color {
    return when {
        progress >= 0.95f -> MasteryGrandmaster
        progress >= 0.8f -> MasteryMaster
        progress >= 0.6f -> MasteryExpert
        progress >= 0.4f -> MasteryJourneyman
        progress >= 0.2f -> MasteryApprentice
        progress > 0f -> MasteryNovice
        else -> Color(0xFF303030)
    }
}

private fun getRelatedCharacters(character: String): List<String> {
    // Find characters with similar Morse patterns
    val morse = MorseCodeData.INTERNATIONAL_MORSE[character] ?: return emptyList()
    return MorseCodeData.INTERNATIONAL_MORSE.entries
        .filter { (k, v) -> k != character && v.length == morse.length }
        .take(4)
        .map { it.key }
        .ifEmpty { listOf("E", "T", "A", "N") }
}
