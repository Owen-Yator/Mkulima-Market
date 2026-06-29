package com.mkulimamarket.app.domain.model

data class CommodityTrend(
    val commodity: String,
    val prices: List<Double>
)