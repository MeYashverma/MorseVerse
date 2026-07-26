package com.morseverse.app

import com.morseverse.core.common.constants.MorseCodeData
import org.junit.Assert.*
import org.junit.Test

class MorseCodeDataTest {

    @Test
    fun `all letters have Morse code mappings`() {
        val letters = 'A'..'Z'
        letters.forEach { letter ->
            val morse = MorseCodeData.INTERNATIONAL_MORSE[letter.toString()]
            assertNotNull("Letter $letter should have a Morse code mapping", morse)
            assertTrue("Morse code for $letter should not be empty", morse!!.isNotEmpty())
        }
    }

    @Test
    fun `all numbers have Morse code mappings`() {
        val numbers = '0'..'9'
        numbers.forEach { number ->
            val morse = MorseCodeData.INTERNATIONAL_MORSE[number.toString()]
            assertNotNull("Number $number should have a Morse code mapping", morse)
            assertTrue("Morse code for $number should not be empty", morse!!.isNotEmpty())
        }
    }

    @Test
    fun `reverse mapping is consistent`() {
        MorseCodeData.INTERNATIONAL_MORSE.forEach { (char, morse) ->
            if (char.length == 1 && !char.startsWith("<")) {
                val reverseChar = MorseCodeData.REVERSE_MORSE[morse]
                assertEquals("Reverse mapping for $morse should be $char", char, reverseChar)
            }
        }
    }

    @Test
    fun `Morse code uses only dots and dashes`() {
        MorseCodeData.INTERNATIONAL_MORSE.values.forEach { morse ->
            assertTrue(
                "Morse code '$morse' should only contain dots and dashes",
                morse.all { it == '.' || it == '-' }
            )
        }
    }

    @Test
    fun `E is single dot`() {
        assertEquals(".", MorseCodeData.INTERNATIONAL_MORSE["E"])
    }

    @Test
    fun `T is single dash`() {
        assertEquals("-", MorseCodeData.INTERNATIONAL_MORSE["T"])
    }

    @Test
    fun `SOS is correct`() {
        assertEquals("...---...", MorseCodeData.INTERNATIONAL_MORSE["<SOS>"])
    }

    @Test
    fun `Koch lessons are not empty`() {
        assertTrue("Koch lessons should not be empty", MorseCodeData.KOCH_LESSONS.isNotEmpty())
    }

    @Test
    fun `traditional groups are not empty`() {
        assertTrue("Traditional groups should not be empty", MorseCodeData.TRADITIONAL_GROUPS.isNotEmpty())
    }

    @Test
    fun `common words are not empty`() {
        assertTrue("Common words should not be empty", MorseCodeData.COMMON_WORDS.isNotEmpty())
    }

    @Test
    fun `Q codes are not empty`() {
        assertTrue("Q codes should not be empty", MorseCodeData.Q_CODES.isNotEmpty())
    }

    @Test
    fun `phonetic alphabet has 26 entries`() {
        assertEquals(26, MorseCodeData.PHONETIC_ALPHABET.size)
    }

    @Test
    fun `memory tips exist for all letters`() {
        val letters = 'A'..'Z'
        letters.forEach { letter ->
            val tip = MorseCodeData.MEMORY_TIPS[letter.toString()]
            assertNotNull("Memory tip should exist for $letter", tip)
            assertTrue("Memory tip for $letter should not be empty", tip!!.isNotEmpty())
        }
    }

    @Test
    fun `story missions are not empty`() {
        assertTrue("Story missions should not be empty", MorseCodeData.STORY_MISSIONS.isNotEmpty())
    }

    @Test
    fun `each Koch lesson has characters`() {
        MorseCodeData.KOCH_LESSONS.forEachIndexed { index, lesson ->
            assertTrue("Koch lesson $index should have characters", lesson.isNotEmpty())
        }
    }
}
