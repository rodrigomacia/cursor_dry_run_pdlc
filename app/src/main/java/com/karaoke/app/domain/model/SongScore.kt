package com.karaoke.app.domain.model

data class SongScore(
    val songId: Long,
    val totalScore: Int,
    val accuracy: Float,
    val pitchScore: Int,
    val rhythmScore: Int,
    val grade: ScoreGrade,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ScoreGrade(val label: String, val minScore: Int) {
    PERFECT("Perfeito!", 95),
    GREAT("Ótimo!", 80),
    GOOD("Bom!", 65),
    OK("Ok!", 50),
    NEEDS_WORK("Continue tentando!", 0);

    companion object {
        fun fromScore(score: Int): ScoreGrade =
            entries.firstOrNull { score >= it.minScore } ?: NEEDS_WORK
    }
}

data class PitchSample(
    val timestamp: Long,
    val detectedPitch: Float,
    val expectedPitch: Float
)
