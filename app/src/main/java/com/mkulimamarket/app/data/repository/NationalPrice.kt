package com.mkulimamarket.app.data.repository

import com.mkulimamarket.app.data.util.CommodityUnitConverter

data class NationalPrice(
    val commodity: String,
    val category: String,
    val unit: String,
    val priceKes: Double,
    val latestDate: String,
    val marketCount: Int
) {
    private val normalizedDetails: CommodityUnitConverter.NormalizedPriceResult
        get() = CommodityUnitConverter.processPrice(
            commodity = commodity,
            category = category,
            rawPrice = priceKes,
            rawUnit = unit
        )

    val displayPrice: String
        get() = normalizedDetails.mainDisplayPrice

    val displayLocalPrice: String?
        get() = normalizedDetails.secondaryDisplayPrice
}