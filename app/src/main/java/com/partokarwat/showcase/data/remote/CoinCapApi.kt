package com.partokarwat.showcase.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinCapApi {
    @GET("assets")
    suspend fun getAsset(
        @Query("limit") limit: Int = 2000,
    ): AssetResponse

    @GET("assets/{id}/history")
    suspend fun getCoinHistory(
        @Path("id") id: String,
    ): CoinHistoryResponse

    @GET("assets/{id}/markets")
    suspend fun getCoinMarkets(
        @Path("id") id: String,
    ): CoinMarketsResponse

    @GET("rates/euro")
    suspend fun getConversionRateToEUR(): RateResponse
}
