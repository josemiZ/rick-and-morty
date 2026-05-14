package com.mickyzg.rickandmorty.data.repository

import com.mickyzg.rickandmorty.data.di.IoDispatcher
import com.mickyzg.rickandmorty.data.local.LocationDao
import com.mickyzg.rickandmorty.data.local.mapper.LocationEntityMapper
import com.mickyzg.rickandmorty.data.remote.RickAndMortyService
import com.mickyzg.rickandmorty.data.remote.mapper.LocationDtoMapper
import com.mickyzg.rickandmorty.domain.model.Location
import com.mickyzg.rickandmorty.domain.repository.LocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first implementation of [LocationRepository].
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
class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao,
    private val service: RickAndMortyService,
    private val dtoMapper: LocationDtoMapper,
    private val entityMapper: LocationEntityMapper,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : LocationRepository {

    override fun observeLocations(): Flow<List<Location>> =
        dao.observeAll()
            .map { entities -> entities.map(entityMapper::toDomain) }
            .flowOn(dispatcher)

    override suspend fun loadLocationsPage(page: Int, name: String?): Result<Int> =
        withContext(dispatcher) {
            runCatching {
                val response = service.getLocations(page, name)
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

    override fun observeLocationById(id: Int): Flow<Location?> =
        dao.observeById(id)
            .map { entity -> entity?.let(entityMapper::toDomain) }
            .flowOn(dispatcher)

    override suspend fun refreshLocationById(id: Int): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                val response = service.getLocationById(id)
                if (!response.isSuccessful)
                    error("HTTP ${response.code()}: ${response.message()}")
                val dto = response.body() ?: error("Empty response body for location $id")
                val existing = dao.getById(id)
                val entity = entityMapper.toEntity(
                    domain = dtoMapper.map(dto),
                    pageIndex = existing?.pageIndex ?: 0
                )
                dao.insertOrUpdate(entity)
            }
        }

    override fun searchLocations(query: String): Flow<List<Location>> {
        if (query.isBlank()) return observeLocations()
        return dao.searchByName(query)
            .map { entities -> entities.map(entityMapper::toDomain) }
            .flowOn(dispatcher)
    }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}

