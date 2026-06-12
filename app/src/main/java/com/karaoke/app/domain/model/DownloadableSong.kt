package com.karaoke.app.domain.model

data class DownloadableSong(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,
    val audioUrl: String,
    val imageUrl: String?,
    val license: String,
    val source: SongSource
)

enum class SongSource {
    JAMENDO,
    LOCAL
}

enum class DownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    FAILED
}
