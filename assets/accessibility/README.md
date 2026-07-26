# MorseVerse Accessibility Guide

## Accessibility Features

### Visual Accessibility

#### Large Text Support
- All text scales with system font size
- Minimum text size: 12sp
- Maximum text size: 72sp (character display)
- Line height scales proportionally

#### High Contrast Mode
- Increases text contrast to WCAG AAA
- Uses bolder font weights
- Adds borders to interactive elements
- Increases color saturation

#### Color Blind Themes
- **Protanopia**: Red-weak (1% of males)
- **Deuteranopia**: Green-weak (1% of males)
- **Tritanopia**: Blue-weak (rare)
- Uses patterns + icons + text, never color alone

#### OLED/AMOLED Optimization
- True black backgrounds
- Maximum contrast ratios
- Reduced power consumption

### Motor Accessibility

#### Large Touch Targets
- Minimum: 48dp × 48dp
- Recommended: 56dp × 56dp
- Adequate spacing between targets

#### One-Handed Mode
- UI elements reachable with thumb
- Bottom-aligned actions
- Swipe gestures for navigation

#### Left-Handed Mode
- Mirrors UI layout
- Actions on left side
- Navigation on left side

### Cognitive Accessibility

#### Simple Language
- Clear, concise instructions
- Consistent terminology
- Progressive disclosure

#### Predictable Behavior
- Consistent navigation
- Standard interactions
- Clear feedback

#### Error Prevention
- Confirmation dialogs for destructive actions
- Undo functionality
- Clear error messages

### Screen Reader Support (TalkBack)

#### Content Descriptions
All interactive elements have content descriptions:

```kotlin
IconButton(
    onClick = { /* ... */ },
    modifier = Modifier.semantics {
        contentDescription = "Play audio"
    }
) {
    Icon(Icons.Filled.VolumeUp, contentDescription = null)
}
```

#### Semantic Properties
```kotlin
Modifier.semantics {
    heading()
    stateDescription = "Completed"
    role = Role.Button
}
```

#### Live Regions
For dynamic content updates:
```kotlin
Modifier.semantics {
    liveRegion = LiveRegionMode.Polite
}
```

### Hearing Accessibility

#### Visual Feedback
- All audio has visual representation
- Waveform visualization
- Morse code display (always visible)
- Haptic feedback for important events

#### Captions
- Audio playback shows Morse code
- Visual timer for timed challenges
- Progress indicators for audio

## WCAG Compliance

### Level AA
- Color contrast ratio ≥ 4.5:1 (normal text)
- Color contrast ratio ≥ 3:1 (large text)
- Touch targets ≥ 44×44px
- Text resizable to 200%

### Level AAA
- Color contrast ratio ≥ 7:1 (normal text)
- Color contrast ratio ≥ 4.5:1 (large text)
- Touch targets ≥ 48×48dp

## Testing Accessibility

### Manual Testing
1. Enable TalkBack
2. Navigate entire app
3. Verify all elements have descriptions
4. Check focus order
5. Test with large text
6. Test with high contrast

### Automated Testing
```kotlin
@Test
fun accessibilityCheck() {
    composeTestRule.setContent {
        MorseVerseTheme {
            HomeScreen(...)
        }
    }

    composeTestRule.onRoot().assertHasNoClickAction()
    composeTestRule.onNodeWithContentDescription("Play audio").assertIsDisplayed()
}
```

### Tools
- **Accessibility Scanner** (Android)
- **Espresso Accessibility Checks**
- **Compose Accessibility Semantics**

## Implementation Checklist

### Screen Level
- [ ] All images have content descriptions
- [ ] All buttons have content descriptions
- [ ] Focus order is logical
- [ ] Color is not sole indicator
- [ ] Text scales properly
- [ ] Touch targets are large enough

### Component Level
- [ ] Interactive elements are focusable
- [ ] State changes are announced
- [ ] Errors are announced
- [ ] Progress is announced
- [ ] Dynamic content uses live regions

### Theme Level
- [ ] High contrast theme available
- [ ] Color blind modes available
- [ ] Large text support
- [ ] Reduced motion option

## Resources

- [Android Accessibility Guide](https://developer.android.com/guide/topics/ui/accessibility)
- [Material Design Accessibility](https://m3.material.io/foundations/accessibility/overview)
- [WCAG 2.1 Guidelines](https://www.w3.org/TR/WCAG21/)
- [Compose Accessibility](https://developer.android.com/jetpack/compose/accessibility)
