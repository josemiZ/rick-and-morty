package com.mickyzg.rickandmorty

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class entry point for Hilt dependency injection.
 *
 * Annotated with [HiltAndroidApp] to trigger Hilt's code generation,
 * including a base class that serves as the application-level dependency container.
 */
@HiltAndroidApp
class App : Application()

