package com.karaoke.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karaoke.app.data.local.dao.LyricsDao
import com.karaoke.app.data.local.dao.SongDao
import com.karaoke.app.data.local.entity.LyricsEntity
import com.karaoke.app.data.local.entity.SongEntity

@Database(
    entities = [SongEntity::class, LyricsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun lyricsDao(): LyricsDao

    companion object {
        const val DATABASE_NAME = "karaoke_db"
    }
}
