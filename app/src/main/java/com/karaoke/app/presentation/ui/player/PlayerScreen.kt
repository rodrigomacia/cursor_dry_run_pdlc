package com.karaoke.app.presentation.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.karaoke.app.presentation.theme.BackgroundDark
import com.karaoke.app.presentation.theme.BackgroundMedium
import com.karaoke.app.presentation.ui.player.components.LyricsDisplay
import com.karaoke.app.presentation.ui.player.components.PlayerControls
import com.karaoke.app.presentation.ui.player.components.ScoreCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PlayerScreen(
    songId: Long,
    onNavigateBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val micPermission = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(songId) {
        viewModel.loadSong(songId)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.player.pause() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundMedium, BackgroundDark, Color.Black)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.song?.title ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White
                        )
                        Text(
                            text = uiState.song?.artist ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        uiState.song?.let { /* toggle favorite */ }
                    }) {
                        Icon(
                            imageVector = if (uiState.song?.isFavorite == true)
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (uiState.song?.isFavorite == true) Color.Red else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )

            // Lyrics area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (uiState.isLoadingLyrics) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text("Buscando letra…", color = Color.White.copy(alpha = 0.6f))
                    }
                } else {
                    LyricsDisplay(
                        lyrics = uiState.lyrics,
                        currentIndex = uiState.currentLyricIndex,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Player controls at bottom
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                PlayerControls(
                    isPlaying = uiState.isPlaying,
                    currentPositionMs = uiState.currentPositionMs,
                    durationMs = uiState.durationMs,
                    isMicEnabled = uiState.isMicEnabled,
                    currentPitch = uiState.currentPitch,
                    onPlayPause = {
                        if (uiState.isPlaying) viewModel.pause() else viewModel.play()
                    },
                    onSeek = viewModel::seekTo,
                    onToggleMic = {
                        if (!micPermission.status.isGranted) {
                            micPermission.launchPermissionRequest()
                        } else {
                            viewModel.toggleMicrophone()
                        }
                    }
                )
            }
        }

        // Score card overlay
        if (uiState.showScoreCard && uiState.score != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                ScoreCard(
                    score = uiState.score!!,
                    onDismiss = viewModel::dismissScoreCard
                )
            }
        }
    }
}
