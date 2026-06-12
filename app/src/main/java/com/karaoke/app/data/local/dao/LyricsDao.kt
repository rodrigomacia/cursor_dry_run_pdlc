package com.karaoke.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.karaoke.app.data.local.entity.LyricsEntity

@Dao
interface LyricsDao {

    @Query("SELECT * FROM lyrics WHERE song_id = :songId LIMIT 1")
    suspend fun getLyricsBySongId(songId: Long): LyricsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyrics(lyrics: LyricsEntity)

    @Query("SELECT COUNT(*) > 0 FROM lyrics WHERE song_id = :songId")
    suspend fun hasCachedLyrics(songId: Long): Boolean

    @Query("DELETE FROM lyrics")
    suspend fun clearAll()

    @Query("DELETE FROM lyrics WHERE song_id = :songId")
    suspend fun deleteBySongId(songId: Long)
}
