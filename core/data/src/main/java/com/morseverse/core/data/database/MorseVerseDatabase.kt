package com.morseverse.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.morseverse.core.data.database.dao.*
import com.morseverse.core.data.database.entities.*

@Database(
    entities = [
        CharacterProgressEntity::class,
        PracticeSessionEntity::class,
        CharacterResultEntity::class,
        DailyStatsEntity::class,
        UserProfileEntity::class,
        AchievementEntity::class,
        TranslationEntryEntity::class,
        StoryProgressEntity::class,
        KochProgressEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MorseVerseDatabase : RoomDatabase() {
    abstract fun characterProgressDao(): CharacterProgressDao
    abstract fun practiceSessionDao(): PracticeSessionDao
    abstract fun characterResultDao(): CharacterResultDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun achievementDao(): AchievementDao
    abstract fun translationHistoryDao(): TranslationHistoryDao
    abstract fun storyProgressDao(): StoryProgressDao
    abstract fun kochProgressDao(): KochProgressDao
}
