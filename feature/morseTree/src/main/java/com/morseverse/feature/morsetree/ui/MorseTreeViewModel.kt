package com.morseverse.feature.morsetree.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseverse.core.common.utils.MorseAudioEngine
import com.morseverse.core.domain.models.AudioConfig
import com.morseverse.core.domain.models.MorseTreeNode
import com.morseverse.core.domain.repository.MorseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MorseTreeViewModel @Inject constructor(
    private val repository: MorseRepository,
    private val audioEngine: MorseAudioEngine
) : ViewModel() {

    private val _tree = MutableStateFlow<MorseTreeNode?>(null)
    val tree: StateFlow<MorseTreeNode?> = _tree.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedNode = MutableStateFlow<MorseTreeNode?>(null)
    val selectedNode: StateFlow<MorseTreeNode?> = _selectedNode.asStateFlow()

    val characterProgress: StateFlow<Map<String, Float>> = repository.getAllCharacterProgress()
        .map { list -> list.associate { it.character to it.mastery } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    init {
        loadTree()
    }

    private fun loadTree() {
        viewModelScope.launch(Dispatchers.Default) {
            _tree.value = repository.getMorseTree()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectNode(node: MorseTreeNode) {
        _selectedNode.value = node
    }

    fun playCharacterAudio(character: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val morse = repository.getCharacter(character)?.morse ?: return@launch
            val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
            val samples = audioEngine.generateMorseAudio(morse, config)
            audioEngine.playAudio(samples)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
