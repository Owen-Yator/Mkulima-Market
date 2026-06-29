package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.mkulimamarket.app.data.repository.AlertRepository
import com.mkulimamarket.app.data.repository.MarketRepository
import com.mkulimamarket.app.domain.model.PriceAlert

class AlertViewModel : ViewModel() {

    private val marketRepository = MarketRepository()
    private val alertRepository = AlertRepository()

    fun getAlert(): PriceAlert? {

        val trends = marketRepository.getPriceTrends()

        return alertRepository.generateAlert(
            commodity = "Maize",
            trends = trends
        )
    }
}