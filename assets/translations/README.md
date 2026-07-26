# MorseVerse Translations

## Localization Status

| Language | Status | Completion |
|----------|--------|------------|
| English (en) | ✅ Complete | 100% |
| Spanish (es) | 🚧 Planned | 0% |
| French (fr) | 🚧 Planned | 0% |
| German (de) | 🚧 Planned | 0% |
| Japanese (ja) | 🚧 Planned | 0% |
| Chinese (zh) | 🚧 Planned | 0% |
| Portuguese (pt) | 🚧 Planned | 0% |
| Russian (ru) | 🚧 Planned | 0% |
| Arabic (ar) | 🚧 Planned | 0% |
| Hindi (hi) | 🚧 Planned | 0% |

## Adding Translations

### 1. Create String Resource File

Create `app/src/main/res/values-{language}/strings.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">MorseVerse</string>
    <string name="nav_home">Inicio</string>
    <string name="nav_learn">Aprender</string>
    <!-- ... more strings -->
</resources>
```

### 2. Update Translation Status

Update this README with the new language status.

### 3. Test

- Run the app with the new locale
- Verify all strings are translated
- Check for truncation or layout issues

## Translation Guidelines

### Do's
- Keep translations concise
- Preserve formatting placeholders (%s, %d)
- Maintain technical accuracy
- Use local conventions for numbers/dates

### Don'ts
- Don't translate Morse code characters
- Don't translate proper nouns (MorseVerse, Koch Method)
- Don't translate technical terms (WPM, QSO)
- Don't change string resource names

## Placeholder Strings

Some strings contain placeholders:

```xml
<string name="xp_earned">+%d XP</string>
<string name="characters_practiced">%d/%d characters</string>
<string name="accuracy_percent">%d%% accuracy</string>
```

Ensure placeholders are preserved in translations.

## RTL Support

For RTL languages (Arabic, Hebrew):

1. Add to `AndroidManifest.xml`:
```xml
<application android:supportsRtl="true">
```

2. Use start/end instead of left/right:
```kotlin
Modifier.padding(start = 16.dp) // Not left
```

3. Mirror icons if needed

## Contributing

To contribute translations:

1. Fork the repository
2. Create a new branch
3. Add translation files
4. Submit a Pull Request

## Resources

- [Android Localization Guide](https://developer.android.com/guide/topics/resources/localization)
- [Material Design Localization](https://m3.material.io/foundations/content-design/overview)
