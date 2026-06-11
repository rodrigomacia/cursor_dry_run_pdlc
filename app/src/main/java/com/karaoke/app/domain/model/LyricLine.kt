package com.karaoke.app.domain.model

/**
 * Represents a single line of synced lyrics.
 * @param startTimeMs timestamp in milliseconds when this line should be highlighted
 * @param endTimeMs timestamp in milliseconds when this line ends (0 = until next line)
 * @param text the lyric text for this line
 * @param words optional word-level timestamps for fine-grained karaoke highlighting
 */
data class LyricLine(
    val startTimeMs: Long,
    val endTimeMs: Long = 0L,
    val text: String,
    val words: List<LyricWord> = emptyList()
)

/**
 * Word-level timing for syllable/word karaoke highlight.
 */
data class LyricWord(
    val startTimeMs: Long,
    val endTimeMs: Long,
    val text: String
)
