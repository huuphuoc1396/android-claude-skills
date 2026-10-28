package com.example.sample.core.data.repository

import com.example.sample.core.common.Dispatcher
import com.example.sample.core.common.SampleDispatchers
import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.data.model.TitleDetails
import com.example.sample.core.data.model.asExternalModel
import com.example.sample.core.data.model.asTmdbMediaType
import com.example.sample.core.data.model.toTmdbMediaType
import com.example.sample.core.network.TmdbDiscoverQuery
import com.example.sample.core.network.TmdbMediaType
import com.example.sample.core.network.TmdbNetworkDataSource
import com.example.sample.core.network.model.NetworkTitle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val SECTION_SIZE = 20
private const val ANIMATION_GENRE = 16
private const val SOAP_GENRE = 10766
private const val LATIN_AMERICA = "BR|MX|CO|AR|CL|PE|VE"

/** Network only for now; the database becomes the source of truth when caching is added. */
internal class DefaultTitleRepository @Inject constructor(
    private val network: TmdbNetworkDataSource,
    @Dispatcher(SampleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TitleRepository {

    override suspend fun getCategoryTitles(category: Category): CategoryTitles = withContext(ioDispatcher) {
        val rule = category.rule()
        val featured = async {
            fetch(rule, Section.Featured, minVoteCount = rule.featuredMinVotes)
        }
        val popular = async { fetch(rule, Section.Popular, minVoteCount = null) }
        CategoryTitles(featured = featured.await(), popular = popular.await())
    }

    override suspend fun search(query: String): List<Title> = withContext(ioDispatcher) {
        network.search(query).mapNotNull { result ->
            result.mediaType?.toTmdbMediaType()?.let { result.asExternalModel(it) }
        }
    }

    override suspend fun getTitleDetails(id: Long, mediaType: MediaType): TitleDetails = withContext(ioDispatcher) {
        val tmdbMediaType = mediaType.asTmdbMediaType()
        network.getDetails(tmdbMediaType, id).asExternalModel(tmdbMediaType, network.region)
    }

    /** Queries every media type of [rule] and merges them, best first by the section's ranking. */
    private suspend fun fetch(rule: CategoryRule, section: Section, minVoteCount: Int?): List<Title> =
        coroutineScope {
            rule.mediaTypes.map { mediaType ->
                async {
                    val query = TmdbDiscoverQuery(
                        mediaType = mediaType,
                        sortBy = section.sortBy,
                        genreId = rule.genreId,
                        originCountries = rule.originCountries,
                        minVoteCount = minVoteCount,
                    )
                    network.discover(query).map { it to mediaType }
                }
            }.awaitAll()
                .flatten()
                .sortedByDescending { (title, _) -> section.rank(title) }
                .take(SECTION_SIZE)
                .map { (title, mediaType) -> title.asExternalModel(mediaType) }
        }

    private enum class Section(val sortBy: String, val rank: (NetworkTitle) -> Double) {
        Featured("vote_average.desc", { it.voteAverage }),
        Popular("popularity.desc", { it.popularity }),
    }

    private class CategoryRule(
        val mediaTypes: List<TmdbMediaType>,
        val featuredMinVotes: Int,
        val genreId: Int? = null,
        val originCountries: String? = null,
    )

    private fun Category.rule() = when (this) {
        Category.Films -> CategoryRule(listOf(TmdbMediaType.Movie), featuredMinVotes = 1000)
        Category.Series -> CategoryRule(listOf(TmdbMediaType.Tv), featuredMinVotes = 500)
        Category.Anime -> CategoryRule(
            mediaTypes = listOf(TmdbMediaType.Movie, TmdbMediaType.Tv),
            featuredMinVotes = 200,
            genreId = ANIMATION_GENRE,
            originCountries = "JP",
        )
        Category.Novelas -> CategoryRule(
            mediaTypes = listOf(TmdbMediaType.Tv),
            featuredMinVotes = 20,
            genreId = SOAP_GENRE,
            originCountries = LATIN_AMERICA,
        )
    }
}
