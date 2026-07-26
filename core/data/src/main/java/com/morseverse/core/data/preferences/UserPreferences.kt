package com.morseverse.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "morseverse_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val COLOR_BLIND_MODE = stringPreferencesKey("color_blind_mode")
        val DAILY_GOAL_MINUTES = intPreferencesKey("daily_goal_minutes")
        val DEFAULT_WPM = intPreferencesKey("default_wpm")
        val DEFAULT_FREQUENCY = intPreferencesKey("default_frequency")
        val DEFAULT_VOLUME = floatPreferencesKey("default_volume")
        val FARNSWORTH_SPACING = booleanPreferencesKey("farnsworth_spacing")
        val FARNSWORTH_WPM = intPreferencesKey("farnsworth_wpm")
        val NOISE_TYPE = stringPreferencesKey("noise_type")
        val NOISE_LEVEL = floatPreferencesKey("noise_level")
        val TONE_TYPE = stringPreferencesKey("tone_type")
        val INPUT_METHOD = stringPreferencesKey("input_method")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
        val SHOW_TUTORIAL = booleanPreferencesKey("show_tutorial")
        val LAST_KOCH_LESSON = intPreferencesKey("last_koch_lesson")
        val LEARNING_METHOD = stringPreferencesKey("learning_method")
        val LARGE_TEXT = booleanPreferencesKey("large_text")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val LEFT_HANDED = booleanPreferencesKey("left_handed")
        val ONE_HANDED_MODE = booleanPreferencesKey("one_handed_mode")
    }

    // Theme
    val themeMode: Flow<String> = dataStore.data.map { it[THEME_MODE] ?: "DARK" }
    val dynamicColor: Flow<Boolean> = dataStore.data.map { it[DYNAMIC_COLOR] ?: false }
    val colorBlindMode: Flow<String> = dataStore.data.map { it[COLOR_BLIND_MODE] ?: "NONE" }

    // Learning
    val dailyGoalMinutes: Flow<Int> = dataStore.data.map { it[DAILY_GOAL_MINUTES] ?: 15 }
    val defaultWpm: Flow<Int> = dataStore.data.map { it[DEFAULT_WPM] ?: 20 }
    val defaultFrequency: Flow<Int> = dataStore.data.map { it[DEFAULT_FREQUENCY] ?: 600 }
    val defaultVolume: Flow<Float> = dataStore.data.map { it[DEFAULT_VOLUME] ?: 0.8f }
    val farnsworthSpacing: Flow<Boolean> = dataStore.data.map { it[FARNSWORTH_SPACING] ?: false }
    val farnsworthWpm: Flow<Int> = dataStore.data.map { it[FARNSWORTH_WPM] ?: 15 }
    val noiseType: Flow<String> = dataStore.data.map { it[NOISE_TYPE] ?: "NONE" }
    val noiseLevel: Flow<Float> = dataStore.data.map { it[NOISE_LEVEL] ?: 0f }
    val toneType: Flow<String> = dataStore.data.map { it[TONE_TYPE] ?: "SINE" }
    val inputMethod: Flow<String> = dataStore.data.map { it[INPUT_METHOD] ?: "TOUCH" }
    val learningMethod: Flow<String> = dataStore.data.map { it[LEARNING_METHOD] ?: "KOCH" }
    val lastKochLesson: Flow<Int> = dataStore.data.map { it[LAST_KOCH_LESSON] ?: 0 }

    // UI
    val hapticsEnabled: Flow<Boolean> = dataStore.data.map { it[HAPTICS_ENABLED] ?: true }
    val soundEnabled: Flow<Boolean> = dataStore.data.map { it[SOUND_ENABLED] ?: true }
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }
    val notificationTime: Flow<String> = dataStore.data.map { it[NOTIFICATION_TIME] ?: "09:00" }
    val showTutorial: Flow<Boolean> = dataStore.data.map { it[SHOW_TUTORIAL] ?: true }
    val largeText: Flow<Boolean> = dataStore.data.map { it[LARGE_TEXT] ?: false }
    val highContrast: Flow<Boolean> = dataStore.data.map { it[HIGH_CONTRAST] ?: false }
    val leftHanded: Flow<Boolean> = dataStore.data.map { it[LEFT_HANDED] ?: false }
    val oneHandedMode: Flow<Boolean> = dataStore.data.map { it[ONE_HANDED_MODE] ?: false }

    // Setters
    suspend fun setThemeMode(mode: String) = dataStore.edit { it[THEME_MODE] = mode }
    suspend fun setDynamicColor(enabled: Boolean) = dataStore.edit { it[DYNAMIC_COLOR] = enabled }
    suspend fun setColorBlindMode(mode: String) = dataStore.edit { it[COLOR_BLIND_MODE] = mode }
    suspend fun setDailyGoalMinutes(minutes: Int) = dataStore.edit { it[DAILY_GOAL_MINUTES] = minutes }
    suspend fun setDefaultWpm(wpm: Int) = dataStore.edit { it[DEFAULT_WPM] = wpm }
    suspend fun setDefaultFrequency(freq: Int) = dataStore.edit { it[DEFAULT_FREQUENCY] = freq }
    suspend fun setDefaultVolume(vol: Float) = dataStore.edit { it[DEFAULT_VOLUME] = vol }
    suspend fun setFarnsworthSpacing(enabled: Boolean) = dataStore.edit { it[FARNSWORTH_SPACING] = enabled }
    suspend fun setFarnsworthWpm(wpm: Int) = dataStore.edit { it[FARNSWORTH_WPM] = wpm }
    suspend fun setNoiseType(type: String) = dataStore.edit { it[NOISE_TYPE] = type }
    suspend fun setNoiseLevel(level: Float) = dataStore.edit { it[NOISE_LEVEL] = level }
    suspend fun setToneType(type: String) = dataStore.edit { it[TONE_TYPE] = type }
    suspend fun setInputMethod(method: String) = dataStore.edit { it[INPUT_METHOD] = method }
    suspend fun setHapticsEnabled(enabled: Boolean) = dataStore.edit { it[HAPTICS_ENABLED] = enabled }
    suspend fun setSoundEnabled(enabled: Boolean) = dataStore.edit { it[SOUND_ENABLED] = enabled }
    suspend fun setNotificationsEnabled(enabled: Boolean) = dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    suspend fun setNotificationTime(time: String) = dataStore.edit { it[NOTIFICATION_TIME] = time }
    suspend fun setShowTutorial(show: Boolean) = dataStore.edit { it[SHOW_TUTORIAL] = show }
    suspend fun setLastKochLesson(lesson: Int) = dataStore.edit { it[LAST_KOCH_LESSON] = lesson }
    suspend fun setLearningMethod(method: String) = dataStore.edit { it[LEARNING_METHOD] = method }
    suspend fun setLargeText(enabled: Boolean) = dataStore.edit { it[LARGE_TEXT] = enabled }
    suspend fun setHighContrast(enabled: Boolean) = dataStore.edit { it[HIGH_CONTRAST] = enabled }
    suspend fun setLeftHanded(enabled: Boolean) = dataStore.edit { it[LEFT_HANDED] = enabled }
    suspend fun setOneHandedMode(enabled: Boolean) = dataStore.edit { it[ONE_HANDED_MODE] = enabled }
}
