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
            CountyPrice("Nakuru Town", "Maize", "KSh 4,600"),
            CountyPrice("Molo Market", "Maize", "KSh 4,400"),
            CountyPrice("Naivasha Market", "Maize", "KSh 4,800")
        )
    }

    // ✅ NEW: Price Trends (ADDED)
    fun getPriceTrends(): List<PriceTrend> {
        return listOf(
            PriceTrend("Week 1", 4100),
            PriceTrend("Week 2", 4300),
            PriceTrend("Week 3", 4500),
            PriceTrend("Week 4", 4800)
        )
    }
}