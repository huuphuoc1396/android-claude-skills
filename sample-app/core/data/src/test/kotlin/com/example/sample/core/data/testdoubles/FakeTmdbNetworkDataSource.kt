package com.example.sample.core.data.testdoubles

import com.example.sample.core.network.TmdbDiscoverQuery
import com.example.sample.core.network.TmdbMediaType
import com.example.sample.core.network.TmdbNetworkDataSource
import com.example.sample.core.network.model.NetworkTitle
import java.io.IOException

class FakeTmdbNetworkDataSource : TmdbNetworkDataSource {
    var results: Map<TmdbMediaType, List<NetworkTitle>> = emptyMap()
    var shouldFail = false
    val queries = mutableListOf<TmdbDiscoverQuery>()

    override suspend fun discover(query: TmdbDiscoverQuery): List<NetworkTitle> {
        queries += query
        if (shouldFail) throw IOException("Network error")
        return results[query.mediaType].orEmpty()
    }
}
