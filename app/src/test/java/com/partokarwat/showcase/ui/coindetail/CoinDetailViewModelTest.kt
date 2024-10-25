package com.partokarwat.showcase.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.util.Result
import com.partokarwat.showcase.ui.coindetail.CoinDetailsViewModelContract.Event
import com.partokarwat.showcase.ui.coindetail.CoinDetailsViewModelContract.Intent
import com.partokarwat.showcase.usecases.GetCoinHistoryUseCase
import com.partokarwat.showcase.usecases.GetCoinMarketVolumesUseCase
import com.partokarwat.showcase.utilities.MainCoroutineRule
import com.partokarwat.showcase.utilities.testCoin
import com.partokarwat.showcase.utilities.testCoinHistoryValues
import com.partokarwat.showcase.utilities.testCoinMarketValues
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okio.IOException
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
    private val getCoinMarketVolumesUseCase = mockk<GetCoinMarketVolumesUseCase>(relaxed = true)
    private lateinit var viewModel: CoinDetailViewModel

    @Before
    fun setUp() {
        val savedStateHandle: SavedStateHandle =
            SavedStateHandle().apply {
                set(COIN_ID_SAVED_STATE_KEY, testCoin.id)
            }
        coEvery {
            getCoinHistoryUseCase(testCoin.id)
        } returns Result.Success(testCoinHistoryValues)
        coEvery {
            getCoinMarketVolumesUseCase(testCoin.id)
        } returns Result.Success(testCoinMarketValues)
        coEvery {
            coinDetailsRepository.getCoinById(testCoin.id)
        } returns flow { emit(testCoin) }
        viewModel =
            CoinDetailViewModel(
                savedStateHandle,
                coinDetailsRepository,
                getCoinHistoryUseCase,
                getCoinMarketVolumesUseCase,
            )
    }

    @Test
    fun `given viewModel when initialised then values are loaded correctly`() =
        runTest {
            viewModel.state.test {
                // when
                skipItems(1)
                viewModel.intent(Intent.ScreenCreated)

                // then
                val state = awaitItem()
                assertEquals(state.coin, testCoin)
                assertEquals(state.history, Result.Success(testCoinHistoryValues))
                assertEquals(state.markets, Result.Success(testCoinMarketValues))
            }
        }

    @Test
    fun `given viewModel when coin history loading fails then error is displayed and market values are still loaded`() {
        runTest {
            viewModel.event.test {
                // given
                coEvery {
                    getCoinHistoryUseCase(testCoin.id)
                } throws IOException("Network error")

                // when
                viewModel.intent(Intent.ScreenCreated)

                // then
                val event = this.awaitItem()
                assertTrue(event is Event.ShowError)
                assertTrue(viewModel.state.value.history.isError)
                assertEquals(viewModel.state.value.markets, Result.Success(testCoinMarketValues))
            }
        }
    }

    @Test
    fun `given viewModel when coin market volumes loading fails then error is displayed and history is still loaded`() {
        runTest {
            viewModel.event.test {
                // given
                coEvery {
                    getCoinMarketVolumesUseCase(testCoin.id)
                } throws IOException("Network error")

                // when
                viewModel.intent(Intent.ScreenCreated)

                // then
                val event = this.awaitItem()
                assertTrue(event is Event.ShowError)
                assertEquals(viewModel.state.value.history, Result.Success(testCoinHistoryValues))
                assertTrue(viewModel.state.value.markets.isError)
            }
        }
    }
}
