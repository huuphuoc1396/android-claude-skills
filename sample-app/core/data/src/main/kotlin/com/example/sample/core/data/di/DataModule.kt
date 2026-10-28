package com.example.sample.core.data.di

import com.example.sample.core.data.repository.ArticleRepository
import com.example.sample.core.data.repository.OfflineFirstArticleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    fun bindsArticleRepository(impl: OfflineFirstArticleRepository): ArticleRepository
}
