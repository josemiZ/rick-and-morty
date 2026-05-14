package com.mickyzg.rickandmorty.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Hilt module that provides coroutine dispatchers as injectable dependencies.
 *
 * Injecting dispatchers (instead of hardcoding [Dispatchers.IO]) allows tests
 * to substitute [kotlinx.coroutines.test.UnconfinedTestDispatcher] or
 * [kotlinx.coroutines.test.StandardTestDispatcher] without reflection hacks.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

