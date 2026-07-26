# MorseVerse Android Widgets

## Widget Types

### 1. Today's Practice Widget
**Size**: 3×2 cells (180dp × 110dp)
**Purpose**: Shows today's practice characters and progress

**Features**:
- Displays current practice characters
- Shows daily goal progress bar
- Tap to open practice session
- Auto-updates every hour

**Layout**:
```
┌──────────────────────┐
│ Today's Practice     │
│                      │
│ K   M   R   S        │
│                      │
│ ████████░░░░ 45%     │
└──────────────────────┘
```

### 2. Random Character Widget
**Size**: 2×2 cells (110dp × 110dp)
**Purpose**: Shows a random Morse character for quick practice

**Features**:
- Random letter/number display
- Morse code representation
- Tap to refresh character
- Tap and hold to play audio
- Auto-updates every 30 minutes

**Layout**:
```
┌──────────────┐
│ Random Char  │
│              │
│      A       │
│     ·—       │
│              │
└──────────────┘
```

### 3. Current Streak Widget
**Size**: 2×1 cells (110dp × 55dp)
**Purpose**: Shows current practice streak

**Features**:
- Streak count display
- Fire emoji indicator
- Tap to open app
- Real-time updates

**Layout**:
```
┌──────────────┐
│ 🔥  7 days  │
└──────────────┘
```

### 4. Quick Translate Widget
**Size**: 4×2 cells (250dp × 110dp)
**Purpose**: Quick Morse code translation

**Features**:
- Text input field
- Morse output display
- Swap direction button
- Copy result button
- Play audio button

**Layout**:
```
┌────────────────────────────────┐
│ Quick Translate                │
│                                │
│ [Type text here...    ]        │
│                                │
│ ·— —··· —·—· —·· ·             │
│                                │
│ [⇄] [Copy] [🔊]               │
└────────────────────────────────┘
```

### 5. Daily Goal Widget
**Size**: 2×1 cells (110dp × 55dp)
**Purpose**: Shows daily goal progress

**Features**:
- Minutes practiced today
- Goal progress bar
- Tap to open app

**Layout**:
```
┌──────────────┐
│ 12/15 min    │
└──────────────┘
```

## Widget Configuration

### XML Configuration

Each widget has a configuration file in `res/xml/`:

```xml
<!-- today_practice_widget_info.xml -->
<appwidget-provider
    android:minWidth="180dp"
    android:minHeight="110dp"
    android:updatePeriodMillis="3600000"
    android:initialLayout="@layout/widget_today_practice"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="home_screen" />
```

### Layout Files

Widget layouts are in `res/layout/`:

```xml
<!-- widget_today_practice.xml -->
<FrameLayout>
    <LinearLayout>
        <TextView android:text="Today's Practice" />
        <TextView android:text="K M R S" />
        <ProgressBar />
    </LinearLayout>
</FrameLayout>
```

## Widget Providers

### TodayPracticeWidget
```kotlin
class TodayPracticeWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}
```

### RandomCharacterWidget
```kotlin
class RandomCharacterWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}
```

## Widget Updates

### Update Frequency
| Widget | Update Interval |
|--------|----------------|
| Today's Practice | 1 hour |
| Random Character | 30 minutes |
| Current Streak | 15 minutes |
| Quick Translate | On interaction |
| Daily Goal | 15 minutes |

### Force Update
```kotlin
val intent = Intent(context, TodayPracticeWidget::class.java).apply {
    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
}
context.sendBroadcast(intent)
```

## Widget Styling

### Colors
- Background: `#000000` (OLED black)
- Text: `#FFFFFF` (White)
- Accent: `#00D4FF` (Cyan)
- Progress: `#22C55E` (Green)

### Corner Radius
- Widget: 16dp
- Cards: 12dp
- Buttons: 8dp

### Typography
- Title: 14sp, Bold
- Body: 16sp, Normal
- Label: 12sp, Normal

## Widget Interactions

### Click Actions
- **Widget body**: Opens MorseVerse app
- **Character**: Plays audio
- **Refresh button**: Updates content
- **Translate button**: Opens translator

### Long Press Actions
- **Character**: Shows character details
- **Widget**: Opens widget configuration

## Widget Best Practices

### Performance
- Use RemoteViews (not Compose)
- Minimize layout complexity
- Cache data locally
- Update only when necessary

### Battery
- Use appropriate update intervals
- Don't update when screen is off
- Use WorkManager for updates

### User Experience
- Clear, readable text
- Consistent with app theme
- Intuitive interactions
- Proper error handling

## Adding New Widgets

1. Create layout XML in `res/layout/`
2. Create info XML in `res/xml/`
3. Create AppWidgetProvider class
4. Register in AndroidManifest.xml
5. Add to widget picker
6. Test on various screen sizes

## Troubleshooting

### Widget Not Updating
- Check update interval
- Verify AppWidgetProvider is registered
- Check for errors in logcat

### Widget Not Appearing
- Verify AndroidManifest registration
- Check widget info XML
- Clear launcher cache

### Layout Issues
- Use fixed dimensions
- Test on multiple screen sizes
- Avoid complex layouts
