package com.example.sample.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.repository.TitleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val titleRepository: TitleRepository,
) : ViewModel() {

    // Keys are the property names of DetailsDestination.
    private val id: Long = checkNotNull(savedStateHandle["id"])
    private val mediaType: MediaType = MediaType.valueOf(checkNotNull(savedStateHandle["mediaType"]))

    private val retryCount = MutableStateFlow(0)

    val uiState: StateFlow<DetailsUiState> = retryCount
        .flatMapLatest {
            flow {
                emit(DetailsUiState.Loading)
                emit(load())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailsUiState.Loading,
        )

    fun retry() {
        retryCount.update { it + 1 }
    }

    private suspend fun load(): DetailsUiState = try {
        DetailsUiState.Success(titleRepository.getTitleDetails(id, mediaType))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        DetailsUiState.Error
    }
}
