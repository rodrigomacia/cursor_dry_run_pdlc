package com.karaoke.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class JamendoResponseDto(
    val headers: JamendoHeadersDto?,
    val results: List<JamendoTrackDto>?
)

data class JamendoHeadersDto(
    val status: String?,
    val code: Int?,
    val error_message: String?,
    val results_count: Int?
)

data class JamendoTrackDto(
    val id: String?,
    val name: String?,
    val duration: Int?,
    @SerializedName("artist_id")
    val artistId: String?,
    @SerializedName("artist_name")
    val artistName: String?,
    @SerializedName("album_name")
    val albumName: String?,
    val audio: String?,
    val audiodownload: String?,
    val image: String?,
    @SerializedName("license_ccurl")
    val licenseUrl: String?,
    val musicinfo: JamendoMusicInfoDto?
)

data class JamendoMusicInfoDto(
    val vocalinstrumental: String?,
    val lang: String?,
    val acousticelectric: String?,
    val speed: String?,
    val tags: JamendoTagsDto?
)

data class JamendoTagsDto(
    val genres: List<String>?,
    val instruments: List<String>?,
    val vartags: List<String>?
)
