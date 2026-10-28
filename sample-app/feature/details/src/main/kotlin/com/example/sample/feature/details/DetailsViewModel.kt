package com.example.sample.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.repository.FavoritesRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val titleRepository: TitleRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    // Keys are the property names of DetailsDestination.
    private val id: Long = checkNotNull(savedStateHandle["id"])
    private val mediaType: MediaType = MediaType.valueOf(checkNotNull(savedStateHandle["mediaType"]))

    private val retryCount = MutableStateFlow(0)

    private val loadState = retryCount.flatMapLatest {
        flow {
            emit(DetailsUiState.Loading)
            emit(load())
        }
    }

    val uiState: StateFlow<DetailsUiState> =
        combine(loadState, favoritesRepository.observeIsFavorite(id, mediaType)) { state, isFavorite ->
            if (state is DetailsUiState.Success) state.copy(isFavorite = isFavorite) else state
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailsUiState.Loading,
        )

    /** Saves [favorite] as the new state; the screen sends the opposite of what it shows. */
    fun setFavorite(favorite: Boolean) {
        viewModelScope.launch { favoritesRepository.setFavorite(id, mediaType, favorite) }
    }

    fun retry() {
        retryCount.update { it + 1 }
    }

    private suspend fun load(): DetailsUiState = try {
        DetailsUiState.Success(titleRepository.getTitleDetails(id, mediaType), isFavorite = false)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        DetailsUiState.Error
    }
}
