package com.mkulimamarket.app.data.remote

import com.mkulimamarket.app.data.model.RawPriceEntry

object HdxPriceParser {

    fun parse(csv: String): List<RawPriceEntry> {

        val lines = csv.lineSequence()
        val result = ArrayList<RawPriceEntry>(8000)

        for (line in lines) {

            if (line.isBlank()) continue
            if (line.startsWith("date", ignoreCase = true)) continue

            val cols = line.split(",")

            if (cols.size < 15) continue

            val date = cols[0].trim()
            val county = cols[2].trim()
            val market = cols[3].trim().ifBlank { county }

            val category = cols[7].trim()
            val commodity = cols[8].trim()
            val unit = cols[10].trim()
            val currency = cols[13].trim()
            val price = cols[14].trim().toDoubleOrNull()

            if (date.isBlank() ||
                county.isBlank() ||
                commodity.isBlank() ||
                price == null ||
                price <= 0.0 ||
                !currency.equals("KES", true)
            ) continue

            result.add(
                RawPriceEntry(
                    date = date,
                    county = county,
                    market = market,
                    category = category,
                    commodity = commodity,
                    unit = unit,
                    priceType = cols[12].trim(),
                    price = price
                )
            )
        }

        return result
    }
}