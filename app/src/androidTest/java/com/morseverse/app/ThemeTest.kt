package com.morseverse.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.morseverse.core.designsystem.theme.*
import org.junit.Rule
import org.junit.Test

class ThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun darkTheme_appliesCorrectly() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                androidx.compose.material3.Text("Test")
            }
        }
        composeTestRule.onNodeWithText("Test").assertIsDisplayed()
    }

    @Test
    fun lightTheme_appliesCorrectly() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.LIGHT) {
                androidx.compose.material3.Text("Test")
            }
        }
        composeTestRule.onNodeWithText("Test").assertIsDisplayed()
    }

    @Test
    fun amoledTheme_appliesCorrectly() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.AMOLED) {
                androidx.compose.material3.Text("Test")
            }
        }
        composeTestRule.onNodeWithText("Test").assertIsDisplayed()
    }
}
