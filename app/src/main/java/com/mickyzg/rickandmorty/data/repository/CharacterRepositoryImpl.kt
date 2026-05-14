package com.mickyzg.rickandmorty.data.repository

import com.mickyzg.rickandmorty.data.di.IoDispatcher
import com.mickyzg.rickandmorty.data.local.CharacterDao
import com.mickyzg.rickandmorty.data.local.mapper.CharacterEntityMapper
import com.mickyzg.rickandmorty.data.remote.RickAndMortyService
import com.mickyzg.rickandmorty.data.remote.mapper.CharacterDtoMapper
import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first implementation of [CharacterRepository].
 *
 * **Read path**: all `observe*` methods return reactive [Flow]s backed by Room.
 * The UI always reads from the local database; the remote API is only a refresh source.
 *
 * **Write path**: `load*` and `refresh*` methods call the remote API, map the response
 * to domain models, and persist them locally. Room then re-emits the updated data to
 * any active `observe*` collectors automatically.
 *
 * **Favorite safety**: [loadCharactersPage] fetches current favorite IDs before inserting
 * so that `OnConflictStrategy.REPLACE` does not silently overwrite the user's flag.
 *
 * **Search**: [searchCharacters] queries the local cache. When no results are found,
 * the ViewModel layer is responsible for triggering a remote search via [loadCharactersPage].
 *
 * **404 handling**: The Rick and Morty API returns HTTP 404 for empty search results.
 * This is treated as "zero items found" rather than an error.
 */
@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val dao: CharacterDao,
    private val service: RickAndMortyService,
    private val dtoMapper: CharacterDtoMapper,
    private val entityMapper: CharacterEntityMapper,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : CharacterRepository {

    override fun observeCharacters(): Flow<List<Character>> =
        dao.observeAll()
            .map { entities -> entities.map(entityMapper::toDomain) }
            .flowOn(dispatcher)

    override suspend fun loadCharactersPage(page: Int, name: String?): Result<Int> =
        withContext(dispatcher) {
            runCatching {
                val response = service.getCharacters(page, name)
                when {
                    // API returns 404 when a name filter matches nothing — treat as empty.
                    response.code() == HTTP_NOT_FOUND -> 0
                    !response.isSuccessful ->
                        error("HTTP ${response.code()}: ${response.message()}")
                    else -> {
                        val body = response.body()
                            ?: error("Empty response body on page $page")
                        val favoriteIds = dao.getFavoriteIds().toSet()
                        val entities = body.results.mapIndexed { index, dto ->
                            val domain = dtoMapper.mapListItem(dto)
                            entityMapper.toEntity(
                                domain = domain,
                                pageIndex = (page - 1) * body.results.size + index,
                                isFavorite = favoriteIds.contains(dto.id)
                            )
                        }
                        dao.insertAll(entities)
                        entities.size
                    }
                }
            }
        }

    override fun observeCharacterById(id: Int): Flow<Character?> =
        dao.observeById(id)
            .map { entity -> entity?.let(entityMapper::toDomain) }
            .flowOn(dispatcher)

    override suspend fun refreshCharacterById(id: Int): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                val response = service.getCharacterById(id)
                if (!response.isSuccessful)
                    error("HTTP ${response.code()}: ${response.message()}")
                val dto = response.body() ?: error("Empty response body for character $id")
                val existing = dao.getById(id)
                val entity = entityMapper.toEntity(
                    domain = dtoMapper.map(dto),
                    pageIndex = existing?.pageIndex ?: 0,
                    isFavorite = existing?.isFavorite ?: false
                )
                dao.insertOrUpdate(entity)
            }
        }

    override fun searchCharacters(query: String): Flow<List<Character>> {
        if (query.isBlank()) return observeCharacters()
        return dao.searchByName(query)
            .map { entities -> entities.map(entityMapper::toDomain) }
            .flowOn(dispatcher)
    }

    override fun observeFavorites(): Flow<List<Character>> =
        dao.observeFavorites()
            .map { entities -> entities.map(entityMapper::toDomain) }
            .flowOn(dispatcher)

    override suspend fun setFavorite(characterId: Int, isFavorite: Boolean): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                if (dao.exists(characterId) == 0)
                    error("Character $characterId not found in local cache")
                dao.updateFavorite(characterId, isFavorite)
            }
        }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}

