package com.mkulimamarket.app.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface WfpPriceService {

    @GET(
        "dataset/e0d3fba6-f9a2-45d7-b949-140c455197ff/" +
                "resource/517ee1bf-2437-4f8c-aa1b-cb9925b9d437/" +
                "download/wfp_food_prices_ken.csv"
    )
    suspend fun downloadPrices(): Response<String>
}