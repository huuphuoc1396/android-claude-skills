package com.example.sample.core.data.repository

import com.example.sample.core.data.model.CastMember
import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.data.model.TitleDetails
import com.example.sample.core.data.testdoubles.FakeTmdbNetworkDataSource
import com.example.sample.core.network.TmdbDiscoverQuery
import com.example.sample.core.network.TmdbMediaType
import com.example.sample.core.network.model.NetworkCast
import com.example.sample.core.network.model.NetworkCountryReleases
import com.example.sample.core.network.model.NetworkCredits
import com.example.sample.core.network.model.NetworkGenre
import com.example.sample.core.network.model.NetworkPage
import com.example.sample.core.network.model.NetworkReleaseDate
import com.example.sample.core.network.model.NetworkReleaseDates
import com.example.sample.core.network.model.NetworkTitle
import com.example.sample.core.network.model.NetworkTitleDetails
import com.example.sample.core.network.model.NetworkVideo
import com.example.sample.core.network.model.NetworkVideos
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

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

    @Test
    fun movieDetails_mapsAllFields() = testScope.runTest {
        network.region = "BR"
        network.details = mapOf(
            1L to NetworkTitleDetails(
                id = 1,
                title = "Imperdoável",
                posterPath = "/poster.jpg",
                overview = "Após cumprir pena...",
                voteAverage = 4.5,
                voteCount = 120,
                runtime = 113,
                releaseDate = "2021-11-24",
                genres = listOf(NetworkGenre("Drama"), NetworkGenre("Thriller")),
                credits = NetworkCredits(listOf(NetworkCast("Sandra Bullock", "/sandra.jpg", order = 0))),
                recommendations = NetworkPage(listOf(NetworkTitle(id = 2, name = "Lucifer", mediaType = "tv"))),
                releaseDates = releaseDates("BR", NetworkReleaseDate("2021-12-10T00:00:00.000Z", type = 3)),
            ),
        )

        val details = repository.getTitleDetails(1, MediaType.Movie)

        assertEquals(
            TitleDetails(
                id = 1,
                mediaType = MediaType.Movie,
                name = "Imperdoável",
                releaseDate = LocalDate.of(2021, 12, 10),
                releaseCountry = "BR",
                ratingPercent = 45,
                runtimeMinutes = 113,
                synopsis = "Após cumprir pena...",
                artworkUrl = "https://image.tmdb.org/t/p/w780/poster.jpg",
                cast = listOf(CastMember("Sandra Bullock", "https://image.tmdb.org/t/p/w185/sandra.jpg")),
                genres = listOf("Drama", "Thriller"),
                recommendations = listOf(Title(2, MediaType.Tv, "Lucifer", null)),
                trailerUrl = null,
            ),
            details,
        )
    }

    @Test
    fun movieDetails_usesRegionalTheatricalRelease() = testScope.runTest {
        network.region = "US"
        network.details = mapOf(
            1L to NetworkTitleDetails(
                id = 1,
                releaseDate = "2021-11-24",
                releaseDates = releaseDates(
                    "US",
                    NetworkReleaseDate("2021-11-24T00:00:00.000Z", type = 4),
                    NetworkReleaseDate("2021-11-26T00:00:00.000Z", type = 3),
                ),
            ),
        )

        val details = repository.getTitleDetails(1, MediaType.Movie)

        assertEquals(LocalDate.of(2021, 11, 26), details.releaseDate)
        assertEquals("US", details.releaseCountry)
    }

    @Test
    fun movieDetails_withoutRegion_usesReleaseDateWithoutCountry() = testScope.runTest {
        network.region = "BR"
        network.details = mapOf(
            1L to NetworkTitleDetails(
                id = 1,
                releaseDate = "2021-11-24",
                releaseDates = releaseDates("US", NetworkReleaseDate("2021-11-26T00:00:00.000Z", type = 3)),
            ),
        )

        val details = repository.getTitleDetails(1, MediaType.Movie)

        assertEquals(LocalDate.of(2021, 11, 24), details.releaseDate)
        assertEquals(null, details.releaseCountry)
    }

    @Test
    fun tvDetails_usesFirstAirDateAndEpisodeRuntime() = testScope.runTest {
        network.details = mapOf(
            1L to NetworkTitleDetails(
                id = 1,
                name = "Lucifer",
                firstAirDate = "2016-01-25",
                originCountry = listOf("US"),
                episodeRunTime = listOf(45),
            ),
        )

        val details = repository.getTitleDetails(1, MediaType.Tv)

        assertEquals("Lucifer", details.name)
        assertEquals(LocalDate.of(2016, 1, 25), details.releaseDate)
        assertEquals("US", details.releaseCountry)
        assertEquals(45, details.runtimeMinutes)
    }

    @Test
    fun details_missingFields_areNull() = testScope.runTest {
        network.details = mapOf(
            1L to NetworkTitleDetails(id = 1, title = "Unknown", overview = " ", runtime = 0, releaseDate = ""),
        )

        val details = repository.getTitleDetails(1, MediaType.Movie)

        assertEquals(null, details.releaseDate)
        assertEquals(null, details.releaseCountry)
        assertEquals(null, details.ratingPercent)
        assertEquals(null, details.runtimeMinutes)
        assertEquals(null, details.synopsis)
        assertEquals(null, details.artworkUrl)
        assertTrue(details.cast.isEmpty() && details.genres.isEmpty() && details.recommendations.isEmpty())
    }

    @Test
    fun details_castSortedAndLimitedToTen() = testScope.runTest {
        network.details = mapOf(
            1L to NetworkTitleDetails(
                id = 1,
                credits = NetworkCredits((12 downTo 1).map { NetworkCast("Actor $it", order = it) }),
            ),
        )

        val cast = repository.getTitleDetails(1, MediaType.Movie).cast

        assertEquals((1..10).map { "Actor $it" }, cast.map { it.name })
    }

    @Test
    fun trailer_picksOfficialYouTubeTrailer() = testScope.runTest {
        network.details = mapOf(
            1L to NetworkTitleDetails(
                id = 1,
                videos = NetworkVideos(
                    listOf(
                        NetworkVideo(key = "teaser", site = "YouTube", type = "Teaser", official = true),
                        NetworkVideo(key = "fan", site = "YouTube", type = "Trailer", official = false),
                        NetworkVideo(key = "vimeo", site = "Vimeo", type = "Trailer", official = true),
                        NetworkVideo(key = "official", site = "YouTube", type = "Trailer", official = true),
                    ),
                ),
            ),
        )

        val trailerUrl = repository.getTitleDetails(1, MediaType.Movie).trailerUrl

        assertEquals("https://www.youtube.com/watch?v=official", trailerUrl)
    }

    @Test
    fun trailer_noYouTubeTrailer_isNull() = testScope.runTest {
        network.details = mapOf(
            1L to NetworkTitleDetails(
                id = 1,
                videos = NetworkVideos(listOf(NetworkVideo(key = "clip", site = "YouTube", type = "Clip"))),
            ),
        )

        assertEquals(null, repository.getTitleDetails(1, MediaType.Movie).trailerUrl)
    }

    @Test
    fun search_keepsOnlyMoviesAndTv() = testScope.runTest {
        network.searchResults = listOf(
            NetworkTitle(id = 1, title = "Matrix", mediaType = "movie"),
            NetworkTitle(id = 2, name = "Keanu Reeves", mediaType = "person"),
            NetworkTitle(id = 3, name = "Matrix Reloaded Show", mediaType = "tv"),
        )

        val results = repository.search("matrix")

        assertEquals(
            listOf(Title(1, MediaType.Movie, "Matrix", null), Title(3, MediaType.Tv, "Matrix Reloaded Show", null)),
            results,
        )
    }

    @Test
    fun search_passesQuery() = testScope.runTest {
        repository.search("lucifer")

        assertEquals(listOf("lucifer"), network.searchQueries)
    }

    private fun releaseDates(country: String, vararg dates: NetworkReleaseDate) =
        NetworkReleaseDates(listOf(NetworkCountryReleases(country, dates.toList())))

    private fun networkTitle(id: Long, popularity: Double = 0.0, voteAverage: Double = 0.0) =
        NetworkTitle(id = id, title = "Title $id", popularity = popularity, voteAverage = voteAverage)
}
