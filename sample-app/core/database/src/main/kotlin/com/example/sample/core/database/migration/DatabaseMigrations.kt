package com.example.sample.core.database.migration

import androidx.room.DeleteTable
import androidx.room.migration.AutoMigrationSpec

internal object DatabaseMigrations {
    /** The News feature was removed; its cache table goes with it. */
    @DeleteTable(tableName = "articles")
    class Schema2to3 : AutoMigrationSpec
}
