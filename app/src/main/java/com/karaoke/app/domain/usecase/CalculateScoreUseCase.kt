package com.karaoke.app.domain.usecase

import com.karaoke.app.domain.model.PitchSample
import com.karaoke.app.domain.model.ScoreGrade
import com.karaoke.app.domain.model.SongScore
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

class CalculateScoreUseCase @Inject constructor() {

    operator fun invoke(songId: Long, samples: List<PitchSample>): SongScore {
        if (samples.isEmpty()) {
            return SongScore(
                songId = songId,
                totalScore = 0,
                accuracy = 0f,
                pitchScore = 0,
                rhythmScore = 0,
                grade = ScoreGrade.NEEDS_WORK
            )
        }

        val pitchScores = samples.map { sample ->
            if (sample.expectedPitch <= 0f) return@map 1f
            val semitoneError = pitchDifferenceInSemitones(sample.detectedPitch, sample.expectedPitch)
            when {
                semitoneError < 0.5f -> 1.0f
                semitoneError < 1.0f -> 0.8f
                semitoneError < 1.5f -> 0.5f
                semitoneError < 2.0f -> 0.2f
                else -> 0.0f
            }
        }

        val avgPitchAccuracy = pitchScores.average().toFloat()
        val pitchScore = (avgPitchAccuracy * 80).roundToInt()
        val rhythmScore = (samples.size.coerceAtMost(100) / 100f * 20).roundToInt()
        val totalScore = (pitchScore + rhythmScore).coerceIn(0, 100)

        return SongScore(
            songId = songId,
            totalScore = totalScore,
            accuracy = avgPitchAccuracy,
            pitchScore = pitchScore,
            rhythmScore = rhythmScore,
            grade = ScoreGrade.fromScore(totalScore)
        )
    }

    private fun pitchDifferenceInSemitones(detected: Float, expected: Float): Float {
        if (detected <= 0f || expected <= 0f) return Float.MAX_VALUE
        return abs(12f * Math.log(detected.toDouble() / expected.toDouble()).toFloat() / Math.log(2.0).toFloat())
    }
}
