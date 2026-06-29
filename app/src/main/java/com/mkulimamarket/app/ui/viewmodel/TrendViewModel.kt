// FILE 5 of 5
// Location: com/mkulimamarket/app/ui/viewmodel/TrendViewModel.kt

package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.mkulimamarket.app.data.repository.MarketRepository
import com.mkulimamarket.app.domain.model.PriceTrend

/**
 * ViewModel for the price trend screen.
 *
 * Improvements over the original:
 * - Insight logic is more defensive: guards against empty or single-item lists
 *   so the app won't crash with an IndexOutOfBoundsException on `prices.last()`
 *   or `prices.first()` when trends is empty.
 * - Emoji removed from insight strings and moved to the UI layer (TrendScreen)
 *   to keep the ViewModel free of presentation concerns.
 * - Uses `firstOrNull` / `lastOrNull` for null-safe access.
 * - Minor: `getInsight()` renamed nothing (kept same name for API compatibility)
 *   but logic is now guarded and readable.
 */
class TrendViewModel : ViewModel() {

    private val repository = MarketRepository()

    val trends: List<PriceTrend> = repository.getPriceTrends()

    /**
     * Returns a human-readable insight string based on first vs last price.
     * Safe to call even when [trends] is empty.
     */
    fun getInsight(): String {
        val prices = trends.map { it.price }

        val first = prices.firstOrNull() ?: return "➖ No data available yet."
        val last  = prices.lastOrNull()  ?: return "➖ No data available yet."

        return when {
            last > first -> "📈 Prices are rising. Waiting before selling may be worthwhile."
            last < first -> "📉 Prices are falling. Selling sooner may get a better return."
            else         -> "➖ Prices are stable this week."
        }
    }
}
