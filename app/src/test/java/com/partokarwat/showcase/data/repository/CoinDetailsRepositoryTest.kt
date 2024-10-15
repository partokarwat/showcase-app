package com.partokarwat.showcase.data.repository

import com.partokarwat.showcase.data.db.CoinDao
import com.partokarwat.showcase.data.remote.CoinCapApi
import com.partokarwat.showcase.utilities.testCoin
import com.partokarwat.showcase.utilities.testCoinHistoryValues
import com.partokarwat.showcase.utilities.testCoinMarketValues
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CoinDetailsRepositoryTest {
    private val coinCapApi = mockk<CoinCapApi>(relaxed = true)
    private val coinDao = mockk<CoinDao>(relaxed = true)
    private val coinDetailsRepository = mockk<CoinDetailsRepository>(relaxed = true)

    @Before
    fun setup() {
        coEvery { coinDao.getCoinById(testCoin.id) } returns flow { emit(testCoin) }
        coEvery { coinCapApi.getCoinHistory(testCoin.id).data } returns testCoinHistoryValues
        coEvery { coinCapApi.getCoinMarkets(testCoin.id).data } returns testCoinMarketValues
    }

    @Test
    fun testGetCoinById() =
        runTest {
            // When
            val coin = coinDetailsRepository.getCoinById(testCoin.id).first()

            // Then
            assertEquals(coin, testCoin)
        }

    @Test
    fun testGetCoinHistory() =
        runTest {
            // When
            val coinHistory = coinDetailsRepository.getCoinHistory(testCoin.id)

            // Then
            assertEquals(coinHistory, testCoinHistoryValues)
        }

    @Test
    fun testGetCoinMarkets() =
        runTest {
            // When
            val coinMarkets = coinDetailsRepository.getCoinMarkets(testCoin.id)

            // Then
            assertEquals(coinMarkets, testCoinMarketValues)
        }
}
