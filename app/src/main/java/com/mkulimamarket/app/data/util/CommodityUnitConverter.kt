package com.mkulimamarket.app.data.util

object CommodityUnitConverter {

    data class NormalizedPriceResult(
        val mainDisplayPrice: String,
        val secondaryDisplayPrice: String?
    )

    data class LocalUnitInfo(
        val multiplier: Double,
        val unitLabel: String
    )

    /**
     * Public base-unit measure a raw price has been normalized to.
     * Exposed so callers that need to AGGREGATE raw rows (e.g. averaging
     * across markets for a national price) can do so on already-normalized,
     * apples-to-apples values instead of re-deriving their own unit logic.
     */
    enum class BaseUnit(val label: String) {
        KG("KG"),
        LITRE("L"),
        COUNT("Unit")
    }

    data class NormalizedRawPrice(
        val pricePerBaseUnit: Double,
        val baseUnit: BaseUnit
    )

    private enum class RawMeasure { WEIGHT_KG, VOLUME_L, COUNT }

    private data class ParsedRawUnit(
        val quantityInBaseUnit: Double,
        val measure: RawMeasure
    )

    // Matches WFP-style compound units: "90 KG", "200 G", "500 ML", "13 KG", etc.
    private val NUMERIC_UNIT_REGEX = Regex("""^(\d+(?:\.\d+)?)\s*(KG|G|ML|L)$""", RegexOption.IGNORE_CASE)

    /**
     * Parses the WFP `unit` column for a SINGLE row into a normalized quantity
     * + measure. This must run per row, never per commodity name — the same
     * commodity can legitimately appear with different raw units across
     * different markets/dates (e.g. "Maize (white, dry)" has both "KG" rows
     * and "90 KG" rows in the same dataset).
     *
     * Unknown/unrecognized unit strings fall back to COUNT with a multiplier
     * of 1.0 rather than guessing — better to show "per unit" than to
     * silently apply a wrong weight conversion.
     */
    private fun parseRawUnit(rawUnit: String): ParsedRawUnit {
        val trimmed = rawUnit.trim()

        when (trimmed.uppercase()) {
            "KG" -> return ParsedRawUnit(1.0, RawMeasure.WEIGHT_KG)
            "L" -> return ParsedRawUnit(1.0, RawMeasure.VOLUME_L)
            "UNIT" -> return ParsedRawUnit(1.0, RawMeasure.COUNT)
        }

        val match = NUMERIC_UNIT_REGEX.find(trimmed)
        if (match != null) {
            val qty = match.groupValues[1].toDoubleOrNull() ?: 1.0
            return when (match.groupValues[2].uppercase()) {
                "KG" -> ParsedRawUnit(qty, RawMeasure.WEIGHT_KG)
                "G" -> ParsedRawUnit(qty / 1000.0, RawMeasure.WEIGHT_KG)
                "ML" -> ParsedRawUnit(qty / 1000.0, RawMeasure.VOLUME_L)
                "L" -> ParsedRawUnit(qty, RawMeasure.VOLUME_L)
                else -> ParsedRawUnit(1.0, RawMeasure.COUNT)
            }
        }

        return ParsedRawUnit(1.0, RawMeasure.COUNT)
    }

    /**
     * THE single normalization entry point. Converts one raw (price, unit)
     * pair from the source data into a genuine per-base-unit price (per kg,
     * per litre, or per count), tagged with which base unit it is.
     *
     * Any code that needs to combine multiple raw rows — average them,
     * compare them, chart them — should call this first and operate on the
     * result, rather than writing its own copy of unit-parsing logic. The
     * national-prices bug (Tomatoes/Cabbage/Onions still showing 1000s of
     * KSh/kg) happened precisely because the repository had its own
     * incomplete inline version of this instead of calling it.
     */
    fun normalizeRawEntry(rawPrice: Double, rawUnit: String): NormalizedRawPrice {
        val parsed = parseRawUnit(rawUnit)
        val pricePerBaseUnit = if (parsed.quantityInBaseUnit > 0.0) {
            rawPrice / parsed.quantityInBaseUnit
        } else {
            rawPrice
        }
        val baseUnit = when (parsed.measure) {
            RawMeasure.WEIGHT_KG -> BaseUnit.KG
            RawMeasure.VOLUME_L -> BaseUnit.LITRE
            RawMeasure.COUNT -> BaseUnit.COUNT
        }
        return NormalizedRawPrice(pricePerBaseUnit, baseUnit)
    }

    /**
     * Returns the local Kenyan-market trading-unit metadata for a commodity,
     * used ONLY to render a helpful secondary "≈ KSh X per bag" line once we
     * already have a correct per-kg price. This never feeds back into the
     * source normalization — that comes from normalizeRawEntry() above.
     */
    fun getLocalUnit(commodity: String, category: String = ""): LocalUnitInfo {
        val name = commodity.lowercase().trim()
        val cat = category.lowercase().trim()

        return when {
            cat.contains("pulse") || cat.contains("cereal") ||
                    name.contains("maize") || name.contains("bean") ||
                    name.contains("sorghum") || name.contains("millet") ||
                    name.contains("cowpea") || name.contains("pigeon pea") ||
                    name.contains("wheat") || name.contains("gram") || name.contains("lentil") ->
                LocalUnitInfo(90.0, "90 kg bag")

            name.contains("rice") -> LocalUnitInfo(25.0, "25 kg bag")
            name.contains("potato") -> LocalUnitInfo(50.0, "50 kg bag")

            name.contains("cabbage") -> LocalUnitInfo(70.0, "70 kg extended bag")
            name.contains("tomato") -> LocalUnitInfo(64.0, "64 kg crate")
            name.contains("onion") -> LocalUnitInfo(50.0, "50 kg net bag")
            name.contains("kale") || name.contains("sukuma") || name.contains("spinach") ->
                LocalUnitInfo(50.0, "50 kg sack")

            // Bananas etc. are reported per-piece/hand in the source data
            // (WFP unit = "Unit") — there's no reliable weight to convert
            // through, so no bag/bunch equivalent is offered for them.

            else -> LocalUnitInfo(1.0, "kg")
        }
    }

    /**
     * Normalizes a single WFP price row and formats it for display.
     * Delegates all unit math to normalizeRawEntry() — this function only
     * handles formatting + the cosmetic local-bag equivalent.
     */
    fun processPrice(
        commodity: String,
        category: String,
        rawPrice: Double,
        rawUnit: String
    ): NormalizedPriceResult {
        val name = commodity.lowercase().trim()
        val cat = category.lowercase().trim()

        val normalized = normalizeRawEntry(rawPrice, rawUnit)

        return when (normalized.baseUnit) {

            BaseUnit.LITRE -> NormalizedPriceResult(
                mainDisplayPrice = "KSh ${"%.2f".format(normalized.pricePerBaseUnit)} / litre",
                secondaryDisplayPrice = null
            )

            BaseUnit.COUNT -> NormalizedPriceResult(
                mainDisplayPrice = "KSh ${"%.2f".format(normalized.pricePerBaseUnit)} / unit",
                secondaryDisplayPrice = null
            )

            BaseUnit.KG -> {
                val unitInfo = getLocalUnit(name, cat)
                val hasLocalEquivalent = unitInfo.multiplier > 1.0
                val localTotal = normalized.pricePerBaseUnit * unitInfo.multiplier

                NormalizedPriceResult(
                    mainDisplayPrice = "KSh ${"%.2f".format(normalized.pricePerBaseUnit)} / kg",
                    secondaryDisplayPrice = if (hasLocalEquivalent)
                        "≈ KSh ${"%,.0f".format(localTotal)} per ${unitInfo.unitLabel}"
                    else null
                )
            }
        }
    }
}
