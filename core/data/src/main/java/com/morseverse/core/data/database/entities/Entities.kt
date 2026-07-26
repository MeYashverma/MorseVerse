package com.morseverse.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════════════════════
// ROOM DATABASE ENTITIES
// ═══════════════════════════════════════════════════════════════════

@Entity(tableName = "character_progress")
data class CharacterProgressEntity(
    @PrimaryKey
    val character: String,
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0,
    val averageReactionTimeMs: Long = 0,
    val mastery: Float = 0f,
    val lastPracticed: Long = 0,
    val streak: Int = 0,
    val level: String = "NOVICE"
)

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey
    val id: String,
    val startTime: Long,
    val endTime: Long = 0,
    val mode: String,
    val totalCharacters: Int = 0,
    val correctCharacters: Int = 0,
    val averageWpm: Float = 0f,
    val averageReactionTimeMs: Long = 0,
    val xpEarned: Int = 0
)

@Entity(tableName = "character_results")
data class CharacterResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: String,
    val character: String,
    val expected: String,
    val isCorrect: Boolean,
    val reactionTimeMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey
    val date: String, // ISO date string
    val totalPracticeMinutes: Int = 0,
    val totalCharacters: Int = 0,
    val totalWords: Int = 0,
    val sessionsCompleted: Int = 0,
    val xpEarned: Int = 0,
    val averageAccuracy: Float = 0f,
    val averageWpm: Float = 0f,
    val streak: Int = 0,
    val dailyGoalMet: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 0, // Single row
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalPracticeMinutes: Int = 0,
    val totalCharacters: Int = 0,
    val totalSessions: Int = 0,
    val currentLevel: Int = 1,
    val dailyGoalMinutes: Int = 15,
    val currentWpm: Int = 20,
    val currentAccuracy: Float = 0f,
    val charactersLearned: String = "", // Comma-separated
    val achievementsUnlocked: String = "", // Comma-separated
    val joinDate: Long = System.currentTimeMillis(),
    val lastActiveDate: String = ""
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val category: String,
    val requirementType: String,
    val requirementValue: Int,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Float = 0f
)

@Entity(tableName = "translation_history")
data class TranslationEntryEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val morse: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)

@Entity(tableName = "story_progress")
data class StoryProgressEntity(
    @PrimaryKey
    val missionId: String,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val progress: Float = 0f,
    val chaptersCompleted: String = "", // Comma-separated chapter IDs
    val lastPlayedAt: Long = 0
)

@Entity(tableName = "koch_progress")
data class KochProgressEntity(
    @PrimaryKey
    val lessonIndex: Int,
    val characters: String, // Comma-separated
    val isCompleted: Boolean = false,
    val accuracy: Float = 0f,
    val attempts: Int = 0
)
