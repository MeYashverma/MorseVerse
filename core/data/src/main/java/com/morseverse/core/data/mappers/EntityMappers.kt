package com.morseverse.core.data.mappers

import com.morseverse.core.data.database.entities.*
import com.morseverse.core.domain.models.*

// ═══════════════════════════════════════════════════════════════════
// ENTITY ↔ DOMAIN MAPPERS
// ═══════════════════════════════════════════════════════════════════

fun CharacterProgressEntity.toDomain(): CharacterProgress = CharacterProgress(
    character = character,
    totalAttempts = totalAttempts,
    correctAttempts = correctAttempts,
    averageReactionTimeMs = averageReactionTimeMs,
    mastery = mastery,
    lastPracticed = lastPracticed,
    streak = streak,
    level = try { MasteryLevel.valueOf(level) } catch (e: Exception) { MasteryLevel.NOVICE }
)

fun CharacterProgress.toEntity(): CharacterProgressEntity = CharacterProgressEntity(
    character = character,
    totalAttempts = totalAttempts,
    correctAttempts = correctAttempts,
    averageReactionTimeMs = averageReactionTimeMs,
    mastery = mastery,
    lastPracticed = lastPracticed,
    streak = streak,
    level = level.name
)

fun PracticeSession.toEntity(): PracticeSessionEntity = PracticeSessionEntity(
    id = id,
    startTime = startTime,
    endTime = endTime,
    mode = mode.name,
    totalCharacters = totalCharacters,
    correctCharacters = correctCharacters,
    averageWpm = averageWpm,
    averageReactionTimeMs = averageReactionTimeMs,
    xpEarned = xpEarned
)

fun DailyStatsEntity.toDomain(): DailyStats = DailyStats(
    date = kotlinx.datetime.LocalDate.parse(date),
    totalPracticeMinutes = totalPracticeMinutes,
    totalCharacters = totalCharacters,
    totalWords = totalWords,
    sessionsCompleted = sessionsCompleted,
    xpEarned = xpEarned,
    averageAccuracy = averageAccuracy,
    averageWpm = averageWpm,
    streak = streak,
    dailyGoalMet = dailyGoalMet
)

fun DailyStats.toEntity(): DailyStatsEntity = DailyStatsEntity(
    date = date.toString(),
    totalPracticeMinutes = totalPracticeMinutes,
    totalCharacters = totalCharacters,
    totalWords = totalWords,
    sessionsCompleted = sessionsCompleted,
    xpEarned = xpEarned,
    averageAccuracy = averageAccuracy,
    averageWpm = averageWpm,
    streak = streak,
    dailyGoalMet = dailyGoalMet
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    totalXp = totalXp,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    totalPracticeMinutes = totalPracticeMinutes,
    totalCharacters = totalCharacters,
    totalSessions = totalSessions,
    currentLevel = currentLevel,
    dailyGoalMinutes = dailyGoalMinutes,
    currentWpm = currentWpm,
    currentAccuracy = currentAccuracy,
    charactersLearned = if (charactersLearned.isEmpty()) emptySet() else charactersLearned.split(",").toSet(),
    achievementsUnlocked = if (achievementsUnlocked.isEmpty()) emptySet() else achievementsUnlocked.split(",").toSet(),
    joinDate = joinDate
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    totalXp = totalXp,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    totalPracticeMinutes = totalPracticeMinutes,
    totalCharacters = totalCharacters,
    totalSessions = totalSessions,
    currentLevel = currentLevel,
    dailyGoalMinutes = dailyGoalMinutes,
    currentWpm = currentWpm,
    currentAccuracy = currentAccuracy,
    charactersLearned = charactersLearned.joinToString(","),
    achievementsUnlocked = achievementsUnlocked.joinToString(","),
    joinDate = joinDate
)

fun AchievementEntity.toDomain(): Achievement = Achievement(
    id = id,
    title = title,
    description = description,
    icon = icon,
    category = try { AchievementCategory.valueOf(category) } catch (e: Exception) { AchievementCategory.MILESTONE },
    requirement = AchievementRequirement(requirementType, requirementValue),
    xpReward = xpReward,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt,
    progress = progress
)

fun Achievement.toEntity(): AchievementEntity = AchievementEntity(
    id = id,
    title = title,
    description = description,
    icon = icon,
    category = category.name,
    requirementType = requirement.type,
    requirementValue = requirement.value,
    xpReward = xpReward,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt,
    progress = progress
)

fun TranslationEntryEntity.toDomain(): TranslationEntry = TranslationEntry(
    id = id,
    text = text,
    morse = morse,
    timestamp = timestamp,
    isFavorite = isFavorite
)

fun TranslationEntry.toEntity(): TranslationEntryEntity = TranslationEntryEntity(
    id = id,
    text = text,
    morse = morse,
    timestamp = timestamp,
    isFavorite = isFavorite
)
