package com.mickyzg.rickandmorty.data.di

import com.mickyzg.rickandmorty.data.repository.CharacterRepositoryImpl
import com.mickyzg.rickandmorty.data.repository.EpisodeRepositoryImpl
import com.mickyzg.rickandmorty.data.repository.LocationRepositoryImpl
import com.mickyzg.rickandmorty.domain.repository.CharacterRepository
import com.mickyzg.rickandmorty.domain.repository.EpisodeRepository
import com.mickyzg.rickandmorty.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds domain repository interfaces to their data-layer implementations.
 *
 * Using `@Binds` (instead of `@Provides`) is more efficient: Hilt generates a direct
 * delegation without an extra factory, and the abstract class form is required for it.
 *
 * All bindings are [Singleton] to guarantee a single source-of-truth instance per process.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        impl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindEpisodeRepository(
        impl: EpisodeRepositoryImpl
    ): EpisodeRepository
}
