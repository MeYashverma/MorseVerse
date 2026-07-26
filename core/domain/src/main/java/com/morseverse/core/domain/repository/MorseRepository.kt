package com.morseverse.core.domain.repository

import com.morseverse.core.domain.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Main repository interface for Morse code data
 */
interface MorseRepository {
    // Character data
    fun getAllCharacters(): List<MorseCharacter>
    fun getCharacter(character: String): MorseCharacter?
    fun getMorseToChar(morse: String): String?
    fun getCharactersByCategory(category: CharacterCategory): List<MorseCharacter>
    fun getMorseTree(): MorseTreeNode

    // Learning
    fun getKochLessons(): List<List<String>>
    fun getTraditionalGroups(): List<List<String>>
    fun getCharacterGroups(): List<List<String>>

    // Progress
    fun getCharacterProgress(character: String): Flow<CharacterProgress>
    fun getAllCharacterProgress(): Flow<List<CharacterProgress>>
    suspend fun updateCharacterProgress(progress: CharacterProgress)
    fun getWeakCharacters(): Flow<List<CharacterProgress>>
    fun getStrongCharacters(): Flow<List<CharacterProgress>>
    fun getTodaysLesson(): Flow<List<String>>

    // Sessions
    suspend fun saveSession(session: PracticeSession)
    fun getSessionHistory(): Flow<List<PracticeSession>>
    fun getRecentSessions(limit: Int): Flow<List<PracticeSession>>
    fun getSessionById(id: String): Flow<PracticeSession?>

    // Statistics
    fun getDailyStats(date: LocalDate): Flow<DailyStats>
    fun getDailyStatsRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyStats>>
    fun getWeeklyStats(): Flow<List<DailyStats>>
    fun getMonthlyStats(): Flow<List<DailyStats>>
    fun getHeatmapData(year: Int): Flow<Map<LocalDate, Int>>

    // Profile
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun addXp(amount: Int)
    suspend fun updateStreak()
    suspend fun updateDailyGoal(minutes: Int)

    // Achievements
    fun getAllAchievements(): Flow<List<Achievement>>
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    suspend fun unlockAchievement(achievementId: String)
    suspend fun checkAchievements(): List<Achievement>

    // Translations
    fun getTranslationHistory(): Flow<List<TranslationEntry>>
    fun getFavorites(): Flow<List<TranslationEntry>>
    suspend fun saveTranslation(entry: TranslationEntry)
    suspend fun deleteTranslation(id: String)
    suspend fun toggleFavorite(id: String)

    // Story
    fun getAllMissions(): Flow<List<StoryMission>>
    fun getMissionById(id: String): Flow<StoryMission?>
    suspend fun completeChapter(missionId: String, chapterId: String)
    suspend fun unlockMission(missionId: String)

    // AI Coach
    suspend fun analyzePerformance(): CoachAnalysis
    fun getCoachSuggestions(): Flow<CoachSuggestion>
}

data class CoachAnalysis(
    val weakCharacters: List<String>,
    val confusingPairs: List<Pair<String, String>>,
    val averageReactionTime: Long,
    val learningSpeed: Float,
    val recommendedWpm: Int,
    val focusCharacters: List<String>,
    val suggestedPracticeMinutes: Int
)

data class CoachSuggestion(
    val type: SuggestionType,
    val title: String,
    val description: String,
    val priority: Int
)

enum class SuggestionType {
    PRACTICE, REVIEW, SPEED_UP, SLOW_DOWN, NEW_CHARACTERS, WEAK_AREA
}
