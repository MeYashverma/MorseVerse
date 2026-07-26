package com.morseverse.feature.morsetree.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.morseverse.core.domain.models.MorseElement
import com.morseverse.core.domain.models.MorseTreeNode
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════
// MORSE TREE SCREEN — Authentic Nothing OS
// Clean, spacious, warm monochrome with dot-matrix feel.
// Interactive binary decision tree with DIT/DAH navigation.
// ═══════════════════════════════════════════════════════════════════

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

    // ── Interactive state ──
    val inputSequence by viewModel.inputSequence.collectAsState()
    val currentNode by viewModel.currentNode.collectAsState()
    val pathNodes by viewModel.pathNodes.collectAsState()
    val practiceTarget by viewModel.practiceTarget.collectAsState()
    val practiceState by viewModel.practiceState.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()

    // ── Canvas transform ──
    var scale by remember { mutableFloatStateOf(0.65f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.3f, 3f)
        offset += panChange
    }

    // ── Animations ──
    val appearAnimation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appearAnimation.animateTo(1f, tween(1400, easing = FastOutSlowInEasing))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pathPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pathPulse"
    )
    val audioGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "audioGlow"
    )

    // ── Practice result animation ──
    var showPracticeResult by remember { mutableStateOf(false) }
    val practiceResultAlpha = remember { Animatable(0f) }
    LaunchedEffect(practiceState) {
        when (practiceState) {
            PracticeState.CORRECT, PracticeState.WRONG -> {
                showPracticeResult = true
                practiceResultAlpha.snapTo(1f)
                delay(1200)
                practiceResultAlpha.animateTo(0f, tween(400))
                showPracticeResult = false
                viewModel.resetPath()
            }
            else -> {}
        }
    }

    // ── Bottom sheet ──
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "morse tree",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 4.sp,
                            color = DarkOnBackground
                        )
                        if (practiceTarget != null) {
                            Text(
                                "practice mode",
                                style = MaterialTheme.typography.labelSmall,
                                color = NothingRed.copy(alpha = 0.7f),
                                letterSpacing = 2.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = DarkOnSurfaceVariant)
                    }
                },
                actions = {
                    var isSearchActive by remember { mutableStateOf(false) }
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text("search...", color = NothingGray500) },
                            singleLine = true,
                            modifier = Modifier.width(140.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                color = DarkOnBackground, letterSpacing = 1.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NothingGray600,
                                unfocusedBorderColor = DarkOutline
                            )
                        )
                    }
                    IconButton(onClick = {
                        isSearchActive = !isSearchActive
                        if (!isSearchActive) viewModel.updateSearchQuery("")
                    }) {
                        Icon(
                            if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                            "Search", tint = DarkOnSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        if (practiceTarget != null) viewModel.endPractice()
                        else viewModel.startPractice("E")
                    }) {
                        Icon(
                            if (practiceTarget != null) Icons.Filled.Stop else Icons.Filled.School,
                            "Practice",
                            tint = if (practiceTarget != null) NothingRed else DarkOnSurfaceVariant
                        )
                    }
                    IconButton(onClick = { scale = 0.65f; offset = Offset.Zero }) {
                        Icon(Icons.Filled.CenterFocusStrong, "Reset", tint = DarkOnSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            DitDahInputPanel(
                inputSequence = inputSequence,
                currentNode = currentNode,
                practiceTarget = practiceTarget,
                practiceState = practiceState,
                onDit = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onDitPressed()
                },
                onDah = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.onDahPressed()
                },
                onReset = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.resetPath()
                },
                onPlayAudio = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.playCurrentPathAudio()
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground)
        ) {
            tree?.let { rootNode ->
                val density = LocalDensity.current
                val textMeasurer = rememberTextMeasurer()
                val nodePositions = remember { mutableMapOf<String, Pair<Float, Float>>() }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformState)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { tapOffset ->
                                    val tappedNode = findNodeAtPosition(
                                        rootNode, size.width / 2f + offset.x,
                                        80f * density.density + offset.y,
                                        scale, density.density, tapOffset, nodePositions
                                    )
                                    if (tappedNode?.character != null) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.selectNode(tappedNode)
                                        showBottomSheet = true
                                    }
                                },
                                onLongPress = { tapOffset ->
                                    val tappedNode = findNodeAtPosition(
                                        rootNode, size.width / 2f + offset.x,
                                        80f * density.density + offset.y,
                                        scale, density.density, tapOffset, nodePositions
                                    )
                                    if (tappedNode?.character != null) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.playCharacterAudio(tappedNode.character!!)
                                    }
                                }
                            )
                        }
                ) {
                    drawMorseTree(
                        rootNode = rootNode,
                        centerX = size.width / 2f + offset.x,
                        topY = 80f * density.density + offset.y,
                        scale = scale,
                        density = density.density,
                        textMeasurer = textMeasurer,
                        progressMap = characterProgress,
                        searchQuery = searchQuery,
                        selectedCharacter = selectedNode?.character,
                        pathNodes = pathNodes,
                        currentNode = currentNode,
                        practiceTarget = practiceTarget,
                        animationProgress = appearAnimation.value,
                        pathPulse = pathPulse,
                        audioGlow = audioGlow,
                        isAudioPlaying = isAudioPlaying,
                        nodePositionsCache = nodePositions
                    )
                }

                // Path display overlay
                if (inputSequence.isNotEmpty()) {
                    PathDisplay(
                        inputSequence = inputSequence,
                        currentNode = currentNode,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp)
                    )
                }

                // Practice result overlay
                if (showPracticeResult) {
                    PracticeResultOverlay(
                        isCorrect = practiceState == PracticeState.CORRECT,
                        alpha = practiceResultAlpha.value,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NothingGray500, strokeWidth = 1.5.dp)
            }

            // Legend
            TreeLegend(modifier = Modifier.align(Alignment.BottomStart))
        }
    }

    // Bottom sheet
    if (showBottomSheet && selectedNode != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
            dragHandle = {
                Box(
                    Modifier.padding(top = 12.dp, bottom = 8.dp)
                        .width(28.dp).height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(NothingGray700)
                )
            }
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
                    },
                    onPracticeHere = {
                        showBottomSheet = false
                        node.character?.let { viewModel.startPractice(it) }
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// DIT/DAH INPUT PANEL — Clean Nothing OS
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun DitDahInputPanel(
    inputSequence: List<MorseElement>,
    currentNode: MorseTreeNode?,
    practiceTarget: String?,
    practiceState: PracticeState,
    onDit: () -> Unit,
    onDah: () -> Unit,
    onReset: () -> Unit,
    onPlayAudio: () -> Unit
) {
    Surface(color = DarkSurface, tonalElevation = 0.dp) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Practice target
            if (practiceTarget != null) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("target ", style = MaterialTheme.typography.labelSmall,
                        color = NothingGray500, letterSpacing = 2.sp)
                    Text(practiceTarget, style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, color = NothingRed)
                    Spacer(Modifier.width(12.dp))
                    Text(practiceState.displayName, style = MaterialTheme.typography.labelSmall,
                        color = when (practiceState) {
                            PracticeState.CORRECT -> MorseGreen
                            PracticeState.WRONG -> MorseRed
                            else -> NothingGray500
                        }, letterSpacing = 1.sp)
                }
                Spacer(Modifier.height(10.dp))
            }

            // Current node
            if (currentNode?.character != null && practiceTarget == null) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(currentNode.character ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, color = NothingRed)
                    Spacer(Modifier.width(12.dp))
                    Text(currentNode.morse.replace(".", "·").replace("-", "—"),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace),
                        color = NothingGray500)
                }
                Spacer(Modifier.height(10.dp))
            }

            // Input sequence
            if (inputSequence.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    inputSequence.forEach { element ->
                        Text(
                            if (element == MorseElement.DIT) "·" else "—",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace),
                            color = NothingRed.copy(alpha = 0.8f),
                            modifier = Modifier.padding(horizontal = 3.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            // Buttons
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                // Reset
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(0.dp))
                    .border(0.5.dp, NothingGray700, RoundedCornerShape(0.dp))
                    .clickable(onClick = onReset), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Refresh, "Reset", tint = NothingGray500,
                        modifier = Modifier.size(20.dp))
                }

                // DIT — circle shape
                Box(Modifier.weight(1f).height(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, NothingGray600, CircleShape)
                    .clickable(onClick = onDit), contentAlignment = Alignment.Center) {
                    Text("·", style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Monospace),
                        color = NothingGray300, fontWeight = FontWeight.Bold)
                }

                // DAH — rectangle shape
                Box(Modifier.weight(1f).height(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, NothingGray600, RoundedCornerShape(4.dp))
                    .clickable(onClick = onDah), contentAlignment = Alignment.Center) {
                    Text("—", style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Monospace),
                        color = NothingGray300, fontWeight = FontWeight.Bold)
                }

                // Play audio
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(0.dp))
                    .border(0.5.dp, NothingGray700, RoundedCornerShape(0.dp))
                    .clickable(onClick = onPlayAudio), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.VolumeUp, "Play", tint = NothingGray500,
                        modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// TREE RENDERING — Clean, spacious, Nothing OS aesthetic
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
    pathNodes: List<MorseTreeNode>,
    currentNode: MorseTreeNode?,
    practiceTarget: String?,
    animationProgress: Float,
    pathPulse: Float,
    audioGlow: Float,
    isAudioPlaying: Boolean,
    nodePositionsCache: MutableMap<String, Pair<Float, Float>>
) {
    // Generous spacing for clean look
    val baseNodeRadius = 14f * density * scale
    val baseHorizontalSpacing = 180f * density * scale  // Much wider
    val baseVerticalSpacing = 70f * density * scale

    val pathNodeIds = pathNodes.map { it.id }.toSet()

    fun drawNode(
        node: MorseTreeNode,
        x: Float, y: Float,
        depth: Int,
        parentX: Float?, parentY: Float?,
        isLeftChild: Boolean?
    ) {
        // Fade out nodes beyond depth 4 for cleanliness
        val nodeAlpha = when {
            depth > 5 -> 0f
            depth > 3 -> ((animationProgress - depth * 0.15f) * 2f).coerceIn(0f, 0.4f)
            else -> ((animationProgress - depth * 0.08f) * 3f).coerceIn(0f, 1f)
        }
        if (nodeAlpha <= 0.02f) return

        nodePositionsCache[node.id] = Pair(x, y)

        val nodeRadius = baseNodeRadius * (1f - depth * 0.04f).coerceAtLeast(0.65f)
        // Spacing narrows with depth but stays readable
        val spacing = baseHorizontalSpacing / (depth.coerceAtLeast(1) + 0.5f).toFloat()

        val isInPath = pathNodeIds.contains(node.id)
        val isCurrentNode = node.id == currentNode?.id
        val isHighlighted = searchQuery.isNotEmpty() &&
            node.character?.contains(searchQuery, ignoreCase = true) == true
        val isSelected = node.character == selectedCharacter
        val isPracticeTarget = node.character == practiceTarget
        val progress = node.character?.let { progressMap[it] } ?: 0f
        val isActive = isInPath || isCurrentNode || isSelected || isHighlighted

        // ── Edge to parent ──
        if (parentX != null && parentY != null) {
            val edgeColor = when {
                isInPath -> NothingRed.copy(alpha = (pathPulse * 0.7f * nodeAlpha).coerceIn(0f, 1f))
                isHighlighted -> NothingGray400.copy(alpha = 0.5f * nodeAlpha)
                else -> NothingGray800.copy(alpha = nodeAlpha * 0.5f)
            }

            // Thin, elegant line
            drawLine(
                color = edgeColor,
                start = Offset(parentX, parentY),
                end = Offset(x, y),
                strokeWidth = if (isInPath) 1.8f * density * scale else 1f * density * scale,
                cap = StrokeCap.Round
            )

            // Subtle glow on active edge
            if (isInPath && isAudioPlaying) {
                drawLine(
                    color = NothingRed.copy(alpha = (audioGlow * 0.08f).coerceIn(0f, 0.15f)),
                    start = Offset(parentX, parentY),
                    end = Offset(x, y),
                    strokeWidth = 6f * density * scale,
                    cap = StrokeCap.Round
                )
            }

            // Edge label (· or —) — only show for path or first 2 levels
            if (depth <= 3 || isInPath) {
                val midX = (parentX + x) / 2f
                val midY = (parentY + y) / 2f
                val isDit = isLeftChild == true
                val edgeLabel = if (isDit) "·" else "—"
                val labelTextSize = (9f * scale).coerceIn(7f, 14f)

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = if (isInPath) NothingRed.copy(alpha = nodeAlpha * 0.7f).toArgb()
                                else NothingGray600.copy(alpha = nodeAlpha * 0.5f).toArgb()
                        textSize = labelTextSize * density
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.MONOSPACE
                    }
                    drawText(edgeLabel, midX, midY + labelTextSize * density * 0.3f, paint)
                }
            }
        }

        // ── Node appearance ──
        val nodeColor = when {
            isCurrentNode && isAudioPlaying -> NothingRed.copy(alpha = audioGlow * 0.9f)
            isCurrentNode -> NothingRed.copy(alpha = 0.9f)
            isInPath -> NothingRed.copy(alpha = (pathPulse * 0.6f).coerceIn(0f, 1f))
            isSelected -> NothingGray300
            isHighlighted -> NothingGray400
            isPracticeTarget -> NothingRed.copy(alpha = 0.3f)
            node.character != null && progress > 0 -> NothingGray600
            node.character != null -> NothingGray700
            else -> NothingGray800
        }

        // Subtle glow for active nodes
        if (isActive && depth <= 4) {
            drawCircle(
                color = NothingRed.copy(alpha = (if (isCurrentNode) audioGlow * 0.06f else 0.04f).coerceIn(0f, 0.1f)),
                radius = nodeRadius * 2.5f,
                center = Offset(x, y)
            )
        }

        // ── Shape: Circle = dit (left), Rounded rect = dah (right) ──
        val isRoot = depth == 0
        val shapeIsCircle = isRoot || isLeftChild == true

        if (shapeIsCircle || isRoot) {
            // CIRCLE — subtle fill, thin outline
            drawCircle(
                color = nodeColor.copy(alpha = nodeAlpha * 0.1f),
                radius = nodeRadius,
                center = Offset(x, y)
            )
            drawCircle(
                color = nodeColor.copy(alpha = nodeAlpha * 0.8f),
                radius = nodeRadius,
                center = Offset(x, y),
                style = Stroke(
                    width = if (isActive) 1.5f * density * scale else 0.8f * density * scale,
                    cap = StrokeCap.Round
                )
            )
        } else {
            // ROUNDED RECTANGLE — subtle fill, thin outline
            val rectW = nodeRadius * 2.4f
            val rectH = nodeRadius * 1.5f
            val corner = CornerRadius(3f * density * scale)

            drawRoundRect(
                color = nodeColor.copy(alpha = nodeAlpha * 0.1f),
                topLeft = Offset(x - rectW / 2f, y - rectH / 2f),
                size = Size(rectW, rectH),
                cornerRadius = corner
            )
            drawRoundRect(
                color = nodeColor.copy(alpha = nodeAlpha * 0.8f),
                topLeft = Offset(x - rectW / 2f, y - rectH / 2f),
                size = Size(rectW, rectH),
                cornerRadius = corner,
                style = Stroke(
                    width = if (isActive) 1.5f * density * scale else 0.8f * density * scale,
                    cap = StrokeCap.Round
                )
            )
        }

        // ── Practice target pulse ──
        if (isPracticeTarget && !isInPath) {
            drawCircle(
                color = NothingRed.copy(alpha = (pathPulse * 0.1f).coerceIn(0f, 0.15f)),
                radius = nodeRadius * 1.8f,
                center = Offset(x, y)
            )
        }

        // ── Character text ──
        if (node.character != null && depth <= 4) {
            val charTextSize = (11f * scale).coerceIn(8f, 18f)
            val textResult = textMeasurer.measure(
                AnnotatedString(node.character ?: ""),
                TextStyle(
                    fontSize = charTextSize.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isInPath || isCurrentNode -> NothingRed.copy(alpha = nodeAlpha * 0.9f)
                        isSelected -> NothingWhite.copy(alpha = nodeAlpha)
                        else -> NothingGray300.copy(alpha = nodeAlpha * 0.8f)
                    },
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            )
            drawText(
                textResult,
                topLeft = Offset(x - textResult.size.width / 2f, y - textResult.size.height / 2f)
            )
        } else if (isRoot) {
            val textResult = textMeasurer.measure(
                AnnotatedString("root"),
                TextStyle(
                    fontSize = (8f * scale).sp,
                    fontWeight = FontWeight.Normal,
                    color = NothingGray500.copy(alpha = nodeAlpha * 0.6f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
            drawText(
                textResult,
                topLeft = Offset(x - textResult.size.width / 2f, y - textResult.size.height / 2f)
            )
        }

        // ── Children ──
        val leftX = x - spacing
        val rightX = x + spacing
        val childY = y + baseVerticalSpacing

        node.leftChild?.let { drawNode(it, leftX, childY, depth + 1, x, y, true) }
        node.rightChild?.let { drawNode(it, rightX, childY, depth + 1, x, y, false) }
    }

    drawNode(rootNode, centerX, topY, 0, null, null, null)
}

// ═══════════════════════════════════════════════════════════════════
// HIT TESTING
// ═══════════════════════════════════════════════════════════════════

private fun findNodeAtPosition(
    node: MorseTreeNode, centerX: Float, topY: Float,
    scale: Float, density: Float, tapOffset: Offset,
    positionCache: Map<String, Pair<Float, Float>>
): MorseTreeNode? {
    if (positionCache.isNotEmpty()) {
        var closest: MorseTreeNode? = null
        var closestDist = Float.MAX_VALUE
        fun search(n: MorseTreeNode) {
            val pos = positionCache[n.id]
            if (pos != null && n.character != null) {
                val dist = kotlin.math.sqrt(
                    (tapOffset.x - pos.first) * (tapOffset.x - pos.first) +
                    (tapOffset.y - pos.second) * (tapOffset.y - pos.second)
                )
                val hitRadius = 22f * density * scale
                if (dist < hitRadius && dist < closestDist) {
                    closestDist = dist; closest = n
                }
            }
            n.leftChild?.let { search(it) }
            n.rightChild?.let { search(it) }
        }
        search(node)
        return closest
    }

    // Fallback
    val baseNodeRadius = 14f * density * scale
    val baseHorizontalSpacing = 180f * density * scale
    val baseVerticalSpacing = 70f * density * scale
    var result: MorseTreeNode? = null

    fun search(n: MorseTreeNode, x: Float, y: Float, depth: Int) {
        val nodeRadius = baseNodeRadius * (1f - depth * 0.04f).coerceAtLeast(0.65f)
        val dist = kotlin.math.sqrt(
            (tapOffset.x - x) * (tapOffset.x - x) + (tapOffset.y - y) * (tapOffset.y - y)
        )
        if (dist <= nodeRadius * 2f && n.character != null) { result = n; return }
        val spacing = baseHorizontalSpacing / (depth.coerceAtLeast(1) + 0.5f).toFloat()
        val childY = y + baseVerticalSpacing
        n.leftChild?.let { search(it, x - spacing, childY, depth + 1) }
        n.rightChild?.let { search(it, x + spacing, childY, depth + 1) }
    }
    search(node, centerX, topY, 0)
    return result
}

// ═══════════════════════════════════════════════════════════════════
// PATH DISPLAY
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun PathDisplay(
    inputSequence: List<MorseElement>,
    currentNode: MorseTreeNode?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        color = DarkSurface.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("path ", style = MaterialTheme.typography.labelSmall,
                color = NothingGray500, letterSpacing = 2.sp)
            inputSequence.forEach { element ->
                Text(
                    if (element == MorseElement.DIT) "·" else "—",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                    color = NothingRed.copy(alpha = 0.8f)
                )
            }
            if (currentNode?.character != null) {
                Spacer(Modifier.width(8.dp))
                Text("→ ${currentNode.character}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = NothingRed)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// PRACTICE RESULT OVERLAY
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun PracticeResultOverlay(isCorrect: Boolean, alpha: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(100.dp).graphicsLayer(alpha = alpha)
            .border(1.dp, if (isCorrect) MorseGreen else MorseRed, RoundedCornerShape(0.dp))
            .background(if (isCorrect) MorseGreen.copy(alpha = 0.1f) else MorseRed.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                if (isCorrect) Icons.Filled.Check else Icons.Filled.Close, null,
                tint = if (isCorrect) MorseGreen else MorseRed,
                modifier = Modifier.size(36.dp)
            )
            Text(
                if (isCorrect) "correct" else "wrong",
                style = MaterialTheme.typography.labelMedium,
                color = if (isCorrect) MorseGreen else MorseRed,
                letterSpacing = 2.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// NODE DETAIL SHEET
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun NodeDetailSheet(
    node: MorseTreeNode, progress: Float,
    onPractice: () -> Unit, onPlayAudio: () -> Unit, onPracticeHere: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Character
        Box(Modifier.size(72.dp).border(1.dp, NothingGray600, RoundedCornerShape(0.dp)),
            contentAlignment = Alignment.Center) {
            Text(node.character ?: "", style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold, color = NothingRed, fontFamily = FontFamily.Monospace)
        }

        Spacer(Modifier.height(16.dp))

        // Morse
        Text(node.morse.replace(".", "·").replace("-", "—"),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Monospace, letterSpacing = 6.sp),
            color = DarkOnBackground)

        Spacer(Modifier.height(12.dp))

        // Progress bar — thin, subtle
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(100.dp).height(1.5.dp).background(NothingGray800)) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress).background(NothingGray400))
            }
            Text("${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = NothingGray500, letterSpacing = 1.sp)
        }

        Spacer(Modifier.height(24.dp))

        // Buttons
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onPlayAudio, Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkOnBackground),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray700)) {
                Icon(Icons.Filled.VolumeUp, null, Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("play", letterSpacing = 2.sp, style = MaterialTheme.typography.labelSmall)
            }
            OutlinedButton(onClick = onPracticeHere, Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NothingRed.copy(alpha = 0.8f)),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray700)) {
                Icon(Icons.Filled.School, null, Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("practice", letterSpacing = 2.sp, style = MaterialTheme.typography.labelSmall)
            }
            Button(onClick = onPractice, Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NothingGray700, contentColor = DarkOnBackground)) {
                Icon(Icons.Filled.OpenInNew, null, Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("details", letterSpacing = 2.sp, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// LEGEND
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun TreeLegend(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(12.dp),
        shape = RoundedCornerShape(0.dp),
        color = DarkSurface.copy(alpha = 0.9f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, NothingGray800)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("legend", style = MaterialTheme.typography.labelSmall,
                color = NothingGray500, letterSpacing = 2.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Canvas(Modifier.size(10.dp)) {
                    drawCircle(NothingGray500, radius = size.minDimension / 2f, style = Stroke(1.5f))
                }
                Text("· dit (circle)", style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace), color = NothingGray500)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Canvas(Modifier.size(10.dp)) {
                    drawRoundRect(NothingGray500, cornerRadius = CornerRadius(1.5f), style = Stroke(1.5f))
                }
                Text("— dah (rect)", style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace), color = NothingGray500)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).background(NothingRed.copy(alpha = 0.2f)))
                Text("active path", style = MaterialTheme.typography.bodySmall, color = NothingGray500)
            }
        }
    }
}
