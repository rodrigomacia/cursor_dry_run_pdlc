package com.karaoke.app.di

import com.karaoke.app.data.repository.DownloadRepositoryImpl
import com.karaoke.app.data.repository.LyricsRepositoryImpl
import com.karaoke.app.data.repository.SongRepositoryImpl
import com.karaoke.app.domain.repository.DownloadRepository
import com.karaoke.app.domain.repository.LyricsRepository
import com.karaoke.app.domain.repository.SongRepository
import com.karaoke.app.util.PitchDetector
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository

    @Binds
    @Singleton
    abstract fun bindLyricsRepository(impl: LyricsRepositoryImpl): LyricsRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository

    companion object {
        @Provides
        @Singleton
        fun providePitchDetector(): PitchDetector = PitchDetector()
    }
}
