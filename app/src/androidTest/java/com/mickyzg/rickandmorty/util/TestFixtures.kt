package com.mickyzg.rickandmorty.util

import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.model.CharacterStatus

/**
 * Reusable test fixtures for Compose UI integration tests.
 */
object TestFixtures {

    fun fakeCharacter(
        id: Int = 1,
        name: String = "Character $id",
        status: CharacterStatus = CharacterStatus.ALIVE,
        species: String = "Human",
        imageUrl: String = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
        isFavorite: Boolean = false
    ) = Character(
        id = id,
        name = name,
        status = status,
        species = species,
        imageUrl = imageUrl,
        isFavorite = isFavorite
    )

    val rick = fakeCharacter(id = 1, name = "Rick Sanchez")
    val morty = fakeCharacter(id = 2, name = "Morty Smith")
    val summer = fakeCharacter(id = 3, name = "Summer Smith", status = CharacterStatus.ALIVE)

    val threeCharacters = listOf(rick, morty, summer)

    val favoritedRick = rick.copy(isFavorite = true)
    val favoritedMorty = morty.copy(isFavorite = true)

    val twoFavorites = listOf(favoritedRick, favoritedMorty)
}

