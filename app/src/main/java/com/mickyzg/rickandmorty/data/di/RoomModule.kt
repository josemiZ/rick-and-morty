package com.mickyzg.rickandmorty.data.di

import android.content.Context
import androidx.room.Room
import com.mickyzg.rickandmorty.data.local.CharacterDao
import com.mickyzg.rickandmorty.data.local.CharacterDatabase
import com.mickyzg.rickandmorty.data.local.EpisodeDao
import com.mickyzg.rickandmorty.data.local.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides Room database related dependencies.
 *
 * [fallbackToDestructiveMigration] is intentional during development: since there is
 * no production data to preserve yet, a clean rebuild on schema change is acceptable.
 * This should be replaced with explicit [androidx.room.migration.Migration] objects
 * before the first public release.
 */
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideCharacterDatabase(
        @ApplicationContext context: Context
    ): CharacterDatabase = Room.databaseBuilder(
        context = context,
        klass = CharacterDatabase::class.java,
        name = CharacterDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun provideCharacterDao(database: CharacterDatabase): CharacterDao =
        database.characterDao()

    @Provides
    fun provideLocationDao(database: CharacterDatabase): LocationDao =
        database.locationDao()

    @Provides
    fun provideEpisodeDao(database: CharacterDatabase): EpisodeDao =
        database.episodeDao()
}
