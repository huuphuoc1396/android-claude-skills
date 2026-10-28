package com.example.sample.core.data.testdoubles

import com.example.sample.core.database.dao.TitleDetailsDao
import com.example.sample.core.database.model.TitleDetailsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeTitleDetailsDao : TitleDetailsDao {
    private val details = MutableStateFlow(emptyMap<Pair<Long, String>, TitleDetailsEntity>())

    override fun observe(id: Long, mediaType: String): Flow<TitleDetailsEntity?> = details.map { it[id to mediaType] }

    override suspend fun upsert(details: TitleDetailsEntity) {
        this.details.update { it + ((details.id to details.mediaType) to details) }
    }
}
