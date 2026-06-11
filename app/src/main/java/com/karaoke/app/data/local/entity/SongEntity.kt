package com.karaoke.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.karaoke.app.domain.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    @ColumnInfo(name = "album_art_uri")
    val albumArtUri: String?,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "has_lyrics")
    val hasLyrics: Boolean = false,
    val genre: String = "",
    val year: Int = 0,
    @ColumnInfo(name = "play_count")
    val playCount: Int = 0,
    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_played")
    val lastPlayed: Long = 0L
) {
    fun toDomain() = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = uri,
        albumArtUri = albumArtUri,
        isFavorite = isFavorite,
        hasLyrics = hasLyrics,
        genre = genre,
        year = year,
        playCount = playCount,
        addedDate = addedDate
    )
}

fun Song.toEntity() = SongEntity(
    id = id,
    title = title,
    artist = artist,
    album = album,
    duration = duration,
    uri = uri,
    albumArtUri = albumArtUri,
    isFavorite = isFavorite,
    hasLyrics = hasLyrics,
    genre = genre,
    year = year,
    playCount = playCount,
    addedDate = addedDate
)
