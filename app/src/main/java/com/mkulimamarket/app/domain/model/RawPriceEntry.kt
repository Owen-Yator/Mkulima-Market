package com.mkulimamarket.app.data.model

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

    val displayPrice: String
        get() = "KES %.2f/%s".format(price, unit)
}