package com.karaoke.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LrcLibResponseDto(
    val id: Int?,
    @SerializedName("track_name")
    val trackName: String?,
    @SerializedName("artist_name")
    val artistName: String?,
    @SerializedName("album_name")
    val albumName: String?,
    val duration: Double?,
    val instrumental: Boolean?,
    @SerializedName("plain_lyrics")
    val plainLyrics: String?,
    @SerializedName("synced_lyrics")
    val syncedLyrics: String?
)
