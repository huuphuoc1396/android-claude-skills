package com.example.sample.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val titleRepository: TitleRepository,
) : ViewModel() {

    // The key is the property name of SearchDestination.
    private val query = MutableStateFlow<String>(checkNotNull(savedStateHandle["query"]))
    private val submittedQuery = MutableStateFlow(query.value)
    private val retryCount = MutableStateFlow(0)

    private val results = combine(submittedQuery, retryCount) { submitted, _ -> submitted }
        .flatMapLatest { submitted ->
            flow {
                emit(SearchResults.Loading)
                emit(search(submitted))
            }
        }

    val uiState: StateFlow<SearchUiState> = combine(query, results, ::SearchUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState(query.value, SearchResults.Loading),
        )

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    /** Runs a new search for the typed text; blank text is ignored. */
    fun submit() {
        val text = query.value.trim()
        if (text.isNotEmpty()) submittedQuery.value = text
    }

    fun retry() {
        retryCount.update { it + 1 }
    }

    private suspend fun search(text: String): SearchResults = try {
        val titles = titleRepository.search(text)
        if (titles.isEmpty()) SearchResults.NoResults else SearchResults.Found(titles)
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        SearchResults.Offline
    } catch (e: Exception) {
        SearchResults.Error
    }
}
