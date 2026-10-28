package com.example.sample.core.data.testdoubles

import com.example.sample.core.database.dao.CategoryTitleDao
import com.example.sample.core.database.model.CategoryTitleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCategoryTitleDao : CategoryTitleDao {
    private val rows = MutableStateFlow(emptyList<CategoryTitleEntity>())

    override fun observe(category: String): Flow<List<CategoryTitleEntity>> =
        rows.map { all -> all.filter { it.category == category }.sortedWith(compareBy({ it.section }, { it.position })) }

    override suspend fun deleteCategory(category: String) {
        rows.update { all -> all.filterNot { it.category == category } }
    }

    override suspend fun insert(rows: List<CategoryTitleEntity>) {
        this.rows.update { it + rows }
    }
}
