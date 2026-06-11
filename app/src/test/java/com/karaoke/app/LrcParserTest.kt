package com.karaoke.app

import com.karaoke.app.util.LrcParser
import org.junit.Test
import org.junit.Assert.*

class LrcParserTest {

    @Test
    fun `parse standard LRC format`() {
        val lrc = """
            [00:10.00]First line
            [00:15.50]Second line
            [00:20.00]Third line
        """.trimIndent()

        val lines = LrcParser.parse(lrc)

        assertEquals(3, lines.size)
        assertEquals(10_000L, lines[0].startTimeMs)
        assertEquals("First line", lines[0].text)
        assertEquals(15_500L, lines[1].startTimeMs)
        assertEquals("Second line", lines[1].text)
    }

    @Test
    fun `end time is set to next line start time`() {
        val lrc = "[00:10.00]Line one\n[00:20.00]Line two"
        val lines = LrcParser.parse(lrc)
        assertEquals(20_000L, lines[0].endTimeMs)
    }

    @Test
    fun `ignores metadata tags`() {
        val lrc = "[ti:My Song]\n[ar:Artist]\n[00:01.00]Actual lyric"
        val lines = LrcParser.parse(lrc)
        assertEquals(1, lines.size)
        assertEquals("Actual lyric", lines[0].text)
    }

    @Test
    fun `toLrc round trip`() {
        val lrc = "[00:10.00]Hello world\n[00:15.00]Second line\n"
        val parsed = LrcParser.parse(lrc)
        val output = LrcParser.toLrc(parsed)
        val reparsed = LrcParser.parse(output)
        assertEquals(parsed.size, reparsed.size)
        assertEquals(parsed[0].startTimeMs, reparsed[0].startTimeMs)
        assertEquals(parsed[0].text, reparsed[0].text)
    }
}
