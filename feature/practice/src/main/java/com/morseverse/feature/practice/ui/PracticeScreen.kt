package com.morseverse.feature.practice.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morseverse.core.designsystem.theme.*
import com.morseverse.core.domain.models.PracticeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    onNavigateBack: () -> Unit,
    onStartPractice: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val practiceCategories = listOf(
        PracticeCategory(
            title = "Core Practice",
            subtitle = "Build your foundation",
            modes = listOf(
                PracticeModeItem(PracticeMode.CHARACTER, Icons.Filled.TextFields, MorseCyan, "Learn individual characters"),
                PracticeModeItem(PracticeMode.WORD, Icons.Filled.TextFormat, MorseAmber, "Practice common words"),
                PracticeModeItem(PracticeMode.SENTENCE, Icons.Filled.Article, MorseGreen, "Full sentence practice"),
                PracticeModeItem(PracticeMode.RANDOM, Icons.Filled.Shuffle, MorseViolet, "Random character mix")
            )
        ),
        PracticeCategory(
            title = "Targeted Practice",
            subtitle = "Focus on what matters",
            modes = listOf(
                PracticeModeItem(PracticeMode.WEAK, Icons.Filled.Warning, MorseRed, "Practice weak characters"),
                PracticeModeItem(PracticeMode.CUSTOM, Icons.Filled.Tune, MorseCyan, "Custom character set"),
                PracticeModeItem(PracticeMode.COMMON_WORDS, Icons.Filled.Star, MorseAmber, "Most common words")
            )
        ),
        PracticeCategory(
            title = "Ham Radio",
            subtitle = "Real-world skills",
            modes = listOf(
                PracticeModeItem(PracticeMode.CALLSIGN, Icons.Filled.CellTower, MorseGreen, "Decode callsigns"),
                PracticeModeItem(PracticeMode.CONTEST, Icons.Filled.EmojiEvents, MorseAmber, "Contest simulation"),
                PracticeModeItem(PracticeMode.TIMED, Icons.Filled.Timer, MorseRed, "Speed challenges")
            )
        ),
        PracticeCategory(
            title = "Special Modes",
            subtitle = "Challenge yourself",
            modes = listOf(
                PracticeModeItem(PracticeMode.DAILY, Icons.Filled.Today, MorseCyan, "Daily challenge"),
                PracticeModeItem(PracticeMode.INFINITE, Icons.Filled.AllInclusive, MorseViolet, "Never-ending practice")
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Practice",
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
            items(practiceCategories) { category ->
                PracticeCategorySection(
                    category = category,
                    onModeClick = { mode ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onStartPractice(mode.name)
                    }
                )
            }
        }
    }
}

@Composable
private fun PracticeCategorySection(
    category: PracticeCategory,
    onModeClick: (PracticeMode) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            category.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            category.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        category.modes.forEach { mode ->
            PracticeModeCard(
                mode = mode,
                onClick = { onModeClick(mode.mode) }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PracticeModeCard(
    mode: PracticeModeItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(mode.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    mode.icon,
                    contentDescription = null,
                    tint = mode.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    mode.mode.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    mode.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

data class PracticeCategory(
    val title: String,
    val subtitle: String,
    val modes: List<PracticeModeItem>
)

data class PracticeModeItem(
    val mode: PracticeMode,
    val icon: ImageVector,
    val color: Color,
    val description: String
)
