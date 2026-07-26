package com.morseverse.core.data.database.dao

import androidx.room.*
import com.morseverse.core.data.database.entities.*
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════
// DATA ACCESS OBJECTS
// ═══════════════════════════════════════════════════════════════════

@Dao
interface CharacterProgressDao {
    @Query("SELECT * FROM character_progress")
    fun getAll(): Flow<List<CharacterProgressEntity>>

    @Query("SELECT * FROM character_progress WHERE character = :character")
    fun getByCharacter(character: String): Flow<CharacterProgressEntity?>

    @Query("SELECT * FROM character_progress WHERE mastery < 0.5 AND totalAttempts > 5 ORDER BY mastery ASC")
    fun getWeakCharacters(): Flow<List<CharacterProgressEntity>>

    @Query("SELECT * FROM character_progress WHERE mastery >= 0.8 ORDER BY mastery DESC")
    fun getStrongCharacters(): Flow<List<CharacterProgressEntity>>

    @Query("SELECT * FROM character_progress ORDER BY lastPracticed ASC LIMIT :limit")
    fun getLeastRecentlyPracticed(limit: Int): Flow<List<CharacterProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: CharacterProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progress: List<CharacterProgressEntity>)

    @Update
    suspend fun update(progress: CharacterProgressEntity)

    @Delete
    suspend fun delete(progress: CharacterProgressEntity)
}

@Dao
interface PracticeSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PracticeSessionEntity)

    @Query("SELECT * FROM practice_sessions ORDER BY startTime DESC")
    fun getAll(): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions WHERE id = :id")
    fun getById(id: String): Flow<PracticeSessionEntity?>

    @Query("SELECT * FROM practice_sessions WHERE startTime BETWEEN :startTime AND :endTime")
    fun getByTimeRange(startTime: Long, endTime: Long): Flow<List<PracticeSessionEntity>>

    @Query("SELECT COUNT(*) FROM practice_sessions")
    fun getTotalSessions(): Flow<Int>

    @Query("SELECT AVG(averageWpm) FROM practice_sessions")
    fun getAverageWpm(): Flow<Float?>

    @Query("SELECT SUM(xpEarned) FROM practice_sessions")
    fun getTotalXp(): Flow<Int?>
}

@Dao
interface CharacterResultDao {
    @Insert
    suspend fun insert(result: CharacterResultEntity)

    @Insert
    suspend fun insertAll(results: List<CharacterResultEntity>)

    @Query("SELECT * FROM character_results WHERE sessionId = :sessionId")
    fun getBySession(sessionId: String): Flow<List<CharacterResultEntity>>

    @Query("SELECT * FROM character_results WHERE character = :character ORDER BY timestamp DESC")
    fun getByCharacter(character: String): Flow<List<CharacterResultEntity>>

    @Query("SELECT character, COUNT(*) as count, SUM(CASE WHEN isCorrect THEN 1 ELSE 0 END) as correct FROM character_results GROUP BY character")
    fun getCharacterAccuracySummary(): Flow<List<CharacterAccuracySummary>>
}

data class CharacterAccuracySummary(
    val character: String,
    val count: Int,
    val correct: Int
)

@Dao
interface DailyStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: DailyStatsEntity)

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    fun getByDate(date: String): Flow<DailyStatsEntity?>

    @Query("SELECT * FROM daily_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getByDateRange(startDate: String, endDate: String): Flow<List<DailyStatsEntity>>

    @Query("SELECT * FROM daily_stats ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<DailyStatsEntity>>

    @Query("SELECT * FROM daily_stats WHERE dailyGoalMet = 1 ORDER BY date DESC")
    fun getGoalMetDays(): Flow<List<DailyStatsEntity>>

    @Query("SELECT SUM(totalPracticeMinutes) FROM daily_stats WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalMinutesInRange(startDate: String, endDate: String): Flow<Int?>
}

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 0")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("UPDATE user_profile SET totalXp = totalXp + :amount WHERE id = 0")
    suspend fun addXp(amount: Int)

    @Query("UPDATE user_profile SET currentStreak = :streak, longestStreak = MAX(longestStreak, :streak) WHERE id = 0")
    suspend fun updateStreak(streak: Int)

    @Query("UPDATE user_profile SET dailyGoalMinutes = :minutes WHERE id = 0")
    suspend fun updateDailyGoal(minutes: Int)
}

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Query("SELECT * FROM achievements ORDER BY category, title")
    fun getAll(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlocked(): Flow<List<AchievementEntity>>

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :id")
    suspend fun unlock(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM achievements WHERE id = :id")
    fun getById(id: String): Flow<AchievementEntity?>

    @Query("UPDATE achievements SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Float)
}

@Dao
interface TranslationHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: TranslationEntryEntity)

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TranslationEntryEntity>>

    @Query("SELECT * FROM translation_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<TranslationEntryEntity>>

    @Query("DELETE FROM translation_history WHERE id = :id")
    suspend fun delete(id: String)

    @Query("UPDATE translation_history SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<TranslationEntryEntity>>
}

@Dao
interface StoryProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: StoryProgressEntity)

    @Query("SELECT * FROM story_progress")
    fun getAll(): Flow<List<StoryProgressEntity>>

    @Query("SELECT * FROM story_progress WHERE missionId = :missionId")
    fun getByMissionId(missionId: String): Flow<StoryProgressEntity?>

    @Query("UPDATE story_progress SET chaptersCompleted = :chapters, progress = :progress, lastPlayedAt = :timestamp WHERE missionId = :missionId")
    suspend fun updateProgress(missionId: String, chapters: String, progress: Float, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE story_progress SET isUnlocked = 1 WHERE missionId = :missionId")
    suspend fun unlock(missionId: String)
}

@Dao
interface KochProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: KochProgressEntity)

    @Query("SELECT * FROM koch_progress ORDER BY lessonIndex ASC")
    fun getAll(): Flow<List<KochProgressEntity>>

    @Query("SELECT * FROM koch_progress WHERE lessonIndex = :index")
    fun getByIndex(index: Int): Flow<KochProgressEntity?>

    @Query("SELECT * FROM koch_progress WHERE isCompleted = 1 ORDER BY lessonIndex DESC LIMIT 1")
    fun getLastCompleted(): Flow<KochProgressEntity?>

    @Query("UPDATE koch_progress SET isCompleted = 1, accuracy = :accuracy WHERE lessonIndex = :index")
    suspend fun completeLesson(index: Int, accuracy: Float)
}
