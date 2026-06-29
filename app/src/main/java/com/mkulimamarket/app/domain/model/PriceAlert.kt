package com.mkulimamarket.app.domain.model

enum class AlertType {
    SPIKE,
    DROP,
    STABLE
}

data class PriceAlert(
    val commodity: String,
    val message: String,
    val type: AlertType
)