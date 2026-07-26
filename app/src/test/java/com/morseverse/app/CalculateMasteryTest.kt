package com.morseverse.app

import com.morseverse.core.domain.models.MasteryLevel
import com.morseverse.core.domain.usecases.CalculateMastery
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculateMasteryTest {

    private lateinit var calculateMastery: CalculateMastery

    @Before
    fun setup() {
        calculateMastery = CalculateMastery()
    }

    @Test
    fun `zero attempts returns zero mastery and NOVICE level`() {
        val (mastery, level) = calculateMastery(0, 0, 0)
        assertEquals(0f, mastery, 0.01f)
        assertEquals(MasteryLevel.NOVICE, level)
    }

    @Test
    fun `perfect accuracy with fast reaction gives high mastery`() {
        val (mastery, level) = calculateMastery(100, 100, 400)
        assertTrue("Mastery should be high", mastery > 0.8f)
        assertTrue("Level should be at least EXPERT", level.ordinal >= MasteryLevel.EXPERT.ordinal)
    }

    @Test
    fun `perfect accuracy with slow reaction gives medium mastery`() {
        val (mastery, level) = calculateMastery(100, 100, 2000)
        assertTrue("Mastery should be medium", mastery > 0.4f)
        assertTrue("Mastery should not be max", mastery < 0.8f)
    }

    @Test
    fun `poor accuracy gives low mastery`() {
        val (mastery, level) = calculateMastery(100, 20, 1000)
        assertTrue("Mastery should be low", mastery < 0.5f)
        assertEquals(MasteryLevel.NOVICE, level)
    }

    @Test
    fun `mastery is between 0 and 1`() {
        // Test various inputs
        val testCases = listOf(
            Triple(0, 0, 0L),
            Triple(1, 1, 100L),
            Triple(100, 50, 500L),
            Triple(1000, 900, 300L),
            Triple(50, 25, 1500L),
            Triple(10, 10, 50L)
        )

        testCases.forEach { (attempts, correct, reactionTime) ->
            val (mastery, _) = calculateMastery(attempts, correct, reactionTime)
            assertTrue(
                "Mastery for ($attempts, $correct, $reactionTime) should be >= 0",
                mastery >= 0f
            )
            assertTrue(
                "Mastery for ($attempts, $correct, $reactionTime) should be <= 1",
                mastery <= 1f
            )
        }
    }

    @Test
    fun `more attempts increases mastery`() {
        val (mastery1, _) = calculateMastery(10, 10, 1000)
        val (mastery2, _) = calculateMastery(100, 100, 1000)
        assertTrue(
            "More attempts should give higher mastery",
            mastery2 > mastery1
        )
    }

    @Test
    fun `higher accuracy increases mastery`() {
        val (mastery1, _) = calculateMastery(100, 50, 1000)
        val (mastery2, _) = calculateMastery(100, 90, 1000)
        assertTrue(
            "Higher accuracy should give higher mastery",
            mastery2 > mastery1
        )
    }

    @Test
    fun `faster reaction time increases mastery`() {
        val (mastery1, _) = calculateMastery(100, 80, 2000)
        val (mastery2, _) = calculateMastery(100, 80, 500)
        assertTrue(
            "Faster reaction should give higher mastery",
            mastery2 > mastery1
        )
    }

    @Test
    fun `GRANDMASTER level requires high mastery`() {
        val (mastery, level) = calculateMastery(1000, 990, 300)
        assertEquals(MasteryLevel.GRANDMASTER, level)
        assertTrue("Mastery should be >= 0.95", mastery >= 0.95f)
    }
}
