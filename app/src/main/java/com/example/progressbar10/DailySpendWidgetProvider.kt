package com.adithyapaib.spendwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DailySpendWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TAG = "DailySpendWidget"
        private val DATE_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == Intent.ACTION_DATE_CHANGED) {
            SpendRepository.resetToday(context)
            updateAllWidgets(context)
        }
    }

    private fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, DailySpendWidgetProvider::class.java)
        )
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        try {
            val views = RemoteViews(context.packageName, R.layout.daily_spend_widget)

            val total = SpendRepository.getTodayTotal(context)
            val today = try {
                LocalDate.now().format(DATE_FMT)
            } catch (_: Exception) {
                "Today"
            }

            val rupeeSymbol = "\u20B9"
            val amountText = try {
                "$rupeeSymbol ${String.format(Locale.getDefault(), "%,.0f", total)}"
            } catch (_: Exception) {
                "$rupeeSymbol ${total.toLong()}"
            }

            views.setTextViewText(R.id.spend_amount, amountText)
            views.setTextViewText(R.id.spend_date, today)

            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d(TAG, "Widget $appWidgetId updated: $amountText on $today")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to update widget $appWidgetId", e)
            try {
                val fallback = RemoteViews(context.packageName, R.layout.daily_spend_widget)
                fallback.setTextViewText(R.id.spend_amount, "\u20B9 0")
                fallback.setTextViewText(R.id.spend_date, context.getString(R.string.waiting_for_sms))
                appWidgetManager.updateAppWidget(appWidgetId, fallback)
            } catch (fe: Exception) {
                Log.e(TAG, "Fallback update also failed", fe)
            }
        }
    }
}
