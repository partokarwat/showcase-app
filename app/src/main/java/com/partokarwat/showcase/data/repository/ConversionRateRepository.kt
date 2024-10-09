package com.partokarwat.showcase.data.repository

import com.partokarwat.showcase.data.remote.CoinCapApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversionRateRepository
    @Inject
    constructor(
        private val coinCapApi: CoinCapApi,
    ) {
        suspend fun getExchangeRateToEuro(): Double =
            coinCapApi
                .getConversionRateToEUR()
                .data
                .rateUsd
                .toDouble()
    }
