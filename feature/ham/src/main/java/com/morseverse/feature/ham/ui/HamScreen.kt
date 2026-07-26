package com.morseverse.feature.ham.ui

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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamScreen(
    onNavigateBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Phonetic", "Q Codes", "Abbreviations")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ham Radio",
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
        Column(modifier = Modifier.padding(padding)) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedTab = index
                        },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> PhoneticAlphabetList()
                1 -> QCodesList()
                2 -> AbbreviationsList()
            }
        }
    }
}

@Composable
private fun PhoneticAlphabetList() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(MorseCodeData.PHONETIC_ALPHABET) { (letter, word, morse) ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Letter
                    Text(
                        letter,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MorseCyan
                    )

                    // Word
                    Text(
                        word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    // Morse
                    Text(
                        morse.replace(".", "·").replace("-", "—"),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MorseAmber
                    )
                }
            }
        }
    }
}

@Composable
private fun QCodesList() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(MorseCodeData.Q_CODES) { (code, question, answer) ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MorseGreen
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Q: $question",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "A: $answer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MorseCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun AbbreviationsList() {
    val abbreviations = listOf(
        "73" to "Best regards",
        "88" to "Love and kisses",
        "CQ" to "Call to all stations",
        "DE" to "From (identification)",
        "ES" to "And",
        "FB" to "Fine business (excellent)",
        "GA" to "Go ahead",
        "GM" to "Good morning",
        "GN" to "Good night",
        "HI" to "Laughter / high",
        "HR" to "Here / hear",
        "OM" to "Old man (male operator)",
        "YL" to "Young lady (female operator)",
        "PSE" to "Please",
        "RIG" to "Station equipment",
        "RST" to "Signal report (Readability, Strength, Tone)",
        "TNX" to "Thanks",
        "TU" to "Thank you",
        "WX" to "Weather",
        "XYL" to "Wife (ex-young lady)"
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(abbreviations) { (abbr, meaning) ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MorseAmber.copy(alpha = 0.1f)
                    ) {
                        Text(
                            abbr,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MorseAmber,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


