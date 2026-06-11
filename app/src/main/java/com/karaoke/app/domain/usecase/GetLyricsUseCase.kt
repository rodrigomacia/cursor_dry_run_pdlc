package com.karaoke.app.domain.usecase

import com.karaoke.app.domain.model.LyricLine
import com.karaoke.app.domain.repository.LyricsRepository
import javax.inject.Inject

class GetLyricsUseCase @Inject constructor(
    private val repository: LyricsRepository
) {
    suspend operator fun invoke(
        songId: Long,
        title: String,
        artist: String,
        duration: Long
    ): List<LyricLine> = repository.getLyrics(songId, title, artist, duration)
}
