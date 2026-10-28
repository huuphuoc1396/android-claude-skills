package com.example.sample.core.data.di

import com.example.sample.core.data.repository.DefaultFavoritesRepository
import com.example.sample.core.data.repository.OfflineFirstTitleRepository
import com.example.sample.core.data.repository.DefaultUserPreferencesRepository
import com.example.sample.core.data.repository.FavoritesRepository
import com.example.sample.core.data.repository.TitleRepository
import com.example.sample.core.data.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    fun bindsTitleRepository(impl: OfflineFirstTitleRepository): TitleRepository

    @Binds
    fun bindsFavoritesRepository(impl: DefaultFavoritesRepository): FavoritesRepository

    @Binds
    fun bindsUserPreferencesRepository(impl: DefaultUserPreferencesRepository): UserPreferencesRepository
}
