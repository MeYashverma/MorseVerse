package com.morseverse.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// ═══════════════════════════════════════════════════════════════════
// NAVIGATION ROUTES
// ═══════════════════════════════════════════════════════════════════

object Routes {
    const val HOME = "home"
    const val LEARN = "learn"
    const val PRACTICE = "practice"
    const val MORSE_TREE = "morse_tree"
    const val TRANSLATOR = "translator"
    const val DECODER = "decoder"
    const val STATISTICS = "statistics"
    const val ACHIEVEMENTS = "achievements"
    const val STORY = "story"
    const val HAM = "ham"
    const val SETTINGS = "settings"
    const val CHARACTER_DETAIL = "character/{character}"
    const val PRACTICE_SESSION = "practice_session/{mode}"
    const val STORY_MISSION = "story_mission/{missionId}"
    const val ABOUT = "about"

    fun characterDetail(character: String) = "character/$character"
    fun practiceSession(mode: String) = "practice_session/$mode"
    fun storyMission(missionId: String) = "story_mission/$missionId"
}

@Composable
fun MorseVerseNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(200))
        }
    ) {
        // Home
        composable(Routes.HOME) {
            com.morseverse.feature.home.ui.HomeScreen(
                onNavigateToLearn = { navController.navigate(Routes.LEARN) },
                onNavigateToPractice = { navController.navigate(Routes.PRACTICE) },
                onNavigateToTree = { navController.navigate(Routes.MORSE_TREE) },
                onNavigateToTranslator = { navController.navigate(Routes.TRANSLATOR) },
                onNavigateToStatistics = { navController.navigate(Routes.STATISTICS) },
                onNavigateToAchievements = { navController.navigate(Routes.ACHIEVEMENTS) },
                onNavigateToStory = { navController.navigate(Routes.STORY) },
                onNavigateToHam = { navController.navigate(Routes.HAM) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToCharacter = { char -> navController.navigate(Routes.characterDetail(char)) },
                onNavigateToPracticeSession = { mode -> navController.navigate(Routes.practiceSession(mode)) }
            )
        }

        // Learn
        composable(Routes.LEARN) {
            com.morseverse.feature.learn.ui.LearnScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTree = { navController.navigate(Routes.MORSE_TREE) },
                onNavigateToCharacter = { char -> navController.navigate(Routes.characterDetail(char)) },
                onNavigateToPractice = { mode -> navController.navigate(Routes.practiceSession(mode)) }
            )
        }

        // Practice
        composable(Routes.PRACTICE) {
            com.morseverse.feature.practice.ui.PracticeScreen(
                onNavigateBack = { navController.popBackStack() },
                onStartPractice = { mode -> navController.navigate(Routes.practiceSession(mode)) }
            )
        }

        // Morse Tree
        composable(Routes.MORSE_TREE) {
            com.morseverse.feature.morsetree.ui.MorseTreeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCharacter = { char -> navController.navigate(Routes.characterDetail(char)) }
            )
        }

        // Translator
        composable(Routes.TRANSLATOR) {
            com.morseverse.feature.translator.ui.TranslatorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Decoder
        composable(Routes.DECODER) {
            com.morseverse.feature.decoder.ui.DecoderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Statistics
        composable(Routes.STATISTICS) {
            com.morseverse.feature.statistics.ui.StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Achievements
        composable(Routes.ACHIEVEMENTS) {
            com.morseverse.feature.achievements.ui.AchievementsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Story
        composable(Routes.STORY) {
            com.morseverse.feature.story.ui.StoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMission = { id -> navController.navigate(Routes.storyMission(id)) }
            )
        }

        // Ham Radio
        composable(Routes.HAM) {
            com.morseverse.feature.ham.ui.HamScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Character Detail
        composable(
            route = Routes.CHARACTER_DETAIL,
            arguments = listOf(navArgument("character") { type = NavType.StringType })
        ) { backStackEntry ->
            val character = backStackEntry.arguments?.getString("character") ?: "A"
            com.morseverse.app.ui.screens.character.CharacterDetailScreen(
                character = character,
                onNavigateBack = { navController.popBackStack() },
                onPractice = { navController.navigate(Routes.practiceSession("CHARACTER")) }
            )
        }

        // Practice Session
        composable(
            route = Routes.PRACTICE_SESSION,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "CHARACTER"
            com.morseverse.feature.practice.ui.PracticeSessionScreen(
                mode = mode,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings
        composable(Routes.SETTINGS) {
            com.morseverse.app.ui.screens.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT) }
            )
        }

        // About
        composable(Routes.ABOUT) {
            com.morseverse.app.ui.screens.about.AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
