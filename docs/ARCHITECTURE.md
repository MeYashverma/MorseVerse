# MorseVerse Architecture

## Overview

MorseVerse follows **Clean Architecture** with **MVVM** pattern, organized into modular layers.

## Module Structure

```
MorseVerse/
├── app/                    # Application layer (UI, Navigation, DI)
├── core/
│   ├── common/             # Shared utilities, constants, extensions
│   ├── domain/             # Business logic (Use Cases, Models, Repository interfaces)
│   ├── data/               # Data layer (Room, DataStore, Repository implementations)
│   └── designsystem/       # Theme, components, animations
├── feature/
│   ├── home/               # Home screen
│   ├── learn/              # Learning methods
│   ├── practice/           # Practice modes
│   ├── morseTree/          # Interactive Morse tree
│   ├── translator/         # Text ⇄ Morse
│   ├── decoder/            # Morse decoder
│   ├── statistics/         # Stats & analytics
│   ├── achievements/       # Achievement system
│   ├── story/              # Story mode
│   └── ham/                # Ham radio toolkit
```

## Architecture Layers

### Presentation Layer (`app`, `feature/*`)

- **Compose UI**: Declarative UI using Jetpack Compose
- **ViewModels**: Business logic for UI, state management
- **Navigation**: Navigation Compose for routing
- **DI**: Hilt dependency injection

### Domain Layer (`core/domain`)

- **Models**: Data classes representing business entities
- **Use Cases**: Single-responsibility business logic
- **Repository Interfaces**: Abstractions for data access

### Data Layer (`core/data`)

- **Room Database**: Local persistence
- **DAOs**: Data access objects
- **Repositories**: Implementations of domain interfaces
- **DataStore**: Key-value preferences
- **Mappers**: Entity ↔ Domain conversions

## Dependency Flow

```
feature/* → domain ← data
    ↓         ↑        ↑
    └──────→ app ←─────┘
```

- Features depend on `domain` and `common`
- `data` implements `domain` interfaces
- `app` wires everything together via Hilt

## State Management

### Unidirectional Data Flow

```
UI → ViewModel → Use Case → Repository → Database
 ↑                                           │
 └───────────────────────────────────────────┘
              (Flow/LiveData)
```

### State Classes

Each screen has a sealed state:

```kotlin
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val profile: UserProfile) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
```

## Dependency Injection (Hilt)

### Module Organization

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(context: Context): MorseVerseDatabase
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindRepository(impl: MorseRepositoryImpl): MorseRepository
}
```

### Scoping

- `SingletonComponent`: Database, Repositories, Audio Engine
- `ViewModelComponent`: ViewModels (automatic via `@HiltViewModel`)
- `ActivityComponent`: Activity-specific dependencies

## Database Schema

### Tables

| Table | Purpose |
|-------|---------|
| `character_progress` | Per-character mastery tracking |
| `practice_sessions` | Session history |
| `character_results` | Individual attempt results |
| `daily_stats` | Aggregated daily statistics |
| `user_profile` | Global user profile |
| `achievements` | Achievement definitions & progress |
| `translation_history` | Translator history |
| `story_progress` | Story mode mission progress |
| `koch_progress` | Koch method lesson progress |

## Audio Architecture

### MorseAudioEngine

```
PCM Generation Pipeline:
1. Calculate timing (dit/dah/space durations)
2. Generate tone samples (sine/square wave)
3. Apply envelope (attack/release for click prevention)
4. Mix with noise (static/rain/weak signal)
5. Output to AudioTrack
```

### Timing Model

```kotlin
dit_duration = 1200 / WPM_ms
dah_duration = dit * 3
symbol_space = dit * 1
char_space = dit * 3
word_space = dit * 7
```

## Canvas Rendering (Morse Tree)

### Render Pipeline

```
1. Calculate tree layout (recursive positioning)
2. Apply transforms (pan/zoom/scale)
3. Draw edges (lines with dot/dash labels)
4. Draw nodes (circles with characters)
5. Draw progress rings (mastery visualization)
6. Draw glow effects (selected/searched)
7. Handle touch events (hit detection)
```

### Performance Optimizations

- **Lazy rendering**: Only draw visible nodes
- **Object pooling**: Reuse paint objects
- **Caching**: Pre-calculate layout on background thread
- **Frame limiting**: Target 60 FPS with Choreographer

## Testing Strategy

### Unit Tests (`test/`)
- Use Case logic
- ViewModel state management
- Data mappers
- Utility functions

### UI Tests (`androidTest/`)
- Screen rendering
- Navigation flows
- Accessibility compliance
- Performance benchmarks

### Integration Tests
- Database operations
- Repository flows
- Audio engine

## Build Variants

| Variant | Debug | Release |
|---------|-------|---------|
| Suffix | `.debug` | (none) |
| Minification | No | Yes |
| Shrink Resources | No | Yes |
| Signing | Debug keystore | Release keystore |
| Logging | Enabled | Disabled |

## CI/CD Pipeline

```
GitHub Actions:
1. Build Debug APK
2. Run Lint
3. Run Unit Tests
4. Build Release APK
5. Build Release AAB
6. Upload Artifacts
7. Create GitHub Release (on tag push)
```

## Future Considerations

### Planned Modules
- `core:ai` - Local AI coaching
- `core:widget` - Android widgets
- `core:notification` - Push notifications
- `feature:leaderboard` - Social features

### Performance Targets
- Cold launch: < 1 second
- Frame rate: 60 FPS
- Memory: < 100 MB baseline
- APK size: < 15 MB
