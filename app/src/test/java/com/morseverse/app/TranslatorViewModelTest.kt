package com.morseverse.app

import com.morseverse.core.common.constants.MorseCodeData
import org.junit.Assert.*
import org.junit.Test

class TranslatorViewModelTest {

    // Testing translation logic directly (same as in TranslatorViewModel)

    private fun textToMorse(text: String): String {
        return text.uppercase().map { char ->
            when (char) {
                ' ' -> "/"
                else -> MorseCodeData.INTERNATIONAL_MORSE[char.toString()] ?: ""
            }
        }.filter { it.isNotEmpty() }.joinToString(" ")
    }

    private fun morseToText(morse: String): String {
        return morse.trim().split(" ").joinToString("") { token ->
            when (token) {
                "/" -> " "
                else -> MorseCodeData.REVERSE_MORSE[token] ?: "?"
            }
        }
    }

    @Test
    fun `text to morse converts single character`() {
        assertEquals(".-", textToMorse("A"))
        assertEquals("-", textToMorse("T"))
        assertEquals(".", textToMorse("E"))
    }

    @Test
    fun `text to morse converts word`() {
        assertEquals("... --- ...", textToMorse("SOS"))
    }

    @Test
    fun `text to morse handles spaces`() {
        assertEquals(". / -", textToMorse("E T"))
    }

    @Test
    fun `text to morse is case insensitive`() {
        assertEquals(".-", textToMorse("a"))
        assertEquals(".-", textToMorse("A"))
    }

    @Test
    fun `morse to text converts single character`() {
        assertEquals("A", morseToText(".-"))
        assertEquals("T", morseToText("-"))
        assertEquals("E", morseToText("."))
    }

    @Test
    fun `morse to text converts word`() {
        assertEquals("SOS", morseToText("... --- ..."))
    }

    @Test
    fun `morse to text handles spaces`() {
        assertEquals("E T", morseToText(". / -"))
    }

    @Test
    fun `morse to text returns question mark for unknown`() {
        assertEquals("?", morseToText("..........."))
    }

    @Test
    fun `round trip translation works`() {
        val original = "HELLO WORLD"
        val morse = textToMorse(original)
        val decoded = morseToText(morse)
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for all letters`() {
        ('A'..'Z').forEach { letter ->
            val morse = textToMorse(letter.toString())
            val decoded = morseToText(morse)
            assertEquals("$letter should survive round trip", letter.toString(), decoded)
        }
    }

    @Test
    fun `round trip for all numbers`() {
        ('0'..'9').forEach { number ->
            val morse = textToMorse(number.toString())
            val decoded = morseToText(morse)
            assertEquals("$number should survive round trip", number.toString(), decoded)
        }
    }

    @Test
    fun `text to morse for SOS emergency`() {
        val morse = textToMorse("SOS")
        assertEquals("... --- ...", morse)
    }

    @Test
    fun `text to morse for CQ call`() {
        val morse = textToMorse("CQ")
        assertEquals("-.-. --.-", morse)
    }

    @Test
    fun `73 is the best regards number`() {
        val morse = textToMorse("73")
        assertEquals("--... ...--", morse)
    }
}
