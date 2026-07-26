# MorseVerse Fonts

## Font Stack

MorseVerse uses the system default font stack for optimal performance and consistency.

### Primary Font
- **Android**: Roboto
- **Fallback**: Noto Sans, sans-serif

### Monospace Font
Used for Morse code display
- **Android**: Droid Sans Mono
- **Fallback**: monospace

### Display Font
Used for large numbers and hero elements
- **Android**: Roboto (Bold)
- **Fallback**: sans-serif

## Typography Scale

| Style | Size | Weight | Line Height | Letter Spacing |
|-------|------|--------|-------------|----------------|
| Display Large | 57sp | Bold | 64sp | -0.25sp |
| Display Medium | 45sp | Bold | 52sp | 0sp |
| Display Small | 36sp | SemiBold | 44sp | 0sp |
| Headline Large | 32sp | Bold | 40sp | 0sp |
| Headline Medium | 28sp | SemiBold | 36sp | 0sp |
| Headline Small | 24sp | SemiBold | 32sp | 0sp |
| Title Large | 22sp | SemiBold | 28sp | 0sp |
| Title Medium | 16sp | Medium | 24sp | 0.15sp |
| Title Small | 14sp | Medium | 20sp | 0.1sp |
| Body Large | 16sp | Normal | 24sp | 0.5sp |
| Body Medium | 14sp | Normal | 20sp | 0.25sp |
| Body Small | 12sp | Normal | 16sp | 0.4sp |
| Label Large | 14sp | SemiBold | 20sp | 0.1sp |
| Label Medium | 12sp | Medium | 16sp | 0.5sp |
| Label Small | 11sp | Medium | 16sp | 0.5sp |

## Morse Code Typography

| Style | Size | Weight | Letter Spacing |
|-------|------|--------|----------------|
| Morse Code Large | 32sp | Bold | 4sp |
| Morse Code | 24sp | Bold | 2sp |
| Morse Code Small | 16sp | Medium | 1sp |
| Character Display | 72sp | Bold | 0sp |

## Custom Fonts

If custom fonts are desired in the future, place TTF/OTF files in this directory.

### Suggested Fonts
- **Inter** - Modern sans-serif
- **JetBrains Mono** - Monospace for code
- **Space Grotesk** - Display font

### Adding Custom Fonts

1. Add font files to `assets/fonts/`
2. Create font family in `res/font/`
3. Update Typography in `Type.kt`

```xml
<!-- res/font/custom_font.xml -->
<?xml version="1.0" encoding="utf-8"?>
<font-family xmlns:app="http://schemas.android.com/apk/res-auto">
    <font
        app:fontStyle="normal"
        app:fontWeight="400"
        app:font="@font/custom_font_regular" />
    <font
        app:fontStyle="normal"
        app:fontWeight="700"
        app:font="@font/custom_font_bold" />
</font-family>
```

## Accessibility

### Large Text Support
- All text scales with system font size
- Minimum touch target: 48dp
- Line height scales proportionally

### High Contrast Mode
- Increases text contrast
- Uses bolder weights
- Adds text shadows if needed

### Color Blind Support
- Never relies on color alone
- Uses icons + text + color
- Provides alternative indicators
