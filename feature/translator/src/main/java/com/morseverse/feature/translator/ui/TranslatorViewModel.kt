package com.morseverse.feature.translator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.common.utils.MorseAudioEngine
import com.morseverse.core.domain.models.AudioConfig
import com.morseverse.core.domain.models.TranslationEntry
import com.morseverse.core.domain.repository.MorseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TranslatorViewModel @Inject constructor(
    private val repository: MorseRepository,
    private val audioEngine: MorseAudioEngine
) : ViewModel() {

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _outputMorse = MutableStateFlow("")
    val outputMorse: StateFlow<String> = _outputMorse.asStateFlow()

    private val _isTextToMorse = MutableStateFlow(true)
    val isTextToMorse: StateFlow<Boolean> = _isTextToMorse.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    val history: StateFlow<List<TranslationEntry>> = repository.getTranslationHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setInputText(text: String) {
        _inputText.value = text
        translate()
    }

    fun toggleDirection() {
        _isTextToMorse.value = !_isTextToMorse.value
        _inputText.value = ""
        _outputMorse.value = ""
    }

    private fun translate() {
        val input = _inputText.value
        if (input.isBlank()) {
            _outputMorse.value = ""
            return
        }

        _outputMorse.value = if (_isTextToMorse.value) {
            textToMorse(input)
        } else {
            morseToText(input)
        }
    }

    private fun textToMorse(text: String): String {
        return text.uppercase().map { char ->
            when (char) {
                ' ' -> "/"  // Word separator
                else -> MorseCodeData.INTERNATIONAL_MORSE[char.toString()] ?: ""
            }
        }.filter { it.isNotEmpty() }.joinToString(" ")
    }

    private fun morseToText(morse: String): String {
        return morse.trim().split(" ").joinToString("") { token ->
            when (token) {
                "/" -> " "
                else -> MorseCodeData.REVERSE_MORSE[token] ?: "?"
            }
        }
    }

    fun playAudio() {
        if (_isPlaying.value) {
            audioEngine.stopAudio()
            _isPlaying.value = false
            return
        }

        val morse = _outputMorse.value
        if (morse.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isPlaying.value = true
            val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
            val samples = audioEngine.generateMorseAudio(morse, config)
            audioEngine.playAudio(samples) {
                _isPlaying.value = false
            }
        }
    }

    fun flashlightOutput() {
        // Would use CameraManager to flash the light in Morse pattern
        // Implementation depends on platform flashlight API
    }

    fun shareTranslation() {
        // Would use Share Intent
    }

    fun saveToFavorites() {
        val text = _inputText.value
        val morse = _outputMorse.value
        if (text.isBlank() || morse.isBlank()) return

        viewModelScope.launch {
            repository.saveTranslation(
                TranslationEntry(
                    id = UUID.randomUUID().toString(),
                    text = text,
                    morse = morse,
                    timestamp = System.currentTimeMillis(),
                    isFavorite = true
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
