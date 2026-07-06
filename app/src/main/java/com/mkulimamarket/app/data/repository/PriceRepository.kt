package com.mkulimamarket.app.data.repository

import android.content.Context
import android.util.Log
import com.mkulimamarket.app.AppContext
import com.mkulimamarket.app.data.cache.CacheManager
import com.mkulimamarket.app.data.cache.RefreshManager
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.data.remote.HdxPriceParser
import com.mkulimamarket.app.data.remote.WfpPriceService
import com.mkulimamarket.app.data.util.CountyMapping
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

private const val TAG = "PriceRepository"

class PriceRepository(
    private val api: WfpPriceService,
    private val context: Context = AppContext.instance,
    private val cacheManager: CacheManager = CacheManager(AppContext.instance),
    private val refreshManager: RefreshManager = RefreshManager(AppContext.instance)
) {

    private val _prices = MutableStateFlow<List<RawPriceEntry>>(emptyList())

    val prices: StateFlow<List<RawPriceEntry>> = _prices.asStateFlow()

    suspend fun loadPrices() {

        if (_prices.value.isNotEmpty()) {
            Log.d(TAG, "Using in-memory cache (${_prices.value.size})")
            return
        }

        try {

            // 1. CACHE
            if (cacheManager.hasCache()) {

                val cachedCsv = cacheManager.readCache()

                if (!cachedCsv.isNullOrBlank()) {

                    val parsed = HdxPriceParser.parse(cachedCsv)

                    _prices.value = parsed.filter {
                        !it.category.contains("non-food", true) &&
                                !it.category.contains("fuel", true)
                    }

                    Log.d(TAG, "Loaded from cache")
                    return
                }
            }

            // 2. SNAPSHOT
            val csv = context.assets
                .open("wfp_food_prices_ken_snapshot.csv")
                .bufferedReader()
                .use { it.readText() }

            val parsed = HdxPriceParser.parse(csv)

            _prices.value = parsed.filter {
                !it.category.contains("non-food", true) &&
                        !it.category.contains("fuel", true)
            }

            cacheManager.saveCache(csv)

            // 3. NETWORK
            if (refreshManager.shouldRefresh()) {

                try {
                    val response = api.downloadPrices()

                    if (response.isSuccessful) {

                        val networkCsv = response.body().orEmpty()

                        if (networkCsv.isNotBlank()) {

                            val netParsed = HdxPriceParser.parse(networkCsv)

                            _prices.value = netParsed.filter {
                                !it.category.contains("non-food", true) &&
                                        !it.category.contains("fuel", true)
                            }

                            cacheManager.saveCache(networkCsv)
                            refreshManager.updateLastRefresh()

                            Log.d(TAG, "Network refresh success")
                        }
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Network update failed", e)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "loadPrices failed", e)
        }
    }

    fun invalidateCache() {
        _prices.value = emptyList()
    }

    private fun normalize(value: String): String =
        value.trim().lowercase()

    fun getByCounty(county: String): Pair<List<RawPriceEntry>, String> {

        val market = CountyMapping.nearestMarket(county)
        val search = normalize(market)

        val matches = _prices.value.filter {
            normalize(it.market).contains(search) ||
                    normalize(it.county).contains(search)
        }

        val latest = matches
            .groupBy { it.commodity }
            .mapValues { (_, entries) ->
                entries.maxByOrNull { it.date }!!
            }
            .values
            .sortedBy { it.commodity }

        return latest to market
    }

    fun getNationalPrices(): List<NationalPrice> {

        return _prices.value
            .groupBy { it.commodity }
            .map { (_, entries) ->

                val latest = entries.maxByOrNull { it.date }!!

                val sameDay = entries.filter { it.date == latest.date }

                val avg = sameDay.map { it.price }.average()

                NationalPrice(
                    commodity = latest.commodity,
                    category = latest.category,
                    unit = latest.unit,
                    priceKes = avg,
                    latestDate = latest.date,
                    marketCount = sameDay.map { it.market }.distinct().size
                )
            }
            .sortedBy { it.commodity }
    }

    fun getCounties(): List<String> =
        CountyMapping.ALL_47_COUNTIES
}