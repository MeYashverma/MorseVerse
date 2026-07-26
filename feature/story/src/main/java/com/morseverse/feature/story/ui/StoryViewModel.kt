package com.morseverse.feature.story.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseverse.core.domain.models.StoryMission
import com.morseverse.core.domain.repository.MorseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: MorseRepository
) : ViewModel() {

    val missions: StateFlow<List<StoryMission>> = repository.getAllMissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
