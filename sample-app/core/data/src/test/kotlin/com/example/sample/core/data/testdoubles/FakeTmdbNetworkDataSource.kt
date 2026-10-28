package com.example.sample.core.data.testdoubles

import com.example.sample.core.network.TmdbDiscoverQuery
import com.example.sample.core.network.TmdbMediaType
import com.example.sample.core.network.TmdbNetworkDataSource
import com.example.sample.core.network.model.NetworkTitle
import com.example.sample.core.network.model.NetworkTitleDetails
import java.io.IOException

class FakeTmdbNetworkDataSource : TmdbNetworkDataSource {
    override var region: String = "US"
    var details: Map<Long, NetworkTitleDetails> = emptyMap()
    var results: Map<TmdbMediaType, List<NetworkTitle>> = emptyMap()
    var shouldFail = false
    val queries = mutableListOf<TmdbDiscoverQuery>()

    override suspend fun discover(query: TmdbDiscoverQuery): List<NetworkTitle> {
        queries += query
        if (shouldFail) throw IOException("Network error")
        return results[query.mediaType].orEmpty()
    }

    var searchResults: List<NetworkTitle> = emptyList()
    val searchQueries = mutableListOf<String>()

    override suspend fun search(query: String): List<NetworkTitle> {
        searchQueries += query
        if (shouldFail) throw IOException("Network error")
        return searchResults
    }

    override suspend fun getDetails(mediaType: TmdbMediaType, id: Long): NetworkTitleDetails {
        if (shouldFail) throw IOException("Network error")
        return details.getValue(id)
    }
}
