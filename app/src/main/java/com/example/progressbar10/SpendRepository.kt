package com.adithyapaib.spendwidget

import android.content.Context
import androidx.core.content.edit
import java.time.LocalDate

/**
 * Repository for reading and writing the daily UPI spend total.
 * Uses SharedPreferences keyed by today's date so the total auto-resets at midnight.
 */
object SpendRepository {

    private const val PREFS_NAME = "daily_spend_prefs"
    private const val KEY_DATE = "spend_date"
    private const val KEY_TOTAL = "spend_total"

    /** Add an amount to today's total and return the new total. */
    fun addSpend(context: Context, amount: Double): Double {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()

        val storedDate = prefs.getString(KEY_DATE, null)
        val currentTotal = if (storedDate == today) {
            prefs.getFloat(KEY_TOTAL, 0f).toDouble()
        } else {
            0.0 // new day → reset
        }

        val newTotal = currentTotal + amount
        prefs.edit {
            putString(KEY_DATE, today)
            putFloat(KEY_TOTAL, newTotal.toFloat())
        }
        return newTotal
    }

    /** Return today's total spend (0.0 if it's a new day or no data yet). */
    fun getTodayTotal(context: Context): Double {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val storedDate = prefs.getString(KEY_DATE, null)
        return if (storedDate == today) {
            prefs.getFloat(KEY_TOTAL, 0f).toDouble()
        } else {
            0.0
        }
    }

}
