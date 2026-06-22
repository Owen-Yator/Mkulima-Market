package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.mkulimamarket.app.data.repository.MarketRepository
import com.mkulimamarket.app.domain.model.PriceTrend

class TrendViewModel : ViewModel() {

    private val repository = MarketRepository()

    val trends: List<PriceTrend> = repository.getPriceTrends()

    fun getInsight(): String {

        val prices = trends.map { it.price }

        return when {
            prices.last() > prices.first() ->
                "📈 Prices are increasing. It may be better to wait before selling."

            prices.last() < prices.first() ->
                "📉 Prices are falling. Selling early may be better."

            else ->
                "➖ Prices are stable."
        }
    }
}