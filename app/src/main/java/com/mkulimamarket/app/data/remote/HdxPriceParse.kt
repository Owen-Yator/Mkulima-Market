package com.mkulimamarket.app.data.remote

import android.util.Log
import com.mkulimamarket.app.data.model.RawPriceEntry

private const val TAG = "HdxPriceParser"

object HdxPriceParser {

    fun parse(csv: String): List<RawPriceEntry> {

        if (csv.isBlank()) return emptyList()

        val results = mutableListOf<RawPriceEntry>()

        val lines = csv.lines()

        for (i in 2 until lines.size) {

            val line = lines[i].trim()

            if (line.isEmpty()) continue

            val cols = parseCsvLine(line)

            if (cols.size < 15) continue

            try {

                val date = cols[0].trim()

                // admin2 column
                val county = cols[2].trim()

                val market = cols[3].trim()

                val category = cols[7].trim()

                val commodity = cols[8].trim()

                val unit = cols[10].trim()

                val priceType = cols[12].trim()

                val currency = cols[13].trim()

                val price = cols[14].trim().toDoubleOrNull()

                if (currency != "KES") continue

                if (price == null) continue

                results.add(
                    RawPriceEntry(
                        date = date,
                        county = county,
                        market = market,
                        category = category,
                        commodity = commodity,
                        unit = unit,
                        priceType = priceType,
                        price = price
                    )
                )

            } catch (_: Exception) {

            }
        }

        Log.d(TAG, "Parsed ${results.size} records")

        return results
    }

    private fun parseCsvLine(line: String): List<String> {

        val result = mutableListOf<String>()

        val current = StringBuilder()

        var inQuotes = false

        for (char in line) {

            when {

                char == '"' ->
                    inQuotes = !inQuotes

                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }

                else ->
                    current.append(char)
            }
        }

        result.add(current.toString())

        return result
    }
}