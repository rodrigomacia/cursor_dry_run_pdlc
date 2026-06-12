package com.karaoke.app.util

import com.karaoke.app.domain.model.LyricLine
import com.karaoke.app.domain.model.LyricWord

/**
 * Parses LRC (Lyric) format files into [LyricLine] domain objects.
 *
 * Supports standard LRC ([mm:ss.xx]text) and Enhanced LRC with
 * word-level timestamps (<mm:ss.xx>word).
 */
object LrcParser {

    private val LINE_REGEX = Regex("""^\[(\d{1,2}):(\d{2})\.(\d{2,3})](.*?)$""")
    private val WORD_REGEX = Regex("""<(\d{1,2}):(\d{2})\.(\d{2,3})>([^<]*)""")
    private val METADATA_REGEX = Regex("""^\[([a-z]+):(.*)]$""")

    fun parse(lrcContent: String): List<LyricLine> {
        val lines = lrcContent.lines()
        val result = mutableListOf<LyricLine>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue
            if (METADATA_REGEX.matches(trimmed)) continue

            val match = LINE_REGEX.find(trimmed) ?: continue
            val (minStr, secStr, csStr, text) = match.destructured

            val minutes = minStr.toLongOrNull() ?: continue
            val seconds = secStr.toLongOrNull() ?: continue
            val centiseconds = csStr.padEnd(3, '0').toLongOrNull() ?: continue

            val startMs = minutes * 60_000L + seconds * 1_000L + centiseconds

            val words = parseWords(text)

            result.add(
                LyricLine(
                    startTimeMs = startMs,
                    text = text.replace(WORD_REGEX, "$4").trim(),
                    words = words
                )
            )
        }

        val sorted = result.sortedBy { it.startTimeMs }

        return sorted.mapIndexed { index, line ->
            val endMs = if (index < sorted.size - 1) sorted[index + 1].startTimeMs else 0L
            line.copy(endTimeMs = endMs)
        }
    }

    private fun parseWords(text: String): List<LyricWord> {
        val matches = WORD_REGEX.findAll(text).toList()
        if (matches.isEmpty()) return emptyList()

        return matches.mapIndexed { index, match ->
            val (min, sec, cs, word) = match.destructured
            val startMs = min.toLong() * 60_000L +
                    sec.toLong() * 1_000L +
                    cs.padEnd(3, '0').toLong()
            val endMs = if (index < matches.size - 1) {
                val nextMatch = matches[index + 1]
                val (nm, ns, ncs, _) = nextMatch.destructured
                nm.toLong() * 60_000L + ns.toLong() * 1_000L + ncs.padEnd(3, '0').toLong()
            } else 0L
            LyricWord(startTimeMs = startMs, endTimeMs = endMs, text = word.trim())
        }
    }

    fun toLrc(lines: List<LyricLine>): String {
        val sb = StringBuilder()
        for (line in lines.sortedBy { it.startTimeMs }) {
            val timestamp = formatTimestamp(line.startTimeMs)
            sb.appendLine("[$timestamp]${line.text}")
        }
        return sb.toString()
    }

    private fun formatTimestamp(ms: Long): String {
        val minutes = ms / 60_000
        val seconds = (ms % 60_000) / 1_000
        val centis = (ms % 1_000) / 10
        return "%02d:%02d.%02d".format(minutes, seconds, centis)
    }
}
