package com.mkulimamarket.app.domain.model

enum class RecommendationType {
    SELL_NOW,
    HOLD,
    MONITOR
}

data class SellRecommendation(
    val commodity: String,
    val recommendation: String,
    val type: RecommendationType
)