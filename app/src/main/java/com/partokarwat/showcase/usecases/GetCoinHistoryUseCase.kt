package com.partokarwat.showcase.usecases

import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCoinHistoryUseCase
    @Inject
    constructor(
        private val coinDetailsRepository: CoinDetailsRepository,
        private val conversionRateRepository: ConversionRateRepository,
    ) {
        suspend operator fun invoke(coinId: String): List<HistoryValue> {
            val historyValues = coinDetailsRepository.getCoinHistory(coinId).takeLast(31)
            val exchangeRateToEUR = conversionRateRepository.getExchangeRateToEuro()
            historyValues.onEach {
                it.priceUsd =
                    it.priceUsd
                        .toDouble()
                        .div(exchangeRateToEUR)
                        .toString()
            }
            return historyValues
        }
    }
