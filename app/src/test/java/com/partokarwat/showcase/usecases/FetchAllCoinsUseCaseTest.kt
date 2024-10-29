package com.partokarwat.showcase.usecases

import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.remote.Asset
import com.partokarwat.showcase.data.remote.AssetResponse
import com.partokarwat.showcase.data.repository.CoinListRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import com.partokarwat.showcase.data.util.Result
import com.partokarwat.showcase.utilities.exchangeRateEur
import com.partokarwat.showcase.utilities.testAssets
import com.partokarwat.showcase.utilities.testCoins
import com.partokarwat.showcase.utilities.timestamp
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class FetchAllCoinsUseCaseTest {

    private val coinListRepository = mockk<CoinListRepository>()
    private val conversionRateRepository = mockk<ConversionRateRepository>()
    private lateinit var fetchAllCoinsUseCase: FetchAllCoinsUseCase

    @Before
    fun setUp() {
        fetchAllCoinsUseCase = FetchAllCoinsUseCase(coinListRepository, conversionRateRepository)

        coEvery { coinListRepository.setLastDataUpdateTimestamp(any()) } returns Unit
        coEvery { coinListRepository.deleteCoinById(any()) } returns Unit
        coEvery { coinListRepository.insertCoin(any()) } returns Unit
    }

    @Test
    fun `invoke() should fetch coins, update timestamp, and insert or update coins in database`() = runTest {
        // Given
        val cryptoApiResponse = AssetResponse(listOf(testAssets[0], testAssets[1]), timestamp)
        val exchangeRate = exchangeRateEur
        coEvery { coinListRepository.getAssetsFromCoinCapApi() } returns cryptoApiResponse
        coEvery { conversionRateRepository.getExchangeUsdToEuroRate() } returns Result.Success(exchangeRate)
        coEvery { coinListRepository.getAllCoins() } returns flowOf(emptyList())

        // When
        fetchAllCoinsUseCase()

        // Then
        coVerify { coinListRepository.setLastDataUpdateTimestamp(cryptoApiResponse.timestamp) }
        coVerify { coinListRepository.insertCoin(any()) }
    }

    @Test
    fun `invoke() should delete coins that are no longer provided by the API`() = runTest {
        // Given
        val cryptoApiResponse = AssetResponse(listOf(testAssets[0]), timestamp)
        val exchangeRate = BigDecimal("1.2")
        coEvery { coinListRepository.getAssetsFromCoinCapApi() } returns cryptoApiResponse
        coEvery { conversionRateRepository.getExchangeUsdToEuroRate() } returns Result.Success(exchangeRate)
        coEvery { coinListRepository.getAllCoins() } returns flowOf(listOf(testCoins[0], testCoins[1]))

        // When
        fetchAllCoinsUseCase()

        // Then
        coVerify { coinListRepository.deleteCoinById(testCoins[1].id) }
    }

    @Test
    fun `invoke() should throw an exception if there is an error from conversion rate repository`() = runTest {
        // Given
        val exception = Exception("Conversion rate error")
        coEvery { coinListRepository.getAssetsFromCoinCapApi() } throws exception
        coEvery { conversionRateRepository.getExchangeUsdToEuroRate() } returns Result.Error(exception)

        // When & Then
        assertThrows<Exception> { runBlocking { fetchAllCoinsUseCase() } }
        coVerify(exactly = 0) { coinListRepository.insertCoin(any()) }
    }

}