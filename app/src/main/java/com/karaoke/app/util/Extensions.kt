package com.karaoke.app.util

import java.util.concurrent.TimeUnit

fun Long.toMinutesSeconds(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes)
    return "%d:%02d".format(minutes, seconds)
}

fun Long.toHoursMinutesSeconds(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) -
            TimeUnit.HOURS.toSeconds(hours) -
            TimeUnit.MINUTES.toSeconds(minutes)
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%d:%02d".format(minutes, seconds)
}

fun Int.toMinutesSeconds(): String = this.toLong().let { ms ->
    val minutes = ms / 60
    val seconds = ms % 60
    "%d:%02d".format(minutes, seconds)
}
