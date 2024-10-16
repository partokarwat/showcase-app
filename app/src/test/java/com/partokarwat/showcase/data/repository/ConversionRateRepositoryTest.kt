package com.partokarwat.showcase.data.repository

import androidx.test.filters.SmallTest
import com.partokarwat.showcase.data.remote.CoinCapApi
import com.partokarwat.showcase.data.remote.RateResponse
import com.partokarwat.showcase.utilities.exchangeRate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@SmallTest
class ConversionRateRepositoryTest {
    private val coinCapApi = mockk<CoinCapApi>(relaxed = true)
    private lateinit var conversionRateRepository: ConversionRateRepository

    @Before
    fun setup() {
        coEvery { coinCapApi.getConversionRateToEUR() } returns RateResponse(exchangeRate)
        conversionRateRepository = ConversionRateRepository(coinCapApi)
    }

    @Test
    fun testGetExchangeRateToEuro() =
        runTest {
            // When
            val exchangeRateFromRepository = conversionRateRepository.getExchangeRateToEuro()

            // Then
            assertEquals(exchangeRateFromRepository, exchangeRate.rateUsd.toBigDecimal())
        }
}
