package com.morseverse.core.domain.usecases

import com.morseverse.core.common.constants.MorseCodeData
import com.morseverse.core.domain.models.*
import com.morseverse.core.domain.repository.MorseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GeneratePracticeChallenge(
    private val repository: MorseRepository
) {
    suspend operator fun invoke(
        mode: PracticeMode,
        characterSet: List<String>? = null,
        count: Int = 10
    ): PracticeChallenge {
        val weakChars = repository.getWeakCharacters().first().map { it.character }
        val allChars = characterSet ?: when (mode) {
            PracticeMode.WEAK -> weakChars.take(count).ifEmpty { listOf("K", "M") }
            PracticeMode.CHARACTER -> {
                val kochLessons = repository.getKochLessons()
                val learnedChars = repository.getAllCharacterProgress().first()
                    .filter { it.totalAttempts > 0 }.map { it.character }
                if (learnedChars.isEmpty()) kochLessons.first() else learnedChars
            }
            PracticeMode.WORD -> {
                val learnedChars = repository.getAllCharacterProgress().first()
                    .filter { it.mastery > 0.3f }.map { it.character }
                learnedChars.ifEmpty { listOf("K", "M", "R", "S") }
            }
            PracticeMode.COMMON_WORDS -> {
                return PracticeChallenge(
                    mode = mode,
                    items = MorseCodeData.COMMON_WORDS.shuffled().take(count).map { word ->
                        PracticeItem(
                            character = word,
                            morse = word.map { c ->
                                MorseCodeData.INTERNATIONAL_MORSE[c.toString()] ?: ""
                            }.joinToString(" "),
                            category = "WORD"
                        )
                    }
                )
            }
            PracticeMode.CALLSIGN -> {
                return generateCallsignChallenge(count)
            }
            else -> repository.getAllCharacters().map { it.character }.take(count)
        }

        return PracticeChallenge(
            mode = mode,
            items = allChars.shuffled().take(count).map { char ->
                val morse = MorseCodeData.INTERNATIONAL_MORSE[char] ?: ""
                PracticeItem(
                    character = char,
                    morse = morse,
                    category = if (char.length == 1 && char[0].isLetter()) "LETTER" else "OTHER"
                )
            }
        )
    }

    private fun generateCallsignChallenge(count: Int): PracticeChallenge {
        val callsigns = (1..count).map {
            val prefix = MorseCodeData.CALLSIGN_PREFIXES.random()
            val suffix = (1..(1..3).random()).map { ('A'..'Z').random() }.joinToString("")
            val number = (0..9).random()
            "$prefix$number$suffix"
        }

        return PracticeChallenge(
            mode = PracticeMode.CALLSIGN,
            items = callsigns.map { call ->
                PracticeItem(
                    character = call,
                    morse = call.mapNotNull { c ->
                        MorseCodeData.INTERNATIONAL_MORSE[c.toString()]
                    }.joinToString(" "),
                    category = "CALLSIGN"
                )
            }
        )
    }
}

class CalculateMastery {
    operator fun invoke(
        totalAttempts: Int,
        correctAttempts: Int,
        averageReactionTimeMs: Long
    ): Pair<Float, MasteryLevel> {
        if (totalAttempts == 0) return 0f to MasteryLevel.NOVICE

        val accuracy = correctAttempts.toFloat() / totalAttempts
        val speedBonus = when {
            averageReactionTimeMs < 500 -> 0.3f
            averageReactionTimeMs < 1000 -> 0.2f
            averageReactionTimeMs < 2000 -> 0.1f
            else -> 0f
        }
        val volumeBonus = (totalAttempts.coerceAtMost(100) / 100f) * 0.2f

        val mastery = (accuracy * 0.5f + speedBonus + volumeBonus).coerceIn(0f, 1f)

        val level = when {
            mastery >= MasteryLevel.GRANDMASTER.threshold -> MasteryLevel.GRANDMASTER
            mastery >= MasteryLevel.MASTER.threshold -> MasteryLevel.MASTER
            mastery >= MasteryLevel.EXPERT.threshold -> MasteryLevel.EXPERT
            mastery >= MasteryLevel.JOURNEYMAN.threshold -> MasteryLevel.JOURNEYMAN
            mastery >= MasteryLevel.APPRENTICE.threshold -> MasteryLevel.APPRENTICE
            else -> MasteryLevel.NOVICE
        }

        return mastery to level
    }
}

class CalculateXp {
    operator fun invoke(
        correct: Int,
        total: Int,
        wpm: Int,
        mode: PracticeMode
    ): Int {
        val baseXp = correct * 5
        val accuracyBonus = if (total > 0) (correct.toFloat() / total * 20).toInt() else 0
        val speedBonus = when {
            wpm >= 40 -> 50
            wpm >= 30 -> 30
            wpm >= 20 -> 15
            else -> 5
        }
        val modeMultiplier: Float = when (mode) {
            PracticeMode.WEAK -> 2f
            PracticeMode.CALLSIGN -> 1.5f
            PracticeMode.CONTEST -> 2f
            PracticeMode.TIMED -> 1.5f
            else -> 1f
        }

        return ((baseXp + accuracyBonus + speedBonus) * modeMultiplier).toInt()
    }
}

data class PracticeChallenge(
    val mode: PracticeMode,
    val items: List<PracticeItem>
)

data class PracticeItem(
    val character: String,
    val morse: String,
    val category: String
)
