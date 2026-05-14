package com.mickyzg.rickandmorty.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [LocationEntity] persistence operations.
 *
 * All [Flow]-returning queries are observed reactively: any write to the
 * `locations` table automatically re-emits the updated data to collectors.
 */
@Dao
interface LocationDao {

    @Query("SELECT * FROM locations ORDER BY pageIndex ASC, id ASC")
    fun observeAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun observeById(id: Int): Flow<LocationEntity?>

    @Query("SELECT * FROM locations WHERE name LIKE '%' || :query || '%' ORDER BY pageIndex ASC, id ASC")
    fun searchByName(query: String): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: LocationEntity)

    /** One-shot fetch used by the repository to read existing metadata before an upsert. */
    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getById(id: Int): LocationEntity?

    @Query("SELECT MAX(pageIndex) FROM locations")
    suspend fun getMaxPageIndex(): Int?

    @Query("SELECT COUNT(*) FROM locations WHERE id = :id")
    suspend fun exists(id: Int): Int
}

