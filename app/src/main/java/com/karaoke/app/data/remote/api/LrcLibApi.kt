package com.karaoke.app.data.remote.api

import com.karaoke.app.data.remote.dto.LrcLibResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * LRCLib is a free, open API for synchronized lyrics — no API key required.
 * Docs: https://lrclib.net/docs
 */
interface LrcLibApi {

    @GET("api/get")
    suspend fun getLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String,
        @Query("duration") duration: Int? = null
    ): LrcLibResponseDto?

    @GET("api/search")
    suspend fun searchLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String? = null
    ): List<LrcLibResponseDto>

    companion object {
        const val BASE_URL = "https://lrclib.net/"
    }
}
