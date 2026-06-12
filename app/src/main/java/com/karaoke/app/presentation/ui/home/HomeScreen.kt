package com.karaoke.app.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import com.karaoke.app.presentation.components.SongCard
import com.karaoke.app.presentation.theme.BackgroundDark
import com.karaoke.app.presentation.theme.BackgroundMedium
import com.karaoke.app.presentation.theme.KaraokePurple

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onSongClick: (Long) -> Unit,
    onNavigateToLibrary: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val storagePermission = rememberMultiplePermissionsState(
        permissions = buildList {
            add(android.Manifest.permission.READ_MEDIA_AUDIO)
        }
    )

    LaunchedEffect(storagePermission.allPermissionsGranted) {
        if (storagePermission.allPermissionsGranted) {
            viewModel.scanLocalSongs()
        }
    }

    uiState.scanMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(3000)
            viewModel.dismissScanMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundMedium, BackgroundDark)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                HomeHero(
                    onStartSinging = {
                        if (uiState.allSongs.isNotEmpty()) {
                            onSongClick(uiState.allSongs.first().id)
                        } else if (!storagePermission.allPermissionsGranted) {
                            storagePermission.launchMultiplePermissionRequest()
                        } else {
                            viewModel.scanLocalSongs()
                        }
                    },
                    onScanSongs = {
                        if (storagePermission.allPermissionsGranted) {
                            viewModel.scanLocalSongs()
                        } else {
                            storagePermission.launchMultiplePermissionRequest()
                        }
                    },
                    isLoading = uiState.isLoading
                )
            }

            if (uiState.scanMessage != null) {
                item {
                    Snackbar(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(uiState.scanMessage ?: "")
                    }
                }
            }

            if (uiState.recentSongs.isNotEmpty()) {
                item {
                    SectionHeader(title = "Tocadas recentemente") { onNavigateToLibrary() }
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.recentSongs.take(10)) { song ->
                            SongCard(
                                song = song,
                                onClick = { onSongClick(song.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(song.id) },
                                compact = true
                            )
                        }
                    }
                }
            }

            if (uiState.allSongs.isNotEmpty()) {
                item {
                    SectionHeader(title = "Todas as músicas") { onNavigateToLibrary() }
                }
                items(uiState.allSongs.take(20)) { song ->
                    SongCard(
                        song = song,
                        onClick = { onSongClick(song.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(song.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                if (uiState.allSongs.size > 20) {
                    item {
                        TextButton(
                            onClick = onNavigateToLibrary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Ver todas as ${uiState.allSongs.size} músicas")
                        }
                    }
                }
            }

            if (uiState.allSongs.isEmpty() && !uiState.isLoading) {
                item { EmptyState(onScanSongs = {
                    if (storagePermission.allPermissionsGranted) viewModel.scanLocalSongs()
                    else storagePermission.launchMultiplePermissionRequest()
                }) }
            }
        }
    }
}

@Composable
private fun HomeHero(
    onStartSinging: () -> Unit,
    onScanSongs: () -> Unit,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(KaraokePurple.copy(alpha = 0.6f), Color.Transparent),
                    radius = 600f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.MicNone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "KaraokeApp",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Cante com emoção",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onStartSinging,
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.MicNone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cantar agora")
                }
                OutlinedButton(onClick = onScanSongs, enabled = !isLoading) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    else {
                        Icon(Icons.Default.QueueMusic, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Escanear")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onViewAll) {
            Text("Ver tudo", fontSize = 12.sp)
        }
    }
}

@Composable
private fun EmptyState(onScanSongs: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.QueueMusic,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Nenhuma música encontrada",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = "Escaneie suas músicas locais ou baixe músicas gratuitas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onScanSongs) {
            Text("Escanear músicas")
        }
    }
}
