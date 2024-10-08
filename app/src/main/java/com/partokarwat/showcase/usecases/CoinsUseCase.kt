package com.partokarwat.showcase.usecases

import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.repository.CoinRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinsUseCase
    @Inject
    constructor(
        private val coinRepository: CoinRepository,
    ) {
        suspend fun refreshCoins() {
            val cryptoCoroutinesApiResponse = coinRepository.getAssetsFromCoinCapApi()
            val timestampOfApiCall = cryptoCoroutinesApiResponse.timestamp
            coinRepository.setLastDataUpdateTimestamp(timestampOfApiCall)
            val allAssetsFromApi = cryptoCoroutinesApiResponse.data
            val assetsWithChangePercent24Hr =
                allAssetsFromApi.filter {
                    it.changePercent24Hr != null
                }
            val exchangeRateToEUR = coinRepository.getExchangeRateToEuro()
            coinRepository.deleteAllCoins()
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
                coinRepository.insertCoin(coin)
            }
        }
    }
