package com.example.sample.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.core.data.model.Category
import com.example.sample.core.data.repository.TitleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val titleRepository: TitleRepository,
) : ViewModel() {

    private val selectedCategory = MutableStateFlow(Category.Films)
    private val retryCount = MutableStateFlow(0)

    val uiState: StateFlow<HomeUiState> =
        combine(selectedCategory, retryCount) { category, _ -> category }
            .flatMapLatest { category ->
                flow {
                    emit(HomeUiState(category, HomeContent.Loading))
                    emit(HomeUiState(category, load(category)))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState(Category.Films, HomeContent.Loading),
            )

    fun selectCategory(category: Category) {
        selectedCategory.value = category
    }

    fun retry() {
        retryCount.update { it + 1 }
    }

    private suspend fun load(category: Category): HomeContent = try {
        val titles = titleRepository.getCategoryTitles(category)
        HomeContent.Success(featured = titles.featured, popular = titles.popular)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        HomeContent.Error
    }
}
