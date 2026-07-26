# Changelog

All notable changes to MorseVerse will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-12-XX

### 🎉 Initial Release

#### ✨ Features
- **Interactive Morse Binary Tree** - Canvas-rendered tree visualization with zoom, pan, and progress tracking
- **Multiple Learning Methods** - Koch, Farnsworth, Traditional, Adaptive, and Story Mode
- **12 Practice Modes** - Character, Word, Sentence, Random, Weak, Custom, Common Words, Callsign, Contest, Timed, Daily, Infinite
- **Custom Audio Engine** - PCM tone generator with 5-60 WPM, adjustable frequency, tone types, and noise simulation
- **Morse Decoder** - Decode from microphone, audio file, clipboard, or manual input
- **Text ⇄ Morse Translator** - Audio playback, flashlight output, favorites, and history
- **Statistics Dashboard** - Accuracy, WPM, heatmap, character mastery, and reaction time tracking
- **Gamification** - XP system, achievements, daily challenges, streaks, and level progression
- **Story Mode** - 3 adventures (SOS Rescue, Spy Mission, Space Mission) with more coming soon
- **Ham Radio Toolkit** - Phonetic alphabet, Q Codes, abbreviations, and callsign practice
- **Character Detail Pages** - Morse code, memory tips, statistics, and practice buttons

#### 🎨 Design
- OLED-first design with true black AMOLED theme
- Material 3 Expressive design system
- 4 theme options (Dark, Light, AMOLED, Material You)
- Smooth animations and haptic feedback
- Accessibility support (large text, high contrast, color blind modes)

#### 🏗️ Architecture
- Clean Architecture with MVVM pattern
- Feature-based modular structure
- Hilt dependency injection
- Room database with 9 tables
- DataStore for preferences
- Coroutines + Flow for async operations

#### 🧪 Testing
- Unit tests for use cases and ViewModels
- Compose UI tests for screens
- GitHub Actions CI/CD pipeline

#### 📱 Platform
- Minimum SDK 26 (Android 8.0)
- Target SDK 35
- Edge-to-edge support
- Landscape and tablet support

---

## Future Plans

### [1.1.0] - Planned
- Android widgets (Today's Practice, Random Character, Quick Translate, Current Streak)
- Push notifications (daily reminder, streak reminder, achievements)
- AI Coach with local analysis
- Bluetooth paddle support
- QSO Simulator
- Band Plans reference

### [1.2.0] - Planned
- Additional story missions
- Leaderboard (local)
- Custom themes
- Export/Import progress
- Tablet optimizations
- Foldable device support

### [2.0.0] - Planned
- Cloud sync (optional)
- Multiplayer contests
- Community features
- More story missions
- Advanced statistics
