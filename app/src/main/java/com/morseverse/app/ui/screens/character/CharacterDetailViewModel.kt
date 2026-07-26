package com.morseverse.app.ui.screens.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseverse.core.common.utils.MorseAudioEngine
import com.morseverse.core.common.utils.AudioConfig
import com.morseverse.core.domain.models.CharacterProgress
import com.morseverse.core.domain.models.MasteryLevel
import com.morseverse.core.domain.repository.MorseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository: MorseRepository,
    private val audioEngine: MorseAudioEngine
) : ViewModel() {

    private val _progress = MutableStateFlow(CharacterProgress(character = ""))
    val progress: StateFlow<CharacterProgress> = _progress.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun loadCharacter(character: String) {
        viewModelScope.launch {
            repository.getCharacterProgress(character).collect {
                _progress.value = it
            }
        }
    }

    fun playAudio(character: String) {
        if (_isPlaying.value) {
            audioEngine.stopAudio()
            _isPlaying.value = false
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val morse = repository.getCharacter(character)?.morse ?: return@launch
            _isPlaying.value = true
            val config = AudioConfig(wpm = 15, frequency = 600, volume = 0.8f)
            val samples = audioEngine.generateMorseAudio(morse, config)
            audioEngine.playAudio(samples) {
                _isPlaying.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
