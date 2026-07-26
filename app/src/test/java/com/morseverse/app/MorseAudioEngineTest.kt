package com.morseverse.app

import com.morseverse.core.common.utils.MorseAudioEngine
import com.morseverse.core.common.utils.AudioConfig
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MorseAudioEngineTest {

    private lateinit var engine: MorseAudioEngine

    @Before
    fun setup() {
        engine = MorseAudioEngine()
    }

    @Test
    fun `dit duration at 20 WPM is 60ms`() {
        val duration = engine.ditDurationMs(20)
        assertEquals(60f, duration, 1f)
    }

    @Test
    fun `dit duration at 10 WPM is 120ms`() {
        val duration = engine.ditDurationMs(10)
        assertEquals(120f, duration, 1f)
    }

    @Test
    fun `dit duration at 40 WPM is 30ms`() {
        val duration = engine.ditDurationMs(40)
        assertEquals(30f, duration, 1f)
    }

    @Test
    fun `dit duration is inversely proportional to WPM`() {
        val duration10 = engine.ditDurationMs(10)
        val duration20 = engine.ditDurationMs(20)
        val duration40 = engine.ditDurationMs(40)

        assertTrue("10 WPM should have longer dit", duration10 > duration20)
        assertTrue("20 WPM should have longer dit", duration20 > duration40)
        assertEquals("Duration should halve when WPM doubles", duration10 / 2, duration20, 1f)
    }

    @Test
    fun `generateMorseAudio returns non-empty for valid morse`() {
        val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
        val samples = engine.generateMorseAudio(".-", config)
        assertTrue("Samples should not be empty", samples.isNotEmpty())
    }

    @Test
    fun `generateMorseAudio returns empty for empty string`() {
        val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
        val samples = engine.generateMorseAudio("", config)
        assertTrue("Samples should be empty for empty input", samples.isEmpty())
    }

    @Test
    fun `generateMorseAudio for E produces shorter audio than T`() {
        val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)
        val eSamples = engine.generateMorseAudio(".", config)
        val tSamples = engine.generateMorseAudio("-", config)
        assertTrue("E (dit) should have fewer samples than T (dah)", eSamples.size < tSamples.size)
    }

    @Test
    fun `generateFeedbackTone produces samples`() {
        val samples = engine.generateFeedbackTone(800, 50f, 0.3f)
        assertTrue("Feedback tone should produce samples", samples.isNotEmpty())
    }

    @Test
    fun `sample values are within short range`() {
        val config = AudioConfig(wpm = 20, frequency = 600, volume = 1.0f)
        val samples = engine.generateMorseAudio("...---...", config)

        samples.forEach { sample ->
            assertTrue("Sample should be >= Short.MIN_VALUE", sample >= Short.MIN_VALUE)
            assertTrue("Sample should be <= Short.MAX_VALUE", sample <= Short.MAX_VALUE)
        }
    }

    @Test
    fun `lower volume produces lower amplitude`() {
        val configLoud = AudioConfig(wpm = 20, frequency = 600, volume = 1.0f)
        val configQuiet = AudioConfig(wpm = 20, frequency = 600, volume = 0.1f)

        val samplesLoud = engine.generateMorseAudio(".", configLoud)
        val samplesQuiet = engine.generateMorseAudio(".", configQuiet)

        val maxLoud = samplesLoud.maxOf { kotlin.math.abs(it.toInt()) }
        val maxQuiet = samplesQuiet.maxOf { kotlin.math.abs(it.toInt()) }

        assertTrue("Loud should have higher amplitude", maxLoud > maxQuiet)
    }

    @Test
    fun `different frequencies produce different samples`() {
        val config1 = AudioConfig(wpm = 20, frequency = 400, volume = 0.8f)
        val config2 = AudioConfig(wpm = 20, frequency = 800, volume = 0.8f)

        val samples1 = engine.generateMorseAudio(".", config1)
        val samples2 = engine.generateMorseAudio(".", config2)

        // Same length but different waveform
        assertEquals("Same morse should produce same length", samples1.size, samples2.size)
        assertFalse("Different frequencies should produce different samples", samples1.contentEquals(samples2))
    }
}
