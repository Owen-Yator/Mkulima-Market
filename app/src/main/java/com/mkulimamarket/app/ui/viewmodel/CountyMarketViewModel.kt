package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.repository.PriceRepository
import com.mkulimamarket.app.di.NetworkModule
import com.mkulimamarket.app.domain.model.CountyPrice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CountyMarketViewModel : ViewModel() {

    private val repository = PriceRepository(NetworkModule.api)

    // Selected county
    private val _county = MutableStateFlow("Nairobi")
    val county: StateFlow<String> = _county.asStateFlow()

    // Prices shown on screen
    private val _prices = MutableStateFlow<List<CountyPrice>>(emptyList())
    val prices: StateFlow<List<CountyPrice>> = _prices.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // Download prices from API
            repository.loadPrices()
            updatePrices()
        }
    }

    fun setCounty(newCounty: String) {
        _county.value = newCounty
        updatePrices()
    }

    private fun updatePrices() {
        // Map RawPriceEntry from repository to CountyPrice domain model
        val rawData = repository.getByCounty(_county.value)
        _prices.value = rawData.map { raw ->
            CountyPrice(
                commodity = raw.commodity,
                county = raw.county,
                market = raw.market,
                price = raw.price,
                unit = raw.unit,
                date = raw.date
            )
        }
    }

    fun getNationalAverages() = repository.getNationalAverages()
}
