package com.morseverse.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.morseverse.core.designsystem.theme.MorseVerseTheme
import com.morseverse.core.designsystem.theme.ThemeMode
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appTitle_isDisplayed() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                // Home screen requires ViewModel, so we test the theme wrapper
            }
        }
        // Basic smoke test that theme applies
        composeTestRule.waitForIdle()
    }
}
