package com.mkulimamarket.app.data.model

import com.mkulimamarket.app.data.util.CommodityUnitConverter

data class RawPriceEntry(
    val date: String,
    val county: String,
    val market: String,
    val category: String,
    val commodity: String,
    val unit: String,
    val priceType: String,
    val price: Double
) {
    private val priceDetails: CommodityUnitConverter.NormalizedPriceResult
        get() = CommodityUnitConverter.processPrice(
            commodity = commodity,
            category = category,
            rawPrice = price,
            rawUnit = unit
        )

    val displayPrice: String
        get() = priceDetails.mainDisplayPrice

    val displayLocalPrice: String?
        get() = priceDetails.secondaryDisplayPrice
}