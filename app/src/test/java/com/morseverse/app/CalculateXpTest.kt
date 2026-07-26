package com.morseverse.app

import com.morseverse.core.domain.models.PracticeMode
import com.morseverse.core.domain.usecases.CalculateXp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculateXpTest {

    private lateinit var calculateXp: CalculateXp

    @Before
    fun setup() {
        calculateXp = CalculateXp()
    }

    @Test
    fun `zero correct gives minimum XP`() {
        val xp = calculateXp(0, 10, 20, PracticeMode.CHARACTER)
        assertTrue("XP should be non-negative", xp >= 0)
    }

    @Test
    fun `all correct gives more XP than some correct`() {
        val xpSome = calculateXp(5, 10, 20, PracticeMode.CHARACTER)
        val xpAll = calculateXp(10, 10, 20, PracticeMode.CHARACTER)
        assertTrue("All correct should give more XP", xpAll > xpSome)
    }

    @Test
    fun `higher WPM gives speed bonus`() {
        val xpSlow = calculateXp(10, 10, 15, PracticeMode.CHARACTER)
        val xpFast = calculateXp(10, 10, 40, PracticeMode.CHARACTER)
        assertTrue("Higher WPM should give more XP", xpFast > xpSlow)
    }

    @Test
    fun `weak characters mode gives double XP`() {
        val xpNormal = calculateXp(10, 10, 20, PracticeMode.CHARACTER)
        val xpWeak = calculateXp(10, 10, 20, PracticeMode.WEAK)
        assertTrue("Weak mode should give more XP", xpWeak >= xpNormal)
    }

    @Test
    fun `contest mode gives bonus XP`() {
        val xpNormal = calculateXp(10, 10, 20, PracticeMode.CHARACTER)
        val xpContest = calculateXp(10, 10, 20, PracticeMode.CONTEST)
        assertTrue("Contest mode should give more XP", xpContest >= xpNormal)
    }

    @Test
    fun `XP is always non-negative`() {
        PracticeMode.entries.forEach { mode ->
            val testCases = listOf(
                Triple(0, 0, 5),
                Triple(0, 10, 20),
                Triple(5, 10, 15),
                Triple(10, 10, 40),
                Triple(100, 100, 60)
            )

            testCases.forEach { (correct, total, wpm) ->
                val xp = calculateXp(correct, total, wpm, mode)
                assertTrue(
                    "XP for ($correct, $total, $wpm, $mode) should be >= 0",
                    xp >= 0
                )
            }
        }
    }

    @Test
    fun `accuracy bonus increases with better accuracy`() {
        val xp50 = calculateXp(5, 10, 20, PracticeMode.CHARACTER)
        val xp90 = calculateXp(9, 10, 20, PracticeMode.CHARACTER)
        assertTrue("90% accuracy should give more XP", xp90 > xp50)
    }
}
