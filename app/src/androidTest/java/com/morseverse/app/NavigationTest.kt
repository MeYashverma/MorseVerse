package com.morseverse.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.morseverse.core.designsystem.theme.MorseVerseTheme
import com.morseverse.core.designsystem.theme.ThemeMode
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainContent_displaysBottomNavigation() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                MorseVerseMainContent()
            }
        }

        // Verify bottom navigation items are displayed
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Learn").assertIsDisplayed()
        composeTestRule.onNodeWithText("Practice").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tree").assertIsDisplayed()
        composeTestRule.onNodeWithText("Translate").assertIsDisplayed()
    }

    @Test
    fun clickingPracticeTab_navigatesToPractice() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                MorseVerseMainContent()
            }
        }

        composeTestRule.onNodeWithText("Practice").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun clickingTreeTab_navigatesToTree() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                MorseVerseMainContent()
            }
        }

        composeTestRule.onNodeWithText("Tree").performClick()
        composeTestRule.waitForIdle()
    }
}
