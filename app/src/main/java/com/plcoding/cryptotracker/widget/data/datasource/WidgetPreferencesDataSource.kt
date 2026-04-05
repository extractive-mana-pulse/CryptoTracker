package com.plcoding.cryptotracker.widget.data.datasource

import android.content.Context
import androidx.core.content.edit

/**
 * Data source for widget-related preference values stored in SharedPreferences.
 *
 * Stores only lightweight metadata:
 * - preferred widget coin id
 * - last widget update timestamp
 */
class WidgetPreferencesDataSource(context: Context) {

    companion object {
        private const val PREFS_NAME = "widget_coin_prefs"
        private const val KEY_LAST_UPDATED_AT = "last_updated_at"
        private const val KEY_PREFERRED_WIDGET_COIN_ID = "preferred_widget_coin_id"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Returns the currently pinned widget coin id, or null if not set. */
    fun getPreferredCoinId(): String? =
        prefs.getString(KEY_PREFERRED_WIDGET_COIN_ID, null)

    /** Persists [coinId] as the preferred widget coin id. */
    fun setPreferredCoinId(coinId: String) {
        prefs.edit { putString(KEY_PREFERRED_WIDGET_COIN_ID, coinId) }
    }

    /** Clears the preferred widget coin id. */
    fun clearPreferredCoinId() {
        prefs.edit { remove(KEY_PREFERRED_WIDGET_COIN_ID) }
    }

    /** Returns the last widget update timestamp in epoch millis. */
    fun getLastUpdatedMillis(): Long =
        prefs.getLong(KEY_LAST_UPDATED_AT, 0L)

    /** Stores [nowMillis] as the latest widget update timestamp. */
    fun markUpdated(nowMillis: Long = System.currentTimeMillis()) {
        prefs.edit { putLong(KEY_LAST_UPDATED_AT, nowMillis) }
    }
}