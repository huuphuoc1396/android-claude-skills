package com.example.sample.core.data.repository

import com.example.sample.core.data.model.MediaType
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeIsFavorite(id: Long, mediaType: MediaType): Flow<Boolean>

    /** Stores the target state rather than toggling, so repeated taps settle on the last one (EC-8). */
    suspend fun setFavorite(id: Long, mediaType: MediaType, favorite: Boolean)
}
