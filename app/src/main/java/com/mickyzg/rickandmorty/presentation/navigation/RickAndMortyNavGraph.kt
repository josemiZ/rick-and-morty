package com.mickyzg.rickandmorty.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mickyzg.rickandmorty.presentation.screen.CharacterDetailScreen
import com.mickyzg.rickandmorty.presentation.screen.CharacterListScreen
import com.mickyzg.rickandmorty.presentation.screen.EpisodeDetailScreen
import com.mickyzg.rickandmorty.presentation.screen.EpisodeListScreen
import com.mickyzg.rickandmorty.presentation.screen.FavoritesScreen
import com.mickyzg.rickandmorty.presentation.screen.LocationDetailScreen
import com.mickyzg.rickandmorty.presentation.screen.LocationListScreen
import com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail.CharacterDetailViewModel
import com.mickyzg.rickandmorty.presentation.viewmodel.characterList.CharacterListViewModel
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail.EpisodeDetailViewModel
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeList.EpisodeListViewModel
import com.mickyzg.rickandmorty.presentation.viewmodel.favorites.FavoritesViewModel
import com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail.LocationDetailViewModel
import com.mickyzg.rickandmorty.presentation.viewmodel.locationList.LocationListViewModel

/**
 * Root navigation graph for the Rick and Morty app.
 *
 * Each composable destination obtains its ViewModel via [hiltViewModel], collects
 * state with [collectAsStateWithLifecycle], and passes pure data down to stateless
 * screen composables.  User actions flow back up through [vm.publish].
 */
@Composable
fun RickAndMortyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Route.CharacterList.path
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ── Top-level tabs ────────────────────────────────────────────────────
        composable(route = Route.CharacterList.path) {
            val vm: CharacterListViewModel = hiltViewModel()
            val uiState by vm.state.collectAsStateWithLifecycle()
            CharacterListScreen(
                uiState = uiState,
                onAction = vm::publish,
                onCharacterClick = { id -> navController.navigate(Route.CharacterDetail.build(id)) }
            )
        }
        composable(route = Route.LocationList.path) {
            val vm: LocationListViewModel = hiltViewModel()
            val uiState by vm.state.collectAsStateWithLifecycle()
            LocationListScreen(
                uiState = uiState,
                onAction = vm::publish,
                onLocationClick = { id -> navController.navigate(Route.LocationDetail.build(id)) }
            )
        }
        composable(route = Route.EpisodeList.path) {
            val vm: EpisodeListViewModel = hiltViewModel()
            val uiState by vm.state.collectAsStateWithLifecycle()
            EpisodeListScreen(
                uiState = uiState,
                onAction = vm::publish,
                onEpisodeClick = { id -> navController.navigate(Route.EpisodeDetail.build(id)) }
            )
        }
        composable(route = Route.Favorites.path) {
            val vm: FavoritesViewModel = hiltViewModel()
            val uiState by vm.state.collectAsStateWithLifecycle()
            FavoritesScreen(
                uiState = uiState,
                onAction = vm::publish,
                onCharacterClick = { id -> navController.navigate(Route.CharacterDetail.build(id)) }
            )
        }

        // ── Detail destinations ───────────────────────────────────────────────
        composable(
            route = Route.CharacterDetail.path,
            arguments = listOf(
                navArgument(Route.CharacterDetail.ARG_CHARACTER_ID) { type = NavType.IntType }
            )
        ) {
            val vm: CharacterDetailViewModel = hiltViewModel()
            val uiState by vm.state.collectAsStateWithLifecycle()
            CharacterDetailScreen(
                uiState = uiState,
                onAction = vm::publish,
                onBack = navController::navigateUp
            )
        }
        composable(
            route = Route.LocationDetail.path,
            arguments = listOf(
                navArgument(Route.LocationDetail.ARG_LOCATION_ID) { type = NavType.IntType }
            )
        ) {
            val vm: LocationDetailViewModel = hiltViewModel()
            val uiState by vm.state.collectAsStateWithLifecycle()
            LocationDetailScreen(
                uiState = uiState,
                onAction = vm::publish,
                onBack = navController::navigateUp
            )
        }
        composable(
            route = Route.EpisodeDetail.path,
            arguments = listOf(
                navArgument(Route.EpisodeDetail.ARG_EPISODE_ID) { type = NavType.IntType }
            )
        ) {
            val vm: EpisodeDetailViewModel = hiltViewModel()
            val uiState by vm.state.collectAsStateWithLifecycle()
            EpisodeDetailScreen(
                uiState = uiState,
                onAction = vm::publish,
                onBack = navController::navigateUp
            )
        }
    }
}
