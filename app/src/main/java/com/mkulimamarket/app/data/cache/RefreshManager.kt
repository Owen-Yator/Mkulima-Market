package com.mkulimamarket.app.data.cache

import android.content.Context
import android.content.SharedPreferences

class RefreshManager(
    context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("price_prefs", Context.MODE_PRIVATE)

    private val KEY_LAST_REFRESH = "last_refresh_time"

    /**
     * Save current time as last refresh
     */
    fun updateLastRefresh() {
        prefs.edit()
            .putLong(KEY_LAST_REFRESH, System.currentTimeMillis())
            .apply()
    }

    /**
     * Check if refresh is needed (7-day rule)
     */
    fun shouldRefresh(): Boolean {

        val lastRefresh = prefs.getLong(KEY_LAST_REFRESH, 0L)

        if (lastRefresh == 0L) return true

        val now = System.currentTimeMillis()

        val diffMillis = now - lastRefresh

        val sevenDaysMillis = 7 * 24 * 60 * 60 * 1000L

        return diffMillis >= sevenDaysMillis
    }

    /**
     * Force refresh (used by pull-to-refresh)
     */
    fun forceRefresh(): Boolean {
        return true
    }
}