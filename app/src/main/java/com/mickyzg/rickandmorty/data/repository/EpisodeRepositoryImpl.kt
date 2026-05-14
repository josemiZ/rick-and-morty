package com.mickyzg.rickandmorty.data.repository

import com.mickyzg.rickandmorty.data.di.IoDispatcher
import com.mickyzg.rickandmorty.data.local.EpisodeDao
import com.mickyzg.rickandmorty.data.local.mapper.EpisodeEntityMapper
import com.mickyzg.rickandmorty.data.remote.RickAndMortyService
import com.mickyzg.rickandmorty.data.remote.mapper.EpisodeDtoMapper
import com.mickyzg.rickandmorty.domain.model.Episode
import com.mickyzg.rickandmorty.domain.repository.EpisodeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first implementation of [EpisodeRepository].
 *
 * Follows the same read/write path strategy as [CharacterRepositoryImpl]:
 *  - `observe*` methods stream from Room (local source of truth).
 *  - `load*` / `refresh*` methods call the remote API, persist locally, and let
 *    Room re-emit updates to active observers automatically.
 *
 * **404 handling**: The Rick and Morty API returns HTTP 404 for empty search results.
 * This is treated as "zero items found" rather than an error.
 */
@Singleton
class EpisodeRepositoryImpl @Inject constructor(
    private val dao: EpisodeDao,
    private val service: RickAndMortyService,
    private val dtoMapper: EpisodeDtoMapper,
    private val entityMapper: EpisodeEntityMapper,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : EpisodeRepository {

    override fun observeEpisodes(): Flow<List<Episode>> =
        dao.observeAll()
            .map { entities -> entities.map(entityMapper::toDomain) }
            .flowOn(dispatcher)

    override suspend fun loadEpisodesPage(page: Int, name: String?): Result<Int> =
        withContext(dispatcher) {
            runCatching {
                val response = service.getEpisodes(page, name)
                when {
                    response.code() == HTTP_NOT_FOUND -> 0
                    !response.isSuccessful ->
                        error("HTTP ${response.code()}: ${response.message()}")
                    else -> {
                        val body = response.body()
                            ?: error("Empty response body on page $page")
                        val entities = body.results.mapIndexed { index, dto ->
                            entityMapper.toEntity(
                                domain = dtoMapper.mapListItem(dto),
                                pageIndex = (page - 1) * body.results.size + index
                            )
                        }
                        dao.insertAll(entities)
                        entities.size
                    }
                }
            }
        }

    override fun observeEpisodeById(id: Int): Flow<Episode?> =
        dao.observeById(id)
            .map { entity -> entity?.let(entityMapper::toDomain) }
            .flowOn(dispatcher)

    override suspend fun refreshEpisodeById(id: Int): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                val response = service.getEpisodeById(id)
                if (!response.isSuccessful)
                    error("HTTP ${response.code()}: ${response.message()}")
                val dto = response.body() ?: error("Empty response body for episode $id")
                val existing = dao.getById(id)
                val entity = entityMapper.toEntity(
                    domain = dtoMapper.map(dto),
                    pageIndex = existing?.pageIndex ?: 0
                )
                dao.insertOrUpdate(entity)
            }
        }

    override fun searchEpisodes(query: String): Flow<List<Episode>> {
        if (query.isBlank()) return observeEpisodes()
        return dao.searchByName(query)
            .map { entities -> entities.map(entityMapper::toDomain) }
            .flowOn(dispatcher)
    }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}

