package com.partokarwat.showcase.usecases

import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.repository.ConversionRateRepository
import com.partokarwat.showcase.utilities.exchangeRateEur
import com.partokarwat.showcase.utilities.testCoin
import com.partokarwat.showcase.utilities.testCoinHistoryValues
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetCoinHistoryUseCaseTest {
    private val coinDetailsRepository = mockk<CoinDetailsRepository>(relaxed = true)
    private val conversionRateRepository = mockk<ConversionRateRepository>(relaxed = true)
    private lateinit var getCoinHistoryUseCase: GetCoinHistoryUseCase

    @Before
    fun setUp() {
        coEvery { coinDetailsRepository.getCoinHistory(testCoin.id) } returns testCoinHistoryValues
        coEvery { conversionRateRepository.getExchangeRateToEuro() } returns exchangeRateEur
        getCoinHistoryUseCase = GetCoinHistoryUseCase(coinDetailsRepository, conversionRateRepository)
    }

    @Test
    fun testInvoke() =
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
