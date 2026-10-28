package com.example.sample.core.testing.repository

import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Test double for [FavoritesRepository], backed by an in-memory set. */
class TestFavoritesRepository : FavoritesRepository {
    private val favorites = MutableStateFlow(emptySet<Pair<Long, MediaType>>())

    override fun observeIsFavorite(id: Long, mediaType: MediaType): Flow<Boolean> =
        favorites.map { (id to mediaType) in it }

    override suspend fun setFavorite(id: Long, mediaType: MediaType, favorite: Boolean) {
        favorites.update { if (favorite) it + (id to mediaType) else it - (id to mediaType) }
    }
}
