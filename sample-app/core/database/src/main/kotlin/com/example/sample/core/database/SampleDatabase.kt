package com.example.sample.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sample.core.database.dao.CategoryTitleDao
import com.example.sample.core.database.dao.FavoriteDao
import com.example.sample.core.database.dao.TitleDetailsDao
import com.example.sample.core.database.migration.DatabaseMigrations
import com.example.sample.core.database.model.CategoryTitleEntity
import com.example.sample.core.database.model.FavoriteEntity
import com.example.sample.core.database.model.TitleDetailsEntity
import com.example.sample.core.database.util.Converters

/** Bump [version] and add an auto-migration for every schema change; the exported schema lives in `schemas/`. */
@Database(
    entities = [FavoriteEntity::class, CategoryTitleEntity::class, TitleDetailsEntity::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = DatabaseMigrations.Schema2to3::class),
    ],
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class SampleDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    abstract fun categoryTitleDao(): CategoryTitleDao

    abstract fun titleDetailsDao(): TitleDetailsDao
}
