package com.partokarwat.showcase.data.repository

import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.db.CoinDao
import com.partokarwat.showcase.data.remote.CoinCapApi
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.remote.MarketValue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinDetailsRepository
    @Inject
    constructor(
        private val coinDao: CoinDao,
        private val coinCapApi: CoinCapApi,
    ) {
        fun getCoinById(id: String): Flow<Coin> = coinDao.getCoinById(id)

        suspend fun getCoinHistory(id: String): List<HistoryValue> = coinCapApi.getCoinHistory(id).data

        suspend fun getCoinMarkets(id: String): List<MarketValue> = coinCapApi.getCoinMarkets(id).data
    }
