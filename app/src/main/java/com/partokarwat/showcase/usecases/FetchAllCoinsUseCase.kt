package com.partokarwat.showcase.usecases

import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.repository.CoinListRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchAllCoinsUseCase
    @Inject
    constructor(
        private val coinListRepository: CoinListRepository,
        private val conversionRateRepository: ConversionRateRepository,
    ) {
        suspend operator fun invoke() {
            val cryptoCoroutinesApiResponse = coinListRepository.getAssetsFromCoinCapApi()
            val timestampOfApiCall = cryptoCoroutinesApiResponse.timestamp
            coinListRepository.setLastDataUpdateTimestamp(timestampOfApiCall)
            val allAssetsFromApi = cryptoCoroutinesApiResponse.data
            val assetsWithChangePercent24Hr =
                allAssetsFromApi.filter {
                    it.changePercent24Hr != null
                }
            val exchangeRateToEUR = conversionRateRepository.getExchangeRateToEuro()
            coinListRepository.deleteAllCoins()
            for (item in assetsWithChangePercent24Hr) {
                val priceEUR: Double = item.priceUsd?.toDouble()?.div(exchangeRateToEUR) ?: 0.0
                val coin =
                    Coin(
                        item.id,
                        item.name,
                        item.symbol,
                        priceEUR,
                        item.changePercent24Hr?.toDouble() ?: 0.0,
                    )
                coinListRepository.insertCoin(coin)
            }
        }
    }
