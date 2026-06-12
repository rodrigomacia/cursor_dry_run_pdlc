package com.karaoke.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.karaoke.app.domain.model.Song
import com.karaoke.app.util.toMinutesSeconds

@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    if (compact) {
        CompactSongCard(song = song, onClick = onClick, onFavoriteClick = onFavoriteClick, modifier = modifier)
    } else {
        FullSongCard(song = song, onClick = onClick, onFavoriteClick = onFavoriteClick, modifier = modifier)
    }
}

@Composable
private fun FullSongCard(
    song: Song,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumArt(uri = song.albumArtUri, size = 52)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = song.duration.toMinutesSeconds(),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    if (song.hasLyrics) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Lyrics,
                            contentDescription = "Com letra",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (song.isFavorite) "Remover favorito" else "Favoritar",
                    tint = if (song.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun CompactSongCard(
    song: Song,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            AlbumArt(uri = song.albumArtUri, size = 140, isFullWidth = true)
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AlbumArt(uri: String?, size: Int, isFullWidth: Boolean = false) {
    if (uri != null) {
        AsyncImage(
            model = uri,
            contentDescription = "Capa do álbum",
            contentScale = ContentScale.Crop,
            modifier = if (isFullWidth) {
                Modifier
                    .fillMaxWidth()
                    .height(size.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            } else {
                Modifier
                    .size(size.dp)
                    .clip(RoundedCornerShape(8.dp))
            }
        )
    } else {
        Surface(
            modifier = if (isFullWidth) {
                Modifier
                    .fillMaxWidth()
                    .height(size.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            } else {
                Modifier
                    .size(size.dp)
                    .clip(RoundedCornerShape(8.dp))
            },
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size((size * 0.4f).dp)
                )
            }
        }
    }
}
