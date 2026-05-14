package com.mickyzg.rickandmorty.presentation.screen.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.model.CharacterGender
import com.mickyzg.rickandmorty.domain.model.CharacterLocation
import com.mickyzg.rickandmorty.domain.model.CharacterStatus
import com.mickyzg.rickandmorty.domain.model.Episode
import com.mickyzg.rickandmorty.domain.model.Location
import com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail.CharacterDetailUiState
import com.mickyzg.rickandmorty.presentation.viewmodel.characterList.CharacterListUiState
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail.EpisodeDetailUiState
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeList.EpisodeListUiState
import com.mickyzg.rickandmorty.presentation.viewmodel.favorites.FavoritesUiState
import com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail.LocationDetailUiState
import com.mickyzg.rickandmorty.presentation.viewmodel.locationList.LocationListUiState

// ── Combined light + dark theme preview annotation ────────────────────────────

/**
 * Convenience annotation that generates two previews for the same composable:
 * one in Light mode and one in Dark mode.
 *
 * Apply it to any `@Composable` preview function and Android Studio will render
 * both theme variants side-by-side.
 */
@Preview(name = "Light Mode", showBackground = true, showSystemUi = false)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreviews

// ── Shared sample data ────────────────────────────────────────────────────────

val previewCharacters: List<Character> = listOf(
    Character(
        id = 1, name = "Rick Sanchez",
        status = CharacterStatus.ALIVE, species = "Human", imageUrl = "",
        isFavorite = true
    ),
    Character(
        id = 2, name = "Morty Smith",
        status = CharacterStatus.ALIVE, species = "Human", imageUrl = ""
    ),
    Character(
        id = 3, name = "Evil Morty",
        status = CharacterStatus.UNKNOWN, species = "Human", imageUrl = ""
    ),
    Character(
        id = 4, name = "Mr. Meeseeks",
        status = CharacterStatus.DEAD, species = "Alien", imageUrl = ""
    ),
)

val previewCharacterDetail: Character = Character(
    id = 1, name = "Rick Sanchez",
    status = CharacterStatus.ALIVE, species = "Human", imageUrl = "",
    type = "Scientist",
    gender = CharacterGender.MALE,
    origin = CharacterLocation("Earth (C-137)", "https://rickandmortyapi.com/api/location/1"),
    location = CharacterLocation("Citadel of Ricks", "https://rickandmortyapi.com/api/location/3"),
    episodeUrls = List(51) { "https://rickandmortyapi.com/api/episode/${it + 1}" },
    createdAtIso = "2017-11-04T18:48:46.250Z",
    isFavorite = true,
    isDetailLoaded = true
)

val previewLocations: List<Location> = listOf(
    Location(id = 1, name = "Earth (C-137)", type = "Planet", dimension = "Dimension C-137"),
    Location(id = 2, name = "Citadel of Ricks", type = "Space station", dimension = "unknown"),
    Location(id = 3, name = "Abadango", type = "Cluster", dimension = "unknown"),
)

val previewLocationDetail: Location = Location(
    id = 1, name = "Earth (C-137)", type = "Planet", dimension = "Dimension C-137",
    residentUrls = List(27) { "https://rickandmortyapi.com/api/character/${it + 1}" },
    url = "https://rickandmortyapi.com/api/location/1",
    createdAtIso = "2017-11-10T12:42:04.162Z",
    isDetailLoaded = true
)

val previewEpisodes: List<Episode> = listOf(
    Episode(id = 1, name = "Pilot", airDate = "December 2, 2013", episodeCode = "S01E01"),
    Episode(id = 2, name = "Lawnmower Dog", airDate = "December 9, 2013", episodeCode = "S01E02"),
    Episode(id = 13, name = "Close Rick-counters", airDate = "October 6, 2014", episodeCode = "S02E10"),
)

val previewEpisodeDetail: Episode = Episode(
    id = 1, name = "Pilot", airDate = "December 2, 2013", episodeCode = "S01E01",
    characterUrls = List(19) { "https://rickandmortyapi.com/api/character/${it + 1}" },
    url = "https://rickandmortyapi.com/api/episode/1",
    createdAtIso = "2017-11-10T12:56:33.798Z",
    isDetailLoaded = true
)

// ── PreviewParameterProviders ─────────────────────────────────────────────────

class CharacterListPreviewProvider : PreviewParameterProvider<CharacterListUiState> {
    override val values = sequenceOf(
        CharacterListUiState(isLoading = true),
        CharacterListUiState(),
        CharacterListUiState(error = "Could not connect. Check your internet connection."),
        CharacterListUiState(characters = previewCharacters),
        CharacterListUiState(characters = previewCharacters, isLoadingMore = true),
        CharacterListUiState(characters = previewCharacters, searchQuery = "Rick"),
    )
}

class CharacterDetailPreviewProvider : PreviewParameterProvider<CharacterDetailUiState> {
    override val values = sequenceOf(
        CharacterDetailUiState(isLoading = true),
        CharacterDetailUiState(error = "Character not found. Please retry."),
        CharacterDetailUiState(character = previewCharacterDetail),
    )
}

class FavoritesPreviewProvider : PreviewParameterProvider<FavoritesUiState> {
    override val values = sequenceOf(
        FavoritesUiState(isLoading = true),
        FavoritesUiState(),
        FavoritesUiState(favorites = previewCharacters.filter { it.isFavorite }),
        FavoritesUiState(favorites = previewCharacters),
    )
}

class LocationListPreviewProvider : PreviewParameterProvider<LocationListUiState> {
    override val values = sequenceOf(
        LocationListUiState(isLoading = true),
        LocationListUiState(),
        LocationListUiState(error = "Network error. Pull to retry."),
        LocationListUiState(locations = previewLocations),
        LocationListUiState(locations = previewLocations, isLoadingMore = true),
    )
}

class LocationDetailPreviewProvider : PreviewParameterProvider<LocationDetailUiState> {
    override val values = sequenceOf(
        LocationDetailUiState(isLoading = true),
        LocationDetailUiState(error = "Location not found."),
        LocationDetailUiState(location = previewLocationDetail),
    )
}

class EpisodeListPreviewProvider : PreviewParameterProvider<EpisodeListUiState> {
    override val values = sequenceOf(
        EpisodeListUiState(isLoading = true),
        EpisodeListUiState(),
        EpisodeListUiState(error = "Network error. Pull to retry."),
        EpisodeListUiState(episodes = previewEpisodes),
        EpisodeListUiState(episodes = previewEpisodes, isLoadingMore = true),
    )
}

class EpisodeDetailPreviewProvider : PreviewParameterProvider<EpisodeDetailUiState> {
    override val values = sequenceOf(
        EpisodeDetailUiState(isLoading = true),
        EpisodeDetailUiState(error = "Episode not found."),
        EpisodeDetailUiState(episode = previewEpisodeDetail),
    )
}

