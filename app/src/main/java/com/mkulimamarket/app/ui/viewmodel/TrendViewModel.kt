package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.di.NetworkModule
import com.mkulimamarket.app.domain.model.AlertType
import com.mkulimamarket.app.domain.model.PriceAlert
import com.mkulimamarket.app.domain.model.PriceTrend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrendViewModel : ViewModel() {

    private val repository = NetworkModule.priceRepository

    private val _selectedCommodity = MutableStateFlow("")
    val selectedCommodity = _selectedCommodity.asStateFlow()

    val availableCommodities: StateFlow<List<String>> =
        repository.prices
            .map { prices ->
                prices
                    .map { it.commodity }
                    .distinct()
                    .sorted()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    init {

        viewModelScope.launch {

            repository.loadPrices()

            val firstCommodity =
                repository.prices.value
                    .map { it.commodity }
                    .distinct()
                    .sorted()
                    .firstOrNull()

            if (firstCommodity != null) {
                _selectedCommodity.value = firstCommodity
            }
        }
    }

    fun selectCommodity(name: String) {
        _selectedCommodity.value = name
    }

    val trends: StateFlow<List<PriceTrend>> =
        combine(
            repository.prices,
            _selectedCommodity
        ) { prices, commodity ->

            if (commodity.isBlank()) {
                emptyList()
            } else {

                prices
                    .filter {
                        it.commodity.equals(
                            commodity,
                            ignoreCase = true
                        )
                    }
                    .sortedBy { it.date }
                    .map {
                        PriceTrend(
                            week = it.date,
                            price = it.price
                        )
                    }
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val insight: StateFlow<String> =
        combine(
            repository.prices,
            _selectedCommodity
        ) { prices, commodity ->

            if (commodity.isBlank()) {
                "Select a crop."
            } else {

                calculateInsight(
                    commodity,
                    prices.filter {
                        it.commodity.equals(
                            commodity,
                            ignoreCase = true
                        )
                    }
                )
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Select a crop."
        )

    val alerts: StateFlow<List<PriceAlert>> =
        repository.prices
            .map { prices ->
                buildAlerts(prices)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun buildTextReport(
        commodity: String,
        trends: List<PriceTrend>
    ): String {

        if (trends.isEmpty()) {
            return "No data available."
        }

        return """
MKULIMA MARKET REPORT

Commodity: $commodity

Records: ${trends.size}

First Price: ${trends.first().price}

Latest Price: ${trends.last().price}
        """.trimIndent()
    }

    private fun buildAlerts(
        prices: List<RawPriceEntry>
    ): List<PriceAlert> {

        return prices
            .groupBy { it.commodity }
            .mapNotNull { (commodity, entries) ->

                val sorted = entries.sortedBy { it.date }

                val first =
                    sorted.firstOrNull()?.price
                        ?: return@mapNotNull null

                val last =
                    sorted.lastOrNull()?.price
                        ?: return@mapNotNull null

                if (first == 0.0)
                    return@mapNotNull null

                val pct =
                    ((last - first) / first) * 100

                when {

                    pct >= 10 ->
                        PriceAlert(
                            commodity = commodity,
                            message = "Prices increased ${"%.1f".format(pct)}%",
                            type = AlertType.SPIKE
                        )

                    pct <= -10 ->
                        PriceAlert(
                            commodity = commodity,
                            message = "Prices decreased ${"%.1f".format(-pct)}%",
                            type = AlertType.DROP
                        )

                    else -> null
                }
            }
    }

    private fun calculateInsight(
        commodity: String,
        entries: List<RawPriceEntry>
    ): String {

        val sorted =
            entries.sortedBy { it.date }

        val first =
            sorted.firstOrNull()?.price
                ?: return "No data available."

        val last =
            sorted.lastOrNull()?.price
                ?: return "No data available."

        if (first == 0.0) {
            return "No data available."
        }

        val pct =
            ((last - first) / first) * 100

        return when {

            pct > 5 ->
                "$commodity prices are rising."

            pct < -5 ->
                "$commodity prices are falling."

            else ->
                "$commodity prices are stable."
        }
    }

    @Suppress("unused")
    private fun parseDate(date: String): LocalDate? =
        runCatching {
            LocalDate.parse(
                date,
                DateTimeFormatter.ISO_LOCAL_DATE
            )
        }.getOrNull()
}