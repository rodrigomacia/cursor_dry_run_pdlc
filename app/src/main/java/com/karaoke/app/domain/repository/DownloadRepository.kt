package com.karaoke.app.domain.repository

import com.karaoke.app.domain.model.DownloadableSong
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    suspend fun searchSongs(query: String, page: Int = 1): List<DownloadableSong>
    suspend fun downloadSong(song: DownloadableSong): Flow<DownloadProgress>
    fun getDownloadingSongs(): Flow<List<String>>
}

data class DownloadProgress(
    val songId: String,
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val isComplete: Boolean = false,
    val error: String? = null
) {
    val progress: Float get() = if (totalBytes > 0) bytesDownloaded.toFloat() / totalBytes else 0f
}
