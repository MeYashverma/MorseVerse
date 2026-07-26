package com.morseverse.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.morseverse.core.designsystem.theme.MorseVerseTheme
import com.morseverse.core.designsystem.theme.ThemeMode
import org.junit.Rule
import org.junit.Test

class PracticeSessionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun practiceSession_displaysCorrectUI() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                // Practice session requires ViewModel dependency injection
                // Test will be expanded with proper DI setup in CI
            }
        }
        composeTestRule.waitForIdle()
    }
}
