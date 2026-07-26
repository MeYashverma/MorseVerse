# MorseVerse Developer Guide

## Prerequisites

### Required Software
- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 17** (included with Android Studio)
- **Android SDK 35**
- **Git**

### Optional Tools
- **ADB** for device debugging
- **Firebase CLI** for analytics (if added later)
- **Fastlane** for automated deployment (if added later)

## Getting Started

### 1. Clone Repository
```bash
git clone https://github.com/username/MorseVerse.git
cd MorseVerse
```

### 2. Open in Android Studio
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned directory
4. Wait for Gradle sync to complete

### 3. Run the App
1. Connect an Android device or start an emulator
2. Click the "Run" button (green play icon)
3. Select your device
4. Wait for the app to build and install

## Project Structure

### Build Configuration

#### `build.gradle.kts` (Root)
- Plugin versions
- Repository configuration
- Module includes

#### `gradle/libs.versions.toml`
- Version catalog for dependencies
- Centralized version management

#### `app/build.gradle.kts`
- App-level configuration
- Signing configurations
- Build types (debug/release)
- Dependencies

### Module Organization

```
app/                    → Main application
core/common/            → Shared utilities
core/domain/            → Business logic
core/data/              → Data access
core/designsystem/      → UI components
feature/home/           → Home screen
feature/learn/          → Learning methods
feature/practice/       → Practice modes
feature/morseTree/      → Tree visualization
feature/translator/     → Text ⇄ Morse
feature/decoder/        → Morse decoder
feature/statistics/     → Stats & analytics
feature/achievements/   → Achievement system
feature/story/          → Story mode
feature/ham/            → Ham radio toolkit
```

## Development Workflow

### 1. Create Feature Branch
```bash
git checkout -b feature/your-feature-name
```

### 2. Make Changes
- Follow coding conventions
- Write meaningful commit messages
- Add tests for new functionality

### 3. Test Your Changes
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run lint check
./gradlew lintDebug

# Build debug APK
./gradlew assembleDebug
```

### 4. Submit Pull Request
1. Push your branch to GitHub
2. Create a Pull Request
3. Request review from maintainers
4. Address feedback
5. Merge after approval

## Coding Conventions

### Kotlin Style

#### Naming
- **Classes**: PascalCase (`MorseTreeViewModel`)
- **Functions**: camelCase (`playAudio()`)
- **Variables**: camelCase (`currentCharacter`)
- **Constants**: UPPER_SNAKE_CASE (`SAMPLE_RATE`)
- **Packages**: lowercase (`com.morseverse.core.data`)

#### Documentation
```kotlin
/**
 * Generates PCM audio samples for Morse code
 *
 * @param morse The Morse code string (e.g., ".- -...")
 * @param config Audio configuration (WPM, frequency, volume)
 * @return ShortArray of PCM samples
 */
fun generateMorseAudio(morse: String, config: AudioConfig): ShortArray {
    // Implementation
}
```

#### Compose Functions
```kotlin
/**
 * Displays a practice session with multiple choice answers
 *
 * @param mode The practice mode (CHARACTER, WORD, etc.)
 * @param onNavigateBack Callback for back navigation
 * @param viewModel The practice session ViewModel
 */
@Composable
fun PracticeSessionScreen(
    mode: String,
    onNavigateBack: () -> Unit,
    viewModel: PracticeSessionViewModel = hiltViewModel()
) {
    // Implementation
}
```

### Compose Best Practices

#### State Hoisting
```kotlin
// ❌ Bad - State inside composable
@Composable
fun Counter() {
    var count by remember { mutableIntStateOf(0) }
    Button(onClick = { count++ }) { Text("$count") }
}

// ✅ Good - State hoisted to caller
@Composable
fun Counter(
    count: Int,
    onIncrement: () -> Unit
) {
    Button(onClick = onIncrement) { Text("$count") }
}
```

#### Remember Usage
```kotlin
// ✅ Good - Remember expensive calculations
val sortedList = remember(list) { list.sortedBy { it.name } }

// ✅ Good - Remember derived state
val isVisible by remember { derivedStateOf { scrollState.firstVisibleItemIndex > 0 } }
```

#### Recomposition Optimization
```kotlin
// ✅ Good - Stable parameters
@Composable
fun UserCard(
    name: String,        // String is stable
    age: Int,            // Int is stable
    onClick: () -> Unit  // Lambda is stable if properly remembered
) {
    // Implementation
}
```

## Architecture Guidelines

### Clean Architecture

#### Domain Layer
```kotlin
// Use Case
class GeneratePracticeChallenge @Inject constructor(
    private val repository: MorseRepository
) {
    suspend operator fun invoke(mode: PracticeMode): PracticeChallenge {
        // Business logic
    }
}
```

#### Data Layer
```kotlin
// Repository Implementation
class MorseRepositoryImpl @Inject constructor(
    private val characterProgressDao: CharacterProgressDao
) : MorseRepository {
    override fun getCharacterProgress(character: String): Flow<CharacterProgress> {
        return characterProgressDao.getByCharacter(character).map { it.toDomain() }
    }
}
```

#### Presentation Layer
```kotlin
// ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MorseRepository
) : ViewModel() {
    val userProfile: StateFlow<UserProfile> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())
}
```

### Dependency Injection

#### Module Definition
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MorseVerseDatabase {
        return Room.databaseBuilder(context, MorseVerseDatabase::class.java, "db").build()
    }
}
```

#### Interface Binding
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMorseRepository(impl: MorseRepositoryImpl): MorseRepository
}
```

## Testing

### Unit Tests
```kotlin
class GeneratePracticeChallengeTest {
    @Test
    fun `generate challenge returns correct number of items`() = runTest {
        val challenge = generatePracticeChallenge(PracticeMode.CHARACTER)
        assertEquals(10, challenge.items.size)
    }
}
```

### UI Tests
```kotlin
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysStreak() {
        composeTestRule.setContent {
            MorseVerseTheme {
                HomeScreen(...)
            }
        }
        composeTestRule.onNodeWithText("day streak").assertIsDisplayed()
    }
}
```

## Build Variants

### Debug
- `applicationIdSuffix = ".debug"`
- `isDebuggable = true`
- `isMinifyEnabled = false`

### Release
- `isMinifyEnabled = true`
- `isShrinkResources = true`
- Signed with release keystore

## Signing Configuration

### Debug
Uses default debug keystore.

### Release
Configure in `local.properties` or environment variables:
```properties
KEYSTORE_PATH=keystore/release.jks
KEYSTORE_PASSWORD=your_password
KEY_ALIAS=morseverse
KEY_PASSWORD=your_key_password
```

## Troubleshooting

### Gradle Sync Failed
1. Check internet connection
2. Invalidate caches: File → Invalidate Caches
3. Delete `.gradle` directory
4. Re-import project

### Build Failed
1. Check error messages in Build output
2. Run `./gradlew assembleDebug --stacktrace`
3. Check for missing dependencies
4. Verify SDK versions

### Emulator Issues
1. Wipe emulator data
2. Cold boot emulator
3. Check system requirements
4. Update emulator images

## Resources

### Documentation
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3](https://m3.material.io/)
- [Android Architecture](https://developer.android.com/topic/architecture)

### Tools
- [Android Studio](https://developer.android.com/studio)
- [Gradle](https://gradle.org/)
- [Hilt](https://dagger.dev/hilt/)
- [Room](https://developer.android.com/training/data-storage/room)

## Support

### Questions
- Open a GitHub Issue
- Check existing documentation
- Search for similar issues

### Bug Reports
Include:
- Steps to reproduce
- Expected behavior
- Actual behavior
- Device/OS information
- Screenshots if applicable

### Feature Requests
Include:
- Description of feature
- Use case
- Expected behavior
- Any alternatives considered
