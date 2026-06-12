package com.karaoke.app.data.remote.api

import com.karaoke.app.data.remote.dto.JamendoResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Jamendo provides free Creative Commons music via a public API.
 * Docs: https://developer.jamendo.com/v3.0
 */
interface JamendoApi {

    @GET("v3.0/tracks/")
    suspend fun searchTracks(
        @Query("client_id") clientId: String = JamendoApi.CLIENT_ID,
        @Query("search") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("audioformat") audioFormat: String = "mp32",
        @Query("include") include: String = "musicinfo",
        @Query("imagesize") imageSize: Int = 200
    ): JamendoResponseDto

    @GET("v3.0/tracks/")
    suspend fun getPopularTracks(
        @Query("client_id") clientId: String = JamendoApi.CLIENT_ID,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("order") order: String = "popularity_total",
        @Query("audioformat") audioFormat: String = "mp32",
        @Query("include") include: String = "musicinfo",
        @Query("imagesize") imageSize: Int = 200
    ): JamendoResponseDto

    companion object {
        const val BASE_URL = "https://api.jamendo.com/"
        // Free public client ID for Jamendo (register at developer.jamendo.com for production)
        const val CLIENT_ID = "b6747d04"
    }
}
