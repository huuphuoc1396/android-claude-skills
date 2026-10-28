package com.example.sample.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.sample.core.database.model.TitleDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TitleDetailsDao {
    @Query("SELECT * FROM title_details WHERE id = :id AND media_type = :mediaType")
    fun observe(id: Long, mediaType: String): Flow<TitleDetailsEntity?>

    @Upsert
    suspend fun upsert(details: TitleDetailsEntity)
}
