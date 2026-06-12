package com.karaoke.app.presentation.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaoke.app.domain.model.DownloadableSong
import com.karaoke.app.domain.model.DownloadStatus
import com.karaoke.app.domain.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadUiState(
    val searchQuery: String = "",
    val songs: List<DownloadableSong> = emptyList(),
    val downloadProgress: Map<String, Float> = emptyMap(),
    val downloadStatuses: Map<String, DownloadStatus> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadUiState())
    val uiState: StateFlow<DownloadUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadPopularSongs()
        observeSearchQuery()
    }

    private fun loadPopularSongs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val songs = downloadRepository.searchSongs("")
            _uiState.update { it.copy(songs = songs, isLoading = false) }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update { it.copy(isLoading = true) }
                    val songs = downloadRepository.searchSongs(query)
                    _uiState.update { it.copy(songs = songs, isLoading = false) }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun downloadSong(song: DownloadableSong) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    downloadStatuses = state.downloadStatuses + (song.id to DownloadStatus.DOWNLOADING)
                )
            }
            downloadRepository.downloadSong(song).collect { progress ->
                _uiState.update { state ->
                    val newProgress = state.downloadProgress + (song.id to progress.progress)
                    val newStatus = when {
                        progress.error != null -> DownloadStatus.FAILED
                        progress.isComplete -> DownloadStatus.DOWNLOADED
                        else -> DownloadStatus.DOWNLOADING
                    }
                    state.copy(
                        downloadProgress = newProgress,
                        downloadStatuses = state.downloadStatuses + (song.id to newStatus)
                    )
                }
            }
        }
    }
}
