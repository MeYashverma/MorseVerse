package com.morseverse.feature.learn.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.domain.models.LearningMethod
import com.morseverse.core.domain.repository.MorseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val repository: MorseRepository
) : ViewModel() {

    private val _selectedMethod = MutableStateFlow(LearningMethod.KOCH)
    val selectedMethod: StateFlow<LearningMethod> = _selectedMethod.asStateFlow()

    val kochProgress = repository.getAllCharacterProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val characterGroups: StateFlow<List<List<String>>> = _selectedMethod.map { method ->
        when (method) {
            LearningMethod.KOCH -> MorseCodeData.KOCH_LESSONS
            LearningMethod.TRADITIONAL -> MorseCodeData.TRADITIONAL_GROUPS
            LearningMethod.FARNSWORTH -> MorseCodeData.KOCH_LESSONS
            else -> MorseCodeData.TRADITIONAL_GROUPS
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MorseCodeData.KOCH_LESSONS)

    val currentLesson: StateFlow<Int> = repository.getKochLessons().let {
        MutableStateFlow(1) // Default
    }

    fun selectMethod(method: LearningMethod) {
        _selectedMethod.value = method
    }

    fun selectLesson(index: Int) {
        // Store selected lesson for practice session
    }
}
