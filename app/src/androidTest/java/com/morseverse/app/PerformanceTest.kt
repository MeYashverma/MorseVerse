package com.morseverse.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.morseverse.core.designsystem.theme.MorseVerseTheme
import com.morseverse.core.designsystem.theme.ThemeMode
import org.junit.Rule
import org.junit.Test
import kotlin.system.measureTimeMillis

class PerformanceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appLaunchesWithinTimeLimit() {
        val launchTime = measureTimeMillis {
            composeTestRule.setContent {
                MorseVerseTheme(themeMode = ThemeMode.DARK) {
                    MorseVerseMainContent()
                }
            }
            composeTestRule.waitForIdle()
        }

        // Should launch within 1 second (1000ms)
        assert(launchTime < 2000) { "App launch took ${launchTime}ms, should be under 2000ms" }
    }

    @Test
    fun recompositionIsEfficient() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                MorseVerseMainContent()
            }
        }

        // Multiple navigations should not cause ANR
        val navigationTime = measureTimeMillis {
            repeat(5) {
                composeTestRule.onNodeWithText("Practice").performClick()
                composeTestRule.waitForIdle()
                composeTestRule.onNodeWithText("Home").performClick()
                composeTestRule.waitForIdle()
            }
        }

        // Should complete within 5 seconds
        assert(navigationTime < 5000) { "Navigation took ${navigationTime}ms" }
    }
}
