# Morse Audio Samples

This directory contains pre-generated Morse code audio samples.

## File Format

- **Format**: WAV (44100 Hz, 16-bit, Mono)
- **Naming**: `{character}_{wpm}.wav`
- **Example**: `A_20.wav`, `SOS_15.wav`

## Sample List

### Individual Characters
- `A_20.wav` through `Z_20.wav`
- `0_20.wav` through `9_20.wav`
- `period_20.wav`, `comma_20.wav`, `question_20.wav`

### Common Words
- `SOS_20.wav`
- `CQ_20.wav`
- `HELLO_20.wav`
- `WORLD_20.wav`

### Speed Variations
- `A_10.wav` (10 WPM)
- `A_15.wav` (15 WPM)
- `A_20.wav` (20 WPM)
- `A_25.wav` (25 WPM)
- `A_30.wav` (30 WPM)

## Generation

Samples are generated at runtime by the `MorseAudioEngine`.

To pre-generate samples for testing:

```kotlin
val engine = MorseAudioEngine()
val config = AudioConfig(wpm = 20, frequency = 600, volume = 0.8f)

// Generate single character
val samples = engine.generateMorseAudio(".-", config)

// Save to file (test only)
// Actual audio is generated in real-time
```

## Audio Settings

| Parameter | Value | Range |
|-----------|-------|-------|
| Sample Rate | 44100 Hz | - |
| Bit Depth | 16-bit | - |
| Channels | Mono | - |
| WPM | 20 | 5-60 |
| Frequency | 600 Hz | 300-1000 Hz |
| Volume | 0.8 | 0.0-1.0 |
| Tone Type | Sine | Sine/Smooth/Buzzy/Radio |

## Timing

| Element | Duration |
|---------|----------|
| Dit | 1200/WPM ms |
| Dah | 3 × Dit |
| Symbol Space | 1 × Dit |
| Character Space | 3 × Dit |
| Word Space | 7 × Dit |
