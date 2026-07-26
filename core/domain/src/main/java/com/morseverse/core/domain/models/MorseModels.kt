package com.morseverse.core.domain.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

// ═══════════════════════════════════════════════════════════════════
// MORSE CODE CORE MODELS
// ═══════════════════════════════════════════════════════════════════

/**
 * Represents a single Morse code element (dit or dah)
 */
enum class MorseElement(val durationMultiplier: Float) {
    DIT(1.0f),
    DAH(3.0f);

    val morseChar: String get() = if (this == DIT) "·" else "—"
}

/**
 * Represents the timing units in Morse code
 */
object MorseTiming {
    const val DIT = 1
    const val DAH = 3
    const val SYMBOL_SPACE = 1       // Between elements of same character
    const val CHARACTER_SPACE = 3    // Between characters
    const val WORD_SPACE = 7         // Between words
}

/**
 * A single Morse character with its representation
 */
data class MorseCharacter(
    val character: String,
    val morse: String,
    val elements: List<MorseElement>,
    val category: CharacterCategory = CharacterCategory.LETTER
) {
    val morseDisplay: String
        get() = morse.replace(".", "·").replace("-", "—")

    val ditDah: String
        get() = elements.joinToString("") { if (it == MorseElement.DIT) "·" else "—" }
}

/**
 * Categories of Morse characters
 */
enum class CharacterCategory(val displayName: String, val description: String) {
    LETTER("Letters", "A-Z"),
    NUMBER("Numbers", "0-9"),
    PUNCTUATION("Punctuation", "Common punctuation marks"),
    PROSIGN("Prosigns", "Procedural signals"),
    SPECIAL("Special", "Special characters")
}

/**
 * Node in the Morse binary tree
 */
data class MorseTreeNode(
    val id: String,
    val character: String?,
    val morse: String,
    val depth: Int,
    val position: TreePosition,
    val leftChild: MorseTreeNode? = null,
    val rightChild: MorseTreeNode? = null,
    val parentNodeId: String? = null,
    val mastery: Float = 0f,       // 0.0 to 1.0
    val isWeak: Boolean = false,
    val isTodaysLesson: Boolean = false
)

/**
 * Position in the tree for rendering
 */
data class TreePosition(
    val x: Float,
    val y: Float
)

// ═══════════════════════════════════════════════════════════════════
// LEARNING & PRACTICE MODELS
// ═══════════════════════════════════════════════════════════════════

/**
 * Learning methods available
 */
enum class LearningMethod(val displayName: String, val description: String) {
    KOCH("Koch Method", "Learn characters at full speed, 2 at a time"),
    FARNSWORTH("Farnsworth", "Standard characters with extra spacing"),
    TRADITIONAL("Traditional", "Learn by character groups (EISH, TMOW, etc.)"),
    ADAPTIVE("Adaptive", "AI-driven personalized learning"),
    STORY("Story Mode", "Learn through interactive missions")
}

/**
 * Practice modes
 */
enum class PracticeMode(val displayName: String, val icon: String) {
    CHARACTER("Character Practice", "text_fields"),
    WORD("Word Practice", "text_format"),
    SENTENCE("Sentence Practice", "article"),
    RANDOM("Random Practice", "shuffle"),
    WEAK("Weak Characters", "warning"),
    CUSTOM("Custom Set", "tune"),
    COMMON_WORDS("Common Words", "star"),
    CALLSIGN("Callsign Practice", "cell_tower"),
    CONTEST("Contest Practice", "emoji_events"),
    TIMED("Timed Challenge", "timer"),
    INFINITE("Infinite Mode", "all_inclusive"),
    DAILY("Daily Challenge", "today")
}

/**
 * Audio configuration for Morse generation
 */
data class AudioConfig(
    val wpm: Int = 20,
    val frequency: Int = 600,
    val volume: Float = 0.8f,
    val farnsworthSpacing: Boolean = false,
    val farnsworthWpm: Int = 15,
    val noiseLevel: Float = 0f,
    val noiseType: NoiseType = NoiseType.NONE,
    val toneType: ToneType = ToneType.SINE
)

enum class NoiseType { NONE, STATIC, RAIN, WEAK_SIGNAL, CONTEST, RADIO }
enum class ToneType { SINE, SMOOTH, BUZZY, RADIO }

/**
 * Input methods for sending practice
 */
enum class InputMethod(val displayName: String) {
    TOUCH("Touch Paddle"),
    STRAIGHT_KEY("Straight Key"),
    IAMBIC("Iambic Paddle"),
    KEYBOARD("Keyboard Input")
}

// ═══════════════════════════════════════════════════════════════════
// PROGRESS & STATISTICS MODELS
// ═══════════════════════════════════════════════════════════════════

/**
 * User progress for a single character
 */
data class CharacterProgress(
    val character: String,
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0,
    val averageReactionTimeMs: Long = 0,
    val mastery: Float = 0f,       // 0.0 to 1.0
    val lastPracticed: Long = 0,
    val streak: Int = 0,
    val level: MasteryLevel = MasteryLevel.NOVICE
) {
    val accuracy: Float
        get() = if (totalAttempts > 0) correctAttempts.toFloat() / totalAttempts else 0f

    val isWeak: Boolean
        get() = mastery < 0.5f && totalAttempts > 5
}

enum class MasteryLevel(val displayName: String, val threshold: Float) {
    NOVICE("Novice", 0f),
    APPRENTICE("Apprentice", 0.2f),
    JOURNEYMAN("Journeyman", 0.4f),
    EXPERT("Expert", 0.6f),
    MASTER("Master", 0.8f),
    GRANDMASTER("Grandmaster", 0.95f)
}

/**
 * A practice session
 */
data class PracticeSession(
    val id: String,
    val startTime: Long,
    val endTime: Long = 0,
    val mode: PracticeMode,
    val totalCharacters: Int = 0,
    val correctCharacters: Int = 0,
    val averageWpm: Float = 0f,
    val averageReactionTimeMs: Long = 0,
    val xpEarned: Int = 0,
    val characters: List<CharacterResult> = emptyList()
) {
    val accuracy: Float
        get() = if (totalCharacters > 0) correctCharacters.toFloat() / totalCharacters else 0f

    val duration: Long
        get() = endTime - startTime
}

data class CharacterResult(
    val character: String,
    val expected: String,
    val isCorrect: Boolean,
    val reactionTimeMs: Long
)

/**
 * Daily statistics
 */
data class DailyStats(
    val date: LocalDate,
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

/**
 * User profile and overall stats
 */
data class UserProfile(
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
    val charactersLearned: Set<String> = emptySet(),
    val achievementsUnlocked: Set<String> = emptySet(),
    val joinDate: Long = System.currentTimeMillis()
)

/**
 * Achievement definitions
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val category: AchievementCategory,
    val requirement: AchievementRequirement,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Float = 0f
)

enum class AchievementCategory(val displayName: String) {
    MILESTONE("Milestone"),
    STREAK("Streak"),
    ACCURACY("Accuracy"),
    SPEED("Speed"),
    EXPLORER("Explorer"),
    MASTER("Master"),
    SPECIAL("Special")
}

data class AchievementRequirement(
    val type: String,
    val value: Int
)

// ═══════════════════════════════════════════════════════════════════
// STORY MODE MODELS
// ═══════════════════════════════════════════════════════════════════

data class StoryMission(
    val id: String,
    val title: String,
    val description: String,
    val category: MissionCategory,
    val difficulty: Int,             // 1-5
    val chapters: List<StoryChapter>,
    val charactersIntroduced: Set<String>,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val progress: Float = 0f
)

data class StoryChapter(
    val id: String,
    val title: String,
    val narrative: String,
    val challenges: List<StoryChallenge>,
    val isCompleted: Boolean = false
)

data class StoryChallenge(
    val type: ChallengeType,
    val prompt: String,
    val expectedAnswer: String,
    val hint: String? = null,
    val xpReward: Int = 10
)

enum class ChallengeType { DECODE, ENCODE, LISTEN, SEND, TRANSLATE }

enum class MissionCategory(val displayName: String, val icon: String) {
    SOS("SOS Rescue", "emergency"),
    SPY("Spy Mission", "shield"),
    SPACE("Space Mission", "rocket_launch"),
    SUBMARINE("Submarine", "directions_boat"),
    TITANIC("Titanic", "sailing"),
    AGENT("Secret Agent", "fingerprint"),
    ALIEN("Alien Signal", "language")
}

// ═══════════════════════════════════════════════════════════════════
// HAM RADIO MODELS
// ═══════════════════════════════════════════════════════════════════

data class QCode(
    val code: String,
    val question: String,
    val answer: String,
    val category: String
)

data class PhoneticAlphabet(
    val letter: String,
    val word: String,
    val morse: String
)

data class HamAbbreviation(
    val abbreviation: String,
    val meaning: String,
    val context: String
)

// ═══════════════════════════════════════════════════════════════════
// TRANSLATOR MODELS
// ═══════════════════════════════════════════════════════════════════

data class TranslationEntry(
    val id: String,
    val text: String,
    val morse: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)

data class DecoderResult(
    val decoded: String,
    val confidence: Float,
    val wpm: Float,
    val rawMorse: String,
    val timestamp: Long
)

// ═══════════════════════════════════════════════════════════════════
// THEME MODELS
// ═══════════════════════════════════════════════════════════════════

enum class AppTheme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    AMOLED("AMOLED"),
    MATERIAL_YOU("Material You")
}

enum class ColorBlindMode(val displayName: String) {
    NONE("None"),
    PROTANOPIA("Protanopia"),
    DEUTERANOPIA("Deuteranopia"),
    TRITANOPIA("Tritanopia")
}
