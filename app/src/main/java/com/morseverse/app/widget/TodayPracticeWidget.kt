package com.morseverse.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.morseverse.app.MainActivity
import com.morseverse.app.R
import com.morseverse.core.domain.repository.MorseRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

/**
 * Home-screen widget showing today's practice characters and daily-goal progress.
 */
@AndroidEntryPoint
class TodayPracticeWidget : AppWidgetProvider() {

    @Inject
    lateinit var repository: MorseRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (appWidgetId in appWidgetIds) {
                    updateWidget(context, appWidgetManager, appWidgetId)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val todaysLesson = repository.getTodaysLesson().first()
        val dailyStats = repository.getDailyStats(today).first()
        val profile = repository.getUserProfile().first()

        val goalMinutes = profile.dailyGoalMinutes.coerceAtLeast(1)
        val percent = ((dailyStats.totalPracticeMinutes.toFloat() / goalMinutes) * 100f)
            .toInt()
            .coerceIn(0, 100)
        val charactersText = if (todaysLesson.isNotEmpty()) {
            todaysLesson.joinToString(" ")
        } else {
            "No lesson yet"
        }

        val views = RemoteViews(context.packageName, R.layout.widget_today_practice).apply {
            setTextViewText(R.id.widget_practice_characters, charactersText)
            setProgressBar(R.id.widget_practice_progress, 100, percent, false)
            setTextViewText(R.id.widget_practice_percent, "$percent% complete")

            val openAppIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setOnClickPendingIntent(R.id.widget_today_practice_root, pendingIntent)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
