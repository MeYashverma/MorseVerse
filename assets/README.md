# MorseVerse Assets

## App Icon

The app icon features:
- Dark background (true black for OLED)
- Morse code "MV" initials
- Decorative tree node dots
- Cyan and amber accent colors
- Clean, minimal design

## Adaptive Icon

- **Background**: Dark gradient with subtle grid pattern
- **Foreground**: Morse code characters with tree nodes
- **Shape**: Adaptive (circle, squircle, etc.)

## Color Scheme

```json
{
  "primary": "#00D4FF",
  "secondary": "#FFB020",
  "tertiary": "#B388FF",
  "success": "#22C55E",
  "error": "#EF4444",
  "background": "#000000",
  "surface": "#0A0A0A"
}
```

## Screenshots

Screenshots are located in the `screenshots/` directory:

| Filename | Description |
|----------|-------------|
| `home.png` | Home screen dashboard |
| `tree.png` | Interactive Morse tree |
| `practice.png` | Practice session |
| `translator.png` | Text ⇄ Morse translator |
| `statistics.png` | Statistics & analytics |
| `story.png` | Story mode missions |
| `ham.png` | Ham radio toolkit |
| `settings.png` | App settings |

## Generating Screenshots

1. Run the app on a device/emulator
2. Navigate to each screen
3. Take screenshots (Power + Volume Down)
4. Save to `screenshots/` directory

## Design Guidelines

- Use OLED black background in screenshots
- Show populated data (not empty states)
- Capture at 1080x2400 resolution
- Use dark theme for consistency

## Icon Specifications

### Launcher Icon
- **Size**: 108x108dp (432x432px at xxxhdpi)
- **Background**: Adaptive icon background
- **Foreground**: Adaptive icon foreground
- **Shape**: Adaptive

### Notification Icon
- **Size**: 24x24dp
- **Color**: White on transparent
- **Style**: Simple, recognizable

### Feature Graphic (Play Store)
- **Size**: 1024x500px
- **Content**: App name + tagline + key features
- **Colors**: Dark background, cyan accents

## File Formats

- **Icons**: XML vector drawables
- **Screenshots**: PNG
- **Animations**: Lottie JSON (optional)
- **Sounds**: WAV/MP3 (for Morse audio preview)
