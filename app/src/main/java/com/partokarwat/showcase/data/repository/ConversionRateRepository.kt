package com.partokarwat.showcase.data.repository

import com.partokarwat.showcase.data.remote.CoinCapApi
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversionRateRepository
    @Inject
    constructor(
        private val coinCapApi: CoinCapApi,
    ) {
        suspend fun getExchangeRateToEuro(): BigDecimal =
            coinCapApi
                .getConversionRateToEUR()
                .data
                .rateUsd
                .toBigDecimal()
    }
