package com.karaoke.app.domain.repository

import com.karaoke.app.domain.model.LyricLine

interface LyricsRepository {
    suspend fun getLyrics(songId: Long, title: String, artist: String, duration: Long): List<LyricLine>
    suspend fun saveLyrics(songId: Long, lyrics: List<LyricLine>)
    suspend fun hasCachedLyrics(songId: Long): Boolean
    suspend fun clearLyricsCache()
}
