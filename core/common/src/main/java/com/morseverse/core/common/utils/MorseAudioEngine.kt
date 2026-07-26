package com.morseverse.core.common.utils

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.morseverse.core.domain.models.AudioConfig
import com.morseverse.core.domain.models.MorseTiming
import com.morseverse.core.domain.models.NoiseType
import com.morseverse.core.domain.models.ToneType
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

/**
 * High-performance Morse code audio tone generator
 * Generates PCM audio for Morse code at any WPM, frequency, and volume
 */
class MorseAudioEngine {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val PARIS_UNIT = 50 // standard PARIS timing
    }

    private var audioTrack: AudioTrack? = null
    private var isPlaying = false

    /**
     * Calculate the duration of a dit in milliseconds at given WPM
     */
    fun ditDurationMs(wpm: Int): Float {
        return (PARIS_UNIT.toFloat() / wpm) * 12f // 1200 / WPM
    }

    /**
     * Generate PCM samples for a sine wave tone
     */
    private fun generateTone(
        frequency: Int,
        durationMs: Float,
        volume: Float,
        toneType: ToneType
    ): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs / 1000).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val value = when (toneType) {
                ToneType.SINE -> sin(2.0 * PI * frequency * t)
                ToneType.SMOOTH -> {
                    val fundamental = sin(2.0 * PI * frequency * t)
                    val harmonic = 0.3 * sin(2.0 * PI * frequency * 2 * t)
                    fundamental + harmonic
                }
                ToneType.BUZZY -> {
                    val fundamental = sin(2.0 * PI * frequency * t)
                    val harmonic2 = 0.5 * sin(2.0 * PI * frequency * 2 * t)
                    val harmonic3 = 0.25 * sin(2.0 * PI * frequency * 3 * t)
                    fundamental + harmonic2 + harmonic3
                }
                ToneType.RADIO -> {
                    val fundamental = sin(2.0 * PI * frequency * t)
                    val noise = (Math.random() * 0.1 - 0.05)
                    fundamental + noise
                }
            }

            // Apply envelope to prevent clicks (5ms attack/release)
            val envelopeSamples = (SAMPLE_RATE * 0.005).toInt()
            val envelope = when {
                i < envelopeSamples -> i.toDouble() / envelopeSamples
                i > numSamples - envelopeSamples -> (numSamples - i).toDouble() / envelopeSamples
                else -> 1.0
            }

            samples[i] = (value * envelope * volume * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }
        return samples
    }

    /**
     * Generate silence
     */
    private fun generateSilence(durationMs: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs / 1000).toInt()
        return ShortArray(numSamples)
    }

    /**
     * Generate noise overlay
     */
    private fun generateNoise(durationMs: Float, level: Float, noiseType: NoiseType): ShortArray {
        if (noiseType == NoiseType.NONE || level == 0f) return ShortArray(0)

        val numSamples = (SAMPLE_RATE * durationMs / 1000).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val noise = when (noiseType) {
                NoiseType.STATIC -> (Math.random() * 2 - 1) * level * Short.MAX_VALUE
                NoiseType.RAIN -> {
                    val drop = if (Math.random() < 0.01) Math.random() * 0.5 else 0.0
                    (Math.random() * 0.3 + drop) * level * Short.MAX_VALUE
                }
                NoiseType.WEAK_SIGNAL -> {
                    val fade = sin(2.0 * PI * 0.5 * i / SAMPLE_RATE) * 0.5 + 0.5
                    (Math.random() * 2 - 1) * level * fade * Short.MAX_VALUE
                }
                NoiseType.CONTEST -> {
                    val qrm = sin(2.0 * PI * (550 + Math.random() * 200) * i / SAMPLE_RATE)
                    qrm * level * Short.MAX_VALUE * 0.3
                }
                NoiseType.RADIO -> {
                    val base = (Math.random() * 2 - 1) * 0.3
                    val crackle = if (Math.random() < 0.005) Math.random() else 0.0
                    (base + crackle) * level * Short.MAX_VALUE
                }
                NoiseType.NONE -> 0.0
            }
            samples[i] = noise.toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }
        return samples
    }

    /**
     * Generate audio samples for a Morse string (e.g., ".- -... -.-.")
     */
    fun generateMorseAudio(
        morse: String,
        config: AudioConfig
    ): ShortArray {
        val ditMs = ditDurationMs(config.wpm)
        val dahMs = ditMs * MorseTiming.DAH
        val symbolSpaceMs = ditMs * MorseTiming.SYMBOL_SPACE
        val charSpaceMs = if (config.farnsworthSpacing) {
            ditMs * MorseTiming.CHARACTER_SPACE * (config.farnsworthWpm.toFloat() / config.wpm)
        } else {
            ditMs * MorseTiming.CHARACTER_SPACE
        }
        val wordSpaceMs = if (config.farnsworthSpacing) {
            ditMs * MorseTiming.WORD_SPACE * (config.farnsworthWpm.toFloat() / config.wpm)
        } else {
            ditMs * MorseTiming.WORD_SPACE
        }

        val result = mutableListOf<Short>()
        var i = 0

        while (i < morse.length) {
            when (morse[i]) {
                '.' -> {
                    result += generateTone(config.frequency, ditMs, config.volume, config.toneType)
                    if (i + 1 < morse.length && morse[i + 1] != ' ') {
                        result += generateSilence(symbolSpaceMs)
                    }
                }
                '-' -> {
                    result += generateTone(config.frequency, dahMs, config.volume, config.toneType)
                    if (i + 1 < morse.length && morse[i + 1] != ' ') {
                        result += generateSilence(symbolSpaceMs)
                    }
                }
                ' ' -> {
                    // Check for word space (multiple spaces or slash)
                    if (i + 1 < morse.length && morse[i + 1] == ' ') {
                        result += generateSilence(wordSpaceMs - charSpaceMs)
                        while (i + 1 < morse.length && morse[i + 1] == ' ') i++
                    } else {
                        result += generateSilence(charSpaceMs - symbolSpaceMs)
                    }
                }
            }
            i++
        }

        return result.toShortArray()
    }

    /**
     * Generate audio for a full text string
     */
    fun generateTextAudio(
        text: String,
        config: AudioConfig,
        charToMorse: (Char) -> String?
    ): ShortArray {
        val result = mutableListOf<Short>()

        for ((index, char) in text.withIndex()) {
            if (char == ' ') {
                val ditMs = ditDurationMs(config.wpm)
                val wordSpaceMs = if (config.farnsworthSpacing) {
                    ditMs * MorseTiming.WORD_SPACE * (config.farnsworthWpm.toFloat() / config.wpm)
                } else {
                    ditMs * MorseTiming.WORD_SPACE
                }
                result += generateSilence(wordSpaceMs)
            } else {
                val morse = charToMorse(char.uppercaseChar()) ?: continue
                result += generateMorseAudio(morse, config)
                if (index < text.length - 1 && text[index + 1] != ' ') {
                    val ditMs = ditDurationMs(config.wpm)
                    val charSpaceMs = if (config.farnsworthSpacing) {
                        ditMs * MorseTiming.CHARACTER_SPACE * (config.farnsworthWpm.toFloat() / config.wpm)
                    } else {
                        ditMs * MorseTiming.CHARACTER_SPACE
                    }
                    result += generateSilence(charSpaceMs)
                }
            }
        }

        // Add noise if needed
        if (config.noiseLevel > 0 && config.noiseType != NoiseType.NONE) {
            val durationMs = result.size.toFloat() / SAMPLE_RATE * 1000
            val noise = generateNoise(durationMs, config.noiseLevel, config.noiseType)
            for (i in 0 until min(result.size, noise.size)) {
                result[i] = (result[i].toInt() + noise[i].toInt())
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
            }
        }

        return result.toShortArray()
    }

    /**
     * Play audio samples through AudioTrack
     */
    fun playAudio(samples: ShortArray, onComplete: () -> Unit = {}) {
        stopAudio()

        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(bufferSize, samples.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack?.write(samples, 0, samples.size)
        audioTrack?.setNotificationMarkerPosition(samples.size)
        audioTrack?.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack?) {
                isPlaying = false
                onComplete()
            }
            override fun onPeriodicNotification(track: AudioTrack?) {}
        })

        isPlaying = true
        audioTrack?.play()
    }

    /**
     * Stop audio playback
     */
    fun stopAudio() {
        isPlaying = false
        audioTrack?.let { track ->
            try {
                track.stop()
                track.flush()
                track.release()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
        audioTrack = null
    }

    /**
     * Generate a short tone for UI feedback
     */
    fun generateFeedbackTone(
        frequency: Int = 800,
        durationMs: Float = 50f,
        volume: Float = 0.3f
    ): ShortArray {
        return generateTone(frequency, durationMs, volume, ToneType.SINE)
    }

    fun release() {
        stopAudio()
    }
}
