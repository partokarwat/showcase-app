package com.partokarwat.showcase.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.db.CoinDao
import com.partokarwat.showcase.data.remote.AssetResponse
import com.partokarwat.showcase.data.remote.CoinCapApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinListRepository
    @Inject
    constructor(
        private val coinDao: CoinDao,
        private val dataStorePreferences: DataStore<Preferences>,
        private val coinCapApi: CoinCapApi,
    ) {
        fun getTopGainersCoins(amount: Int): Flow<List<Coin>> = coinDao.getTopGainersCoins(amount)

        fun getTopLoserCoins(amount: Int): Flow<List<Coin>> = coinDao.getTopLoserCoins(amount)

        fun getAllCoins(): Flow<List<Coin>> = coinDao.getCoinAllCoins()

        fun deleteCoinById(id: String) {
            coinDao.deleteCoinById(id)
        }

        fun insertCoin(coin: Coin) {
            coinDao.insertCoin(coin)
        }

        fun getLastDataUpdateTimestamp(): Flow<Long> {
            val timestampFlow: Flow<Long> =
                dataStorePreferences.data
                    .map { preferences ->
                        preferences[TIMESTAMP] ?: 0
                    }
            return timestampFlow
        }

        suspend fun setLastDataUpdateTimestamp(timestampOfApiCall: Long) {
            dataStorePreferences.edit { settings ->
                settings[TIMESTAMP] = timestampOfApiCall
            }
        }

        suspend fun getAssetsFromCoinCapApi(): AssetResponse = coinCapApi.getAsset()

        companion object {
            val TIMESTAMP = longPreferencesKey("timestamp")
        }
    }
