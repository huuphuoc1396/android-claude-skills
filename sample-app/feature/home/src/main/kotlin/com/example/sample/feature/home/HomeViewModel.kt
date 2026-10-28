package com.example.sample.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.repository.TitleRepository
import com.example.sample.core.data.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val titleRepository: TitleRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    /** The search text, kept while results are open and across process death. */
    val query: StateFlow<String> = savedStateHandle.getStateFlow(QUERY_KEY, "")

    /** The selected tab, kept across rotation and process death (EC-7). */
    private val selectedCategory = savedStateHandle.getStateFlow(CATEGORY_KEY, Category.Films)
    private val refreshState = MutableStateFlow(RefreshState.Loading)
    private var refreshJob: Job? = null

    /** The cache of the selected category, plus how its latest refresh went (AC-11). */
    val uiState: StateFlow<HomeUiState> = selectedCategory
        .flatMapLatest { category ->
            combine(titleRepository.observeCategoryTitles(category), refreshState) { titles, refresh ->
                HomeUiState(category, content(titles, refresh))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(selectedCategory.value, HomeContent.Loading),
        )

    init {
        refresh()
        // EC-1: the offline notice goes away once fresh content loads, so retry when the connection returns.
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online && refreshState.value == RefreshState.Offline) refresh()
            }
        }
    }

    fun onQueryChange(query: String) {
        savedStateHandle[QUERY_KEY] = query
    }

    fun selectCategory(category: Category) {
        savedStateHandle[CATEGORY_KEY] = category
        refresh()
    }

    fun retry() = refresh()

    /** Refreshes the selected category; a newer refresh cancels an older one so states never mix. */
    private fun refresh() {
        val category = selectedCategory.value
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            refreshState.value = RefreshState.Loading
            refreshState.value = try {
                titleRepository.refreshCategory(category)
                RefreshState.Done
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                RefreshState.Offline
            } catch (e: Exception) {
                RefreshState.Failed
            }
        }
    }

    private fun content(titles: CategoryTitles, refresh: RefreshState): HomeContent = when {
        titles.featured.isNotEmpty() || titles.popular.isNotEmpty() -> HomeContent.Success(
            featured = titles.featured,
            popular = titles.popular,
            offlineNotice = refresh == RefreshState.Offline,
            refreshFailed = refresh == RefreshState.Failed,
        )
        refresh == RefreshState.Loading -> HomeContent.Loading
        refresh == RefreshState.Done -> HomeContent.Empty
        else -> HomeContent.Error(offline = refresh == RefreshState.Offline)
    }

    private enum class RefreshState { Loading, Done, Offline, Failed }

    private companion object {
        const val QUERY_KEY = "query"
        const val CATEGORY_KEY = "category"
    }
}
