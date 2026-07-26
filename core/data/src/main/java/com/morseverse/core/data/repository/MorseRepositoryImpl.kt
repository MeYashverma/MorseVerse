package com.morseverse.core.data.repository

import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.data.database.dao.*
import com.morseverse.core.data.database.entities.*
import com.morseverse.core.data.mappers.toDomain
import com.morseverse.core.data.mappers.toEntity
import com.morseverse.core.domain.models.*
import com.morseverse.core.domain.repository.CoachAnalysis
import com.morseverse.core.domain.repository.CoachSuggestion
import com.morseverse.core.domain.repository.MorseRepository
import com.morseverse.core.domain.repository.SuggestionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MorseRepositoryImpl @Inject constructor(
    private val characterProgressDao: CharacterProgressDao,
    private val practiceSessionDao: PracticeSessionDao,
    private val characterResultDao: CharacterResultDao,
    private val dailyStatsDao: DailyStatsDao,
    private val userProfileDao: UserProfileDao,
    private val achievementDao: AchievementDao,
    private val translationHistoryDao: TranslationHistoryDao,
    private val storyProgressDao: StoryProgressDao,
    private val kochProgressDao: KochProgressDao
) : MorseRepository {

    // ─── CHARACTER DATA ──────────────────────────────────────────────

    override fun getAllCharacters(): List<MorseCharacter> {
        return MorseCodeData.INTERNATIONAL_MORSE.map { (char, morse) ->
            val elements = morse.map { if (it == '.') MorseElement.DIT else MorseElement.DAH }
            val category = when {
                char.length == 1 && char[0].isLetter() -> CharacterCategory.LETTER
                char.length == 1 && char[0].isDigit() -> CharacterCategory.NUMBER
                char.length == 1 -> CharacterCategory.PUNCTUATION
                char.startsWith("<") -> CharacterCategory.PROSIGN
                else -> CharacterCategory.SPECIAL
            }
            MorseCharacter(char, morse, elements, category)
        }
    }

    override fun getCharacter(character: String): MorseCharacter? {
        val morse = MorseCodeData.INTERNATIONAL_MORSE[character.uppercase()] ?: return null
        val elements = morse.map { if (it == '.') MorseElement.DIT else MorseElement.DAH }
        return MorseCharacter(character.uppercase(), morse, elements)
    }

    override fun getMorseToChar(morse: String): String? {
        return MorseCodeData.REVERSE_MORSE[morse]
    }

    override fun getCharactersByCategory(category: CharacterCategory): List<MorseCharacter> {
        return getAllCharacters().filter { it.category == category }
    }

    override fun getMorseTree(): MorseTreeNode {
        return buildMorseTree()
    }

    override fun getKochLessons(): List<List<String>> = MorseCodeData.KOCH_LESSONS

    override fun getTraditionalGroups(): List<List<String>> = MorseCodeData.TRADITIONAL_GROUPS

    override fun getCharacterGroups(): List<List<String>> = MorseCodeData.TRADITIONAL_GROUPS

    // ─── PROGRESS ────────────────────────────────────────────────────

    override fun getCharacterProgress(character: String): Flow<CharacterProgress> {
        return characterProgressDao.getByCharacter(character).map { entity ->
            entity?.toDomain() ?: CharacterProgress(character = character)
        }
    }

    override fun getAllCharacterProgress(): Flow<List<CharacterProgress>> {
        return characterProgressDao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun updateCharacterProgress(progress: CharacterProgress) {
        characterProgressDao.insert(progress.toEntity())
    }

    override fun getWeakCharacters(): Flow<List<CharacterProgress>> {
        return characterProgressDao.getWeakCharacters().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getStrongCharacters(): Flow<List<CharacterProgress>> {
        return characterProgressDao.getStrongCharacters().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getTodaysLesson(): Flow<List<String>> {
        return characterProgressDao.getLeastRecentlyPracticed(5).map { list ->
            if (list.isEmpty()) {
                listOf("K", "M") // Start with Koch lesson 1
            } else {
                list.map { it.character }
            }
        }
    }

    // ─── SESSIONS ────────────────────────────────────────────────────

    override suspend fun saveSession(session: PracticeSession) {
        practiceSessionDao.insert(session.toEntity())
        if (session.characters.isNotEmpty()) {
            characterResultDao.insertAll(session.characters.map { result ->
                CharacterResultEntity(
                    sessionId = session.id,
                    character = result.character,
                    expected = result.expected,
                    isCorrect = result.isCorrect,
                    reactionTimeMs = result.reactionTimeMs
                )
            })
        }
    }

    override fun getSessionHistory(): Flow<List<PracticeSession>> {
        return practiceSessionDao.getAll().map { list ->
            list.map { entity ->
                val results = characterResultDao.getBySession(entity.id)
                PracticeSession(
                    id = entity.id,
                    startTime = entity.startTime,
                    endTime = entity.endTime,
                    mode = PracticeMode.valueOf(entity.mode),
                    totalCharacters = entity.totalCharacters,
                    correctCharacters = entity.correctCharacters,
                    averageWpm = entity.averageWpm,
                    averageReactionTimeMs = entity.averageReactionTimeMs,
                    xpEarned = entity.xpEarned
                )
            }
        }
    }

    override fun getRecentSessions(limit: Int): Flow<List<PracticeSession>> {
        return practiceSessionDao.getRecent(limit).map { list ->
            list.map { entity ->
                PracticeSession(
                    id = entity.id,
                    startTime = entity.startTime,
                    endTime = entity.endTime,
                    mode = PracticeMode.valueOf(entity.mode),
                    totalCharacters = entity.totalCharacters,
                    correctCharacters = entity.correctCharacters,
                    averageWpm = entity.averageWpm,
                    averageReactionTimeMs = entity.averageReactionTimeMs,
                    xpEarned = entity.xpEarned
                )
            }
        }
    }

    override fun getSessionById(id: String): Flow<PracticeSession?> {
        return practiceSessionDao.getById(id).map { entity ->
            entity?.let {
                PracticeSession(
                    id = it.id,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    mode = PracticeMode.valueOf(it.mode),
                    totalCharacters = it.totalCharacters,
                    correctCharacters = it.correctCharacters,
                    averageWpm = it.averageWpm,
                    averageReactionTimeMs = it.averageReactionTimeMs,
                    xpEarned = it.xpEarned
                )
            }
        }
    }

    // ─── STATISTICS ──────────────────────────────────────────────────

    override fun getDailyStats(date: LocalDate): Flow<DailyStats> {
        return dailyStatsDao.getByDate(date.toString()).map { entity ->
            entity?.toDomain() ?: DailyStats(date = date)
        }
    }

    override fun getDailyStatsRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyStats>> {
        return dailyStatsDao.getByDateRange(startDate.toString(), endDate.toString()).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getWeeklyStats(): Flow<List<DailyStats>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val weekAgo = today.minus(7, DateTimeUnit.DAY)
        return getDailyStatsRange(weekAgo, today)
    }

    override fun getMonthlyStats(): Flow<List<DailyStats>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val monthAgo = today.minus(30, DateTimeUnit.DAY)
        return getDailyStatsRange(monthAgo, today)
    }

    override fun getHeatmapData(year: Int): Flow<Map<LocalDate, Int>> {
        val startDate = LocalDate(year, 1, 1)
        val endDate = LocalDate(year, 12, 31)
        return getDailyStatsRange(startDate, endDate).map { list ->
            list.associate { it.date to it.totalCharacters }
        }
    }

    // ─── PROFILE ─────────────────────────────────────────────────────

    override fun getUserProfile(): Flow<UserProfile> {
        return userProfileDao.getProfile().map { entity ->
            entity?.toDomain() ?: UserProfile()
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.insert(profile.toEntity())
    }

    override suspend fun addXp(amount: Int) {
        userProfileDao.addXp(amount)
    }

    override suspend fun updateStreak() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val profile = userProfileDao.getProfile()
        // Simple streak logic - if last active was yesterday, increment
        userProfileDao.updateStreak(1) // Simplified
    }

    override suspend fun updateDailyGoal(minutes: Int) {
        userProfileDao.updateDailyGoal(minutes)
    }

    // ─── ACHIEVEMENTS ────────────────────────────────────────────────

    override fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getUnlocked().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun unlockAchievement(achievementId: String) {
        achievementDao.unlock(achievementId)
    }

    override suspend fun checkAchievements(): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()
        // Achievement checking logic would go here
        return newlyUnlocked
    }

    // ─── TRANSLATIONS ────────────────────────────────────────────────

    override fun getTranslationHistory(): Flow<List<TranslationEntry>> {
        return translationHistoryDao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getFavorites(): Flow<List<TranslationEntry>> {
        return translationHistoryDao.getFavorites().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveTranslation(entry: TranslationEntry) {
        translationHistoryDao.insert(entry.toEntity())
    }

    override suspend fun deleteTranslation(id: String) {
        translationHistoryDao.delete(id)
    }

    override suspend fun toggleFavorite(id: String) {
        translationHistoryDao.toggleFavorite(id)
    }

    // ─── STORY ───────────────────────────────────────────────────────

    override fun getAllMissions(): Flow<List<StoryMission>> {
        return storyProgressDao.getAll().map { progressList ->
            MorseCodeData.STORY_MISSIONS.map { data ->
                val progress = progressList.find { it.missionId == data.id }
                StoryMission(
                    id = data.id,
                    title = data.title,
                    description = data.description,
                    category = MissionCategory.SOS,
                    difficulty = 1,
                    chapters = data.chapters.map { (title, narrative) ->
                        StoryChapter(
                            id = "${data.id}_${title.replace(" ", "_").lowercase()}",
                            title = title,
                            narrative = narrative,
                            challenges = emptyList()
                        )
                    },
                    charactersIntroduced = data.characters.toSet(),
                    xpReward = 100,
                    isUnlocked = progress?.isUnlocked ?: (data == MorseCodeData.STORY_MISSIONS.first()),
                    isCompleted = progress?.isCompleted ?: false,
                    progress = progress?.progress ?: 0f
                )
            }
        }
    }

    override fun getMissionById(id: String): Flow<StoryMission?> {
        return getAllMissions().map { missions ->
            missions.find { it.id == id }
        }
    }

    override suspend fun completeChapter(missionId: String, chapterId: String) {
        val progress = storyProgressDao.getByMissionId(missionId)
        // Update chapter completion
        storyProgressDao.updateProgress(
            missionId = missionId,
            chapters = chapterId,
            progress = 0.5f
        )
    }

    override suspend fun unlockMission(missionId: String) {
        storyProgressDao.unlock(missionId)
    }

    // ─── AI COACH ────────────────────────────────────────────────────

    override suspend fun analyzePerformance(): CoachAnalysis {
        val weakChars = characterProgressDao.getWeakCharacters()
        val allProgress = characterProgressDao.getAll()

        return CoachAnalysis(
            weakCharacters = emptyList(),
            confusingPairs = emptyList(),
            averageReactionTime = 0,
            learningSpeed = 1.0f,
            recommendedWpm = 20,
            focusCharacters = emptyList(),
            suggestedPracticeMinutes = 15
        )
    }

    override fun getCoachSuggestions(): Flow<CoachSuggestion> {
        return characterProgressDao.getWeakCharacters().map { weakChars ->
            if (weakChars.isNotEmpty()) {
                CoachSuggestion(
                    type = SuggestionType.WEAK_AREA,
                    title = "Focus on weak characters",
                    description = "You have ${weakChars.size} characters that need practice",
                    priority = 1
                )
            } else {
                CoachSuggestion(
                    type = SuggestionType.NEW_CHARACTERS,
                    title = "Ready for new characters!",
                    description = "Your mastery is looking good. Try learning new characters.",
                    priority = 2
                )
            }
        }
    }

    // ─── HELPERS ─────────────────────────────────────────────────────

    private fun buildMorseTree(): MorseTreeNode {
        fun buildNode(
            id: String,
            char: String?,
            morse: String,
            depth: Int,
            pos: TreePosition
        ): MorseTreeNode {
            val leftChar = MorseCodeData.INTERNATIONAL_MORSE.entries
                .find { it.value == "$morse." }?.key
            val rightChar = MorseCodeData.INTERNATIONAL_MORSE.entries
                .find { it.value == "$morse-" }?.key

            val leftChild = if (depth < 5) {
                buildNode("${id}L", leftChar, "$morse.", depth + 1,
                    TreePosition(pos.x - 1f / (depth + 1), pos.y + 1f))
            } else null

            val rightChild = if (depth < 5) {
                buildNode("${id}R", rightChar, "$morse-", depth + 1,
                    TreePosition(pos.x + 1f / (depth + 1), pos.y + 1f))
            } else null

            return MorseTreeNode(
                id = id,
                character = char,
                morse = morse,
                depth = depth,
                position = pos,
                leftChild = leftChild,
                rightChild = rightChild,
                parentNodeId = if (id.isNotEmpty()) id.dropLast(1) else null
            )
        }

        return buildNode("", null, "", 0, TreePosition(0f, 0f))
    }
}
