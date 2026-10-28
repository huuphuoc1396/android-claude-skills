package com.example.sample.core.data.repository

import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.testdoubles.FakeFavoriteDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultFavoritesRepositoryTest {
    private val testScope = TestScope(StandardTestDispatcher())
    private val dao = FakeFavoriteDao()
    private val repository = DefaultFavoritesRepository(
        favoriteDao = dao,
        ioDispatcher = StandardTestDispatcher(testScope.testScheduler),
    )

    @Test
    fun setFavorite_true_isObserved() = testScope.runTest {
        repository.setFavorite(1, MediaType.Movie, favorite = true)

        assertTrue(repository.observeIsFavorite(1, MediaType.Movie).first())
    }

    @Test
    fun setFavorite_false_removes() = testScope.runTest {
        repository.setFavorite(1, MediaType.Movie, favorite = true)
        repository.setFavorite(1, MediaType.Movie, favorite = false)

        assertFalse(repository.observeIsFavorite(1, MediaType.Movie).first())
    }

    @Test
    fun setFavorite_repeated_keepsLastState() = testScope.runTest {
        listOf(true, true, false, true).forEach { repository.setFavorite(1, MediaType.Tv, it) }

        assertTrue(repository.observeIsFavorite(1, MediaType.Tv).first())
        assertEquals(1, dao.favorites.value.size)
    }

    @Test
    fun sameIdDifferentMediaType_isSeparate() = testScope.runTest {
        repository.setFavorite(1, MediaType.Movie, favorite = true)

        assertFalse(repository.observeIsFavorite(1, MediaType.Tv).first())
    }
}
