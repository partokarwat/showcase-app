package com.partokarwat.showcase.usecases

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import com.partokarwat.showcase.utilities.MainCoroutineRule
import com.partokarwat.showcase.utilities.RethrowingExceptionHandler
import com.partokarwat.showcase.utilities.exchangeRateEur
import com.partokarwat.showcase.utilities.testCoin
import com.partokarwat.showcase.utilities.testCoinMarketValues
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCoinMarketVolumesUseCaseTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule
    val throwRule = RethrowingExceptionHandler()

    private val coinDetailsRepository = mockk<CoinDetailsRepository>(relaxed = true)
    private val conversionRateRepository = mockk<ConversionRateRepository>(relaxed = true)
    private lateinit var getCoinMarketVolumesUseCase: GetCoinMarketVolumesUseCase

    @Before
    fun setUp() {
        coEvery { coinDetailsRepository.getCoinMarkets(testCoin.id) } returns testCoinMarketValues
        coEvery { conversionRateRepository.getExchangeUsdToEuroRate() } returns Result.success(exchangeRateEur)
        getCoinMarketVolumesUseCase = GetCoinMarketVolumesUseCase(coinDetailsRepository, conversionRateRepository)
    }

    @Test
    fun `given useCase when used then the correct values are returned`() =
        runTest {
            // When
            val coinMarketValues = getCoinMarketVolumesUseCase(testCoin.id)

            // Then
            assertEquals(
                coinMarketValues,
                testCoinMarketValues.onEach {
                    it.volumeUsd24Hr =
                        it.volumeUsd24Hr
                            .toBigDecimal()
                            .div(exchangeRateEur)
                            .toString()
                },
            )
        }
}
