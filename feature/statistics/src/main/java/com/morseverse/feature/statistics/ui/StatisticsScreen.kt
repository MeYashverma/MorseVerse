package com.morseverse.feature.statistics.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morseverse.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Weekly", "Monthly", "Heatmap")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Statistics",
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
            // Tab selector
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    containerColor = Color.Transparent,
                    edgePadding = 0.dp,
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
            }

            // Overview stats cards
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OverviewStatCard(
                        modifier = Modifier.weight(1f),
                        value = "1,247",
                        label = "Total XP",
                        icon = Icons.Filled.Star,
                        color = MorseAmber
                    )
                    OverviewStatCard(
                        modifier = Modifier.weight(1f),
                        value = "42",
                        label = "Sessions",
                        icon = Icons.Filled.History,
                        color = MorseCyan
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OverviewStatCard(
                        modifier = Modifier.weight(1f),
                        value = "78%",
                        label = "Avg Accuracy",
                        icon = Icons.Filled.GpsFixed,
                        color = MorseGreen
                    )
                    OverviewStatCard(
                        modifier = Modifier.weight(1f),
                        value = "22",
                        label = "Avg WPM",
                        icon = Icons.Filled.Speed,
                        color = MorseViolet
                    )
                }
            }

            // Accuracy Chart
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Accuracy Over Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    AccuracyChart(
                        data = listOf(0.65f, 0.72f, 0.68f, 0.78f, 0.82f, 0.75f, 0.85f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp)
                    )
                }
            }

            // WPM Chart
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Speed Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    SpeedChart(
                        data = listOf(12f, 15f, 14f, 18f, 20f, 22f, 21f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp)
                    )
                }
            }

            // Practice Heatmap
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Practice Heatmap",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    PracticeHeatmap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            // Weak Characters
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Characters Needing Practice",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Q", "Z", "X", "J", "V", "P")) { char ->
                        WeakCharacterChip(character = char, accuracy = (40..65).random() / 100f)
                    }
                }
            }

            // Strong Characters
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Strongest Characters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("E", "T", "A", "I", "N", "M")) { char ->
                        StrongCharacterChip(character = char, accuracy = (90..99).random() / 100f)
                    }
                }
            }

            // Reaction Time
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Reaction Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Average",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "1.2s",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MorseCyan
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Best",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "0.4s",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MorseGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccuracyChart(data: List<Float>, modifier: Modifier = Modifier) {
    val lineColor = MorseCyan

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val maxVal = 1f
        val stepX = size.width / (data.size - 1).coerceAtLeast(1)

        val path = Path()
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height * (1f - value / maxVal)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw dots
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height * (1f - value / maxVal)

            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun SpeedChart(data: List<Float>, modifier: Modifier = Modifier) {
    val barColor = MorseAmber

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val maxVal = data.max()
        val barWidth = size.width / data.size * 0.6f
        val spacing = size.width / data.size

        data.forEachIndexed { index, value ->
            val barHeight = (value / maxVal) * size.height * 0.9f
            val x = index * spacing + (spacing - barWidth) / 2
            val y = size.height - barHeight

            drawRoundRect(
                color = barColor.copy(alpha = 0.8f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}

@Composable
private fun PracticeHeatmap(modifier: Modifier = Modifier) {
    val colors = listOf(HeatmapEmpty, HeatmapLevel1, HeatmapLevel2, HeatmapLevel3, HeatmapLevel4)

    Column(modifier = modifier) {
        // Month labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul").forEach { month ->
                Text(
                    month,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Heatmap grid
        repeat(7) { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(52) { day ->
                    val intensity = (0..4).random()
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors[intensity])
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
        }

        // Legend
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Less",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(4.dp))
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
                Spacer(Modifier.width(2.dp))
            }
            Spacer(Modifier.width(4.dp))
            Text(
                "More",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeakCharacterChip(character: String, accuracy: Float) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MorseRed.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MorseRed.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                character,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MorseRed
            )
            Text(
                "${(accuracy * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MorseRed.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun StrongCharacterChip(character: String, accuracy: Float) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MorseGreen.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MorseGreen.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                character,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MorseGreen
            )
            Text(
                "${(accuracy * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MorseGreen.copy(alpha = 0.7f)
            )
        }
    }
}
