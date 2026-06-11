package com.karaoke.app.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaoke.app.domain.model.Song
import com.karaoke.app.domain.usecase.GetAllSongsUseCase
import com.karaoke.app.domain.usecase.GetRecentSongsUseCase
import com.karaoke.app.domain.usecase.ScanLocalSongsUseCase
import com.karaoke.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val recentSongs: List<Song> = emptyList(),
    val allSongs: List<Song> = emptyList(),
    val scanMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val getRecentSongsUseCase: GetRecentSongsUseCase,
    private val scanLocalSongsUseCase: ScanLocalSongsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        collectSongs()
    }

    private fun collectSongs() {
        viewModelScope.launch {
            getRecentSongsUseCase(10).collect { recent ->
                _uiState.value = _uiState.value.copy(recentSongs = recent)
            }
        }
        viewModelScope.launch {
            getAllSongsUseCase().collect { all ->
                _uiState.value = _uiState.value.copy(allSongs = all)
            }
        }
    }

    fun scanLocalSongs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val count = scanLocalSongsUseCase()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    scanMessage = "$count música(s) encontrada(s)"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(songId)
        }
    }

    fun dismissScanMessage() {
        _uiState.value = _uiState.value.copy(scanMessage = null)
    }
}
