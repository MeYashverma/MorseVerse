package com.morseverse.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseverse.core.domain.models.*
import com.morseverse.core.domain.repository.MorseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MorseRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val dailyStats: StateFlow<DailyStats> = repository.getDailyStats(
        Clock.System.todayIn(TimeZone.currentSystemDefault())
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        DailyStats(date = Clock.System.todayIn(TimeZone.currentSystemDefault()))
    )

    val weakCharacters: StateFlow<List<String>> = repository.getWeakCharacters()
        .map { list -> list.map { it.character } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSessions: StateFlow<List<PracticeSession>> = repository.getRecentSessions(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaysLesson: StateFlow<List<String>> = repository.getTodaysLesson()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
