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
// MORSE TREE SCREEN — Nothing OS Aesthetic
// Interactive binary decision tree with DIT/DAH input,
// live path animation, audio sync, and practice mode.
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

    // ── Interactive state from ViewModel ──
    val inputSequence by viewModel.inputSequence.collectAsState()
    val currentNode by viewModel.currentNode.collectAsState()
    val pathNodes by viewModel.pathNodes.collectAsState()
    val practiceTarget by viewModel.practiceTarget.collectAsState()
    val practiceState by viewModel.practiceState.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()

    // ── Canvas transform ──
    var scale by remember { mutableFloatStateOf(0.55f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.2f, 3f)
        offset += panChange
    }

    // ── Animation states ──
    val appearAnimation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appearAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    // ── Path pulse animation ──
    val infiniteTransition = rememberInfiniteTransition(label = "pathPulse")
    val pathPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pathPulse"
    )

    // ── Audio active glow ──
    val audioGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
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
                delay(1500)
                practiceResultAlpha.animateTo(0f, tween(500))
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
                            "MORSE TREE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (practiceTarget != null) {
                            Text(
                                "PRACTICE MODE",
                                style = MaterialTheme.typography.labelSmall,
                                color = NothingRed,
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
                        Icon(
                            Icons.Filled.ArrowBack,
                            "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Search toggle
                    var isSearchActive by remember { mutableStateOf(false) }

                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = {
                                Text(
                                    "Search...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.width(160.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NothingRed,
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
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Practice toggle
                    IconButton(onClick = {
                        if (practiceTarget != null) {
                            viewModel.endPractice()
                        } else {
                            viewModel.startPractice("E") // Start with simplest
                        }
                    }) {
                        Icon(
                            if (practiceTarget != null) Icons.Filled.Stop else Icons.Filled.School,
                            contentDescription = "Practice",
                            tint = if (practiceTarget != null) NothingRed
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Reset view
                    IconButton(onClick = {
                        scale = 0.55f
                        offset = Offset.Zero
                    }) {
                        Icon(
                            Icons.Filled.CenterFocusStrong,
                            "Reset view",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // ── DIT/DAH Input Panel ──
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            tree?.let { rootNode ->
                val density = LocalDensity.current
                val textMeasurer = rememberTextMeasurer()

                // ── Cache node positions for performance ──
                val nodePositions = remember(rootNode, scale, offset, density.density) {
                    mutableMapOf<String, Pair<Float, Float>>()
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformState)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { tapOffset ->
                                    val canvasWidth = size.width
                                    val canvasHeight = size.height
                                    val centerX = canvasWidth / 2f + offset.x
                                    val topY = 60f * density.density + offset.y

                                    val tappedNode = findNodeAtPosition(
                                        rootNode, centerX, topY, scale,
                                        density.density, tapOffset, nodePositions
                                    )
                                    if (tappedNode != null && tappedNode.character != null) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.selectNode(tappedNode)
                                        showBottomSheet = true
                                    }
                                },
                                onLongPress = { tapOffset ->
                                    val centerX = size.width / 2f + offset.x
                                    val topY = 60f * density.density + offset.y

                                    val tappedNode = findNodeAtPosition(
                                        rootNode, centerX, topY, scale,
                                        density.density, tapOffset, nodePositions
                                    )
                                    if (tappedNode != null && tappedNode.character != null) {
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
                        topY = 60f * density.density + offset.y,
                        scale = scale,
                        density = density.density,
                        textMeasurer = textMeasurer,
                        progressMap = characterProgress,
                        searchQuery = searchQuery,
                        selectedCharacter = selectedNode?.character,
                        pathNodes = pathNodes,
                        currentNode = currentNode,
                        practiceTarget = practiceTarget,
                        practiceState = practiceState,
                        animationProgress = appearAnimation.value,
                        pathPulse = pathPulse,
                        audioGlow = audioGlow,
                        isAudioPlaying = isAudioPlaying,
                        nodePositionsCache = nodePositions
                    )
                }

                // ── Current path display ──
                if (inputSequence.isNotEmpty()) {
                    PathDisplay(
                        inputSequence = inputSequence,
                        currentNode = currentNode,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                    )
                }

                // ── Practice result overlay ──
                if (showPracticeResult) {
                    PracticeResultOverlay(
                        isCorrect = practiceState == PracticeState.CORRECT,
                        alpha = practiceResultAlpha.value,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

            } ?: run {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = NothingRed,
                        strokeWidth = 2.dp
                    )
                }
            }

            // ── Legend (Nothing-style) ──
            TreeLegend(modifier = Modifier.align(Alignment.BottomStart))
        }
    }

    // ── Bottom Sheet for character details ──
    if (showBottomSheet && selectedNode != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp), // Nothing: sharp corners
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .width(32.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkOutline)
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
// DIT/DAH INPUT PANEL — Nothing OS aesthetic
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
    Surface(
        color = DarkSurface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 8.dp) // Safe area
        ) {
            // ── Practice target indicator ──
            if (practiceTarget != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "TARGET: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkOnSurfaceVariant,
                        letterSpacing = 2.sp
                    )
                    Text(
                        practiceTarget,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = NothingRed
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        practiceState.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (practiceState) {
                            PracticeState.CORRECT -> MorseGreen
                            PracticeState.WRONG -> MorseRed
                            else -> DarkOnSurfaceVariant
                        },
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Current node indicator ──
            if (currentNode?.character != null && practiceTarget == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "→ ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkOnSurfaceVariant
                    )
                    Text(
                        currentNode.character ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = NothingRed
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        currentNode.morse.replace(".", "·").replace("-", "—"),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = DarkOnSurfaceVariant
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Input sequence display ──
            if (inputSequence.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    inputSequence.forEach { element ->
                        Text(
                            if (element == MorseElement.DIT) "·" else "—",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = NothingRed,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── DIT / DAH buttons ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reset button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(0.dp)) // Nothing: sharp corners
                        .border(1.dp, DarkOutline, RoundedCornerShape(0.dp))
                        .clickable(onClick = onReset),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Reset",
                        tint = DarkOnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // DIT button (circle — dot)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(CircleShape)
                        .border(2.dp, NothingRed, CircleShape)
                        .clickable(onClick = onDit),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "·",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = NothingRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // DAH button (rectangle — dash)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(0.dp)) // Nothing: sharp corners = dash
                        .border(2.dp, NothingRed, RoundedCornerShape(0.dp))
                        .background(NothingRed.copy(alpha = 0.05f))
                        .clickable(onClick = onDah),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "—",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = NothingRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Play audio button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(0.dp))
                        .border(1.dp, DarkOutline, RoundedCornerShape(0.dp))
                        .clickable(onClick = onPlayAudio),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = "Play audio",
                        tint = DarkOnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// TREE RENDERING — Canvas with Nothing OS aesthetic
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
    practiceState: PracticeState,
    animationProgress: Float,
    pathPulse: Float,
    audioGlow: Float,
    isAudioPlaying: Boolean,
    nodePositionsCache: MutableMap<String, Pair<Float, Float>>
) {
    val baseNodeRadius = 16f * density * scale
    val baseHorizontalSpacing = 130f * density * scale
    val baseVerticalSpacing = 80f * density * scale

    // Cache path node IDs for O(1) lookup
    val pathNodeIds = pathNodes.map { it.id }.toSet()

    fun drawNode(
        node: MorseTreeNode,
        x: Float,
        y: Float,
        depth: Int,
        parentX: Float?,
        parentY: Float?,
        isLeftChild: Boolean? // null = root, true = dit, false = dah
    ) {
        // Animate appearance based on depth
        val nodeAlpha = ((animationProgress - depth * 0.08f) * 3f).coerceIn(0f, 1f)
        if (nodeAlpha <= 0f) return

        // Cache position
        nodePositionsCache[node.id] = Pair(x, y)

        val nodeRadius = baseNodeRadius * (1f - depth * 0.03f).coerceAtLeast(0.7f)
        val spacing = baseHorizontalSpacing / (depth.coerceAtLeast(1) + 1).toFloat()

        val isInPath = pathNodeIds.contains(node.id)
        val isCurrentNode = node.id == currentNode?.id
        val isHighlighted = searchQuery.isNotEmpty() &&
            node.character?.contains(searchQuery, ignoreCase = true) == true
        val isSelected = node.character == selectedCharacter
        val isPracticeTarget = node.character == practiceTarget
        val progress = node.character?.let { progressMap[it] } ?: 0f

        // ── Draw edge to parent ──
        if (parentX != null && parentY != null) {
            val edgeAlpha = if (isInPath) pathPulse else 0.4f
            val edgeColor = when {
                isInPath -> NothingRed.copy(alpha = edgeAlpha * nodeAlpha)
                isHighlighted -> MorseAmber.copy(alpha = 0.6f * nodeAlpha)
                else -> DarkOutline.copy(alpha = nodeAlpha * 0.6f)
            }

            // Edge line
            drawLine(
                color = edgeColor,
                start = Offset(parentX, parentY),
                end = Offset(x, y),
                strokeWidth = if (isInPath) 2.5f * density * scale else 1.5f * density * scale,
                cap = StrokeCap.Round
            )

            // Glow on active edge during audio
            if (isInPath && isAudioPlaying) {
                drawLine(
                    color = NothingRed.copy(alpha = audioGlow * 0.15f),
                    start = Offset(parentX, parentY),
                    end = Offset(x, y),
                    strokeWidth = 8f * density * scale,
                    cap = StrokeCap.Round
                )
            }

            // Dot/Dah label on edge
            val midX = (parentX + x) / 2f
            val midY = (parentY + y) / 2f
            val isDit = isLeftChild == true
            val edgeLabel = if (isDit) "·" else "—"
            val labelTextSize = (11f * scale).coerceIn(8f, 18f)

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = if (isInPath) NothingRed.copy(alpha = nodeAlpha).toArgb()
                            else DarkOnSurfaceVariant.copy(alpha = nodeAlpha * 0.6f).toArgb()
                    textSize = labelTextSize * density
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.MONOSPACE,
                        android.graphics.Typeface.NORMAL
                    )
                }
                drawText(edgeLabel, midX, midY + labelTextSize * density * 0.35f, paint)
            }
        }

        // ── Node shape: Circle = dit branch, Rectangle = dash branch ──
        val isActive = isInPath || isCurrentNode || isSelected || isHighlighted
        val nodeColor = when {
            isCurrentNode && isAudioPlaying -> NothingRed.copy(alpha = audioGlow)
            isCurrentNode -> NothingRed
            isInPath -> NothingRed.copy(alpha = pathPulse * 0.8f)
            isSelected -> MorseCyan
            isHighlighted -> MorseAmber
            isPracticeTarget -> NothingRed.copy(alpha = 0.5f)
            node.character != null && progress > 0 -> NothingGray400
            node.character != null -> NothingGray600
            else -> NothingGray800
        }

        // Node glow
        if (isActive) {
            val glowRadius = nodeRadius * 2.5f
            val glowAlpha = if (isCurrentNode && isAudioPlaying) audioGlow * 0.15f
                           else if (isInPath) pathPulse * 0.1f
                           else 0.08f
            drawCircle(
                color = NothingRed.copy(alpha = glowAlpha.coerceIn(0f, 0.3f)),
                radius = glowRadius,
                center = Offset(x, y)
            )
        }

        // Draw shape based on node type
        val isRoot = depth == 0
        val shapeIsCircle = isRoot || isLeftChild == true // Dit = circle

        if (shapeIsCircle || isRoot) {
            // ── CIRCLE (dit branch or root) ──
            // Fill
            drawCircle(
                color = nodeColor.copy(alpha = nodeAlpha * 0.15f),
                radius = nodeRadius,
                center = Offset(x, y)
            )
            // Outline
            drawCircle(
                color = nodeColor.copy(alpha = nodeAlpha),
                radius = nodeRadius,
                center = Offset(x, y),
                style = Stroke(
                    width = if (isActive) 2.5f * density * scale else 1.5f * density * scale,
                    cap = StrokeCap.Round
                )
            )
        } else {
            // ── RECTANGLE (dah branch) ──
            val rectW = nodeRadius * 2.2f
            val rectH = nodeRadius * 1.6f
            val cornerRadius = CornerRadius(4f * density * scale)

            // Fill
            drawRoundRect(
                color = nodeColor.copy(alpha = nodeAlpha * 0.15f),
                topLeft = Offset(x - rectW / 2f, y - rectH / 2f),
                size = Size(rectW, rectH),
                cornerRadius = cornerRadius
            )
            // Outline
            drawRoundRect(
                color = nodeColor.copy(alpha = nodeAlpha),
                topLeft = Offset(x - rectW / 2f, y - rectH / 2f),
                size = Size(rectW, rectH),
                cornerRadius = cornerRadius,
                style = Stroke(
                    width = if (isActive) 2.5f * density * scale else 1.5f * density * scale,
                    cap = StrokeCap.Round
                )
            )
        }

        // ── Progress ring (for leaf nodes with mastery) ──
        if (node.character != null && progress > 0 && !isInPath) {
            drawArc(
                color = NothingGray400.copy(alpha = nodeAlpha * 0.5f),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(x - nodeRadius - 3f, y - nodeRadius - 3f),
                size = Size((nodeRadius + 3f) * 2f, (nodeRadius + 3f) * 2f),
                style = Stroke(width = 1.5f * density * scale, cap = StrokeCap.Round)
            )
        }

        // ── Practice target flash ──
        if (isPracticeTarget && !isInPath) {
            val flashAlpha = (pathPulse * 0.3f).coerceIn(0f, 0.3f)
            drawCircle(
                color = NothingRed.copy(alpha = flashAlpha),
                radius = nodeRadius * 1.8f,
                center = Offset(x, y)
            )
        }

        // ── Character text ──
        if (node.character != null) {
            val charTextSize = (13f * scale).coerceIn(9f, 22f)
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString(node.character ?: ""),
                style = TextStyle(
                    fontSize = charTextSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isInPath || isCurrentNode -> NothingRed.copy(alpha = nodeAlpha)
                        isSelected -> MorseCyan.copy(alpha = nodeAlpha)
                        else -> Color.White.copy(alpha = nodeAlpha * 0.9f)
                    },
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace
                )
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x - textLayoutResult.size.width / 2f,
                    y - textLayoutResult.size.height / 2f
                )
            )
        } else if (isRoot) {
            // Root node label
            val rootTextSize = (10f * scale).coerceIn(7f, 14f)
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString("ROOT"),
                style = TextStyle(
                    fontSize = rootTextSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = NothingRed.copy(alpha = nodeAlpha * 0.8f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
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

        // ── Draw children ──
        val leftX = x - spacing
        val rightX = x + spacing
        val childY = y + baseVerticalSpacing

        node.leftChild?.let { child ->
            drawNode(child, leftX, childY, depth + 1, x, y, true)
        }
        node.rightChild?.let { child ->
            drawNode(child, rightX, childY, depth + 1, x, y, false)
        }
    }

    drawNode(rootNode, centerX, topY, 0, null, null, null)
}

// ═══════════════════════════════════════════════════════════════════
// NODE HIT TESTING
// ═══════════════════════════════════════════════════════════════════

private fun findNodeAtPosition(
    node: MorseTreeNode,
    centerX: Float,
    topY: Float,
    scale: Float,
    density: Float,
    tapOffset: Offset,
    positionCache: Map<String, Pair<Float, Float>>
): MorseTreeNode? {
    // Use cached positions if available
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
                val hitRadius = 24f * density * scale
                if (dist < hitRadius && dist < closestDist) {
                    closestDist = dist
                    closest = n
                }
            }
            n.leftChild?.let { search(it) }
            n.rightChild?.let { search(it) }
        }
        search(node)
        return closest
    }

    // Fallback: calculate positions
    val baseNodeRadius = 16f * density * scale
    val baseHorizontalSpacing = 130f * density * scale
    val baseVerticalSpacing = 80f * density * scale

    var result: MorseTreeNode? = null

    fun search(n: MorseTreeNode, x: Float, y: Float, depth: Int) {
        val nodeRadius = baseNodeRadius * (1f - depth * 0.03f).coerceAtLeast(0.7f)
        val distance = kotlin.math.sqrt(
            (tapOffset.x - x) * (tapOffset.x - x) +
            (tapOffset.y - y) * (tapOffset.y - y)
        )

        if (distance <= nodeRadius * 2f && n.character != null) {
            result = n
            return
        }

        val spacing = baseHorizontalSpacing / (depth.coerceAtLeast(1) + 1).toFloat()
        val childY = y + baseVerticalSpacing

        n.leftChild?.let { search(it, x - spacing, childY, depth + 1) }
        n.rightChild?.let { search(it, x + spacing, childY, depth + 1) }
    }

    search(node, centerX, topY, 0)
    return result
}

// ═══════════════════════════════════════════════════════════════════
// PATH DISPLAY — Shows current input sequence
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun PathDisplay(
    inputSequence: List<MorseElement>,
    currentNode: MorseTreeNode?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp), // Nothing: sharp corners
        color = DarkSurface.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "PATH:",
                style = MaterialTheme.typography.labelSmall,
                color = DarkOnSurfaceVariant,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.width(4.dp))
            inputSequence.forEach { element ->
                Text(
                    if (element == MorseElement.DIT) "·" else "—",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = NothingRed
                )
            }
            if (currentNode?.character != null) {
                Spacer(Modifier.width(8.dp))
                Text(
                    "→ ${currentNode.character}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NothingRed
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// PRACTICE RESULT OVERLAY
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun PracticeResultOverlay(
    isCorrect: Boolean,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .graphicsLayer(alpha = alpha)
            .clip(RoundedCornerShape(0.dp))
            .background(
                if (isCorrect) MorseGreen.copy(alpha = 0.2f)
                else MorseRed.copy(alpha = 0.2f)
            )
            .border(
                2.dp,
                if (isCorrect) MorseGreen else MorseRed,
                RoundedCornerShape(0.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                tint = if (isCorrect) MorseGreen else MorseRed,
                modifier = Modifier.size(48.dp)
            )
            Text(
                if (isCorrect) "CORRECT" else "WRONG",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) MorseGreen else MorseRed,
                letterSpacing = 2.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// NODE DETAIL SHEET — Nothing OS aesthetic
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun NodeDetailSheet(
    node: MorseTreeNode,
    progress: Float,
    onPractice: () -> Unit,
    onPlayAudio: () -> Unit,
    onPracticeHere: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Character display
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(2.dp, NothingRed, RoundedCornerShape(0.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                node.character ?: "",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = NothingRed,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.height(16.dp))

        // Morse code
        Text(
            node.morse.replace(".", "·").replace("-", "—"),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Monospace,
                letterSpacing = 6.sp
            ),
            color = DarkOnBackground
        )

        Spacer(Modifier.height(8.dp))

        // Mastery progress bar (Nothing-style: thin, monochrome)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(2.dp)
                    .background(DarkOutline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(NothingRed)
                )
            }
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = DarkOnSurfaceVariant,
                letterSpacing = 1.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        // Action buttons (Nothing-style: outlined, sharp corners)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onPlayAudio,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkOnBackground
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Icon(Icons.Filled.VolumeUp, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "PLAY",
                    letterSpacing = 2.sp,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            OutlinedButton(
                onClick = onPracticeHere,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NothingRed
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, NothingRed)
            ) {
                Icon(Icons.Filled.School, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "PRACTICE",
                    letterSpacing = 2.sp,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Button(
                onClick = onPractice,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NothingRed,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Filled.OpenInNew, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "DETAILS",
                    letterSpacing = 2.sp,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// TREE LEGEND — Nothing OS aesthetic
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun TreeLegend(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(12.dp),
        shape = RoundedCornerShape(0.dp),
        color = DarkSurface.copy(alpha = 0.9f),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "LEGEND",
                style = MaterialTheme.typography.labelSmall,
                color = DarkOnSurfaceVariant,
                letterSpacing = 2.sp
            )

            // Dit = Circle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(
                        color = NothingGray400,
                        radius = size.minDimension / 2f,
                        style = Stroke(width = 2f)
                    )
                }
                Text(
                    "· DIT (circle)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = DarkOnSurfaceVariant
                )
            }

            // Dah = Rectangle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawRoundRect(
                        color = NothingGray400,
                        cornerRadius = CornerRadius(2f),
                        style = Stroke(width = 2f)
                    )
                }
                Text(
                    "— DAH (rect)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = DarkOnSurfaceVariant
                )
            }

            // Path highlight
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(NothingRed.copy(alpha = 0.3f))
                )
                Text(
                    "Active path",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkOnSurfaceVariant
                )
            }
        }
    }
}
