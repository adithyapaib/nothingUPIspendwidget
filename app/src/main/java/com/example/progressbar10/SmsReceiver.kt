package com.adithyapaib.spendwidget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

/**
 * Listens for incoming SMS messages from supported senders (format: XX-SLCBNK-S).
 * Parses the spend amount and updates the daily total in SharedPreferences,
 * then triggers a widget refresh.
 *
 * SMS format example:
 *   "Rs. 85 spent on your credit card xx0527 at Hungerbox on 08-Jul-26
 *    (UPI Ref: 125967877146)."
 */
class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SmsReceiver"

        // Matches any sender of the form XX-SLCBNK-S (e.g. AX-SLCBNK-S, VM-SLCBNK-S)
        private val SENDER_REGEX = Regex("""^[A-Z]{2}-SLCBNK-S$""", RegexOption.IGNORE_CASE)

        // Matches "Rs. 85" or "Rs.85" or "Rs. 1,234.56" - captures the numeric amount
        private val AMOUNT_REGEX = Regex("""Rs\.\s*([\d,]+(?:\.\d+)?)""", RegexOption.IGNORE_CASE)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return

        val bundle = intent.extras ?: return
        val pdus = bundle.get("pdus") as? Array<*> ?: return
        val format = bundle.getString("format")

        for (pdu in pdus) {
            val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray, format)
            val sender = smsMessage.displayOriginatingAddress ?: continue
            val body = smsMessage.messageBody ?: continue

            Log.d(TAG, "SMS from: $sender | body: $body")

            if (!SENDER_REGEX.matches(sender)) continue

            val amountMatch = AMOUNT_REGEX.find(body) ?: continue
            val amountStr = amountMatch.groupValues[1].replace(",", "")
            val amount = amountStr.toDoubleOrNull() ?: continue

            Log.d(TAG, "Detected spend: ₹$amount from $sender")

            val newTotal = SpendRepository.addSpend(context, amount)
            Log.d(TAG, "New daily total: ₹$newTotal")

            // Refresh all DailySpendWidgetProvider widgets
            refreshWidgets(context)
        }
    }

    private fun refreshWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, DailySpendWidgetProvider::class.java)
        )
        if (ids.isNotEmpty()) {
            val updateIntent = Intent(context, DailySpendWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(updateIntent)
        }
    }
}
