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
import javax.inject.Inject

/**
 * Home-screen widget showing a random Morse character for quick practice.
 * Refreshes automatically on the interval set in random_character_widget_info.xml,
 * and also whenever the widget is tapped.
 */
@AndroidEntryPoint
class RandomCharacterWidget : AppWidgetProvider() {

    @Inject
    lateinit var repository: MorseRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val character = repository.getAllCharacters().randomOrNull()

        val views = RemoteViews(context.packageName, R.layout.widget_random_character).apply {
            setTextViewText(R.id.widget_random_character_text, character?.character ?: "?")
            setTextViewText(R.id.widget_random_character_morse, character?.morseDisplay ?: "")

            val openAppIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setOnClickPendingIntent(R.id.widget_random_character_root, pendingIntent)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
