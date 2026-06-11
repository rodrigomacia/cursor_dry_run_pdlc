package com.karaoke.app.util

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Real-time pitch detection using autocorrelation (YIN algorithm simplified).
 * Emits detected frequency in Hz via a Flow.
 */
class PitchDetector {

    companion object {
        const val SAMPLE_RATE = 44100
        const val BUFFER_SIZE_FRAMES = 2048
        private const val YIN_THRESHOLD = 0.15f
    }

    fun startDetection(): Flow<Float> = flow {
        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = maxOf(minBufferSize, BUFFER_SIZE_FRAMES * 2)

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            return@flow
        }

        val buffer = ShortArray(BUFFER_SIZE_FRAMES)
        recorder.startRecording()

        try {
            while (coroutineContext.isActive) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    val pitch = detectPitch(buffer, read)
                    emit(pitch)
                }
            }
        } finally {
            recorder.stop()
            recorder.release()
        }
    }.flowOn(Dispatchers.IO)

    fun getAmplitude(buffer: ShortArray, length: Int): Float {
        var sum = 0.0
        for (i in 0 until length) {
            sum += buffer[i] * buffer[i].toDouble()
        }
        return sqrt(sum / length).toFloat()
    }

    private fun detectPitch(buffer: ShortArray, length: Int): Float {
        val floatBuffer = FloatArray(length) { buffer[it] / 32768f }
        return yinPitch(floatBuffer)
    }

    private fun yinPitch(buffer: FloatArray): Float {
        val yinBuffer = FloatArray(buffer.size / 2)

        yinBuffer[0] = 1f
        var runningSum = 0f

        for (tau in 1 until yinBuffer.size) {
            var sum = 0f
            for (j in 0 until yinBuffer.size) {
                val delta = buffer[j] - buffer[j + tau]
                sum += delta * delta
            }
            yinBuffer[tau] = sum
            runningSum += sum
            yinBuffer[tau] *= tau / runningSum
        }

        var tau = 2
        while (tau < yinBuffer.size) {
            if (yinBuffer[tau] < YIN_THRESHOLD) {
                while (tau + 1 < yinBuffer.size && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    tau++
                }
                return SAMPLE_RATE.toFloat() / tau
            }
            tau++
        }

        return -1f
    }

    fun noteFromFrequency(frequency: Float): String {
        if (frequency <= 0f) return "-"
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val noteNum = (12 * (ln(frequency / 440.0) / ln(2.0))).toInt() + 69
        if (noteNum < 0) return "-"
        return noteNames[noteNum % 12]
    }
}
