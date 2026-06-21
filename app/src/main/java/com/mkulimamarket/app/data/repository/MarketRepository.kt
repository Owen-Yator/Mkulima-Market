package com.mkulimamarket.app.data.repository

import com.mkulimamarket.app.domain.model.CommodityPrice
import com.mkulimamarket.app.domain.model.CountyPrice

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
}