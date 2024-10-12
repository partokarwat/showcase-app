package com.partokarwat.showcase.usecases

import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.remote.Asset
import com.partokarwat.showcase.data.repository.CoinListRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
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
            deleteNoMoreProvidedCoinsFromDatabase(assetsWithChangePercent24Hr)
            insertNewCoinsAndUpdateOldCoins(assetsWithChangePercent24Hr, exchangeRateToEUR)
        }

        private fun insertNewCoinsAndUpdateOldCoins(
            assetsWithChangePercent24Hr: List<Asset>,
            exchangeRateToEUR: BigDecimal,
        ) {
            for (item in assetsWithChangePercent24Hr) {
                val priceEUR: Double =
                    item.priceUsd?.toDouble()?.div(exchangeRateToEUR.toDouble()) ?: 0.0
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

        private suspend fun deleteNoMoreProvidedCoinsFromDatabase(assetsWithChangePercent24Hr: List<Asset>) {
            val savedCoins = coinListRepository.getAllCoins().first()
            val assetsWithChangePercent24HrIds = assetsWithChangePercent24Hr.map { it.id }
            val savedCoinsIds = savedCoins.map { it.id }
            val commonIds = assetsWithChangePercent24HrIds.intersect(savedCoinsIds.toSet())
            val coinsToDelete = savedCoins.filter { it.id !in commonIds }
            for (coin in coinsToDelete) {
                coinListRepository.deleteCoinById(coin.id)
            }
        }
    }
