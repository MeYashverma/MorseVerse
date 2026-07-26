package com.morseverse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.morseverse.app.navigation.MorseVerseNavGraph
import com.morseverse.app.navigation.Routes
import com.morseverse.core.designsystem.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MorseVerseTheme(themeMode = ThemeMode.DARK, dynamicColor = false) {
                MorseVerseMainContent()
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorseVerseMainContent() {
    val navController = rememberNavController()
    val haptic = LocalHapticFeedback.current

    val bottomNavItems = listOf(
        BottomNavItem(Routes.HOME, "home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Routes.LEARN, "learn", Icons.Filled.School, Icons.Outlined.School),
        BottomNavItem(Routes.PRACTICE, "practice", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
        BottomNavItem(Routes.MORSE_TREE, "tree", Icons.Filled.AccountTree, Icons.Outlined.AccountTree),
        BottomNavItem(Routes.TRANSLATOR, "translate", Icons.Filled.Translate, Icons.Outlined.Translate)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            if (showBottomBar) {
                Column {
                    // Subtle top border
                    Box(Modifier.fillMaxWidth().height(0.5.dp).background(NothingGray800))
                    NavigationBar(
                        containerColor = DarkSurface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(68.dp)
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true; restoreState = true
                                    }
                                },
                                icon = {
                                    Column {
                                        Icon(
                                            if (selected) item.selectedIcon else item.unselectedIcon,
                                            item.label, modifier = Modifier.size(20.dp)
                                        )
                                        if (selected) {
                                            Spacer(Modifier.height(3.dp))
                                            Box(Modifier.size(3.dp).background(NothingRed.copy(alpha = 0.7f)))
                                        }
                                    }
                                },
                                label = {
                                    Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelSmall,
                                        letterSpacing = if (selected) 1.sp else 0.5.sp,
                                        fontWeight = if (selected) FontWeight.Normal else FontWeight.Light)
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NothingGray300,
                                    selectedTextColor = NothingGray300,
                                    unselectedIconColor = NothingGray600,
                                    unselectedTextColor = NothingGray600,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            MorseVerseNavGraph(navController = navController)
        }
    }
}
