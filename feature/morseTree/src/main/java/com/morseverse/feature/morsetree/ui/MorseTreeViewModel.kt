package com.morseverse.feature.morsetree.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.common.utils.AudioConfig
import com.morseverse.core.common.utils.MorseAudioEngine
import com.morseverse.core.domain.models.MorseElement
import com.morseverse.core.domain.models.MorseTreeNode
import com.morseverse.core.domain.repository.MorseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════════════════
// PRACTICE STATE
// ═══════════════════════════════════════════════════════════════════

enum class PracticeState(val displayName: String) {
    IDLE(""),
    WAITING("Enter morse..."),
    CORRECT("✓ Correct!"),
    WRONG("✗ Wrong!")
}

// ═══════════════════════════════════════════════════════════════════
// MORSE TREE VIEW MODEL
// Manages interactive tree navigation, path tracking, audio sync,
// and practice mode state.
// ═══════════════════════════════════════════════════════════════════

@HiltViewModel
class MorseTreeViewModel @Inject constructor(
    private val repository: MorseRepository,
    private val audioEngine: MorseAudioEngine
) : ViewModel() {

    // ── Tree data ──
    private val _tree = MutableStateFlow<MorseTreeNode?>(null)
    val tree: StateFlow<MorseTreeNode?> = _tree.asStateFlow()

    // ── Search ──
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── Selected node (for bottom sheet) ──
    private val _selectedNode = MutableStateFlow<MorseTreeNode?>(null)
    val selectedNode: StateFlow<MorseTreeNode?> = _selectedNode.asStateFlow()

    // ── Character progress ──
    val characterProgress: StateFlow<Map<String, Float>> = repository.getAllCharacterProgress()
        .map { list -> list.associate { it.character to it.mastery } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // ── Interactive tree state ──
    private val _inputSequence = MutableStateFlow<List<MorseElement>>(emptyList())
    val inputSequence: StateFlow<List<MorseElement>> = _inputSequence.asStateFlow()

    private val _currentNode = MutableStateFlow<MorseTreeNode?>(null)
    val currentNode: StateFlow<MorseTreeNode?> = _currentNode.asStateFlow()

    private val _pathNodes = MutableStateFlow<List<MorseTreeNode>>(emptyList())
    val pathNodes: StateFlow<List<MorseTreeNode>> = _pathNodes.asStateFlow()

    // ── Practice state ──
    private val _practiceTarget = MutableStateFlow<String?>(null)
    val practiceTarget: StateFlow<String?> = _practiceTarget.asStateFlow()

    private val _practiceState = MutableStateFlow(PracticeState.IDLE)
    val practiceState: StateFlow<PracticeState> = _practiceState.asStateFlow()

    // ── Audio state ──
    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private var audioJob: Job? = null

    // ── All characters for practice ──
    private val allCharacters = MorseCodeData.INTERNATIONAL_MORSE.keys
        .filter { it.length == 1 && it[0].isLetter() }
        .toList()

    init {
        loadTree()
    }

    private fun loadTree() {
        viewModelScope.launch(Dispatchers.Default) {
            val rootNode = repository.getMorseTree()
            _tree.value = rootNode
            _currentNode.value = rootNode
            _pathNodes.value = listOf(rootNode)
        }
    }

    // ─── SEARCH ─────────────────────────────────────────────────────

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // ─── NODE SELECTION ─────────────────────────────────────────────

    fun selectNode(node: MorseTreeNode) {
        _selectedNode.value = node
    }

    // ─── INTERACTIVE TREE NAVIGATION ────────────────────────────────

    /**
     * User pressed DIT (·) — move to left child
     */
    fun onDitPressed() {
        val current = _currentNode.value ?: return
        val leftChild = current.leftChild ?: return

        _inputSequence.value = _inputSequence.value + MorseElement.DIT
        _currentNode.value = leftChild
        _pathNodes.value = _pathNodes.value + leftChild

        // Play dit sound
        playElementAudio(MorseElement.DIT)

        // Check if we reached a character
        checkCharacterReached(leftChild)
    }

    /**
     * User pressed DAH (—) — move to right child
     */
    fun onDahPressed() {
        val current = _currentNode.value ?: return
        val rightChild = current.rightChild ?: return

        _inputSequence.value = _inputSequence.value + MorseElement.DAH
        _currentNode.value = rightChild
        _pathNodes.value = _pathNodes.value + rightChild

        // Play dah sound
        playElementAudio(MorseElement.DAH)

        // Check if we reached a character
        checkCharacterReached(rightChild)
    }

    /**
     * Reset the current path back to root
     */
    fun resetPath() {
        audioJob?.cancel()
        _isAudioPlaying.value = false

        val root = _tree.value ?: return
        _inputSequence.value = emptyList()
        _currentNode.value = root
        _pathNodes.value = listOf(root)

        if (_practiceState.value != PracticeState.IDLE) {
            _practiceState.value = PracticeState.WAITING
        }
    }

    // ─── PRACTICE MODE ──────────────────────────────────────────────

    /**
     * Start practice mode with a target character
     */
    fun startPractice(targetCharacter: String) {
        _practiceTarget.value = targetCharacter.uppercase()
        _practiceState.value = PracticeState.WAITING
        resetPath()
    }

    /**
     * End practice mode
     */
    fun endPractice() {
        _practiceTarget.value = null
        _practiceState.value = PracticeState.IDLE
        resetPath()
    }

    /**
     * Check if the user reached a character node during practice
     */
    private fun checkCharacterReached(node: MorseTreeNode) {
        val character = node.character ?: return
        val target = _practiceTarget.value ?: return

        if (character == target) {
            _practiceState.value = PracticeState.CORRECT
            // Play success feedback
            playSuccessFeedback()
        } else if (node.leftChild == null && node.rightChild == null) {
            // Leaf node but wrong character
            _practiceState.value = PracticeState.WRONG
            // Play error feedback
            playErrorFeedback()
        }
        // If not a leaf and not the target, keep waiting for more input
    }

    // ─── AUDIO ──────────────────────────────────────────────────────

    /**
     * Play a single dit or dah element sound
     */
    private fun playElementAudio(element: MorseElement) {
        audioJob?.cancel()
        audioJob = viewModelScope.launch(Dispatchers.IO) {
            _isAudioPlaying.value = true
            val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
            val morse = if (element == MorseElement.DIT) "." else "-"
            val samples = audioEngine.generateMorseAudio(morse, config)
            audioEngine.playAudio(samples) {
                _isAudioPlaying.value = false
            }
        }
    }

    /**
     * Play the full Morse audio for the current path's character
     */
    fun playCurrentPathAudio() {
        val node = _currentNode.value ?: return
        val character = node.character ?: return
        playCharacterAudio(character)
    }

    /**
     * Play Morse audio for a specific character
     */
    fun playCharacterAudio(character: String) {
        audioJob?.cancel()
        audioJob = viewModelScope.launch(Dispatchers.IO) {
            _isAudioPlaying.value = true
            val morse = repository.getCharacter(character)?.morse ?: return@launch
            val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
            val samples = audioEngine.generateMorseAudio(morse, config)
            audioEngine.playAudio(samples) {
                _isAudioPlaying.value = false
            }
        }
    }

    /**
     * Play the full path audio — animates through each element with delays
     */
    fun playPathAudio() {
        val path = _pathNodes.value
        if (path.size <= 1) return

        audioJob?.cancel()
        audioJob = viewModelScope.launch(Dispatchers.IO) {
            _isAudioPlaying.value = true
            val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
            val ditMs = audioEngine.ditDurationMs(config.wpm)

            for (i in 1 until path.size) {
                val node = path[i]
                val morse = node.morse
                // Play just the last element of this node's morse
                val lastElement = if (morse.isNotEmpty()) morse.last().toString() else continue
                val samples = audioEngine.generateMorseAudio(lastElement, config)
                audioEngine.playAudio(samples) {}
                delay((ditMs * if (lastElement == "-") 4 else 2).toLong())
            }
            _isAudioPlaying.value = false
        }
    }

    /**
     * Play success feedback tone
     */
    private fun playSuccessFeedback() {
        viewModelScope.launch(Dispatchers.IO) {
            val samples = audioEngine.generateFeedbackTone(
                frequency = 880,
                durationMs = 100f,
                volume = 0.4f
            )
            audioEngine.playAudio(samples) {}
        }
    }

    /**
     * Play error feedback tone
     */
    private fun playErrorFeedback() {
        viewModelScope.launch(Dispatchers.IO) {
            val samples = audioEngine.generateFeedbackTone(
                frequency = 300,
                durationMs = 200f,
                volume = 0.4f
            )
            audioEngine.playAudio(samples) {}
        }
    }

    // ─── PRACTICE WITH NEXT CHARACTER ───────────────────────────────

    /**
     * Move to next practice character
     */
    fun nextPracticeCharacter() {
        val currentTarget = _practiceTarget.value ?: return
        val currentIndex = allCharacters.indexOf(currentTarget)
        val nextIndex = (currentIndex + 1) % allCharacters.size
        startPractice(allCharacters[nextIndex])
    }

    /**
     * Practice a random character
     */
    fun practiceRandomCharacter() {
        val randomChar = allCharacters.random()
        startPractice(randomChar)
    }

    // ─── CLEANUP ────────────────────────────────────────────────────

    override fun onCleared() {
        super.onCleared()
        audioJob?.cancel()
        audioEngine.release()
    }
}
