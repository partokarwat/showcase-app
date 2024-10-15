package com.partokarwat.showcase.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.partokarwat.showcase.data.db.CoinDao
import com.partokarwat.showcase.data.remote.AssetResponse
import com.partokarwat.showcase.data.remote.CoinCapApi
import com.partokarwat.showcase.ui.coinslist.CoinListViewModel.Companion.LIST_SIZE
import com.partokarwat.showcase.utilities.testAssets
import com.partokarwat.showcase.utilities.testCoins
import com.partokarwat.showcase.utilities.timestamp
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CoinListRepositoryTest {
    private val dataStorePreferences = mockk<DataStore<Preferences>>(relaxed = true)
    private val coinCapApi = mockk<CoinCapApi>(relaxed = true)
    private val coinDao = mockk<CoinDao>(relaxed = true)
    private lateinit var coinListRepository: CoinListRepository

    @Before
    fun setUp() {
        every { coinDao.getTopGainersCoins(LIST_SIZE) } returns
            flow {
                emit(testCoins.sortedByDescending { it.priceEur })
            }
        every { coinDao.getTopLoserCoins(LIST_SIZE) } returns
            flow {
                emit(testCoins.sortedBy { it.priceEur })
            }
        every { coinDao.getCoinAllCoins() } returns flow { emit(testCoins) }
        coEvery { coinCapApi.getAsset() } returns AssetResponse(testAssets, timestamp)
        coinListRepository = CoinListRepository(coinDao, dataStorePreferences, coinCapApi)
    }

    @Test
    fun testGetTopGainersCoins() =
        runTest {
            // When
            val topGainers = coinListRepository.getTopGainersCoins(LIST_SIZE).first()

            // Then
            assertEquals(topGainers, testCoins.sortedByDescending { it.priceEur })
        }

    @Test
    fun testGetTopLoserCoins() =
        runTest {
            // When
            val topLosers = coinListRepository.getTopLoserCoins(LIST_SIZE).first()

            // Then
            assertEquals(topLosers, testCoins.sortedBy { it.priceEur })
        }

    @Test
    fun testGetAllCoins() =
        runTest {
            // When
            val allCoins = coinListRepository.getAllCoins().first()

            // Then
            assertEquals(allCoins, testCoins)
        }

    @Test
    fun testGetAssetsFromCoinCapApi() =
        runTest {
            // When
            val assets = coinListRepository.getAssetsFromCoinCapApi()

            // Then
            assertEquals(assets.data, testAssets)
            assertEquals(assets.timestamp, timestamp)
        }
}
