package com.mkulimamarket.app.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.mkulimamarket.app.data.repository.MarketRepository
import com.mkulimamarket.app.domain.model.CommodityPrice

class NationalPricesViewModel : ViewModel() {

    private val repository = MarketRepository()

    private val allPrices = repository.getNationalPrices()

    var searchQuery by mutableStateOf("")
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    val prices: List<CommodityPrice>
        get() = if (searchQuery.isBlank()) {
            allPrices
        } else {
            allPrices.filter {
                it.commodity.contains(searchQuery, ignoreCase = true)
            }
        }
}