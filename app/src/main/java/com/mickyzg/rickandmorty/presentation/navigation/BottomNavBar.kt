package com.mickyzg.rickandmorty.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

/** Defines a single item rendered in [BottomNavBar]. */
private data class BottomNavItem(
    val route: Route,
    val label: String,
    val icon: ImageVector
)

private val navItems = listOf(
    BottomNavItem(Route.CharacterList, "Characters", Icons.Default.Person),
    BottomNavItem(Route.LocationList, "Locations", Icons.Default.LocationOn),
    BottomNavItem(Route.EpisodeList, "Episodes", Icons.Default.PlayCircle),
    BottomNavItem(Route.Favorites, "Favorites", Icons.Default.Favorite)
)

/** Routes whose destinations are top-level; the bottom bar is visible only here. */
val topLevelRoutes: Set<String> = navItems.map { it.route.path }.toSet()

/**
 * Material3 [NavigationBar] with 4 tabs: Characters, Locations, Episodes, Favorites.
 *
 * Uses [NavHostController.navigate] with `popUpTo` + `launchSingleTop` so that
 * navigating between tabs does not accumulate a growing back stack.
 */
@Composable
fun BottomNavBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route.path,
                onClick = {
                    navController.navigate(item.route.path) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

