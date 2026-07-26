package com.morseverse.feature.translator.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: TranslatorViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    val inputText by viewModel.inputText.collectAsState()
    val outputMorse by viewModel.outputMorse.collectAsState()
    val isTextToMorse by viewModel.isTextToMorse.collectAsState()
    val history by viewModel.history.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Translator",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ─── DIRECTION TOGGLE ─────────────────────────────────────
            DirectionToggle(
                isTextToMorse = isTextToMorse,
                onToggle = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.toggleDirection()
                }
            )

            Spacer(Modifier.height(20.dp))

            // ─── INPUT SECTION ─────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isTextToMorse) "Text Input" else "Morse Input",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val text = clipboardManager.getText()?.text ?: ""
                                viewModel.setInputText(text)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.ContentPaste,
                                contentDescription = "Paste",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.setInputText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        placeholder = {
                            Text(
                                if (isTextToMorse) "Type text here..." else "Type Morse code here (. - / )",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = if (isTextToMorse) FontFamily.Default else FontFamily.Monospace,
                            fontSize = 18.sp
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MorseCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── SWAP BUTTON ───────────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.toggleDirection()
                    },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MorseCyan.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.SwapVert,
                            contentDescription = "Swap direction",
                            tint = MorseCyan
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── OUTPUT SECTION ────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isTextToMorse) "Morse Code" else "Text Output",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row {
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    clipboardManager.setText(AnnotatedString(outputMorse))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.shareTranslation()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Output display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        contentAlignment = if (outputMorse.isEmpty()) Alignment.Center else Alignment.TopStart
                    ) {
                        if (outputMorse.isEmpty()) {
                            Text(
                                "Translation will appear here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        } else {
                            Text(
                                outputMorse,
                                style = if (isTextToMorse) {
                                    MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 20.sp,
                                        letterSpacing = 2.sp
                                    )
                                } else {
                                    MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                                },
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ─── ACTION BUTTONS ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play Audio
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.playAudio()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) MorseRed else MorseCyan
                    )
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isPlaying) "Stop" else "Play Audio")
                }

                // Flashlight
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.flashlightOutput()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Filled.FlashlightOn,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Flashlight")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Save to favorites
            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.saveToFavorites()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = outputMorse.isNotEmpty()
            ) {
                Icon(Icons.Outlined.FavoriteBorder, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save to Favorites")
            }

            Spacer(Modifier.height(24.dp))

            // ─── QUICK REFERENCE ───────────────────────────────────────
            Text(
                "Quick Reference",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))

            // Morse reference grid
            val referenceChars = listOf(
                listOf("A" to ".-", "B" to "-...", "C" to "-.-.", "D" to "-.."),
                listOf("E" to ".", "F" to "..-.", "G" to "--.", "H" to "...."),
                listOf("I" to "..", "J" to ".---", "K" to "-.-", "L" to ".-.."),
                listOf("M" to "--", "N" to "-.", "O" to "---", "P" to ".--.")
            )

            referenceChars.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { (char, morse) ->
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    char,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MorseCyan
                                )
                                Text(
                                    morse.replace(".", "·").replace("-", "—"),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun DirectionToggle(
    isTextToMorse: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                onClick = { if (!isTextToMorse) onToggle() },
                shape = RoundedCornerShape(12.dp),
                color = if (isTextToMorse) MorseCyan.copy(alpha = 0.15f) else Color.Transparent
            ) {
                Text(
                    "Text → Morse",
                    modifier = Modifier.padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isTextToMorse) MorseCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isTextToMorse) FontWeight.SemiBold else FontWeight.Normal
                )
            }
            Surface(
                modifier = Modifier.weight(1f),
                onClick = { if (isTextToMorse) onToggle() },
                shape = RoundedCornerShape(12.dp),
                color = if (!isTextToMorse) MorseCyan.copy(alpha = 0.15f) else Color.Transparent
            ) {
                Text(
                    "Morse → Text",
                    modifier = Modifier.padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (!isTextToMorse) MorseCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (!isTextToMorse) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
