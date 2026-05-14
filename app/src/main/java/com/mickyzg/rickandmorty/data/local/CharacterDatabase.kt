package com.mickyzg.rickandmorty.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for the Rick and Morty app.
 *
 * Holds the local source of truth for the offline-first architecture:
 * cached characters, locations and episodes from the API, plus user favorites.
 *
 * Version history:
 *  - v1: initial schema (characters placeholder)
 *  - v2: full schema — expanded characters, added locations and episodes tables
 */
@Database(
    entities = [
        CharacterEntity::class,
        LocationEntity::class,
        EpisodeEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class CharacterDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao
    abstract fun locationDao(): LocationDao
    abstract fun episodeDao(): EpisodeDao

    companion object {
        const val DATABASE_NAME = "rickandmorty.db"
    }
}
