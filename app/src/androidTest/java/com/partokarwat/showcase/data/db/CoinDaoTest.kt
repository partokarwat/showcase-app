package com.partokarwat.showcase.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.partokarwat.showcase.utilities.listSize
import com.partokarwat.showcase.utilities.testCoin
import com.partokarwat.showcase.utilities.testCoins
import com.partokarwat.showcase.utilities.updatedTestCoin
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

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() =
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            coinDao = database.coinDao()

            coinDao.upsertAll(testCoins)
        }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getAllCoins() =
        runBlocking {
            val coinList = coinDao.getCoinAllCoins().first()
            assertThat(coinList.size, equalTo(3))

            assertThat(coinList[0], equalTo(testCoins[0]))
            assertThat(coinList[1], equalTo(testCoins[1]))
            assertThat(coinList[2], equalTo(testCoins[2]))
        }

    @Test
    fun getTopGainersCoins() =
        runBlocking {
            val coinList = coinDao.getTopGainersCoins(listSize).first()
            assertThat(coinList.size, equalTo(3))

            assertThat(coinList[0], equalTo(testCoins[1]))
            assertThat(coinList[1], equalTo(testCoins[0]))
            assertThat(coinList[2], equalTo(testCoins[2]))
        }

    @Test
    fun getTopLoserCoins() =
        runBlocking {
            val coinList = coinDao.getTopLoserCoins(listSize).first()
            assertThat(coinList.size, equalTo(3))

            assertThat(coinList[0], equalTo(testCoins[2]))
            assertThat(coinList[1], equalTo(testCoins[0]))
            assertThat(coinList[2], equalTo(testCoins[1]))
        }

    @Test
    fun testGetCoin() =
        runBlocking {
            assertThat(coinDao.getCoinById(testCoins[1].id).first(), equalTo(testCoins[1]))
        }

    @Test
    fun testInsertCoin() =
        runBlocking {
            coinDao.insertCoin(updatedTestCoin)
            val coinList = coinDao.getCoinAllCoins().first()
            assertThat(coinList.size, equalTo(3))
            assertThat(coinList[2], equalTo(updatedTestCoin))
        }

    @Test
    fun testInsertCoins() =
        runBlocking {
            coinDao.insertCoins(listOf(updatedTestCoin, testCoin))
            val coinList = coinDao.getCoinAllCoins().first()
            assertThat(coinList.size, equalTo(testCoins.size + 1))
            assertThat(coinList[2], equalTo(updatedTestCoin))
            assertThat(coinList[3], equalTo(testCoin))
        }

    @Test
    fun testDeleteAll() =
        runBlocking {
            coinDao.deleteAllCoins()
            val coinList = coinDao.getCoinAllCoins().first()
            assertThat(coinList.size, equalTo(0))
        }

    @Test
    fun testDeleteCoinById() =
        runBlocking {
            coinDao.deleteCoinById(testCoins[2].id)
            val coinList = coinDao.getCoinAllCoins().first()
            assertThat(coinList.size, equalTo(testCoins.size - 1))
        }
}
