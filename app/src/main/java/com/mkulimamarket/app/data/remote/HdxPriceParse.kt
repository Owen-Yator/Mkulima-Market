package com.mkulimamarket.app.data.remote

import android.util.Log
import com.mkulimamarket.app.data.model.RawPriceEntry

private const val TAG = "HdxPriceParser"

object HdxPriceParser {

    fun parse(csv: String): List<RawPriceEntry> {

        if (csv.isBlank()) {
            Log.e(TAG, "CSV is empty")
            return emptyList()
        }

        val lines = csv.lines()
        val results = mutableListOf<RawPriceEntry>()
        var skippedRows = 0

        Log.d(TAG, "CSV contains ${lines.size} lines")

        if (lines.size > 2) {
            Log.d(TAG, "Header: ${lines[0]}")
            Log.d(TAG, "HXL: ${lines[1]}")
        }

        for (i in 2 until lines.size) {

            val line = lines[i].trim()

            if (line.isBlank()) continue

            val cols = parseCsvLine(line)

            if (cols.size < 15) {
                skippedRows++
                continue
            }

            try {

                val date = cols[0].trim()

                // admin2 column
                val county = cols[2].trim()

                // market column
                val market = cols[3].trim().ifBlank { county }

                val category = cols[7].trim()
                val commodity = cols[8].trim()
                val unit = cols[10].trim()
                val priceType = cols[12].trim()
                val currency = cols[13].trim()
                val price = cols[14].trim().toDoubleOrNull()

                if (date.isBlank()) {
                    skippedRows++
                    continue
                }

                if (county.isBlank()) {
                    skippedRows++
                    continue
                }

                if (commodity.isBlank()) {
                    skippedRows++
                    continue
                }

                if (!currency.equals("KES", ignoreCase = true)) {
                    skippedRows++
                    continue
                }

                if (price == null || price <= 0.0) {
                    skippedRows++
                    continue
                }

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

            } catch (e: Exception) {

                skippedRows++

                Log.e(
                    TAG,
                    "Failed to parse row ${i + 1}: ${e.message}"
                )
            }
        }

        Log.d(
            TAG,
            "Successfully parsed ${results.size} records"
        )

        Log.d(
            TAG,
            "Skipped $skippedRows invalid rows"
        )

        val availableMarkets = results
            .map { it.county }
            .distinct()
            .sorted()

        Log.d(
            TAG,
            "========== AVAILABLE MARKETS =========="
        )

        availableMarkets.forEach {
            Log.d(TAG, it)
        }

        Log.d(
            TAG,
            "======================================="
        )

        return results
    }

    /**
     * CSV parser supporting quoted fields.
     */
    private fun parseCsvLine(line: String): List<String> {

        val result = mutableListOf<String>()

        val current = StringBuilder()

        var inQuotes = false

        for (char in line) {

            when {

                char == '"' -> {
                    inQuotes = !inQuotes
                }

                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }

                else -> {
                    current.append(char)
                }
            }
        }

        result.add(current.toString())

        return result
    }
}