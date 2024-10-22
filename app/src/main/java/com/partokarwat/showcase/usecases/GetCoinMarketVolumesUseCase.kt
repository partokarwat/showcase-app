package com.partokarwat.showcase.usecases

import com.partokarwat.showcase.data.remote.MarketValue
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import com.partokarwat.showcase.data.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCoinMarketVolumesUseCase
    @Inject
    constructor(
        private val coinDetailsRepository: CoinDetailsRepository,
        private val conversionRateRepository: ConversionRateRepository,
    ) {
        suspend operator fun invoke(coinId: String): Result<List<MarketValue>> {
            val marketValues = coinDetailsRepository.getCoinMarkets(coinId)
            val exchangeRateToEUR = conversionRateRepository.getExchangeUsdToEuroRate().getOrThrow()
            marketValues.onEach {
                it.volumeUsd24Hr =
                    it.volumeUsd24Hr
                        .toBigDecimal()
                        .div(exchangeRateToEUR)
                        .toString()
            }
            return Result.Success(marketValues)
        }
    }
