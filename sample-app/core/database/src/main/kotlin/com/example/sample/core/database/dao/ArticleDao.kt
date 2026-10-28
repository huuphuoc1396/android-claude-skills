package com.example.sample.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.sample.core.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY published_at DESC")
    fun observeArticles(): Flow<List<ArticleEntity>>

    @Upsert
    suspend fun upsertArticles(articles: List<ArticleEntity>)
}
