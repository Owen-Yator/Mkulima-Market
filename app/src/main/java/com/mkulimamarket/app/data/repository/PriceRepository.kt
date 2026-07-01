package com.mkulimamarket.app.data.repository

import android.util.Log
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.data.remote.HdxPriceParser
import com.mkulimamarket.app.data.remote.WfpPriceService
import com.mkulimamarket.app.data.util.CountyMapping
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "PriceRepository"

class PriceRepository(
    private val api: WfpPriceService
) {

    private val _prices =
        MutableStateFlow<List<RawPriceEntry>>(emptyList())

    val prices: StateFlow<List<RawPriceEntry>> = _prices

    /**
     * Downloads and caches the latest WFP dataset.
     */
    suspend fun loadPrices() {

        if (_prices.value.isNotEmpty()) {
            Log.d(TAG, "Using cached prices (${_prices.value.size})")
            return
        }

        try {

            val response = api.downloadPrices()

            if (!response.isSuccessful) {

                Log.e(
                    TAG,
                    "Download failed. HTTP ${response.code()}"
                )

                return
            }

            val csv = response.body() ?: ""

            if (csv.isBlank()) {

                Log.e(TAG, "Downloaded CSV is empty")

                return
            }

            val parsed = HdxPriceParser.parse(csv)

            val foodOnly = parsed.filter {

                !it.category.contains("non-food", ignoreCase = true) &&
                        !it.category.contains("fuel", ignoreCase = true)
            }

            _prices.value = foodOnly

            Log.d(
                TAG,
                "Loaded ${foodOnly.size} food price records"
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to load prices",
                e
            )
        }
    }

    /**
     * Clears the cached dataset.
     */
    fun invalidateCache() {

        _prices.value = emptyList()

        Log.d(TAG, "Price cache cleared")
    }

    /**
     * Normalizes strings before comparison.
     *
     * Fixes:
     *  - Tharaka-Nithi vs Tharaka Nithi
     *  - Elgeyo-Marakwet vs Elgeyo Marakwet
     *  - Extra spaces
     *  - Capitalization differences
     */
    private fun normalize(value: String): String {

        return value
            .trim()
            .lowercase()
            .replace("-", " ")
            .replace("'", "")
            .replace(Regex("\\s+"), " ")
    }

    /**
     * Returns the latest price for every commodity
     * available for the selected county.
     */
    fun getByCounty(
        county: String
    ): Pair<List<RawPriceEntry>, String> {

        val market =
            CountyMapping.nearestMarket(county)

        val search =
            normalize(market)

        Log.d(
            TAG,
            "County selected: $county"
        )

        Log.d(
            TAG,
            "Mapped market: $market"
        )

        val matches = _prices.value.filter {

            normalize(it.county).contains(search) ||

                    normalize(it.market).contains(search)
        }

        Log.d(
            TAG,
            "Matches found: ${matches.size}"
        )

        val latestPerCommodity =
            matches
                .groupBy { it.commodity }
                .mapValues { (_, entries) ->

                    entries.maxByOrNull {
                        it.date
                    }!!
                }
                .values
                .sortedBy {
                    it.commodity
                }

        return Pair(
            latestPerCommodity,
            market
        )
    }

    /**
     * National average price per commodity.
     */
    fun getNationalPrices(): List<NationalPrice> {

        return _prices.value

            .groupBy { it.commodity }

            .map { (_, entries) ->

                val latest =
                    entries.maxByOrNull {
                        it.date
                    }!!

                val latestEntries =
                    entries.filter {

                        it.date == latest.date
                    }

                val averagePrice =
                    latestEntries
                        .map { it.price }
                        .average()

                NationalPrice(
                    commodity = latest.commodity,
                    category = latest.category,
                    unit = latest.unit,
                    priceKes = averagePrice,
                    latestDate = latest.date,
                    marketCount =
                        latestEntries
                            .map { it.market }
                            .distinct()
                            .size
                )
            }

            .sortedBy {
                it.commodity
            }
    }

    /**
     * Returns all counties shown in the dropdown.
     */
    fun getCounties(): List<String> {

        return CountyMapping.ALL_47_COUNTIES
    }
}

data class NationalPrice(

    val commodity: String,

    val category: String,

    val unit: String,

    val priceKes: Double,

    val latestDate: String,

    val marketCount: Int
) {

    val displayPrice: String
        get() =
            "KSh ${"%.2f".format(priceKes)} / $unit"

    val categoryLabel: String
        get() =
            category.ifBlank {
                "Other"
            }
}