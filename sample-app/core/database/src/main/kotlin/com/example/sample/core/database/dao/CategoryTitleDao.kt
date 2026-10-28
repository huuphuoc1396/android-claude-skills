package com.example.sample.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.sample.core.database.model.CategoryTitleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryTitleDao {
    @Query("SELECT * FROM category_titles WHERE category = :category ORDER BY section, position")
    fun observe(category: String): Flow<List<CategoryTitleEntity>>

    /** Swaps a category's cached posters for [rows] in one transaction. */
    @Transaction
    suspend fun replaceCategory(category: String, rows: List<CategoryTitleEntity>) {
        deleteCategory(category)
        insert(rows)
    }

    @Query("DELETE FROM category_titles WHERE category = :category")
    suspend fun deleteCategory(category: String)

    @Insert
    suspend fun insert(rows: List<CategoryTitleEntity>)
}
