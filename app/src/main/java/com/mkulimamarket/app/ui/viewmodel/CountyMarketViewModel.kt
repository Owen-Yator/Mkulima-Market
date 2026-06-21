package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.mkulimamarket.app.data.repository.MarketRepository

class CountyMarketViewModel : ViewModel() {

    private val repository = MarketRepository()

    val prices = repository.getCountyPrices()
}
