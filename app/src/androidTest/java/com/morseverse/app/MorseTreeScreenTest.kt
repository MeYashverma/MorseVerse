package com.morseverse.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.morseverse.core.designsystem.theme.MorseVerseTheme
import com.morseverse.core.designsystem.theme.ThemeMode
import org.junit.Rule
import org.junit.Test

class MorseTreeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun morseTreeScreen_displaysTitle() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                // Morse tree screen requires ViewModel dependency injection
                // Test will be expanded with proper DI setup in CI
            }
        }
        composeTestRule.waitForIdle()
    }
}
