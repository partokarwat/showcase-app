package com.partokarwat.showcase.usecases

import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import com.partokarwat.showcase.data.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCoinHistoryUseCase
    @Inject
    constructor(
        private val coinDetailsRepository: CoinDetailsRepository,
        private val conversionRateRepository: ConversionRateRepository,
    ) {
        suspend operator fun invoke(coinId: String): Result<List<HistoryValue>> {
            val historyValues = coinDetailsRepository.getCoinHistory(coinId)
            val exchangeRateToEUR = conversionRateRepository.getExchangeUsdToEuroRate().getOrThrow()
            historyValues.onEach {
                it.priceUsd =
                    it.priceUsd
                        .toBigDecimal()
                        .div(exchangeRateToEUR)
                        .toString()
            }
            return Result.Success(historyValues)
        }
    }
