package com.karaoke.app.presentation.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karaoke.app.presentation.components.SongCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onSongClick: (Long) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Biblioteca") },
            actions = {
                Box {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Ordenar")
                    }
                    DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Por título") },
                            onClick = { viewModel.setSortOrder(SortOrder.TITLE); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Por artista") },
                            onClick = { viewModel.setSortOrder(SortOrder.ARTIST); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Por álbum") },
                            onClick = { viewModel.setSortOrder(SortOrder.ALBUM); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Mais recentes") },
                            onClick = { viewModel.setSortOrder(SortOrder.RECENT); showSortMenu = false }
                        )
                    }
                }
            }
        )

        // Search bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar músicas, artistas…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpar")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        // Tab row
        TabRow(selectedTabIndex = uiState.activeTab.ordinal) {
            Tab(
                selected = uiState.activeTab == LibraryTab.ALL,
                onClick = { viewModel.setActiveTab(LibraryTab.ALL) },
                text = { Text("Todas (${uiState.songs.size})") }
            )
            Tab(
                selected = uiState.activeTab == LibraryTab.FAVORITES,
                onClick = { viewModel.setActiveTab(LibraryTab.FAVORITES) },
                text = { Text("Favoritas (${uiState.favoriteSongs.size})") }
            )
        }

        val displaySongs = when {
            uiState.searchQuery.isNotEmpty() -> uiState.searchResults
            uiState.activeTab == LibraryTab.FAVORITES -> uiState.favoriteSongs
            else -> uiState.songs
        }

        if (uiState.isSearching) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (displaySongs.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.searchQuery.isNotEmpty()) "Nenhum resultado para \"${uiState.searchQuery}\""
                    else "Nenhuma música aqui ainda",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(displaySongs, key = { it.id }) { song ->
                    SongCard(
                        song = song,
                        onClick = { onSongClick(song.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(song.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
