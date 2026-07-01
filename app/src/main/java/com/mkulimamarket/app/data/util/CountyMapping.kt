package com.mkulimamarket.app.data.util

object CountyMapping {

    /**
     * Every Kenyan county mapped to the nearest WFP monitored market.
     *
     * Counties that have a monitored market map directly to themselves.
     * Other counties map to the nearest available monitored market.
     */

    private val COUNTY_TO_MARKET = mapOf(

        // Direct WFP monitored markets
        "Bungoma" to "Bungoma",
        "Embu" to "Embu",
        "Garissa" to "Garissa",
        "Isiolo" to "Isiolo",
        "Kakamega" to "Kakamega",
        "Kisii" to "Kisii",
        "Kisumu" to "Kisumu",
        "Kitui" to "Kitui",
        "Lamu" to "Lamu",
        "Machakos" to "Machakos",
        "Mandera" to "Mandera",
        "Marsabit" to "Marsabit",
        "Meru" to "Meru",
        "Mombasa" to "Mombasa",
        "Nairobi" to "Nairobi",
        "Nakuru" to "Nakuru",
        "Narok" to "Narok",
        "Nyeri" to "Nyeri",
        "Wajir" to "Wajir",

        // Counties mapped to nearest monitored market

        "Baringo" to "Nakuru",

        "Bomet" to "Kisii",

        "Busia" to "Bungoma",

        "Elgeyo Marakwet" to "Eldoret",

        "Homa Bay" to "Kisumu",

        "Kajiado" to "Nairobi",

        "Kericho" to "Nakuru",

        "Kiambu" to "Nairobi",

        "Kilifi" to "Malindi",

        "Kirinyaga" to "Nyeri",

        "Kwale" to "Mombasa",

        "Laikipia" to "Nyeri",

        "Makueni" to "Machakos",

        "Migori" to "Kisumu",

        "Murang'a" to "Nyeri",

        "Nandi" to "Eldoret",

        "Nyamira" to "Kisii",

        "Nyandarua" to "Nyeri",

        "Samburu" to "Isiolo",

        "Siaya" to "Kisumu",

        "Taita Taveta" to "Mombasa",

        "Tana River" to "Garissa",

        "Tharaka Nithi" to "Meru",

        "Trans Nzoia" to "Eldoret",

        "Turkana" to "Lodwar",

        "Uasin Gishu" to "Eldoret",

        "Vihiga" to "Kakamega",

        "West Pokot" to "Eldoret"
    )

    /**
     * Returns the market that should be queried.
     */
    fun nearestMarket(county: String): String {
        return COUNTY_TO_MARKET[county] ?: county
    }

    /**
     * Returns true if the county has its own monitored market.
     */
    fun hasDirectCoverage(county: String): Boolean {
        val market = nearestMarket(county)
        return market.equals(county, ignoreCase = true)
    }

    /**
     * List of all counties shown in the dropdown.
     */
    val ALL_47_COUNTIES = listOf(
        "Baringo",
        "Bomet",
        "Bungoma",
        "Busia",
        "Elgeyo Marakwet",
        "Embu",
        "Garissa",
        "Homa Bay",
        "Isiolo",
        "Kajiado",
        "Kakamega",
        "Kericho",
        "Kiambu",
        "Kilifi",
        "Kirinyaga",
        "Kisii",
        "Kisumu",
        "Kitui",
        "Kwale",
        "Laikipia",
        "Lamu",
        "Machakos",
        "Makueni",
        "Mandera",
        "Marsabit",
        "Meru",
        "Migori",
        "Mombasa",
        "Murang'a",
        "Nairobi",
        "Nakuru",
        "Nandi",
        "Narok",
        "Nyamira",
        "Nyandarua",
        "Nyeri",
        "Samburu",
        "Siaya",
        "Taita Taveta",
        "Tana River",
        "Tharaka Nithi",
        "Trans Nzoia",
        "Turkana",
        "Uasin Gishu",
        "Vihiga",
        "Wajir",
        "West Pokot"
    )
}