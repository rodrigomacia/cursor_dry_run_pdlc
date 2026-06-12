package com.karaoke.app.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.karaoke.app.data.local.dao.SongDao
import com.karaoke.app.data.local.entity.SongEntity
import com.karaoke.app.data.local.entity.toEntity
import com.karaoke.app.domain.model.Song
import com.karaoke.app.domain.repository.SongRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songDao: SongDao
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> =
        songDao.getAllSongs().map { entities -> entities.map { it.toDomain() } }

    override fun getFavoriteSongs(): Flow<List<Song>> =
        songDao.getFavoriteSongs().map { entities -> entities.map { it.toDomain() } }

    override fun getRecentSongs(limit: Int): Flow<List<Song>> =
        songDao.getRecentSongs(limit).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getSongById(id: Long): Song? =
        songDao.getSongById(id)?.toDomain()

    override suspend fun searchSongs(query: String): List<Song> =
        songDao.searchSongs(query).map { it.toDomain() }

    override suspend fun toggleFavorite(songId: Long) =
        songDao.toggleFavorite(songId)

    override suspend fun incrementPlayCount(songId: Long) =
        songDao.incrementPlayCount(songId)

    override suspend fun deleteSong(songId: Long) =
        songDao.deleteSong(songId)

    override suspend fun scanLocalSongs(): Int {
        val songs = queryMediaStore()
        if (songs.isNotEmpty()) {
            songDao.insertAll(songs)
        }
        return songs.size
    }

    private fun queryMediaStore(): List<SongEntity> {
        val songs = mutableListOf<SongEntity>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 30000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val genreCol = cursor.getColumnIndex(MediaStore.Audio.Media.GENRE)
            val yearCol = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
            val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val albumId = cursor.getLong(albumIdCol)
                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"), albumId
                ).toString()

                songs.add(
                    SongEntity(
                        id = id,
                        title = cursor.getString(titleCol) ?: "Unknown",
                        artist = cursor.getString(artistCol) ?: "Unknown Artist",
                        album = cursor.getString(albumCol) ?: "Unknown Album",
                        duration = cursor.getLong(durationCol),
                        uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                        ).toString(),
                        albumArtUri = albumArtUri,
                        genre = if (genreCol >= 0) cursor.getString(genreCol) ?: "" else "",
                        year = if (yearCol >= 0) cursor.getInt(yearCol) else 0,
                        addedDate = cursor.getLong(dateAddedCol) * 1000L
                    )
                )
            }
        }

        return songs
    }
}
