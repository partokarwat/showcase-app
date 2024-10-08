package com.partokarwat.showcase.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinCapApi {
    @GET("assets")
    suspend fun getAsset(
        @Query("limit") limit: Int = 2000,
    ): AssetResponse

    @GET("rates/euro")
    suspend fun getConversionRateToEUR(): RateResponse

}