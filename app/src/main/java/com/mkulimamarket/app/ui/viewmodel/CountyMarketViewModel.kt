package com.mkulimamarket.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.data.repository.PriceRepository
import com.mkulimamarket.app.data.util.CountyMapping
import com.mkulimamarket.app.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CountyMarketVM"

class CountyMarketViewModel : ViewModel() {

    private val repository =
        PriceRepository(NetworkModule.api)

    sealed class UiState {

        object Loading : UiState()

        object Empty : UiState()

        data class Error(
            val message: String
        ) : UiState()

        data class Success(
            val prices: List<RawPriceEntry>,
            val sourceMarket: String,
            val isFallback: Boolean
        ) : UiState()
    }

    private val _uiState =
        MutableStateFlow<UiState>(UiState.Loading)

    val uiState =
        _uiState.asStateFlow()

    private val _selectedCounty =
        MutableStateFlow("Nairobi")

    val selectedCounty =
        _selectedCounty.asStateFlow()

    val counties =
        CountyMapping.ALL_47_COUNTIES

    init {
        load()
    }

    fun onCountySelected(county: String) {

        if (county == _selectedCounty.value) return

        _selectedCounty.value = county

        refreshPrices()
    }

    private fun load() {

        viewModelScope.launch {

            try {

                _uiState.value = UiState.Loading

                repository.loadPrices()

                refreshPrices()

            } catch (e: Exception) {

                Log.e(TAG, "Failed to load prices", e)

                _uiState.value =
                    UiState.Error(
                        e.message ?: "Unable to load market prices."
                    )
            }
        }
    }

    private fun refreshPrices() {

        val county =
            _selectedCounty.value

        Log.d(TAG, "Refreshing prices for $county")

        val (prices, market) =
            repository.getByCounty(county)

        if (prices.isEmpty()) {

            Log.d(TAG, "No prices found for $county")

            _uiState.value =
                UiState.Empty

            return
        }

        Log.d(
            TAG,
            "Loaded ${prices.size} commodities from $market"
        )

        _uiState.value =
            UiState.Success(
                prices = prices,
                sourceMarket = market,

                // Uses CountyMapping instead of string comparison
                isFallback =
                    !CountyMapping.hasDirectCoverage(county)
            )
    }

    /**
     * Allows pull-to-refresh or manual reload.
     */
    fun refresh() {

        repository.invalidateCache()

        load()
    }
}