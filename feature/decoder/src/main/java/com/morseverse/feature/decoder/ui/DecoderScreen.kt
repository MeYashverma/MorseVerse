package com.morseverse.feature.decoder.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morseverse.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecoderScreen(
    onNavigateBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedSource by remember { mutableStateOf(DecoderSource.MANUAL) }
    var decodedText by remember { mutableStateOf("") }
    var rawMorse by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var confidence by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Decoder",
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
            // Source selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DecoderSource.entries.forEach { source ->
                    FilterChip(
                        selected = selectedSource == source,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedSource = source
                        },
                        label = { Text(source.displayName) },
                        leadingIcon = if (selectedSource == source) {
                            { Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MorseCyan.copy(alpha = 0.15f),
                            selectedLabelColor = MorseCyan
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Waveform visualization
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                WaveformDisplay(
                    isListening = isListening,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Confidence meter
            if (isListening || confidence > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Confidence",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            progress = { confidence },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = when {
                                confidence > 0.8f -> MorseGreen
                                confidence > 0.5f -> MorseAmber
                                else -> MorseRed
                            },
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                        Text(
                            "${(confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Raw Morse output
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Raw Morse",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        rawMorse.ifEmpty { "Waiting for input..." },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.dp
                        ),
                        color = if (rawMorse.isNotEmpty()) MorseCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Decoded text output
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Decoded Text",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        decodedText.ifEmpty { "Decoded text will appear here..." },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (decodedText.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Action buttons
            when (selectedSource) {
                DecoderSource.MICROPHONE -> {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isListening = !isListening
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) MorseRed else MorseCyan
                        )
                    ) {
                        Icon(
                            if (isListening) Icons.Filled.MicOff else Icons.Filled.Mic,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            if (isListening) "Stop Listening" else "Start Listening",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                DecoderSource.MANUAL -> {
                    OutlinedTextField(
                        value = rawMorse,
                        onValueChange = { rawMorse = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Type Morse code here (. - / )") },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MorseCyan
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            // Decode the Morse input
                            decodedText = decodeMorse(rawMorse)
                            confidence = 1f
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MorseCyan)
                    ) {
                        Icon(Icons.Filled.Decode, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Decode")
                    }
                }
                DecoderSource.CLIPBOARD -> {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            // Read from clipboard
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MorseCyan)
                    ) {
                        Icon(Icons.Filled.ContentPaste, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Read from Clipboard")
                    }
                }
                DecoderSource.FILE -> {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            // Open file picker
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Filled.AudioFile, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Select Audio File")
                    }
                }
            }
        }
    }
}

@Composable
private fun WaveformDisplay(
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val waveColor = if (isListening) MorseCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)

    Canvas(modifier = modifier) {
        val centerY = size.height / 2f
        val points = 100
        val step = size.width / points

        val path = Path()
        for (i in 0 until points) {
            val x = i * step
            val amplitude = if (isListening) {
                (kotlin.math.sin(i * 0.3f) * 20f + (Math.random() * 15 - 7.5)).toFloat()
            } else {
                kotlin.math.sin(i * 0.1f) * 5f
            }

            if (i == 0) {
                path.moveTo(x, centerY + amplitude)
            } else {
                path.lineTo(x, centerY + amplitude)
            }
        }

        drawPath(
            path = path,
            color = waveColor,
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
    }
}

private fun decodeMorse(morse: String): String {
    val morseMap = mapOf(
        ".-" to "A", "-..." to "B", "-.-." to "C", "-.." to "D",
        "." to "E", "..-." to "F", "--." to "G", "...." to "H",
        ".." to "I", ".---" to "J", "-.-" to "K", ".-.." to "L",
        "--" to "M", "-." to "N", "---" to "O", ".--." to "P",
        "--.-" to "Q", ".-." to "R", "..." to "S", "-" to "T",
        "..-" to "U", "...-" to "V", ".--" to "W", "-..-" to "X",
        "-.--" to "Y", "--.." to "Z", "-----" to "0", ".----" to "1",
        "..---" to "2", "...--" to "3", "....-" to "4", "....." to "5",
        "-...." to "6", "--..." to "7", "---.." to "8", "----." to "9"
    )

    return morse.trim().split(" ").joinToString("") { token ->
        when (token) {
            "/" -> " "
            else -> morseMap[token] ?: "?"
        }
    }
}

enum class DecoderSource(val displayName: String) {
    MICROPHONE("Mic"),
    MANUAL("Manual"),
    CLIPBOARD("Clipboard"),
    FILE("File")
}
