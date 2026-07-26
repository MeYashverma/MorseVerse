package com.morseverse.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morseverse.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
            // Audio Settings
            item {
                SettingsSectionHeader("Audio")
            }
            item {
                SettingsSlider(
                    icon = Icons.Filled.Speed,
                    title = "Default WPM",
                    value = 20f,
                    valueRange = 5f..60f,
                    onValueChange = {}
                )
            }
            item {
                SettingsSlider(
                    icon = Icons.Filled.MusicNote,
                    title = "Frequency (Hz)",
                    value = 600f,
                    valueRange = 300f..1000f,
                    onValueChange = {}
                )
            }
            item {
                SettingsSlider(
                    icon = Icons.Filled.VolumeUp,
                    title = "Volume",
                    value = 0.8f,
                    valueRange = 0f..1f,
                    onValueChange = {}
                )
            }
            item {
                SettingsDropdown(
                    icon = Icons.Filled.GraphicEq,
                    title = "Tone Type",
                    options = listOf("Sine", "Smooth", "Buzzy", "Radio"),
                    selected = "Sine",
                    onSelect = {}
                )
            }

            // Learning Settings
            item {
                SettingsSectionHeader("Learning")
            }
            item {
                SettingsDropdown(
                    icon = Icons.Filled.School,
                    title = "Learning Method",
                    options = listOf("Koch", "Farnsworth", "Traditional", "Adaptive"),
                    selected = "Koch",
                    onSelect = {}
                )
            }
            item {
                SettingsSlider(
                    icon = Icons.Filled.Timer,
                    title = "Daily Goal (minutes)",
                    value = 15f,
                    valueRange = 5f..60f,
                    steps = 10,
                    onValueChange = {}
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Filled.SpaceBar,
                    title = "Farnsworth Spacing",
                    subtitle = "Extra spacing between characters",
                    checked = false,
                    onCheckedChange = {}
                )
            }

            // Interface Settings
            item {
                SettingsSectionHeader("Interface")
            }
            item {
                SettingsDropdown(
                    icon = Icons.Filled.Palette,
                    title = "Theme",
                    options = listOf("Dark", "Light", "AMOLED", "Material You"),
                    selected = "Dark",
                    onSelect = {}
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Filled.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on interactions",
                    checked = true,
                    onCheckedChange = {}
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Filled.TextIncrease,
                    title = "Large Text",
                    subtitle = "Increase text size",
                    checked = false,
                    onCheckedChange = {}
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Filled.Contrast,
                    title = "High Contrast",
                    subtitle = "Increase color contrast",
                    checked = false,
                    onCheckedChange = {}
                )
            }

            // Notifications
            item {
                SettingsSectionHeader("Notifications")
            }
            item {
                SettingsToggle(
                    icon = Icons.Filled.Notifications,
                    title = "Daily Reminder",
                    subtitle = "Get reminded to practice",
                    checked = true,
                    onCheckedChange = {}
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Filled.LocalFireDepartment,
                    title = "Streak Reminder",
                    subtitle = "Don't lose your streak!",
                    checked = true,
                    onCheckedChange = {}
                )
            }

            // About
            item {
                SettingsSectionHeader("About")
            }
            item {
                SettingsNavigationItem(
                    icon = Icons.Filled.Info,
                    title = "About MorseVerse",
                    subtitle = "Version 1.0.0",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateToAbout()
                    }
                )
            }
            item {
                SettingsNavigationItem(
                    icon = Icons.Filled.Code,
                    title = "Open Source Licenses",
                    subtitle = "View third-party licenses",
                    onClick = {}
                )
            }
            item {
                SettingsNavigationItem(
                    icon = Icons.Filled.Star,
                    title = "Rate MorseVerse",
                    subtitle = "Leave a review on Google Play",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MorseCyan,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MorseCyan,
                    checkedTrackColor = MorseCyan.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun SettingsSlider(
    icon: ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Text(
                    "${value.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MorseCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = MorseCyan,
                    activeTrackColor = MorseCyan
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsDropdown(
    icon: ImageVector,
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Text(
                    selected,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MorseCyan
                )
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
