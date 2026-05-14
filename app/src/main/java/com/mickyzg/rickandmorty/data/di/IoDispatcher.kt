package com.mickyzg.rickandmorty.data.di

import javax.inject.Qualifier

/**
 * Hilt qualifier for [kotlinx.coroutines.Dispatchers.IO].
 *
 * Use this annotation to inject a testable [kotlinx.coroutines.CoroutineDispatcher]
 * instead of hardcoding `Dispatchers.IO` inside classes.
 *
 * ```kotlin
 * class MyRepository @Inject constructor(
 *     @param:IoDispatcher private val dispatcher: CoroutineDispatcher
 * )
 * ```
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

