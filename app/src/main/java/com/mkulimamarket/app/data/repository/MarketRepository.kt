package com.mkulimamarket.app.data.repository

import com.mkulimamarket.app.domain.model.CommodityPrice
import com.mkulimamarket.app.domain.model.CountyPrice
import com.mkulimamarket.app.domain.model.PriceTrend

class MarketRepository {

    fun getNationalPrices(): List<CommodityPrice> {
        return listOf(
            CommodityPrice("Maize", "KSh 4,500"),
            CommodityPrice("Beans", "KSh 8,200"),
            CommodityPrice("Rice", "KSh 7,000"),
            CommodityPrice("Potatoes", "KSh 3,600"),
            CommodityPrice("Tomatoes", "KSh 5,100")
        )
    }

    fun getCountyPrices(): List<CountyPrice> {
        return listOf(
            CountyPrice("Maize", "Nakuru", "Nakuru Town", 4600.0, "90kg Bag", "2024-05-15"),
            CountyPrice("Maize", "Nakuru", "Molo Market", 4400.0, "90kg Bag", "2024-05-15"),
            CountyPrice("Maize", "Nakuru", "Naivasha Market", 4800.0, "90kg Bag", "2024-05-15")
        )
    }

    // ✅ Price Trends
    fun getPriceTrends(): List<PriceTrend> {
        return listOf(
            PriceTrend("Week 1", 4100),
            PriceTrend("Week 2", 4300),
            PriceTrend("Week 3", 4500),
            PriceTrend("Week 4", 4800)
        )
    }
}