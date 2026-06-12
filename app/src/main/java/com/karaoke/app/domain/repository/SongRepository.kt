package com.karaoke.app.domain.repository

import com.karaoke.app.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getFavoriteSongs(): Flow<List<Song>>
    fun getRecentSongs(limit: Int = 10): Flow<List<Song>>
    suspend fun getSongById(id: Long): Song?
    suspend fun searchSongs(query: String): List<Song>
    suspend fun toggleFavorite(songId: Long)
    suspend fun incrementPlayCount(songId: Long)
    suspend fun scanLocalSongs(): Int
    suspend fun deleteSong(songId: Long)
}
