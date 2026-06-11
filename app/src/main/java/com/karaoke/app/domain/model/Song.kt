package com.karaoke.app.domain.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val albumArtUri: String?,
    val isFavorite: Boolean = false,
    val hasLyrics: Boolean = false,
    val genre: String = "",
    val year: Int = 0,
    val playCount: Int = 0,
    val addedDate: Long = System.currentTimeMillis()
)
