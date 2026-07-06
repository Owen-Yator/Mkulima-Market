package com.mkulimamarket.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.data.util.CountyMapping
import com.mkulimamarket.app.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CountyMarketVM"

class CountyMarketViewModel : ViewModel() {

    private val repository = NetworkModule.priceRepository

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()

        data class Error(val message: String) : UiState()

        data class Success(
            val prices: List<RawPriceEntry>,
            val sourceMarket: String,
            val isFallback: Boolean
        ) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedCounty = MutableStateFlow("Nairobi")
    val selectedCounty = _selectedCounty.asStateFlow()

    val counties = CountyMapping.ALL_47_COUNTIES

    init {
        observePrices()
        load()
    }

    private fun observePrices() {
        viewModelScope.launch {
            repository.prices.collect { list ->
                refreshPrices(list)
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                repository.loadPrices()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    e.message ?: "Unable to load data"
                )
            }
        }
    }

    fun onCountySelected(county: String) {
        if (county == _selectedCounty.value) return
        _selectedCounty.value = county
        refreshPrices(repository.prices.value)
    }

    private fun refreshPrices(list: List<RawPriceEntry>) {

        val county = _selectedCounty.value
        val market = CountyMapping.nearestMarket(county)
        val search = market.lowercase()

        val matches = list.filter {
            it.market.lowercase().contains(search) ||
                    it.county.lowercase().contains(search)
        }

        val latest = matches
            .groupBy { it.commodity }
            .mapValues { (_, entries) ->
                entries.maxByOrNull { it.date }!!
            }
            .values
            .toList()

        _uiState.value = if (latest.isEmpty()) {
            UiState.Empty
        } else {
            UiState.Success(
                prices = latest,
                sourceMarket = market,
                isFallback = !CountyMapping.hasDirectCoverage(county)
            )
        }
    }

    fun refresh() {
        repository.invalidateCache()
        load()
    }
}