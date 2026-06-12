package com.karaoke.app.domain.usecase

import com.karaoke.app.domain.model.Song
import com.karaoke.app.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<Song>> = repository.getAllSongs()
}

class GetRecentSongsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(limit: Int = 10): Flow<List<Song>> = repository.getRecentSongs(limit)
}

class GetFavoriteSongsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<Song>> = repository.getFavoriteSongs()
}

class SearchSongsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(query: String): List<Song> = repository.searchSongs(query)
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(songId: Long) = repository.toggleFavorite(songId)
}

class ScanLocalSongsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(): Int = repository.scanLocalSongs()
}
