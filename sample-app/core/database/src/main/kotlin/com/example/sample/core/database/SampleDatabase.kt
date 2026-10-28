package com.example.sample.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sample.core.database.dao.ArticleDao
import com.example.sample.core.database.dao.FavoriteDao
import com.example.sample.core.database.model.ArticleEntity
import com.example.sample.core.database.model.FavoriteEntity

/** Bump [version] and add an auto-migration for every schema change; the exported schema lives in `schemas/`. */
@Database(
    entities = [ArticleEntity::class, FavoriteEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    exportSchema = true,
)
abstract class SampleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    abstract fun favoriteDao(): FavoriteDao
}
