package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.di.NetworkModule
import com.mkulimamarket.app.domain.model.PriceTrend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrendViewModel : ViewModel() {

    private val repository = NetworkModule.priceRepository

    private val _trends =
        MutableStateFlow<List<PriceTrend>>(emptyList())

    val trends: StateFlow<List<PriceTrend>> =
        _trends.asStateFlow()

    val insight: StateFlow<String> = repository.prices.map { prices ->
        calculateInsight(prices.filter { it.commodity.equals("Maize", true) })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Calculating...")

    init {
        viewModelScope.launch {
            repository.prices.collect { prices ->
                _trends.value =
                    prices
                        .groupBy { it.commodity }
                        .flatMap { (_, entries) ->
                            entries
                                .sortedBy { it.date }
                                .map {
                                    PriceTrend(
                                        week = it.date,
                                        price = it.price
                                    )
                                }
                        }
            }
        }
    }

    private fun calculateInsight(maizeEntries: List<com.mkulimamarket.app.data.model.RawPriceEntry>): String {
        val sorted = maizeEntries.sortedBy { it.date }
        val first = sorted.firstOrNull()?.price ?: return "➖ No data available yet."
        val last = sorted.lastOrNull()?.price ?: return "➖ No data available yet."

        return when {
            last > first -> "📈 Prices are rising. Waiting before selling may be worthwhile."
            last < first -> "📉 Prices are falling. Selling sooner may get a better return."
            else -> "➖ Prices are stable this week."
        }
    }
}