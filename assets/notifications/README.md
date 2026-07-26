# MorseVerse Notifications

## Notification Types

### 1. Daily Reminder
**Purpose**: Remind user to practice
**Schedule**: User-configurable (default: 9:00 AM)
**Channel**: `daily_reminder`

**Content**:
- Title: "Time to practice Morse code! 🔤"
- Body: "You've practiced for X days. Keep your streak alive!"
- Action: Opens practice session

**Priority**: Default
**Sound**: Default notification sound
**Vibration**: Short pulse

### 2. Streak Reminder
**Purpose**: Prevent streak from breaking
**Schedule**: 8:00 PM (if no practice today)
**Channel**: `streak_reminder`

**Content**:
- Title: "Don't lose your streak! 🔥"
- Body: "You have a X-day streak. Practice today to keep it going!"
- Action: Opens practice session

**Priority**: High
**Sound**: Default notification sound
**Vibration**: Short pulse

### 3. Achievement Unlocked
**Purpose**: Celebrate achievements
**Schedule**: Immediate
**Channel**: `achievements`

**Content**:
- Title: "Achievement Unlocked! 🏆"
- Body: "{achievement_name} - {achievement_description}"
- Action: Opens achievements screen

**Priority**: High
**Sound**: Achievement sound
**Vibration**: Success pattern

### 4. Daily Challenge
**Purpose**: Promote daily challenge
**Schedule**: 10:00 AM daily
**Channel**: `daily_challenge`

**Content**:
- Title: "Daily Challenge Ready! 🎯"
- Body: "New challenge available. Earn bonus XP!"
- Action: Opens daily challenge

**Priority**: Default
**Sound**: Default notification sound
**Vibration**: None

### 5. Weekly Summary
**Purpose**: Weekly progress summary
**Schedule**: Sunday 6:00 PM
**Channel**: `weekly_summary`

**Content**:
- Title: "Weekly Summary 📊"
- Body: "This week: X sessions, Y minutes, Z% accuracy"
- Action: Opens statistics screen

**Priority**: Low
**Sound**: None
**Vibration**: None

## Notification Channels

### Channel Configuration

```kotlin
private fun createNotificationChannels() {
    val dailyReminder = NotificationChannel(
        "daily_reminder",
        "Daily Reminder",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Daily practice reminder"
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 250, 100, 250)
    }

    val streakReminder = NotificationChannel(
        "streak_reminder",
        "Streak Reminder",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Streak break prevention"
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 500, 200, 500)
    }

    val achievements = NotificationChannel(
        "achievements",
        "Achievements",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Achievement notifications"
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 100, 100, 100, 100, 100)
    }

    val dailyChallenge = NotificationChannel(
        "daily_challenge",
        "Daily Challenge",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Daily challenge notifications"
        enableVibration(false)
    }

    val weeklySummary = NotificationChannel(
        "weekly_summary",
        "Weekly Summary",
        NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = "Weekly progress summary"
        enableVibration(false)
        setShowBadge(false)
    }
}
```

## Scheduling

### WorkManager Integration

```kotlin
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        showDailyReminderNotification()
        return Result.success()
    }
}

// Schedule daily reminder
val dailyReminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
    1, TimeUnit.DAYS
)
    .setInitialDelay(calculateDelay(9, 0), TimeUnit.MILLISECONDS)
    .build()

WorkManager.getInstance(context)
    .enqueueUniquePeriodicWork(
        "daily_reminder",
        ExistingPeriodicWorkPolicy.KEEP,
        dailyReminderRequest
    )
```

### AlarmManager (Exact Timing)

```kotlin
val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
val intent = Intent(this, ReminderReceiver::class.java)
val pendingIntent = PendingIntent.getBroadcast(
    this, 0, intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

// Set alarm for 9:00 AM daily
val calendar = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 9)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
}

alarmManager.setRepeating(
    AlarmManager.RTC_WAKEUP,
    calendar.timeInMillis,
    AlarmManager.INTERVAL_DAY,
    pendingIntent
)
```

## Notification Actions

### Practice Action
```kotlin
val practiceIntent = Intent(context, MainActivity::class.java).apply {
    action = "OPEN_PRACTICE"
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
}
val practicePendingIntent = PendingIntent.getActivity(
    context, 0, practiceIntent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

val practiceAction = NotificationCompat.Action.Builder(
    R.drawable.ic_practice,
    "Practice Now",
    practicePendingIntent
).build()
```

### Dismiss Action
```kotlin
val dismissIntent = Intent(context, NotificationReceiver::class.java).apply {
    action = "DISMISS_NOTIFICATION"
}
val dismissPendingIntent = PendingIntent.getBroadcast(
    context, 1, dismissIntent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

val dismissAction = NotificationCompat.Action.Builder(
    R.drawable.ic_dismiss,
    "Dismiss",
    dismissPendingIntent
).build()
```

## Notification Styles

### Big Text Style
```kotlin
val bigTextStyle = NotificationCompat.BigTextStyle()
    .bigText("You've practiced for 7 days in a row! Your accuracy has improved by 15% this week.")
    .setBigContentTitle("Weekly Progress")
    .setSummaryText("MorseVerse")
```

### Inbox Style
```kotlin
val inboxStyle = NotificationCompat.InboxStyle()
    .addLine("7-day streak 🔥")
    .addLine("85% accuracy this week")
    .addLine("New achievement unlocked!")
    .setBigContentTitle("Weekly Summary")
    .setSummaryText("MorseVerse")
```

### Progress Style
```kotlin
val progressStyle = NotificationCompat.ProgressStyle()
    .setProgress(45)
    .setMaxProgress(100)
    .setProgressPercentFormat("%.0f%%")
```

## User Preferences

### Notification Settings

```kotlin
// DataStore preferences
val notificationsEnabled: Flow<Boolean> = dataStore.data.map {
    it[NOTIFICATIONS_ENABLED] ?: true
}

val dailyReminderEnabled: Flow<Boolean> = dataStore.data.map {
    it[DAILY_REMINDER_ENABLED] ?: true
}

val reminderTime: Flow<String> = dataStore.data.map {
    it[REMINDER_TIME] ?: "09:00"
}

val streakReminderEnabled: Flow<Boolean> = dataStore.data.map {
    it[STREAK_REMINDER_ENABLED] ?: true
}
```

### Settings UI

```kotlin
@Composable
fun NotificationSettings(
    notificationsEnabled: Boolean,
    dailyReminderEnabled: Boolean,
    reminderTime: String,
    streakReminderEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleDailyReminder: (Boolean) -> Unit,
    onSetReminderTime: (String) -> Unit,
    onToggleStreakReminder: (Boolean) -> Unit
) {
    Column {
        SwitchSetting(
            title = "Enable Notifications",
            checked = notificationsEnabled,
            onCheckedChange = onToggleNotifications
        )

        if (notificationsEnabled) {
            SwitchSetting(
                title = "Daily Reminder",
                checked = dailyReminderEnabled,
                onCheckedChange = onToggleDailyReminder
            )

            if (dailyReminderEnabled) {
                TimePickerSetting(
                    title = "Reminder Time",
                    time = reminderTime,
                    onTimeChange = onSetReminderTime
                )
            }

            SwitchSetting(
                title = "Streak Reminder",
                checked = streakReminderEnabled,
                onCheckedChange = onToggleStreakReminder
            )
        }
    }
}
```

## Testing Notifications

### Debug Notifications
```kotlin
// Show test notification immediately
fun showTestNotification() {
    val notification = NotificationCompat.Builder(context, "daily_reminder")
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("Test Notification")
        .setContentText("This is a test notification from MorseVerse")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(1001, notification)
}
```

### Notification Testing Checklist
- [ ] Notification appears on time
- [ ] Content is correct
- [ ] Actions work properly
- [ ] Sound/vibration is appropriate
- [ ] Tapping opens correct screen
- [ ] Dismissing works
- [ ] Settings are respected
- [ ] Channel configuration is correct

## Best Practices

### Timing
- Don't send notifications at night (10 PM - 8 AM)
- Respect user's time zone
- Allow user to configure timing

### Content
- Keep titles short (< 50 chars)
- Keep body text concise (< 200 chars)
- Use emojis sparingly
- Include clear call-to-action

### Frequency
- Max 1 notification per day (routine)
- Immediate for achievements
- Weekly for summaries

### Battery
- Use WorkManager for scheduling
- Don't wake device unnecessarily
- Respect Do Not Disturb mode

## Permissions

### Android 13+ (API 33+)
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### Runtime Permission
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
}
```

## Troubleshooting

### Notifications Not Appearing
1. Check notification permission
2. Verify channel is created
3. Check notification importance
4. Verify WorkManager scheduling
5. Check Do Not Disturb settings

### Notifications Appearing at Wrong Time
1. Check time zone handling
2. Verify alarm scheduling
3. Check battery optimization
4. Test with adb commands

### Actions Not Working
1. Verify PendingIntent flags
2. Check BroadcastReceiver registration
3. Test with adb commands
4. Check for crashes in logcat
