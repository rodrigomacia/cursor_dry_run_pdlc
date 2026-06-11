package com.karaoke.app.presentation.ui.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.karaoke.app.domain.model.LyricLine
import com.karaoke.app.domain.model.PitchSample
import com.karaoke.app.domain.model.Song
import com.karaoke.app.domain.model.SongScore
import com.karaoke.app.domain.repository.SongRepository
import com.karaoke.app.domain.usecase.CalculateScoreUseCase
import com.karaoke.app.domain.usecase.GetLyricsUseCase
import com.karaoke.app.util.PitchDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val song: Song? = null,
    val lyrics: List<LyricLine> = emptyList(),
    val currentLyricIndex: Int = -1,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isLoadingLyrics: Boolean = false,
    val isMicEnabled: Boolean = false,
    val currentPitch: Float = -1f,
    val score: SongScore? = null,
    val showScoreCard: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songRepository: SongRepository,
    private val getLyricsUseCase: GetLyricsUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase,
    private val pitchDetector: PitchDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) startPositionTracking() else stopPositionTracking()
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    finalizeSong()
                }
            }
        })
    }

    private var positionJob: Job? = null
    private var micJob: Job? = null
    private val pitchSamples = mutableListOf<PitchSample>()

    fun loadSong(songId: Long) {
        viewModelScope.launch {
            val song = songRepository.getSongById(songId) ?: return@launch
            _uiState.update { it.copy(song = song, isLoadingLyrics = true) }

            val mediaItem = MediaItem.fromUri(song.uri)
            player.setMediaItem(mediaItem)
            player.prepare()

            fetchLyrics(song)
        }
    }

    private suspend fun fetchLyrics(song: Song) {
        try {
            val lyrics = getLyricsUseCase(song.id, song.title, song.artist, song.duration)
            _uiState.update { it.copy(lyrics = lyrics, isLoadingLyrics = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoadingLyrics = false, error = "Letra não encontrada") }
        }
    }

    fun play() = player.play()
    fun pause() = player.pause()

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        updateLyricIndex(positionMs)
    }

    fun toggleMicrophone() {
        val enabled = !_uiState.value.isMicEnabled
        _uiState.update { it.copy(isMicEnabled = enabled) }
        if (enabled) startMicCapture() else stopMicCapture()
    }

    private fun startPositionTracking() {
        positionJob?.cancel()
        positionJob = viewModelScope.launch {
            while (true) {
                val pos = player.currentPosition
                val dur = player.duration.coerceAtLeast(0L)
                _uiState.update { it.copy(currentPositionMs = pos, durationMs = dur) }
                updateLyricIndex(pos)
                delay(100)
            }
        }
    }

    private fun stopPositionTracking() {
        positionJob?.cancel()
    }

    private fun updateLyricIndex(positionMs: Long) {
        val lyrics = _uiState.value.lyrics
        if (lyrics.isEmpty()) return

        val index = lyrics.indexOfLast { it.startTimeMs <= positionMs }
        if (index != _uiState.value.currentLyricIndex) {
            _uiState.update { it.copy(currentLyricIndex = index) }
        }
    }

    private fun startMicCapture() {
        micJob?.cancel()
        micJob = viewModelScope.launch {
            pitchDetector.startDetection().collect { pitch ->
                _uiState.update { it.copy(currentPitch = pitch) }
                if (pitch > 0 && _uiState.value.isPlaying) {
                    val currentIndex = _uiState.value.currentLyricIndex
                    val expectedPitch = if (currentIndex >= 0) estimateExpectedPitch(currentIndex) else -1f
                    pitchSamples.add(
                        PitchSample(
                            timestamp = System.currentTimeMillis(),
                            detectedPitch = pitch,
                            expectedPitch = expectedPitch
                        )
                    )
                }
            }
        }
    }

    private fun stopMicCapture() {
        micJob?.cancel()
        _uiState.update { it.copy(currentPitch = -1f) }
    }

    private fun estimateExpectedPitch(lyricIndex: Int): Float {
        // Simplified: return a reference pitch based on lyric index position
        // In a real karaoke app this would come from UltraStar note data
        return 220f + (lyricIndex % 12) * 20f
    }

    private fun finalizeSong() {
        stopMicCapture()
        if (pitchSamples.isNotEmpty()) {
            val songId = _uiState.value.song?.id ?: return
            val score = calculateScoreUseCase(songId, pitchSamples)
            _uiState.update { it.copy(score = score, showScoreCard = true) }
        }
    }

    fun dismissScoreCard() {
        _uiState.update { it.copy(showScoreCard = false) }
        pitchSamples.clear()
    }

    override fun onCleared() {
        stopPositionTracking()
        stopMicCapture()
        player.release()
        super.onCleared()
    }
}
