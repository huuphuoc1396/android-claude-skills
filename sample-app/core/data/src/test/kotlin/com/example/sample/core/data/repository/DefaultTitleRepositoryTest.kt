package com.example.sample.core.data.repository

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.data.testdoubles.FakeTmdbNetworkDataSource
import com.example.sample.core.network.TmdbDiscoverQuery
import com.example.sample.core.network.TmdbMediaType
import com.example.sample.core.network.model.NetworkTitle
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class DefaultTitleRepositoryTest {
    private val testScope = TestScope(StandardTestDispatcher())
    private lateinit var network: FakeTmdbNetworkDataSource
    private lateinit var repository: DefaultTitleRepository

    @Before
    fun setUp() {
        network = FakeTmdbNetworkDataSource()
        repository = DefaultTitleRepository(
            network = network,
            ioDispatcher = StandardTestDispatcher(testScope.testScheduler),
        )
    }

    @Test
    fun films_queriesMoviesWithoutFilters() = testScope.runTest {
        repository.getCategoryTitles(Category.Films)

        assertEquals(
            setOf(
                TmdbDiscoverQuery(TmdbMediaType.Movie, sortBy = "vote_average.desc", minVoteCount = 1000),
                TmdbDiscoverQuery(TmdbMediaType.Movie, sortBy = "popularity.desc"),
            ),
            network.queries.toSet(),
        )
    }

    @Test
    fun series_queriesTvWithoutFilters() = testScope.runTest {
        repository.getCategoryTitles(Category.Series)

        assertEquals(
            setOf(
                TmdbDiscoverQuery(TmdbMediaType.Tv, sortBy = "vote_average.desc", minVoteCount = 500),
                TmdbDiscoverQuery(TmdbMediaType.Tv, sortBy = "popularity.desc"),
            ),
            network.queries.toSet(),
        )
    }

    @Test
    fun anime_queriesMovieAndTvAnimationFromJapan() = testScope.runTest {
        repository.getCategoryTitles(Category.Anime)

        assertEquals(4, network.queries.size)
        assertEquals(setOf(TmdbMediaType.Movie, TmdbMediaType.Tv), network.queries.map { it.mediaType }.toSet())
        assertTrue(network.queries.all { it.genreId == 16 && it.originCountries == "JP" })
    }

    @Test
    fun anime_mergesAndSortsByPopularity() = testScope.runTest {
        network.results = mapOf(
            TmdbMediaType.Movie to listOf(networkTitle(id = 1, popularity = 10.0)),
            TmdbMediaType.Tv to listOf(networkTitle(id = 2, popularity = 30.0), networkTitle(id = 3, popularity = 20.0)),
        )

        val popular = repository.getCategoryTitles(Category.Anime).popular

        assertEquals(listOf(2L, 3L, 1L), popular.map { it.id })
        assertEquals(listOf(MediaType.Tv, MediaType.Tv, MediaType.Movie), popular.map { it.mediaType })
    }

    @Test
    fun novelas_queriesSoapTvFromLatinAmerica() = testScope.runTest {
        repository.getCategoryTitles(Category.Novelas)

        assertTrue(network.queries.all { it.mediaType == TmdbMediaType.Tv })
        assertTrue(network.queries.all { it.genreId == 10766 && it.originCountries == "BR|MX|CO|AR|CL|PE|VE" })
    }

    @Test
    fun featured_sortsByVoteAverageWithMinVotes() = testScope.runTest {
        network.results = mapOf(
            TmdbMediaType.Movie to listOf(
                networkTitle(id = 1, voteAverage = 7.0),
                networkTitle(id = 2, voteAverage = 9.0),
            ),
        )

        val featured = repository.getCategoryTitles(Category.Films).featured

        assertEquals(listOf(2L, 1L), featured.map { it.id })
        assertEquals(1000, network.queries.single { it.sortBy == "vote_average.desc" }.minVoteCount)
    }

    @Test
    fun mapsTitleOrName_andBuildsPosterUrl() = testScope.runTest {
        network.results = mapOf(
            TmdbMediaType.Tv to listOf(
                NetworkTitle(id = 1, name = "Lucifer", posterPath = "/lucifer.jpg"),
                NetworkTitle(id = 2, title = "Matrix", posterPath = null),
            ),
        )

        val popular = repository.getCategoryTitles(Category.Series).popular

        assertEquals(
            listOf(
                Title(1, MediaType.Tv, "Lucifer", "https://image.tmdb.org/t/p/w342/lucifer.jpg"),
                Title(2, MediaType.Tv, "Matrix", null),
            ),
            popular,
        )
    }

    @Test
    fun networkError_isThrown() = testScope.runTest {
        network.shouldFail = true

        val error = runCatching { repository.getCategoryTitles(Category.Films) }.exceptionOrNull()

        assertTrue(error is IOException)
    }

    private fun networkTitle(id: Long, popularity: Double = 0.0, voteAverage: Double = 0.0) =
        NetworkTitle(id = id, title = "Title $id", popularity = popularity, voteAverage = voteAverage)
}
