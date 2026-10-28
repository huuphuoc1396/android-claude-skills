package com.example.sample.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sample.core.database.dao.ArticleDao
import com.example.sample.core.database.model.ArticleEntity

/** Bump [version] and add an auto-migration for every schema change; the exported schema lives in `schemas/`. */
@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class SampleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}
