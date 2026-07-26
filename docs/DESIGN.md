# MorseVerse Design System

## Design Philosophy

### Core Principles

1. **OLED-First**: True black backgrounds for AMOLED displays
2. **Nothing OS Inspired**: Clean, minimal, dot-matrix aesthetic
3. **Material 3 Expressive**: Latest Material Design language
4. **Tactile**: Every interaction feels intentional
5. **Accessible**: High contrast, large text support

---

## Color Palette

### Primary Colors

| Name | Hex | Usage |
|------|-----|-------|
| Morse Cyan | `#00D4FF` | Primary actions, selected states |
| Morse Amber | `#FFB020` | Secondary actions, warnings |
| Morse Violet | `#B388FF` | Tertiary, accents |
| Morse Green | `#22C55E` | Success, correct answers |
| Morse Red | `#EF4444` | Error, incorrect answers |
| Morse Yellow | `#FBBF24` | Warnings, highlights |

### Dark Theme Surfaces

| Name | Hex | Usage |
|------|-----|-------|
| Background | `#000000` | Screen background (OLED) |
| Surface | `#0A0A0A` | Cards, elevated elements |
| Surface Variant | `#141414` | Subtle backgrounds |
| Surface Elevated | `#1A1A1A` | Modals, bottom sheets |
| On Background | `#E5E5E5` | Primary text |
| On Surface | `#E5E5E5` | Secondary text |
| On Surface Variant | `#8A8A8A` | Tertiary text |
| Outline | `#2A2A2A` | Borders, dividers |

### Mastery Level Colors

| Level | Color | Threshold |
|-------|-------|-----------|
| Novice | `#6B7280` | 0% |
| Apprentice | `#3B82F6` | 20% |
| Journeyman | `#8B5CF6` | 40% |
| Expert | `#F59E0B` | 60% |
| Master | `#EF4444` | 80% |
| Grandmaster | `#FFD700` | 95% |

### Heatmap Colors

| Level | Color |
|-------|-------|
| Empty | `#1A1A1A` |
| Level 1 | `#0E4429` |
| Level 2 | `#006D32` |
| Level 3 | `#26A641` |
| Level 4 | `#39D353` |

---

## Typography

### Font Stack

- **Primary**: System default (San Francisco / Roboto)
- **Monospace**: System monospace (for Morse code display)

### Type Scale

| Style | Size | Weight | Usage |
|-------|------|--------|-------|
| Display Large | 57sp | Bold | Hero numbers |
| Display Medium | 45sp | Bold | Large stats |
| Display Small | 36sp | SemiBold | Section heroes |
| Headline Large | 32sp | Bold | Screen titles |
| Headline Medium | 28sp | SemiBold | Section headers |
| Headline Small | 24sp | SemiBold | Card titles |
| Title Large | 22sp | SemiBold | List titles |
| Title Medium | 16sp | Medium | Card subtitles |
| Title Small | 14sp | Medium | Labels |
| Body Large | 16sp | Normal | Body text |
| Body Medium | 14sp | Normal | Secondary text |
| Body Small | 12sp | Normal | Captions |
| Label Large | 14sp | SemiBold | Buttons |
| Label Medium | 12sp | Medium | Tags |
| Label Small | 11sp | Medium | Fine print |

### Morse Code Typography

| Style | Size | Weight | Letter Spacing |
|-------|------|--------|----------------|
| Morse Code Large | 32sp | Bold | 4sp |
| Morse Code | 24sp | Bold | 2sp |
| Morse Code Small | 16sp | Medium | 1sp |
| Character Display | 72sp | Bold | 0sp |

---

## Spacing

### Scale

| Token | Value | Usage |
|-------|-------|-------|
| `spacing_xs` | 4dp | Tight spacing |
| `spacing_sm` | 8dp | Component padding |
| `spacing_md` | 16dp | Card padding, gaps |
| `spacing_lg` | 24dp | Section spacing |
| `spacing_xl` | 32dp | Screen margins |

---

## Corner Radius

### Scale

| Token | Value | Usage |
|-------|-------|-------|
| `radius_sm` | 8dp | Chips, small elements |
| `radius_md` | 12dp | Buttons, inputs |
| `radius_lg` | 16dp | Cards, containers |
| `radius_xl` | 20dp | Large cards |
| `radius_2xl` | 24dp | Bottom sheets |
| `radius_full` | 999dp | Circular elements |

---

## Components

### Cards

- No shadows (flat design)
- Subtle background color differentiation
- 20dp corner radius
- 16-20dp padding
- Optional accent border (1dp)

### Buttons

**Primary**:
- Cyan background
- Black text
- 16dp corner radius
- 56dp height (large), 44dp (medium)

**Secondary**:
- Amber background
- Black text
- Same dimensions as primary

**Outline**:
- Transparent background
- Cyan border (1dp)
- Cyan text

### Navigation

**Bottom Navigation**:
- 80dp height
- No elevation
- Surface background
- Cyan selected indicator
- Label below icon

### Input Fields

- Outlined style
- Cyan focus border
- 16dp corner radius
- Surface variant background

### Progress Bars

- 6-8dp height
- 4dp corner radius
- Cyan/Amber/Green fill
- Surface track

### Chips

- 12dp corner radius
- Surface variant background
- Selected: Primary container

---

## Animations

### Timing

| Type | Duration | Easing |
|------|----------|--------|
| Fast | 150ms | EaseIn |
| Normal | 300ms | EaseInOut |
| Slow | 500ms | EaseOut |
| Extra Slow | 1000ms | EaseInOut |

### Transitions

**Screen Enter**: Slide from right (300ms) + Fade
**Screen Exit**: Slide to left (200ms) + Fade
**Card Press**: Scale to 0.98 (100ms)
**Feedback**: Expand + Color change (300ms)

### Canvas Animations

**Tree Node Appear**: Scale from 0 (300ms, depth-delayed)
**Selected Glow**: Pulsing alpha (1000ms, repeat)
**Zoom**: Smooth interpolation (200ms)
**Pan**: Momentum with deceleration

---

## Icons

### Style

- Material Icons (Outlined for unselected, Filled for selected)
- 20-24dp size
- Match text color or use accent

### Key Icons

| Action | Icon |
|--------|------|
| Home | `home` |
| Learn | `school` |
| Practice | `fitness_center` |
| Tree | `account_tree` |
| Translate | `translate` |
| Audio | `volume_up` |
| Play | `play_arrow` |
| Stop | `stop` |
| Search | `search` |
| Settings | `settings` |
| Back | `arrow_back` |
| Close | `close` |
| Check | `check_circle` |
| Error | `cancel` |
| Warning | `warning` |
| Star | `star` |
| Fire | `local_fire_department` |

---

## Haptic Feedback

| Interaction | Feedback Type |
|-------------|---------------|
| Button tap | TextHandleMove |
| Long press | LongPress |
| Correct answer | TextHandleMove |
| Wrong answer | LongPress |
| Navigation | TextHandleMove |
| Toggle switch | TextHandleMove |
| Slider change | TextHandleMove |

---

## Themes

### Dark (Default)

- OLED black background
- Cyan accents
- High contrast text

### Light

- White background
- Dark cyan accents
- Dark text

### AMOLED

- Pure black everything
- Maximum contrast
- Battery saving

### Material You

- Dynamic color from wallpaper
- System theme integration
- Adaptive accents

---

## Grid System

### Phone

- 16dp margins
- 8dp gutter
- 4dp baseline grid

### Tablet

- 24dp margins
- 12dp gutter
- 8-column grid

### Landscape

- Side navigation (optional)
- Split view for tree
- Compact cards

---

## Empty States

- Centered content
- Subtle icon (48dp)
- Primary message (16sp)
- Secondary message (14sp, onSurfaceVariant)
- Optional action button

---

## Loading States

- Centered CircularProgressIndicator
- Cyan color
- No text (keep it clean)
- Skeleton screens for lists (optional)

---

## Error States

- Red accent
- Error icon
- Clear message
- Retry action
- Surface variant background
