package com.karaoke.app.presentation.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaoke.app.domain.model.Song
import com.karaoke.app.domain.usecase.GetAllSongsUseCase
import com.karaoke.app.domain.usecase.GetFavoriteSongsUseCase
import com.karaoke.app.domain.usecase.SearchSongsUseCase
import com.karaoke.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LibraryTab { ALL, FAVORITES }
enum class SortOrder { TITLE, ARTIST, ALBUM, RECENT }

data class LibraryUiState(
    val activeTab: LibraryTab = LibraryTab.ALL,
    val sortOrder: SortOrder = SortOrder.TITLE,
    val searchQuery: String = "",
    val songs: List<Song> = emptyList(),
    val favoriteSongs: List<Song> = emptyList(),
    val searchResults: List<Song> = emptyList(),
    val isSearching: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val getFavoriteSongsUseCase: GetFavoriteSongsUseCase,
    private val searchSongsUseCase: SearchSongsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        collectSongs()
        collectSearchResults()
    }

    private fun collectSongs() {
        viewModelScope.launch {
            getAllSongsUseCase().collect { songs ->
                _uiState.update { it.copy(songs = sortSongs(songs, _uiState.value.sortOrder)) }
            }
        }
        viewModelScope.launch {
            getFavoriteSongsUseCase().collect { favorites ->
                _uiState.update { it.copy(favoriteSongs = favorites) }
            }
        }
    }

    private fun collectSearchResults() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
                    } else {
                        _uiState.update { it.copy(isSearching = true) }
                        val results = searchSongsUseCase(query)
                        _uiState.update { it.copy(searchResults = results, isSearching = false) }
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun setActiveTab(tab: LibraryTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun setSortOrder(order: SortOrder) {
        _uiState.update { state ->
            state.copy(
                sortOrder = order,
                songs = sortSongs(state.songs, order)
            )
        }
    }

    fun toggleFavorite(songId: Long) {
        viewModelScope.launch { toggleFavoriteUseCase(songId) }
    }

    private fun sortSongs(songs: List<Song>, order: SortOrder): List<Song> = when (order) {
        SortOrder.TITLE -> songs.sortedBy { it.title }
        SortOrder.ARTIST -> songs.sortedBy { it.artist }
        SortOrder.ALBUM -> songs.sortedBy { it.album }
        SortOrder.RECENT -> songs.sortedByDescending { it.addedDate }
    }
}
