package com.example.sample.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.repository.FavoritesRepository
import com.example.sample.core.data.repository.TitleRepository
import com.example.sample.core.data.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val titleRepository: TitleRepository,
    private val favoritesRepository: FavoritesRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    // Keys are the property names of DetailsDestination.
    private val id: Long = checkNotNull(savedStateHandle["id"])
    private val mediaType: MediaType = MediaType.valueOf(checkNotNull(savedStateHandle["mediaType"]))

    private val refreshState = MutableStateFlow(RefreshState.Loading)

    /** The cached details, the favorite flag, and how the latest refresh went (AC-11). */
    val uiState: StateFlow<DetailsUiState> = combine(
        titleRepository.observeTitleDetails(id, mediaType),
        favoritesRepository.observeIsFavorite(id, mediaType),
        refreshState,
    ) { details, isFavorite, refresh ->
        when {
            details != null -> DetailsUiState.Success(
                details = details,
                isFavorite = isFavorite,
                offlineNotice = refresh == RefreshState.Offline,
                refreshFailed = refresh == RefreshState.Failed,
            )
            refresh == RefreshState.Loading || refresh == RefreshState.Done -> DetailsUiState.Loading
            else -> DetailsUiState.Error(offline = refresh == RefreshState.Offline)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailsUiState.Loading,
    )

    init {
        refresh()
        // EC-1, EC-2: retry when the connection returns instead of waiting for "Tentar novamente".
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online && refreshState.value == RefreshState.Offline) refresh()
            }
        }
    }

    /** Saves [favorite] as the new state; the screen sends the opposite of what it shows. */
    fun setFavorite(favorite: Boolean) {
        viewModelScope.launch { favoritesRepository.setFavorite(id, mediaType, favorite) }
    }

    fun retry() = refresh()

    private fun refresh() {
        viewModelScope.launch {
            refreshState.value = RefreshState.Loading
            refreshState.value = try {
                titleRepository.refreshTitleDetails(id, mediaType)
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

    private enum class RefreshState { Loading, Done, Offline, Failed }
}
