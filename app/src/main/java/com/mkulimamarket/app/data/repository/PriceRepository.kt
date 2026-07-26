package com.mkulimamarket.app.data.repository

import android.content.Context
import android.util.Log
import com.mkulimamarket.app.AppContext
import com.mkulimamarket.app.data.cache.CacheManager
import com.mkulimamarket.app.data.cache.RefreshManager
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.data.remote.HdxPriceParser
import com.mkulimamarket.app.data.remote.WfpPriceService
import com.mkulimamarket.app.data.util.CommodityUnitConverter
import com.mkulimamarket.app.data.util.CountyMapping
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "PriceRepository"

class PriceRepository(
    private val api: WfpPriceService,
    private val context: Context = AppContext.instance,
    private val cacheManager: CacheManager = CacheManager(AppContext.instance),
    private val refreshManager: RefreshManager = RefreshManager(AppContext.instance)
) {

    private val _prices = MutableStateFlow<List<RawPriceEntry>>(emptyList())

    val prices: StateFlow<List<RawPriceEntry>> =
        _prices.asStateFlow()

    private val refreshMutex = Mutex()

    /**
     * Initializes repository data.
     * Loads local data immediately, then refreshes from network if needed.
     */
    suspend fun initialize() {
        loadLocalPrices()
        refreshFromNetwork()
    }

    /**
     * Loads data from memory, cache or bundled snapshot.
     * Returns almost immediately.
     */
    suspend fun loadLocalPrices() {

        if (_prices.value.isNotEmpty()) {
            Log.d(TAG, "Using in-memory cache (${_prices.value.size})")
            return
        }

        try {

            // -------------------------------------------------------------
            // CACHE
            // -------------------------------------------------------------
            if (cacheManager.hasCache()) {

                val cachedCsv = cacheManager.readCache()

                if (!cachedCsv.isNullOrBlank()) {

                    updatePricesFromCsv(cachedCsv)

                    Log.d(
                        TAG,
                        "Loaded ${_prices.value.size} prices from cache"
                    )

                    return
                }
            }

            // -------------------------------------------------------------
            // SNAPSHOT
            // -------------------------------------------------------------
            val snapshotCsv = context.assets
                .open("wfp_food_prices_ken_snapshot.csv")
                .bufferedReader()
                .use { it.readText() }

            updatePricesFromCsv(snapshotCsv)

            cacheManager.saveCache(snapshotCsv)

            Log.d(
                TAG,
                "Loaded ${_prices.value.size} prices from bundled snapshot"
            )

        } catch (e: Exception) {

            Log.e(TAG, "loadLocalPrices failed", e)

        }
    }

    /**
     * Downloads the newest dataset without blocking the UI.
     * Only one refresh can occur at a time.
     */
    suspend fun refreshFromNetwork() {

        if (!refreshManager.shouldRefresh()) {
            Log.d(TAG, "Refresh not required yet.")
            return
        }

        refreshMutex.withLock {

            // Another coroutine may already have refreshed while we waited.
            if (!refreshManager.shouldRefresh()) {
                Log.d(TAG, "Refresh already completed.")
                return
            }

            try {

                Log.d(TAG, "Attempting network refresh...")

                val response = api.downloadPrices()

                Log.d(TAG, "HTTP Code = ${response.code()}")

                if (!response.isSuccessful) {

                    Log.e(
                        TAG,
                        "Network request failed. HTTP ${response.code()}"
                    )
                    return
                }

                val networkCsv = response.body().orEmpty()

                Log.d(
                    TAG,
                    "Downloaded ${networkCsv.length} characters"
                )

                if (networkCsv.isBlank()) {

                    Log.w(TAG, "Downloaded CSV is empty.")
                    return
                }

                val parsed = HdxPriceParser.parse(networkCsv)

                val filtered = parsed.filter {

                    !it.category.contains("non-food", true) &&
                            !it.category.contains("fuel", true)
                }

                if (filtered.isEmpty()) {

                    Log.w(TAG, "Network CSV contained no usable records.")
                    return
                }

                _prices.value = filtered

                cacheManager.saveCache(networkCsv)

                refreshManager.updateLastRefresh()

                Log.d(
                    TAG,
                    "Repository updated with ${filtered.size} records."
                )

            } catch (e: Exception) {

                Log.e(TAG, "Network refresh failed", e)
            }
        }
    }

    /**
     * Parses CSV and updates the repository.
     */
    private fun updatePricesFromCsv(csv: String) {

        _prices.value = HdxPriceParser
            .parse(csv)
            .filterFoodPrices()
    }

    /**
     * Removes non-food and fuel commodities.
     */
    private fun List<RawPriceEntry>.filterFoodPrices(): List<RawPriceEntry> {

        return filter {

            !it.category.contains("non-food", true) &&
                    !it.category.contains("fuel", true)

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

    // --- In PriceRepository.kt, replace getNationalPrices() with this ---

    fun getNationalPrices(): List<NationalPrice> {
        return _prices.value
            .groupBy { it.commodity }
            .map { (_, entries) ->
                val latest = entries.maxByOrNull { it.date }!!
                val sameDay = entries.filter { it.date == latest.date }

                // Normalize EVERY row through the single shared parser — this is
                // the only place unit math should happen. No more inline
                // "if unit == 90 KG / 50 KG / 25 KG" copy that silently missed
                // 64 KG, 126 KG, 13 KG, 200 G, 500 ML, L and Unit.
                val normalizedEntries = sameDay.map { entry ->
                    CommodityUnitConverter.normalizeRawEntry(entry.price, entry.unit)
                }

                // Defensive: if a commodity's rows somehow normalize to more than
                // one base measure (shouldn't happen for a well-formed commodity,
                // but the source data has already surprised us once), average
                // only the dominant group so we never blend KG prices with L or
                // per-unit prices into one meaningless number.
                val dominantGroup = normalizedEntries
                    .groupBy { it.baseUnit }
                    .maxByOrNull { it.value.size }!!
                    .value

                val baseUnit = dominantGroup.first().baseUnit
                val normalizedAvg = dominantGroup.map { it.pricePerBaseUnit }.average()

                NationalPrice(
                    commodity = latest.commodity,
                    category = latest.category,
                    unit = baseUnit.label, // "KG", "L", or "Unit" — whichever this commodity actually is
                    priceKes = normalizedAvg,
                    latestDate = latest.date,
                    marketCount = sameDay.map { it.market }.distinct().size
                )
            }
            .sortedBy { it.commodity }
    }


    fun getCounties(): List<String> =
        CountyMapping.ALL_47_COUNTIES
}
