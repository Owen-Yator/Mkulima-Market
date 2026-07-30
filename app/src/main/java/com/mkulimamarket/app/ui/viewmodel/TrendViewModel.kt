package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.data.util.CommodityUnitConverter
import com.mkulimamarket.app.di.NetworkModule
import com.mkulimamarket.app.domain.model.AlertType
import com.mkulimamarket.app.domain.model.PriceAlert
import com.mkulimamarket.app.domain.model.PriceTrend
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class TrendTimeRange(val label: String, val months: Long) {
    ONE_MONTH("1M", 1),
    THREE_MONTHS("3M", 3),
    SIX_MONTHS("6M", 6),
    ALL("ALL", 120)
}

class TrendViewModel : ViewModel() {

    private val repository = NetworkModule.priceRepository

    private val _selectedCommodity = MutableStateFlow("")
    val selectedCommodity = _selectedCommodity.asStateFlow()

    private val _selectedRange = MutableStateFlow(TrendTimeRange.THREE_MONTHS)
    val selectedRange = _selectedRange.asStateFlow()

    val availableCommodities: StateFlow<List<String>> =
        repository.prices
            .map { prices ->
                prices.map { it.commodity }.distinct().sorted()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.loadLocalPrices()
            val firstCommodity = repository.prices.value
                .map { it.commodity }
                .distinct()
                .sorted()
                .firstOrNull()

            if (firstCommodity != null && _selectedCommodity.value.isBlank()) {
                _selectedCommodity.value = firstCommodity
            }
        }

        viewModelScope.launch {
            repository.refreshFromNetwork()
        }
    }

    fun selectCommodity(name: String) {
        _selectedCommodity.value = name
    }

    fun selectTimeRange(range: TrendTimeRange) {
        _selectedRange.value = range
    }

    val trends: StateFlow<List<PriceTrend>> =
        combine(
            repository.prices,
            _selectedCommodity,
            _selectedRange
        ) { prices, commodity, range ->
            if (commodity.isBlank()) {
                emptyList()
            } else {
                val filteredByCrop = prices.filter { it.commodity.equals(commodity, ignoreCase = true) }
                val dateFiltered = filterByRange(filteredByCrop, range)

                dateFiltered.sortedBy { it.date }.map { entry ->
                    val normalized = CommodityUnitConverter.normalizeRawEntry(
                        rawPrice = entry.price,
                        rawUnit = entry.unit
                    )
                    PriceTrend(
                        week = entry.date,
                        price = normalized.pricePerBaseUnit
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val insight: StateFlow<String> =
        combine(trends, _selectedCommodity) { trendList, commodity ->
            if (commodity.isBlank() || trendList.isEmpty()) {
                "Select a crop to view market movement."
            } else {
                val first = trendList.first().price
                val last = trendList.last().price
                if (first == 0.0) return@combine "Insufficient data."
                val pct = ((last - first) / first) * 100

                when {
                    pct > 5 -> "$commodity market prices are trending upwards (+${"%.1f".format(pct)}%). Consider selling if holding stock."
                    pct < -5 -> "$commodity prices have dropped (${"%.1f".format(pct)}%). Buyers can leverage lower rates."
                    else -> "$commodity prices remain stable across the selected timeframe."
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Select a crop.")

    val alerts: StateFlow<List<PriceAlert>> =
        repository.prices
            .map { prices -> buildAlerts(prices) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun buildTextReport(commodity: String, trends: List<PriceTrend>): String {
        if (trends.isEmpty()) return "No data available."

        val prices = trends.map { it.price }
        val firstPrice = prices.first()
        val lastPrice = prices.last()
        val minPrice = prices.minOrNull() ?: 0.0
        val maxPrice = prices.maxOrNull() ?: 0.0
        val changePct = if (firstPrice > 0) ((lastPrice - firstPrice) / firstPrice) * 100 else 0.0

        return """
==============================================
         MKULIMA MARKET ANALYSIS REPORT       
==============================================
Commodity:       ${commodity.uppercase()}
Time Period:     ${trends.first().week} to ${trends.last().week}
Data Samples:    ${trends.size} records
----------------------------------------------
PRICING METRICS (Per Kg Base)
----------------------------------------------
Starting Price:  KSh ${"%.2f".format(firstPrice)}
Current Price:   KSh ${"%.2f".format(lastPrice)}
Lowest Recorded: KSh ${"%.2f".format(minPrice)}
Highest Recorded:KSh ${"%.2f".format(maxPrice)}
Net Movement:    ${if (changePct >= 0) "+" else ""}${"%.1f".format(changePct)}%
----------------------------------------------
SUMMARY INSIGHT:
${
            when {
                changePct > 5 -> "High upward pressure detected. Prices are escalating."
                changePct < -5 -> "Downward price trend observed. Market supply is healthy."
                else -> "Market remains balanced with minimal price volatility."
            }
        }
==============================================
Generated via Mkulima Market Mobile
        """.trimIndent()
    }

    private fun filterByRange(entries: List<RawPriceEntry>, range: TrendTimeRange): List<RawPriceEntry> {
        if (range == TrendTimeRange.ALL) return entries
        val sorted = entries.sortedByDescending { it.date }
        val latestDateStr = sorted.firstOrNull()?.date ?: return entries
        val latestDate = parseDate(latestDateStr) ?: return entries

        val cutoffDate = latestDate.minusMonths(range.months)

        return entries.filter { entry ->
            val entryDate = parseDate(entry.date)
            entryDate != null && !entryDate.isBefore(cutoffDate)
        }
    }

    private fun buildAlerts(prices: List<RawPriceEntry>): List<PriceAlert> {
        return prices
            .groupBy { it.commodity }
            .mapNotNull { (commodity, entries) ->
                val sorted = entries.sortedBy { it.date }
                val first = sorted.firstOrNull()?.price ?: return@mapNotNull null
                val last = sorted.lastOrNull()?.price ?: return@mapNotNull null
                if (first == 0.0) return@mapNotNull null

                val pct = ((last - first) / first) * 100

                when {
                    pct >= 8 -> PriceAlert(
                        commodity = commodity,
                        message = "Spiked by ${"%.1f".format(pct)}% in recent data",
                        type = AlertType.SPIKE
                    )
                    pct <= -8 -> PriceAlert(
                        commodity = commodity,
                        message = "Dropped by ${"%.1f".format(-pct)}% in recent data",
                        type = AlertType.DROP
                    )
                    else -> null
                }
            }
    }

    private fun parseDate(date: String): LocalDate? =
        runCatching { LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull()
}
