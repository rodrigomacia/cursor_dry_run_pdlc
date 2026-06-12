package com.karaoke.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.karaoke.app.presentation.ui.download.DownloadScreen
import com.karaoke.app.presentation.ui.home.HomeScreen
import com.karaoke.app.presentation.ui.library.LibraryScreen
import com.karaoke.app.presentation.ui.player.PlayerScreen
import com.karaoke.app.presentation.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Library : Screen("library")
    object Download : Screen("download")
    object Settings : Screen("settings")
    object Player : Screen("player/{songId}") {
        fun createRoute(songId: Long) = "player/$songId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Library, "Biblioteca", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
    BottomNavItem(Screen.Download, "Baixar", Icons.Filled.Download, Icons.Outlined.Download),
    BottomNavItem(Screen.Settings, "Config.", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun KaraokeNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route?.startsWith("player/") == false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSongClick = { songId ->
                        navController.navigate(Screen.Player.createRoute(songId))
                    },
                    onNavigateToLibrary = {
                        navController.navigate(Screen.Library.route)
                    }
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    onSongClick = { songId ->
                        navController.navigate(Screen.Player.createRoute(songId))
                    }
                )
            }

            composable(Screen.Download.route) {
                DownloadScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(
                route = Screen.Player.route,
                arguments = listOf(navArgument("songId") { type = NavType.LongType })
            ) { backStackEntry ->
                val songId = backStackEntry.arguments?.getLong("songId") ?: return@composable
                PlayerScreen(
                    songId = songId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
