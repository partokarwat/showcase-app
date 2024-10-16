package com.partokarwat.showcase.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.test.filters.SmallTest
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.usecases.GetCoinHistoryUseCase
import com.partokarwat.showcase.usecases.GetMarketVolumesUseCase
import com.partokarwat.showcase.utilities.MainCoroutineRule
import com.partokarwat.showcase.utilities.testCoin
import com.partokarwat.showcase.utilities.testCoinHistoryValues
import com.partokarwat.showcase.utilities.testCoinMarketValues
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
class CoinDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private val coinDetailsRepository = mockk<CoinDetailsRepository>(relaxed = true)
    private val getCoinHistoryUseCase = mockk<GetCoinHistoryUseCase>(relaxed = true)
    private val getMarketVolumesUseCase = mockk<GetMarketVolumesUseCase>(relaxed = true)
    private lateinit var viewModel: CoinDetailViewModel

    @Before
    fun setUp() {
        val savedStateHandle: SavedStateHandle =
            SavedStateHandle().apply {
                set(COIN_ID_SAVED_STATE_KEY, testCoin.id)
            }
        coEvery {
            getCoinHistoryUseCase(testCoin.id)
        } returns testCoinHistoryValues
        coEvery {
            getMarketVolumesUseCase(testCoin.id)
        } returns testCoinMarketValues
        viewModel =
            CoinDetailViewModel(
                savedStateHandle,
                coinDetailsRepository,
                getCoinHistoryUseCase,
                getMarketVolumesUseCase,
            )
    }

    @Test
    fun `given viewModel when initialised then values are loaded correctly`() =
        runTest {
            assertEquals(viewModel.coinHistory.first(), testCoinHistoryValues)
            assertEquals(viewModel.coinMarkets.first(), testCoinMarketValues)
        }
}
