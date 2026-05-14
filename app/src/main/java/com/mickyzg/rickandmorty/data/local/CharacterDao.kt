package com.mickyzg.rickandmorty.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [CharacterEntity] persistence operations.
 *
 * All [Flow]-returning queries are observed reactively: any write to the
 * `characters` table automatically re-emits the updated data to collectors.
 *
 * [OnConflictStrategy.REPLACE] on inserts preserves remote data updates while
 * a manual [updateFavorite] guard in the repository ensures the user's favorite
 * flag is never silently overwritten by a page refresh.
 */
@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY pageIndex ASC, id ASC")
    fun observeAll(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun observeById(id: Int): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavorites(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :query || '%' ORDER BY pageIndex ASC, id ASC")
    fun searchByName(query: String): Flow<List<CharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CharacterEntity)

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    /** One-shot fetch used by the repository to preserve [isFavorite] during REPLACE inserts. */
    @Query("SELECT id FROM characters WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<Int>

    /** One-shot fetch used by the repository to read existing metadata before an upsert. */
    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getById(id: Int): CharacterEntity?

    @Query("SELECT MAX(pageIndex) FROM characters")
    suspend fun getMaxPageIndex(): Int?

    @Query("SELECT COUNT(*) FROM characters WHERE id = :id")
    suspend fun exists(id: Int): Int
}
