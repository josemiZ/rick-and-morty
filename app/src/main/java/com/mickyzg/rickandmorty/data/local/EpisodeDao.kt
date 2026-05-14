package com.mickyzg.rickandmorty.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [EpisodeEntity] persistence operations.
 *
 * All [Flow]-returning queries are observed reactively: any write to the
 * `episodes` table automatically re-emits the updated data to collectors.
 */
@Dao
interface EpisodeDao {

    @Query("SELECT * FROM episodes ORDER BY pageIndex ASC, id ASC")
    fun observeAll(): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE id = :id")
    fun observeById(id: Int): Flow<EpisodeEntity?>

    @Query("SELECT * FROM episodes WHERE name LIKE '%' || :query || '%' ORDER BY pageIndex ASC, id ASC")
    fun searchByName(query: String): Flow<List<EpisodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<EpisodeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: EpisodeEntity)

    /** One-shot fetch used by the repository to read existing metadata before an upsert. */
    @Query("SELECT * FROM episodes WHERE id = :id")
    suspend fun getById(id: Int): EpisodeEntity?

    @Query("SELECT MAX(pageIndex) FROM episodes")
    suspend fun getMaxPageIndex(): Int?

    @Query("SELECT COUNT(*) FROM episodes WHERE id = :id")
    suspend fun exists(id: Int): Int
}

