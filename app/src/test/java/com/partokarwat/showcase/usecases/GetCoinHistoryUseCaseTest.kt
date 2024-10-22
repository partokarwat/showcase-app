package com.partokarwat.showcase.usecases

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import com.partokarwat.showcase.utilities.MainCoroutineRule
import com.partokarwat.showcase.utilities.RethrowingExceptionHandler
import com.partokarwat.showcase.utilities.exchangeRateEur
import com.partokarwat.showcase.utilities.testCoin
import com.partokarwat.showcase.utilities.testCoinHistoryValues
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
class GetCoinHistoryUseCaseTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule
    val throwRule = RethrowingExceptionHandler()

    private val coinDetailsRepository = mockk<CoinDetailsRepository>(relaxed = true)
    private val conversionRateRepository = mockk<ConversionRateRepository>(relaxed = true)
    private lateinit var getCoinHistoryUseCase: GetCoinHistoryUseCase

    @Before
    fun setUp() {
        coEvery { coinDetailsRepository.getCoinHistory(testCoin.id) } returns testCoinHistoryValues
        coEvery { conversionRateRepository.getExchangeUsdToEuroRate() } returns Result.success(exchangeRateEur)
        getCoinHistoryUseCase = GetCoinHistoryUseCase(coinDetailsRepository, conversionRateRepository)
    }

    @Test
    fun `given useCase when used then the correct values are returned`() =
        runTest {
            // When
            val coinHistory = getCoinHistoryUseCase(testCoin.id)

            // Then
            assertEquals(
                coinHistory,
                testCoinHistoryValues.onEach {
                    it.priceUsd =
                        it.priceUsd
                            .toBigDecimal()
                            .div(exchangeRateEur)
                            .toString()
                },
            )
        }
}
