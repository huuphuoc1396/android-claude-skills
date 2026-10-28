package com.example.sample.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.core.data.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    private val refreshState = MutableStateFlow(RefreshState.Loading)

    val uiState: StateFlow<NewsUiState> =
        combine(articleRepository.observeArticles(), refreshState) { articles, refresh ->
            when {
                articles.isNotEmpty() -> NewsUiState.Success(articles)
                refresh == RefreshState.Failed -> NewsUiState.Error
                refresh == RefreshState.Done -> NewsUiState.Empty
                else -> NewsUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NewsUiState.Loading,
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshState.value = RefreshState.Loading
            refreshState.value = try {
                articleRepository.refresh()
                RefreshState.Done
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                RefreshState.Failed
            }
        }
    }

    private enum class RefreshState { Loading, Done, Failed }
}
