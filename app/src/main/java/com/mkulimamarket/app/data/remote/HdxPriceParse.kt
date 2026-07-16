package com.mkulimamarket.app.data.remote

import android.util.Log
import com.mkulimamarket.app.data.model.RawPriceEntry

object HdxPriceParser {

    private const val TAG = "CSV"

    fun parse(csv: String): List<RawPriceEntry> {

        val result = mutableListOf<RawPriceEntry>()

        csv.lineSequence()
            .drop(1)
            .forEachIndexed { index, line ->

                val cols = line.split(",")

                if (cols.size < 15) {
                    Log.d(TAG, "Line $index skipped: only ${cols.size} columns")
                    return@forEachIndexed
                }

                val price = cols[14].toDoubleOrNull()

                if (price == null) {
                    Log.d(TAG, "Bad price line:")
                    Log.d(TAG, line)
                    return@forEachIndexed
                }

                result.add(
                    RawPriceEntry(
                        date = cols[0].trim(),
                        county = cols[2].trim(),
                        market = cols[3].trim(),
                        category = cols[7].trim(),
                        commodity = cols[8].trim(),
                        unit = cols[10].trim(),
                        priceType = cols[12].trim(),
                        price = price
                    )
                )
            }

        Log.d(TAG, "Rows parsed = ${result.size}")

        return result
    }
}