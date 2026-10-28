package com.example.sample.core.data.testdoubles

import com.example.sample.core.database.dao.FavoriteDao
import com.example.sample.core.database.model.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Mirrors the table: a set keyed by (id, media type), so inserts ignore duplicates. */
class FakeFavoriteDao : FavoriteDao {
    val favorites = MutableStateFlow(emptySet<FavoriteEntity>())

    override fun observeIsFavorite(id: Long, mediaType: String): Flow<Boolean> =
        favorites.map { FavoriteEntity(id, mediaType) in it }

    override suspend fun insert(favorite: FavoriteEntity) {
        favorites.update { it + favorite }
    }

    override suspend fun delete(favorite: FavoriteEntity) {
        favorites.update { it - favorite }
    }
}
