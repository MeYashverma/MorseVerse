package com.morseverse.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.morseverse.core.designsystem.theme.MorseVerseTheme
import com.morseverse.core.designsystem.theme.ThemeMode
import org.junit.Rule
import org.junit.Test

class AccessibilityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainContent_hasAccessibleLabels() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                MorseVerseMainContent()
            }
        }

        // Verify bottom navigation items have content descriptions
        composeTestRule.onNodeWithContentDescription("Home").assertExists()
        composeTestRule.onNodeWithContentDescription("Learn").assertExists()
        composeTestRule.onNodeWithContentDescription("Practice").assertExists()
        composeTestRule.onNodeWithContentDescription("Tree").assertExists()
        composeTestRule.onNodeWithContentDescription("Translate").assertExists()
    }

    @Test
    fun allInteractiveElements_areClickable() {
        composeTestRule.setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK) {
                MorseVerseMainContent()
            }
        }

        // Verify interactive elements are clickable
        composeTestRule.onNodeWithText("Home").assertHasClickAction()
        composeTestRule.onNodeWithText("Learn").assertHasClickAction()
        composeTestRule.onNodeWithText("Practice").assertHasClickAction()
        composeTestRule.onNodeWithText("Tree").assertHasClickAction()
        composeTestRule.onNodeWithText("Translate").assertHasClickAction()
    }
}
