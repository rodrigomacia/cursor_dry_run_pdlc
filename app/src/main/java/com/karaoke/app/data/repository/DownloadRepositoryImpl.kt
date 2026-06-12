package com.karaoke.app.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.karaoke.app.data.remote.api.JamendoApi
import com.karaoke.app.domain.model.DownloadableSong
import com.karaoke.app.domain.model.SongSource
import com.karaoke.app.domain.repository.DownloadProgress
import com.karaoke.app.domain.repository.DownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val jamendoApi: JamendoApi,
    private val okHttpClient: OkHttpClient
) : DownloadRepository {

    private val _downloadingSongs = MutableStateFlow<List<String>>(emptyList())

    override suspend fun searchSongs(query: String, page: Int): List<DownloadableSong> {
        return try {
            val offset = (page - 1) * 20
            val response = if (query.isBlank()) {
                jamendoApi.getPopularTracks()
            } else {
                jamendoApi.searchTracks(query = query, offset = offset)
            }
            response.results?.mapNotNull { track ->
                if (track.id == null || track.audio == null) return@mapNotNull null
                DownloadableSong(
                    id = track.id,
                    title = track.name ?: "Unknown",
                    artist = track.artistName ?: "Unknown Artist",
                    album = track.albumName ?: "",
                    duration = track.duration ?: 0,
                    audioUrl = track.audiodownload ?: track.audio,
                    imageUrl = track.image,
                    license = "CC",
                    source = SongSource.JAMENDO
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun downloadSong(song: DownloadableSong): Flow<DownloadProgress> = flow {
        _downloadingSongs.value = _downloadingSongs.value + song.id

        try {
            val request = Request.Builder().url(song.audioUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                emit(DownloadProgress(song.id, 0, 0, error = "HTTP ${response.code}"))
                return@flow
            }

            val body = response.body ?: run {
                emit(DownloadProgress(song.id, 0, 0, error = "Empty response"))
                return@flow
            }

            val totalBytes = body.contentLength()
            val fileName = "${song.artist} - ${song.title}.mp3"
                .replace(Regex("[/\\\\:*?\"<>|]"), "_")

            val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
                    put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/KaraokeApp")
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values
                )
                uri?.let { context.contentResolver.openOutputStream(it) }
            } else {
                // API 26-28: write to app-specific external files directory
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                    ?: context.filesDir
                dir.mkdirs()
                java.io.FileOutputStream(java.io.File(dir, fileName))
            }

            if (outputStream == null) {
                emit(DownloadProgress(song.id, 0, 0, error = "Cannot open output stream"))
                return@flow
            }

            var bytesDownloaded = 0L
            val buffer = ByteArray(8192)
            body.byteStream().use { input ->
                outputStream.use { output ->
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        bytesDownloaded += bytes
                        emit(DownloadProgress(song.id, bytesDownloaded, totalBytes))
                        bytes = input.read(buffer)
                    }
                }
            }

            emit(DownloadProgress(song.id, bytesDownloaded, totalBytes, isComplete = true))
        } catch (e: Exception) {
            emit(DownloadProgress(song.id, 0, 0, error = e.message ?: "Download failed"))
        } finally {
            _downloadingSongs.value = _downloadingSongs.value - song.id
        }
    }.flowOn(Dispatchers.IO)

    override fun getDownloadingSongs(): Flow<List<String>> = _downloadingSongs
}
