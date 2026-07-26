# MorseVerse Screens Documentation

## Screen Inventory

### 1. Home Screen
**Route**: `home`
**Purpose**: Dashboard showing user progress and quick actions

**Components**:
- Streak & Daily Goal Card
- Quick Stats Row (Accuracy, WPM, Characters, Sessions)
- Continue Lesson Card (animated glow)
- Quick Actions (Practice, Translate, Tree, Story)
- Weak Characters horizontal scroll
- Today's Challenge Card
- Feature Cards Grid (Learning, Statistics, Ham Radio, Achievements)
- Recent Activity list

**Data Sources**:
- `UserProfile` (streak, XP, daily goal)
- `DailyStats` (practice minutes)
- `CharacterProgress` (weak characters)
- `PracticeSession` (recent activity)

---

### 2. Learn Screen
**Route**: `learn`
**Purpose**: Select learning method and view progress

**Components**:
- Learning Method selector (Koch, Farnsworth, Traditional, Adaptive, Story)
- Progress Overview Card
- Morse Tree quick access card
- Lesson list (character groups)
- Alphabet Grid reference

**Learning Methods**:
- **Koch**: 2 characters at a time, full speed
- **Farnsworth**: Standard characters with extra spacing
- **Traditional**: Character groups (EISH, TMOW, etc.)
- **Adaptive**: AI-driven personalized

---

### 3. Practice Screen
**Route**: `practice`
**Purpose**: Select practice mode

**Practice Modes**:
- Character Practice
- Word Practice
- Sentence Practice
- Random Practice
- Weak Characters
- Custom Character Set
- Common Words
- Callsign Practice
- Contest Practice
- Timed Challenge
- Daily Challenge
- Infinite Mode

---

### 4. Practice Session Screen
**Route**: `practice_session/{mode}`
**Purpose**: Active practice session

**Components**:
- Progress bar
- Stats row (Accuracy, Streak, WPM)
- Morse code display (animated transitions)
- Audio playback button
- Multiple choice buttons (4 options)
- Feedback animation (correct/incorrect)
- Hint & Skip buttons
- Session completion screen with results

**Flow**:
1. Load practice challenge
2. Display Morse code
3. Auto-play audio
4. User selects answer
5. Show feedback
6. Update character progress
7. Auto-advance
8. Show completion stats

---

### 5. Morse Tree Screen
**Route**: `morse_tree`
**Purpose**: Interactive binary tree visualization

**Components**:
- Canvas-rendered tree (infinite, zoomable, pannable)
- Search functionality
- Zoom controls (+, -, reset)
- Legend overlay (mastery levels)
- Zoom indicator
- Node detail bottom sheet

**Interactions**:
- **Tap**: Select node, show details
- **Pinch**: Zoom in/out
- **Pan**: Move around tree
- **Search**: Highlight matching characters

**Rendering**:
- 60 FPS Canvas rendering
- Progressive node appearance (animated)
- Edge labels (· or —)
- Progress rings (mastery color)
- Glow effects (selected/searched nodes)

---

### 6. Translator Screen
**Route**: `translator`
**Purpose**: Text ⇄ Morse translation

**Components**:
- Direction toggle (Text→Morse / Morse→Text)
- Input field
- Swap button
- Output display
- Action buttons (Play Audio, Flashlight, Save)
- Quick Reference grid

**Features**:
- Real-time translation
- Audio playback
- Flashlight output
- Copy/Paste/Share
- Save to favorites
- Translation history

---

### 7. Decoder Screen
**Route**: `decoder`
**Purpose**: Decode Morse from various sources

**Components**:
- Source selector (Microphone, Manual, Clipboard, File)
- Waveform visualization
- Confidence meter
- Raw Morse output
- Decoded text output
- Action buttons (context-dependent)

**Sources**:
- **Microphone**: Live audio decoding
- **Manual**: Type Morse code
- **Clipboard**: Read from clipboard
- **File**: Audio file input

---

### 8. Statistics Screen
**Route**: `statistics`
**Purpose**: Progress tracking and analytics

**Components**:
- Tab selector (Overview, Weekly, Monthly, Heatmap)
- Overview stat cards (XP, Sessions, Accuracy, WPM)
- Accuracy line chart
- Speed bar chart
- Practice heatmap (GitHub-style)
- Weak/Strong character lists
- Reaction time display

---

### 9. Achievements Screen
**Route**: `achievements`
**Purpose**: View and track achievements

**Components**:
- Progress summary (unlocked/total)
- Achievement cards with progress bars

**Achievement Categories**:
- Milestone (first steps, centurion)
- Streak (7-day, 30-day)
- Accuracy (perfect 10, 95%+ accuracy)
- Speed (20 WPM, 30 WPM, 40 WPM)
- Explorer (try all modes)
- Master (alphabet master, word master)
- Special (night owl, early bird)

---

### 10. Story Screen
**Route**: `story`
**Purpose**: Learn through interactive missions

**Components**:
- Intro card
- Mission cards with progress

**Missions**:
1. **SOS Rescue** (K, M, R, S, U, E)
   - Chapter 1: The Signal
   - Chapter 2: Quick Response
   - Chapter 3: More Letters
   - Chapter 4: The Rescue

2. **Spy Mission** (A, I, N, R, W, D)
   - Chapter 1: The Briefing
   - Chapter 2: First Intercept
   - Chapter 3: Double Agent
   - Chapter 4: Extraction

3. **Space Mission** (H, L, P, B, V, K)
   - Chapter 1: Lost in Space
   - Chapter 2: The Message
   - Chapter 3: Rescue Plan
   - Chapter 4: Homecoming

---

### 11. Ham Radio Screen
**Route**: `ham`
**Purpose**: Ham radio reference and practice

**Components**:
- Tab selector (Phonetic, Q Codes, Abbreviations)
- Phonetic alphabet list
- Q Codes reference
- Common abbreviations list

---

### 12. Character Detail Screen
**Route**: `character/{character}`
**Purpose**: Detailed view of a single character

**Components**:
- Large character display (animated)
- Morse code representation
- Mastery progress bar
- Stats (Accuracy, Attempts, Streak)
- Memory tip
- Action buttons (Play Audio, Practice)
- Related characters

---

### 13. Settings Screen
**Route**: `settings`
**Purpose**: App configuration

**Sections**:
- **Audio**: WPM, Frequency, Volume, Tone Type
- **Learning**: Method, Daily Goal, Farnsworth Spacing
- **Interface**: Theme, Haptics, Large Text, High Contrast
- **Notifications**: Daily Reminder, Streak Reminder
- **About**: Version, Licenses, Rate

---

### 14. About Screen
**Route**: `about`
**Purpose**: App information

**Components**:
- App icon
- App name & version
- Description
- Feature highlights
- Credits

---

## Navigation Structure

```
Bottom Navigation:
├── Home (home)
├── Learn (learn)
├── Practice (practice)
├── Tree (morse_tree)
└── Translate (translator)

Stack Navigation:
├── character/{character}
├── practice_session/{mode}
├── story_mission/{missionId}
├── statistics
├── achievements
├── story
├── ham
├── decoder
├── settings
└── about
```

## Animations

### Transitions
- **Enter**: Slide in from right + fade in (300ms)
- **Exit**: Slide out to left + fade out (200ms)
- **Pop Enter**: Slide in from left + fade in (300ms)
- **Pop Exit**: Slide out to right + fade out (200ms)

### Component Animations
- **Morse code transitions**: Scale + fade (300ms)
- **Feedback**: Expand/shrink with color change
- **Cards**: Subtle elevation change on press
- **Progress bars**: Animated fill
- **Streak counter**: Bounce animation

### Canvas Animations
- **Tree nodes**: Progressive appearance (depth-based delay)
- **Selected node**: Pulsing glow
- **Zoom**: Smooth interpolation
- **Pan**: Momentum-based with deceleration

## Haptic Feedback

| Action | Haptic Type |
|--------|-------------|
| Tap button | TextHandleMove |
| Long press | LongPress |
| Correct answer | TextHandleMove |
| Wrong answer | LongPress |
| Navigation | TextHandleMove |
| Toggle switch | TextHandleMove |

## Accessibility

- **Content descriptions** on all interactive elements
- **Minimum touch targets**: 48dp
- **Color contrast**: WCAG AA compliant
- **Large text support**: Scalable typography
- **TalkBack support**: Proper semantics
- **Landscape support**: Responsive layouts
- **Tablet support**: Adaptive grid layouts
