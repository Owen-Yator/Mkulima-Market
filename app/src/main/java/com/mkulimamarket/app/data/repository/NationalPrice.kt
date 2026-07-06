package com.mkulimamarket.app.data.repository

data class NationalPrice(
    val commodity: String,
    val category: String,
    val unit: String,
    val priceKes: Double,
    val latestDate: String,
    val marketCount: Int
) {
    val displayPrice: String
        get() = "KSh ${"%.2f".format(priceKes)}"
}