package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.repository.AlertRepository
import com.mkulimamarket.app.di.NetworkModule
import com.mkulimamarket.app.domain.model.PriceAlert
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AlertViewModel : ViewModel() {

    private val alertRepository = AlertRepository()
    private val priceRepository = NetworkModule.priceRepository

    val alert: StateFlow<PriceAlert?> = priceRepository.prices.map { prices ->
        prices.groupBy { it.commodity }
            .let { grouped ->
                val maize = grouped["Maize"] ?: return@let null
                val trend = maize.sortedBy { it.date }
                    .takeLast(2)
                    .map {
                        com.mkulimamarket.app.domain.model.PriceTrend(it.date, it.price)
                    }
                alertRepository.generateAlert("Maize", trend)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}