package com.mkulimamarket.app.domain.model

data class CountyPrice(
    val commodity: String,
    val county: String,
    val market: String,
    val price: Double,
    val unit: String,
    val date: String
) {
    val displayPrice: String
        get() = "KES %.2f".format(price)
}