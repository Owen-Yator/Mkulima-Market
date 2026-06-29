package com.mkulimamarket.app.data.repository

import com.mkulimamarket.app.domain.model.AlertType
import com.mkulimamarket.app.domain.model.PriceAlert
import com.mkulimamarket.app.domain.model.PriceTrend

class AlertRepository {

    fun generateAlert(
        commodity: String,
        trends: List<PriceTrend>
    ): PriceAlert? {

        if (trends.size < 2) return null

        val previousPrice = trends[trends.size - 2].price
        val currentPrice = trends.last().price

        return when {
            currentPrice > previousPrice ->
                PriceAlert(
                    commodity,
                    "$commodity prices increased from KSh $previousPrice to KSh $currentPrice.",
                    AlertType.SPIKE
                )

            currentPrice < previousPrice ->
                PriceAlert(
                    commodity,
                    "$commodity prices dropped from KSh $previousPrice to KSh $currentPrice.",
                    AlertType.DROP
                )

            else ->
                PriceAlert(
                    commodity,
                    "$commodity prices remain stable at KSh $currentPrice.",
                    AlertType.STABLE
                )
        }
    }
}