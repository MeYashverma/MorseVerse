# 🔤 MorseVerse

**The Ultimate Morse Code Learning Experience for Android**

[![Build](https://github.com/username/MorseVerse/actions/workflows/build.yml/badge.svg)](https://github.com/username/MorseVerse/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.12-orange.svg)](https://developer.android.com/jetpack/compose)

---

## ✨ Features

### 🌳 Interactive Morse Tree
The classic International Morse Binary Tree rendered as a beautiful, interactive Canvas visualization.
- Zoom, pan, infinite canvas
- Progress rings showing mastery
- Weak character highlighting
- Animated path tracing
- 60 FPS smooth rendering

### 📚 Multiple Learning Methods
- **Koch Method** - Learn at full speed, 2 characters at a time
- **Farnsworth** - Standard characters with extra spacing
- **Traditional** - Learn by character groups
- **Adaptive** - AI-driven personalized learning
- **Story Mode** - Learn through interactive missions

### 🎮 Practice Modes
| Mode | Description |
|------|-------------|
| Character | Individual character practice |
| Word | Common word practice |
| Sentence | Full sentence practice |
| Random | Random character mix |
| Weak | Focus on weak characters |
| Custom | Custom character sets |
| Callsign | Ham radio callsign practice |
| Contest | Contest simulation |
| Timed | Speed challenges |
| Daily | Daily challenge |
| Infinite | Never-ending practice |

### 🎧 Audio Engine
- Custom PCM tone generator
- 5-60 WPM support
- Adjustable frequency (300-1000 Hz)
- Multiple tone types (Sine, Smooth, Buzzy, Radio)
- Farnsworth timing
- Noise simulation (Static, Rain, Weak Signal, Contest)

### 📡 Decoder
- Decode from microphone (live audio)
- Decode from audio file
- Decode from clipboard
- Manual Morse input
- Waveform visualization
- Confidence meter

### 🔄 Translator
- Text ⇄ Morse translation
- Audio playback
- Flashlight output
- Copy/Paste/Share
- Favorites & history

### 📊 Statistics
- Accuracy tracking
- WPM progress
- Practice heatmap (GitHub-style)
- Weekly/Monthly stats
- Character mastery levels
- Reaction time analysis

### 🏆 Gamification
- XP system
- Achievement badges
- Daily challenges
- Streak tracking
- Level progression
- Leaderboard (coming soon)

### 🗺️ Story Mode
Learn Morse code through exciting adventures:
- **SOS Rescue** - Save a stranded sailor
- **Spy Mission** - Intercept enemy communications
- **Space Mission** - Communicate with astronauts
- And more coming soon!

### 📻 Ham Radio Toolkit
- International Phonetic Alphabet
- Q Codes reference
- Common abbreviations
- Callsign practice
- Contest simulation

### 🎨 Design
- **OLED-first** - True black for AMOLED displays
- **Material 3 Expressive** - Latest Material Design
- **Nothing OS inspired** - Clean, minimal aesthetic
- **Dynamic Color** - Material You support
- **4 Themes** - Dark, Light, AMOLED, Material You
- **Accessibility** - Large text, high contrast, color blind modes

---

## 📱 Screenshots

| Home | Morse Tree | Practice | Translator |
|------|-----------|----------|------------|
| ![Home](screenshots/home.png) | ![Tree](screenshots/tree.png) | ![Practice](screenshots/practice.png) | ![Translator](screenshots/translator.png) |

| Statistics | Story Mode | Ham Radio | Settings |
|-----------|-----------|-----------|----------|
| ![Stats](screenshots/statistics.png) | ![Story](screenshots/story.png) | ![Ham](screenshots/ham.png) | ![Settings](screenshots/settings.png) |

---

## 🏗️ Architecture

```
MorseVerse/
├── app/                          # Main application module
│   └── src/main/java/
│       └── com/morseverse/app/
│           ├── MainActivity.kt
│           ├── MorseVerseApp.kt
│           ├── navigation/       # Navigation graph
│           ├── di/               # Hilt modules
│           └── ui/               # App-level UI
│
├── core/                         # Core modules
│   ├── common/                   # Shared utilities
│   │   └── src/main/java/
│   │       └── com/morseverse/core/common/
│   │           ├── constants/    # Morse code data
│   │           ├── utils/        # Audio engine, helpers
│   │           └── extensions/
│   │
│   ├── data/                     # Data layer
│   │   └── src/main/java/
│   │       └── com/morseverse/core/data/
│   │           ├── database/     # Room database
│   │           │   ├── dao/
│   │           │   └── entities/
│   │           ├── repository/
│   │           ├── preferences/  # DataStore
│   │           ├── mappers/
│   │           └── di/
│   │
│   ├── domain/                   # Domain layer
│   │   └── src/main/java/
│   │       └── com/morseverse/core/domain/
│   │           ├── models/       # Data classes
│   │           ├── usecases/     # Business logic
│   │           └── repository/   # Interfaces
│   │
│   └── designsystem/             # Design system
│       └── src/main/java/
│           └── com/morseverse/core/designsystem/
│               ├── theme/        # Colors, Typography, Theme
│               ├── components/   # Shared components
│               └── animations/
│
├── feature/                      # Feature modules
│   ├── home/                     # Home screen
│   ├── learn/                    # Learning methods
│   ├── practice/                 # Practice modes
│   ├── morseTree/                # Morse binary tree
│   ├── translator/               # Text ⇄ Morse
│   ├── decoder/                  # Morse decoder
│   ├── statistics/               # Stats & analytics
│   ├── achievements/             # Achievements
│   ├── story/                    # Story mode
│   └── ham/                      # Ham radio toolkit
│
├── docs/                         # Documentation
├── screenshots/                  # App screenshots
├── assets/                       # Design assets
└── .github/workflows/            # CI/CD
```

### Architecture Pattern
- **MVVM** with Clean Architecture
- **Repository Pattern** for data access
- **Use Cases** for business logic
- **Hilt** for dependency injection
- **Room** for local database
- **DataStore** for preferences
- **Coroutines + Flow** for async operations

---

## 🛠️ Tech Stack

| Technology | Purpose |
|-----------|---------|
| Kotlin 2.1 | Language |
| Jetpack Compose | UI Toolkit |
| Material 3 | Design System |
| Hilt | Dependency Injection |
| Room | Local Database |
| DataStore | Preferences |
| Navigation Compose | Navigation |
| Coroutines + Flow | Concurrency |
| Canvas API | Custom Rendering |
| AudioTrack | Audio Playback |
| JUnit | Unit Testing |
| Compose UI Tests | UI Testing |
| GitHub Actions | CI/CD |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 35

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/username/MorseVerse.git
   ```

2. Open in Android Studio

3. Sync Gradle

4. Run on device or emulator

### Build
```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Run tests
./gradlew testDebugUnitTest
```

---

## 🧪 Testing

```bash
# Unit tests
./gradlew testDebugUnitTest

# UI tests
./gradlew connectedDebugAndroidTest

# Lint check
./gradlew lintDebug
```

---

## 📖 Documentation

- [Architecture Guide](docs/ARCHITECTURE.md)
- [Developer Guide](docs/DEVELOPER.md)
- [Contributing Guide](CONTRIBUTING.md)
- [Changelog](CHANGELOG.md)

---

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- International Morse Code standard
- The Morse code learning community
- Material Design 3 guidelines
- Nothing OS design inspiration

---

**Built with ❤️ using Kotlin & Jetpack Compose**
