package com.mkulimamarket.app.di

import com.mkulimamarket.app.AppContext
import com.mkulimamarket.app.data.remote.WfpPriceService
import com.mkulimamarket.app.data.repository.PriceRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val HDX_BASE_URL = "https://data.humdata.org/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(HDX_BASE_URL)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val api: WfpPriceService =
        retrofit.create(WfpPriceService::class.java)

    val priceRepository: PriceRepository by lazy {
        PriceRepository(
            api = api,
            context = AppContext.instance
        )
    }
}