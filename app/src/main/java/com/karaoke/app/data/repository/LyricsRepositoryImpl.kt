package com.karaoke.app.data.repository

import com.karaoke.app.data.local.dao.LyricsDao
import com.karaoke.app.data.local.entity.LyricsEntity
import com.karaoke.app.data.remote.api.LrcLibApi
import com.karaoke.app.domain.model.LyricLine
import com.karaoke.app.domain.repository.LyricsRepository
import com.karaoke.app.util.LrcParser
import javax.inject.Inject

class LyricsRepositoryImpl @Inject constructor(
    private val lrcLibApi: LrcLibApi,
    private val lyricsDao: LyricsDao
) : LyricsRepository {

    override suspend fun getLyrics(
        songId: Long,
        title: String,
        artist: String,
        duration: Long
    ): List<LyricLine> {
        // 1. Check local cache first
        val cached = lyricsDao.getLyricsBySongId(songId)
        if (cached != null) {
            return LrcParser.parse(cached.lrcContent)
        }

        // 2. Fetch from LRCLib API
        return try {
            val durationSeconds = (duration / 1000).toInt()
            val response = lrcLibApi.getLyrics(
                trackName = title,
                artistName = artist,
                duration = if (durationSeconds > 0) durationSeconds else null
            )

            val syncedLyrics = response?.syncedLyrics
            if (!syncedLyrics.isNullOrBlank()) {
                lyricsDao.insertLyrics(
                    LyricsEntity(songId = songId, lrcContent = syncedLyrics)
                )
                LrcParser.parse(syncedLyrics)
            } else {
                // Fallback to plain lyrics if synced not available
                val plainLyrics = response?.plainLyrics
                if (!plainLyrics.isNullOrBlank()) {
                    val simpleLrc = plainLyrics.lines().mapIndexed { index, line ->
                        "[${formatTime(index * 3000L)}]$line"
                    }.joinToString("\n")
                    LrcParser.parse(simpleLrc)
                } else emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun saveLyrics(songId: Long, lyrics: List<LyricLine>) {
        val lrcContent = LrcParser.toLrc(lyrics)
        lyricsDao.insertLyrics(LyricsEntity(songId = songId, lrcContent = lrcContent))
    }

    override suspend fun hasCachedLyrics(songId: Long): Boolean =
        lyricsDao.hasCachedLyrics(songId)

    override suspend fun clearLyricsCache() =
        lyricsDao.clearAll()

    private fun formatTime(ms: Long): String {
        val minutes = ms / 60_000
        val seconds = (ms % 60_000) / 1_000
        val centis = (ms % 1_000) / 10
        return "%02d:%02d.%02d".format(minutes, seconds, centis)
    }
}
