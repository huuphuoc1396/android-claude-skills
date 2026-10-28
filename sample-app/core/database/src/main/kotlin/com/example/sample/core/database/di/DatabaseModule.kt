package com.example.sample.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.sample.core.database.SampleDatabase
import com.example.sample.core.database.dao.ArticleDao
import com.example.sample.core.database.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesSampleDatabase(@ApplicationContext context: Context): SampleDatabase =
        Room.databaseBuilder(context, SampleDatabase::class.java, "sample-database").build()

    @Provides
    fun providesArticleDao(database: SampleDatabase): ArticleDao = database.articleDao()

    @Provides
    fun providesFavoriteDao(database: SampleDatabase): FavoriteDao = database.favoriteDao()
}
