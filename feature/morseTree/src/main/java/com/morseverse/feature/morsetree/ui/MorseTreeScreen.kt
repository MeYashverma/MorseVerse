package com.morseverse.feature.morsetree.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.morseverse.core.designsystem.theme.*
import com.morseverse.core.domain.models.MorseTreeNode
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorseTreeScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCharacter: (String) -> Unit,
    viewModel: MorseTreeViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    val tree by viewModel.tree.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedNode by viewModel.selectedNode.collectAsState()
    val characterProgress by viewModel.characterProgress.collectAsState()
    val showSearch by remember { mutableStateOf(false) }

    // Canvas transform state
    var scale by remember { mutableFloatStateOf(0.5f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.2f, 3f)
        offset += panChange
    }

    // Animate the initial appearance
    val appearAnimation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appearAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    // Bottom sheet for node details
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Morse Tree",
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
                actions = {
                    // Search
                    var isSearchActive by remember { mutableStateOf(false) }

                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text("Search character...") },
                            singleLine = true,
                            modifier = Modifier.width(200.dp),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }

                    IconButton(onClick = {
                        isSearchActive = !isSearchActive
                        if (!isSearchActive) viewModel.updateSearchQuery("")
                    }) {
                        Icon(
                            if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }

                    // Zoom controls
                    IconButton(onClick = { scale = (scale * 1.3f).coerceAtMost(3f) }) {
                        Icon(Icons.Filled.ZoomIn, "Zoom in")
                    }
                    IconButton(onClick = { scale = (scale * 0.7f).coerceAtLeast(0.2f) }) {
                        Icon(Icons.Filled.ZoomOut, "Zoom out")
                    }
                    IconButton(onClick = {
                        scale = 0.5f
                        offset = Offset.Zero
                    }) {
                        Icon(Icons.Filled.CenterFocusStrong, "Reset view")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            tree?.let { rootNode ->
                val density = LocalDensity.current
                val textMeasurer = rememberTextMeasurer()

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformState)
                        .pointerInput(Unit) {
                            detectTapGestures { tapOffset ->
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                val centerX = canvasWidth / 2f + offset.x
                                val topY = 60f * density.density + offset.y

                                // Find tapped node
                                val tappedNode = findNodeAtPosition(
                                    rootNode,
                                    centerX,
                                    topY,
                                    scale,
                                    density.density,
                                    tapOffset
                                )
                                if (tappedNode != null && tappedNode.character != null) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.selectNode(tappedNode)
                                    showBottomSheet = true
                                }
                            }
                        }
                ) {
                    // Draw the tree
                    drawMorseTree(
                        rootNode = rootNode,
                        centerX = size.width / 2f + offset.x,
                        topY = 60f * density.density + offset.y,
                        scale = scale,
                        density = density.density,
                        textMeasurer = textMeasurer,
                        progressMap = characterProgress,
                        searchQuery = searchQuery,
                        selectedCharacter = selectedNode?.character,
                        animationProgress = appearAnimation.value
                    )
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MorseCyan)
                }
            }

            // Legend
            LegendOverlay(modifier = Modifier.align(Alignment.BottomStart))

            // Zoom indicator
            ZoomIndicator(
                scale = scale,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }

    // Bottom Sheet for character details
    if (showBottomSheet && selectedNode != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            selectedNode?.let { node ->
                NodeDetailSheet(
                    node = node,
                    progress = characterProgress[node.character] ?: 0f,
                    onPractice = {
                        showBottomSheet = false
                        node.character?.let { onNavigateToCharacter(it) }
                    },
                    onPlayAudio = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.playCharacterAudio(node.character ?: "")
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// TREE RENDERING
// ═══════════════════════════════════════════════════════════════════

private fun DrawScope.drawMorseTree(
    rootNode: MorseTreeNode,
    centerX: Float,
    topY: Float,
    scale: Float,
    density: Float,
    textMeasurer: TextMeasurer,
    progressMap: Map<String, Float>,
    searchQuery: String,
    selectedCharacter: String?,
    animationProgress: Float
) {
    val baseNodeRadius = 18f * density * scale
    val baseHorizontalSpacing = 140f * density * scale
    val baseVerticalSpacing = 90f * density * scale

    fun drawNode(
        node: MorseTreeNode,
        x: Float,
        y: Float,
        depth: Int,
        parentX: Float?,
        parentY: Float?
    ) {
        // Animate appearance based on depth
        val nodeAlpha = ((animationProgress - depth * 0.1f) * 3f).coerceIn(0f, 1f)
        if (nodeAlpha <= 0f) return

        val nodeRadius = baseNodeRadius * (1f - depth * 0.05f).coerceAtLeast(0.6f)
        val spacing = baseHorizontalSpacing / (depth + 1).coerceAtLeast(1)

        // Draw edge to parent
        if (parentX != null && parentY != null) {
            val edgeColor = if (node.character != null) {
                val progress = progressMap[node.character] ?: 0f
                progressToColor(progress)
            } else {
                Color(0xFF2A2A2A)
            }

            drawLine(
                color = edgeColor.copy(alpha = nodeAlpha * 0.6f),
                start = Offset(parentX, parentY),
                end = Offset(x, y),
                strokeWidth = 2f * density * scale,
                cap = StrokeCap.Round
            )

            // Dot/Dah label on edge
            val midX = (parentX + x) / 2f
            val midY = (parentY + y) / 2f
            val isDit = node.morse.isNotEmpty() && node.morse.last() == '.'
            val edgeLabel = if (isDit) "·" else "—"
            val labelTextSize = (10f * scale).coerceIn(8f, 16f)

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = edgeColor.copy(alpha = nodeAlpha * 0.8f).toArgb()
                    textSize = labelTextSize * density
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                drawText(edgeLabel, midX, midY, paint)
            }
        }

        // Draw node circle
        val isHighlighted = searchQuery.isNotEmpty() &&
            node.character?.contains(searchQuery, ignoreCase = true) == true
        val isSelected = node.character == selectedCharacter
        val progress = node.character?.let { progressMap[it] } ?: 0f

        val nodeColor = when {
            isSelected -> MorseCyan
            isHighlighted -> MorseAmber
            node.character != null && progress > 0 -> progressToColor(progress)
            node.character != null -> Color(0xFF404040)
            else -> Color(0xFF252525)
        }

        // Glow for selected/highlighted
        if (isSelected || isHighlighted) {
            drawCircle(
                color = nodeColor.copy(alpha = 0.2f * nodeAlpha),
                radius = nodeRadius * 2f,
                center = Offset(x, y)
            )
        }

        // Node background
        drawCircle(
            color = nodeColor.copy(alpha = nodeAlpha * 0.3f),
            radius = nodeRadius,
            center = Offset(x, y)
        )

        // Progress ring
        if (node.character != null && progress > 0) {
            drawArc(
                color = progressToColor(progress).copy(alpha = nodeAlpha),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(x - nodeRadius, y - nodeRadius),
                size = Size(nodeRadius * 2, nodeRadius * 2),
                style = Stroke(width = 3f * density * scale, cap = StrokeCap.Round)
            )
        }

        // Node outline
        drawCircle(
            color = nodeColor.copy(alpha = nodeAlpha * 0.8f),
            radius = nodeRadius,
            center = Offset(x, y),
            style = Stroke(width = 2f * density * scale)
        )

        // Character text
        val character = node.character
        if (character != null) {
            val charTextSize = (14f * scale).coerceIn(10f, 24f)
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString(character),
                style = TextStyle(
                    fontSize = charTextSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = nodeAlpha),
                    textAlign = TextAlign.Center
                )
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x - textLayoutResult.size.width / 2f,
                    y - textLayoutResult.size.height / 2f
                )
            )
        }

        // Draw children
        val leftX = x - spacing
        val rightX = x + spacing
        val childY = y + baseVerticalSpacing

        node.leftChild?.let { child ->
            drawNode(child, leftX, childY, depth + 1, x, y)
        }
        node.rightChild?.let { child ->
            drawNode(child, rightX, childY, depth + 1, x, y)
        }
    }

    drawNode(rootNode, centerX, topY, 0, null, null)
}

// ═══════════════════════════════════════════════════════════════════
// NODE FINDING
// ═══════════════════════════════════════════════════════════════════

private fun findNodeAtPosition(
    node: MorseTreeNode,
    centerX: Float,
    topY: Float,
    scale: Float,
    density: Float,
    tapOffset: Offset
): MorseTreeNode? {
    val baseNodeRadius = 18f * density * scale
    val baseHorizontalSpacing = 140f * density * scale
    val baseVerticalSpacing = 90f * density * scale

    var result: MorseTreeNode? = null

    fun search(
        node: MorseTreeNode,
        x: Float,
        y: Float,
        depth: Int
    ) {
        val nodeRadius = baseNodeRadius * (1f - depth * 0.05f).coerceAtLeast(0.6f)
        val distance = kotlin.math.sqrt(
            (tapOffset.x - x) * (tapOffset.x - x) +
            (tapOffset.y - y) * (tapOffset.y - y)
        )

        if (distance <= nodeRadius * 1.5f && node.character != null) {
            result = node
            return
        }

        val spacing = baseHorizontalSpacing / (depth + 1).coerceAtLeast(1)
        val childY = y + baseVerticalSpacing

        node.leftChild?.let { search(it, x - spacing, childY, depth + 1) }
        node.rightChild?.let { search(it, x + spacing, childY, depth + 1) }
    }

    search(node, centerX, topY, 0)
    return result
}

// ═══════════════════════════════════════════════════════════════════
// UI COMPONENTS
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun NodeDetailSheet(
    node: MorseTreeNode,
    progress: Float,
    onPractice: () -> Unit,
    onPlayAudio: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        )

        Spacer(Modifier.height(24.dp))

        // Character display
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(progressToColor(progress).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                node.character ?: "",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = progressToColor(progress)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Morse code
        Text(
            node.morse.replace(".", "·").replace("-", "—"),
            style = MorseTypography.morseCodeLarge,
            color = MorseCyan
        )

        Spacer(Modifier.height(8.dp))

        // Mastery
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(120.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressToColor(progress),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(24.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onPlayAudio,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.VolumeUp, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Play Audio")
            }

            Button(
                onClick = onPractice,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MorseCyan)
            ) {
                Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Practice")
            }
        }
    }
}

@Composable
private fun LegendOverlay(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Mastery",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LegendItem(color = MasteryNovice, label = "Novice")
            LegendItem(color = MasteryApprentice, label = "Apprentice")
            LegendItem(color = MasteryJourneyman, label = "Journeyman")
            LegendItem(color = MasteryExpert, label = "Expert")
            LegendItem(color = MasteryMaster, label = "Master")
            LegendItem(color = MasteryGrandmaster, label = "Grandmaster")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ZoomIndicator(scale: Float, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 2.dp
    ) {
        Text(
            "${(scale * 100).toInt()}%",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// HELPERS
// ═══════════════════════════════════════════════════════════════════

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
