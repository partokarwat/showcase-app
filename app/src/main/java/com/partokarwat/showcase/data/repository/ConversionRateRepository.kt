package com.partokarwat.showcase.data.repository

import com.partokarwat.showcase.data.remote.CoinCapApi
import com.partokarwat.showcase.data.util.Result
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversionRateRepository
    @Inject
    constructor(
        private val coinCapApi: CoinCapApi,
    ) {
        suspend fun getExchangeUsdToEuroRate(): Result<BigDecimal> =
            try {
                val response = coinCapApi.getUsdConversionRateToEUR()
                Result.Success(BigDecimal(response.data.rateUsd))
            } catch (e: Exception) {
                Result.Error(e)
            }
    }
