package com.example.sample.core.data.repository

import com.example.sample.core.common.Dispatcher
import com.example.sample.core.common.SampleDispatchers
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.database.dao.FavoriteDao
import com.example.sample.core.database.model.FavoriteEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DefaultFavoritesRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    @Dispatcher(SampleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : FavoritesRepository {

    override fun observeIsFavorite(id: Long, mediaType: MediaType): Flow<Boolean> =
        favoriteDao.observeIsFavorite(id, mediaType.name)

    override suspend fun setFavorite(id: Long, mediaType: MediaType, favorite: Boolean) = withContext(ioDispatcher) {
        val entity = FavoriteEntity(id = id, mediaType = mediaType.name)
        if (favorite) favoriteDao.insert(entity) else favoriteDao.delete(entity)
    }
}
