package com.mickyzg.rickandmorty.presentation.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Hilt module for presentation-layer bindings scoped to ViewModels.
 *
 * ViewModels themselves are wired automatically via `@HiltViewModel`; this module
 * is reserved for ViewModel-scoped collaborators (e.g. UI-state mappers,
 * savers, dispatcher overrides) that should be re-created per ViewModel
 * instance and survive configuration changes alongside it.
 *
 * NOTE: Concrete `@Binds` / `@Provides` declarations will be added when the
 * first ViewModel-scoped dependency is introduced.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class PresentationModule

