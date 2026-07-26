package com.morseverse.feature.practice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.common.utils.AudioConfig
import com.morseverse.core.common.utils.MorseAudioEngine
import com.morseverse.core.domain.models.CharacterProgress
import com.morseverse.core.domain.models.MasteryLevel
import com.morseverse.core.domain.models.PracticeMode
import com.morseverse.core.domain.models.PracticeSession
import com.morseverse.core.domain.repository.MorseRepository
import com.morseverse.core.domain.usecases.CalculateMastery
import com.morseverse.core.domain.usecases.CalculateXp
import com.morseverse.core.domain.usecases.GeneratePracticeChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PracticeSessionViewModel @Inject constructor(
    private val repository: MorseRepository,
    private val audioEngine: MorseAudioEngine,
    private val generatePracticeChallenge: GeneratePracticeChallenge,
    private val calculateMastery: CalculateMastery,
    private val calculateXp: CalculateXp
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var currentItems = mutableListOf<com.morseverse.core.domain.usecases.PracticeItem>()
    private var correctCount = 0
    private var totalCount = 0
    private var streak = 0
    private var bestStreak = 0
    private var startTime = 0L
    private var itemStartTime = 0L

    fun startSession(mode: PracticeMode) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isComplete = false) }

            val challenge = generatePracticeChallenge(mode)
            currentItems = challenge.items.toMutableList()
            correctCount = 0
            totalCount = 0
            streak = 0
            bestStreak = 0
            startTime = System.currentTimeMillis()

            if (currentItems.isNotEmpty()) {
                loadItem(0)
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    totalItems = currentItems.size
                )
            }
        }
    }

    private fun loadItem(index: Int) {
        if (index >= currentItems.size) {
            completeSession()
            return
        }

        val item = currentItems[index]
        itemStartTime = System.currentTimeMillis()

        // Generate answer choices
        val correctAnswer = item.character
        val allChars = MorseCodeData.INTERNATIONAL_MORSE.keys.toList()
        val wrongAnswers = allChars.filter { it != correctAnswer }.shuffled().take(3)
        val choices = (wrongAnswers + correctAnswer).shuffled()

        _uiState.update {
            it.copy(
                currentIndex = index,
                currentMorse = item.morse,
                choices = choices,
                lastResult = null
            )
        }

        // Auto-play audio
        playCurrentAudio()
    }

    fun submitAnswer(answer: String) {
        val item = currentItems.getOrNull(_uiState.value.currentIndex) ?: return
        val reactionTime = System.currentTimeMillis() - itemStartTime
        val isCorrect = answer == item.character

        totalCount++
        if (isCorrect) {
            correctCount++
            streak++
            bestStreak = maxOf(bestStreak, streak)
        } else {
            streak = 0
        }

        val accuracy = if (totalCount > 0) correctCount.toFloat() / totalCount else 0f

        _uiState.update {
            it.copy(
                lastResult = AnswerResult(
                    given = answer,
                    expected = item.character,
                    isCorrect = isCorrect,
                    reactionTimeMs = reactionTime
                ),
                accuracy = accuracy,
                streak = streak,
                bestStreak = bestStreak
            )
        }

        // Update character progress
        viewModelScope.launch {
            val existingProgress = repository.getCharacterProgress(item.character).first()
            val (newMastery, newLevel) = calculateMastery(
                totalAttempts = existingProgress.totalAttempts + 1,
                correctAttempts = existingProgress.correctAttempts + if (isCorrect) 1 else 0,
                averageReactionTimeMs = ((existingProgress.averageReactionTimeMs * existingProgress.totalAttempts + reactionTime) / (existingProgress.totalAttempts + 1))
            )

            repository.updateCharacterProgress(
                existingProgress.copy(
                    totalAttempts = existingProgress.totalAttempts + 1,
                    correctAttempts = existingProgress.correctAttempts + if (isCorrect) 1 else 0,
                    averageReactionTimeMs = ((existingProgress.averageReactionTimeMs * existingProgress.totalAttempts + reactionTime) / (existingProgress.totalAttempts + 1)),
                    mastery = newMastery,
                    lastPracticed = System.currentTimeMillis(),
                    streak = if (isCorrect) existingProgress.streak + 1 else 0,
                    level = newLevel
                )
            )
        }

        // Auto advance after delay
        viewModelScope.launch {
            kotlinx.coroutines.delay(if (isCorrect) 800L else 1500L)
            loadItem(_uiState.value.currentIndex + 1)
        }
    }

    fun skipCurrent() {
        loadItem(_uiState.value.currentIndex + 1)
    }

    fun showHint() {
        val item = currentItems.getOrNull(_uiState.value.currentIndex) ?: return
        // Filter choices to show only 2 options including the correct one
        val wrongAnswer = _uiState.value.choices.filter { it != item.character }.randomOrNull() ?: return
        _uiState.update {
            it.copy(choices = listOf(item.character, wrongAnswer).shuffled())
        }
    }

    fun playCurrentAudio() {
        val morse = _uiState.value.currentMorse
        if (morse.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
            val samples = audioEngine.generateMorseAudio(morse, config)
            audioEngine.playAudio(samples)
        }
    }

    private fun completeSession() {
        val xp = calculateXp(
            correct = correctCount,
            total = totalCount,
            wpm = 20,
            mode = PracticeMode.CHARACTER
        )

        _uiState.update {
            it.copy(
                isComplete = true,
                xpEarned = xp
            )
        }

        // Save session
        viewModelScope.launch {
            repository.saveSession(
                PracticeSession(
                    id = UUID.randomUUID().toString(),
                    startTime = startTime,
                    endTime = System.currentTimeMillis(),
                    mode = PracticeMode.CHARACTER,
                    totalCharacters = totalCount,
                    correctCharacters = correctCount,
                    averageWpm = 20f,
                    xpEarned = xp
                )
            )
            repository.addXp(xp)
        }
    }

    fun endSession() {
        audioEngine.stopAudio()
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
