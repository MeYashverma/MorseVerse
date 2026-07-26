package com.morseverse.core.common.utils

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

enum class ToneType { SINE, SMOOTH, BUZZY, RADIO }
enum class NoiseType { NONE, STATIC, RAIN, WEAK_SIGNAL, CONTEST, RADIO }

data class AudioConfig(
    val wpm: Int = 20,
    val frequency: Int = 600,
    val volume: Float = 0.8f,
    val farnsworthSpacing: Boolean = false,
    val farnsworthWpm: Int = 15,
    val noiseLevel: Float = 0f,
    val noiseType: NoiseType = NoiseType.NONE,
    val toneType: ToneType = ToneType.SINE
)

object MorseTiming {
    const val DIT = 1
    const val DAH = 3
    const val SYMBOL_SPACE = 1
    const val CHARACTER_SPACE = 3
    const val WORD_SPACE = 7
}

class MorseAudioEngine {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val PARIS_UNIT = 50
    }

    private var audioTrack: AudioTrack? = null
    private var isPlaying = false

    fun ditDurationMs(wpm: Int): Float {
        return 1200f / wpm
    }

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
                ToneType.SMOOTH -> sin(2.0 * PI * frequency * t) + 0.3 * sin(2.0 * PI * frequency * 2 * t)
                ToneType.BUZZY -> sin(2.0 * PI * frequency * t) + 0.5 * sin(2.0 * PI * frequency * 2 * t) + 0.25 * sin(2.0 * PI * frequency * 3 * t)
                ToneType.RADIO -> sin(2.0 * PI * frequency * t) + (Math.random() * 0.1 - 0.05)
            }
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

    private fun generateSilence(durationMs: Float): ShortArray {
        return ShortArray((SAMPLE_RATE * durationMs / 1000).toInt())
    }

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

    fun generateMorseAudio(morse: String, config: AudioConfig): ShortArray {
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
                    result.addAll(generateTone(config.frequency, ditMs, config.volume, config.toneType).toList())
                    if (i + 1 < morse.length && morse[i + 1] != ' ') {
                        result.addAll(generateSilence(symbolSpaceMs).toList())
                    }
                }
                '-' -> {
                    result.addAll(generateTone(config.frequency, dahMs, config.volume, config.toneType).toList())
                    if (i + 1 < morse.length && morse[i + 1] != ' ') {
                        result.addAll(generateSilence(symbolSpaceMs).toList())
                    }
                }
                ' ' -> {
                    if (i + 1 < morse.length && morse[i + 1] == ' ') {
                        result.addAll(generateSilence(wordSpaceMs - charSpaceMs).toList())
                        while (i + 1 < morse.length && morse[i + 1] == ' ') i++
                    } else {
                        result.addAll(generateSilence(charSpaceMs - symbolSpaceMs).toList())
                    }
                }
            }
            i++
        }
        return result.toShortArray()
    }

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
                result.addAll(generateSilence(wordSpaceMs).toList())
            } else {
                val morse = charToMorse(char.uppercaseChar()) ?: continue
                result.addAll(generateMorseAudio(morse, config).toList())
                if (index < text.length - 1 && text[index + 1] != ' ') {
                    val ditMs = ditDurationMs(config.wpm)
                    val charSpaceMs = if (config.farnsworthSpacing) {
                        ditMs * MorseTiming.CHARACTER_SPACE * (config.farnsworthWpm.toFloat() / config.wpm)
                    } else {
                        ditMs * MorseTiming.CHARACTER_SPACE
                    }
                    result.addAll(generateSilence(charSpaceMs).toList())
                }
            }
        }
        if (config.noiseLevel > 0 && config.noiseType != NoiseType.NONE) {
            val durationMs = result.size.toFloat() / SAMPLE_RATE * 1000
            val noise = generateNoise(durationMs, config.noiseLevel, config.noiseType)
            for (j in 0 until min(result.size, noise.size)) {
                result[j] = (result[j].toInt() + noise[j].toInt())
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
            }
        }
        return result.toShortArray()
    }

    fun playAudio(samples: ShortArray, onComplete: () -> Unit = {}) {
        stopAudio()
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT
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

    fun stopAudio() {
        isPlaying = false
        audioTrack?.let { track ->
            try {
                track.stop()
                track.flush()
                track.release()
            } catch (_: Exception) { }
        }
        audioTrack = null
    }

    fun generateFeedbackTone(frequency: Int = 800, durationMs: Float = 50f, volume: Float = 0.3f): ShortArray {
        return generateTone(frequency, durationMs, volume, ToneType.SINE)
    }

    fun release() {
        stopAudio()
    }
}
l