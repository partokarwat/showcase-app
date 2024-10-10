package com.partokarwat.showcase.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoinDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var coinDao: CoinDao
    private val bitcoin = Coin("bitcoin", "Bitcoin", "BTC", 62157.5903, -2.23)
    private val ethereum = Coin("ethereum", "Ethereum", "ETH", 2510.16464, 3.57)
    private val binanceCoin = Coin("binance-coin", "BNB", "BNB", 552.61, -3.30)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() =
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            coinDao = database.coinDao()

            coinDao.upsertAll(listOf(bitcoin, ethereum, binanceCoin))
        }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getTop100GainersCoins() =
        runBlocking {
            val coinList = coinDao.getTop100GainersCoins().first()
            assertThat(coinList.size, equalTo(3))

            assertThat(coinList[0], equalTo(ethereum))
            assertThat(coinList[1], equalTo(bitcoin))
            assertThat(coinList[2], equalTo(binanceCoin))
        }

    @Test
    fun getTop100LoserCoins() =
        runBlocking {
            val coinList = coinDao.getTop100LoserCoins().first()
            assertThat(coinList.size, equalTo(3))

            assertThat(coinList[0], equalTo(binanceCoin))
            assertThat(coinList[1], equalTo(bitcoin))
            assertThat(coinList[2], equalTo(ethereum))
        }

    @Test
    fun testGetCoin() =
        runBlocking {
            assertThat(coinDao.getCoinById(ethereum.id).first(), equalTo(ethereum))
        }

    @Test
    fun testInsertCoin() =
        runBlocking {
            val binanceCoinUpdated = Coin("binance-coin", "BNB", "BNB", 621.61, 5.30)
            coinDao.insertCoin(binanceCoinUpdated)
            val coinList = coinDao.getTop100LoserCoins().first()
            assertThat(coinList.size, equalTo(3))
            assertThat(coinList[2], equalTo(binanceCoinUpdated))
        }

    @Test
    fun testInsertCoins() =
        runBlocking {
            val binanceCoinUpdated = Coin("binance-coin", "BNB", "BNB", 621.61, 5.30)
            val solana = Coin("solana", "Solana", "SOL", 147.98545, -4.46)
            coinDao.insertCoins(listOf(binanceCoinUpdated, solana))
            val coinList = coinDao.getTop100LoserCoins().first()
            assertThat(coinList.size, equalTo(4))
            assertThat(coinList[3], equalTo(binanceCoinUpdated))
            assertThat(coinList[0], equalTo(solana))
        }

    @Test
    fun testDeleteAll() =
        runBlocking {
            coinDao.deleteAllCoins()
            val coinList = coinDao.getTop100LoserCoins().first()
            assertThat(coinList.size, equalTo(0))
        }
}
