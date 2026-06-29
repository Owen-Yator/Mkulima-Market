package com.mkulimamarket.app.data.repository

import android.util.Log
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.data.remote.HdxPriceParser
import com.mkulimamarket.app.data.remote.WfpPriceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "PriceRepository"

class PriceRepository(
    private val api: WfpPriceService
) {

    private val _prices =
        MutableStateFlow<List<RawPriceEntry>>(emptyList())

    val prices: StateFlow<List<RawPriceEntry>> = _prices

    suspend fun loadPrices() {

        if (_prices.value.isNotEmpty()) return

        try {

            Log.d(TAG, "Downloading prices")

            val response = api.downloadPrices()

            if (!response.isSuccessful) {
                Log.e(TAG, "HTTP Error ${response.code()}")
                return
            }

            val csv = response.body() ?: ""

            val parsed = HdxPriceParser.parse(csv)

            _prices.value = parsed

            Log.d(TAG, "Loaded ${parsed.size} prices")

        } catch (e: Exception) {

            Log.e(TAG, "Error: ${e.message}", e)
        }
    }

    fun getByCounty(county: String): List<RawPriceEntry> {

        return _prices.value
            .filter {
                it.county.contains(
                    county,
                    ignoreCase = true
                )
            }
            .groupBy { it.commodity }
            .mapValues {
                it.value.maxByOrNull { p -> p.date }!!
            }
            .values
            .sortedBy { it.commodity }
    }

    fun getCounties(): List<String> =
        _prices.value
            .map { it.county }
            .distinct()
            .sorted()

    fun getNationalAverages(): Map<String, Double> =
        _prices.value
            .groupBy { it.commodity }
            .mapValues { (_, entries) ->
                entries.map { it.price }.average()
            }

    fun getPriceTrend(
        county: String,
        commodity: String
    ): List<RawPriceEntry> {

        return _prices.value.filter {

            it.county.contains(
                county,
                true
            ) &&

                    it.commodity.equals(
                        commodity,
                        true
                    )
        }
    }
}