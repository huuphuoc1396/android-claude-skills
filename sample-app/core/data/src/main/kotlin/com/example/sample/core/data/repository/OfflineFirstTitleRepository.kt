package com.example.sample.core.data.repository

import com.example.sample.core.common.Dispatcher
import com.example.sample.core.common.SampleDispatchers
import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.data.model.TitleDetails
import com.example.sample.core.data.model.asCategoryEntity
import com.example.sample.core.data.model.asEntity
import com.example.sample.core.data.model.asExternalModel
import com.example.sample.core.data.model.asTmdbMediaType
import com.example.sample.core.data.model.toTmdbMediaType
import com.example.sample.core.database.dao.CategoryTitleDao
import com.example.sample.core.database.dao.TitleDetailsDao
import com.example.sample.core.network.TmdbDiscoverQuery
import com.example.sample.core.network.TmdbMediaType
import com.example.sample.core.network.TmdbNetworkDataSource
import com.example.sample.core.network.model.NetworkTitle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val SECTION_SIZE = 20
private const val ANIMATION_GENRE = 16
private const val SOAP_GENRE = 10766
private const val LATIN_AMERICA = "BR|MX|CO|AR|CL|PE|VE"

private const val FEATURED = "featured"
private const val POPULAR = "popular"

/** The database is the source of truth; the network only refreshes it. Search is network only. */
internal class OfflineFirstTitleRepository @Inject constructor(
    private val categoryTitleDao: CategoryTitleDao,
    private val titleDetailsDao: TitleDetailsDao,
    private val network: TmdbNetworkDataSource,
    @Dispatcher(SampleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TitleRepository {

    override fun observeCategoryTitles(category: Category): Flow<CategoryTitles> =
        categoryTitleDao.observe(category.name).map { rows ->
            CategoryTitles(
                featured = rows.filter { it.section == FEATURED }.map { it.asExternalModel() },
                popular = rows.filter { it.section == POPULAR }.map { it.asExternalModel() },
            )
        }

    override suspend fun refreshCategory(category: Category) = withContext(ioDispatcher) {
        val rule = category.rule()
        val featured = async {
            fetch(rule, Section.Featured, minVoteCount = rule.featuredMinVotes)
        }
        val popular = async { fetch(rule, Section.Popular, minVoteCount = null) }
        val rows = featured.await().mapIndexed { position, title -> title.asCategoryEntity(category.name, FEATURED, position) } +
            popular.await().mapIndexed { position, title -> title.asCategoryEntity(category.name, POPULAR, position) }
        categoryTitleDao.replaceCategory(category.name, rows)
    }

    override suspend fun search(query: String): List<Title> = withContext(ioDispatcher) {
        network.search(query).mapNotNull { result ->
            result.mediaType?.toTmdbMediaType()?.let { result.asExternalModel(it) }
        }
    }

    override fun observeTitleDetails(id: Long, mediaType: MediaType): Flow<TitleDetails?> =
        titleDetailsDao.observe(id, mediaType.name).map { it?.asExternalModel() }

    override suspend fun refreshTitleDetails(id: Long, mediaType: MediaType) = withContext(ioDispatcher) {
        val tmdbMediaType = mediaType.asTmdbMediaType()
        val details = network.getDetails(tmdbMediaType, id).asExternalModel(tmdbMediaType, network.region)
        titleDetailsDao.upsert(details.asEntity())
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
