package com.partokarwat.showcase.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.remote.MarketValue
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.util.Result
import com.partokarwat.showcase.ui.coindetail.CoinDetailViewModel.UiState
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@SmallTest
class CoinDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private val savedStateHandle: SavedStateHandle =
        SavedStateHandle().apply {
            set(COIN_ID_SAVED_STATE_KEY, testCoin.id)
        }
    private val coinDetailsRepository = mockk<CoinDetailsRepository>(relaxed = true)
    private val getCoinHistoryUseCase = mockk<GetCoinHistoryUseCase>(relaxed = true)
    private val getCoinMarketVolumesUseCase = mockk<GetCoinMarketVolumesUseCase>(relaxed = true)
    private lateinit var viewModel: CoinDetailViewModel

    @Before
    fun setUp() {
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
            // given
            val expectedState =
                UiState(
                    coin = testCoin,
                    history = Result.Success(testCoinHistoryValues),
                    markets = Result.Success(testCoinMarketValues),
                )

            // when
            viewModel =
                CoinDetailViewModel(
                    savedStateHandle,
                    coinDetailsRepository,
                    getCoinHistoryUseCase,
                    getCoinMarketVolumesUseCase,
                )

            viewModel.uiState.test {
                skipItems(2)

                // then
                val actualState = awaitItem()
                assertEquals(actualState, expectedState)
            }
        }

    @ParameterizedTest
    @MethodSource("errorScenarios")
    fun `given viewModel when data loading fails then error is displayed and other details are loaded`(
        expectedHistoryResult: Result<List<HistoryValue>>,
        expectedMarketResult: Result<List<MarketValue>>,
    ) {
        runTest {
            coEvery { getCoinHistoryUseCase(testCoin.id) } returns expectedHistoryResult
            coEvery { getCoinMarketVolumesUseCase(testCoin.id) } returns expectedMarketResult

            viewModel =
                CoinDetailViewModel(
                    savedStateHandle,
                    coinDetailsRepository,
                    getCoinHistoryUseCase,
                    getCoinMarketVolumesUseCase,
                )

            viewModel.uiState.test {
                assertNotNull(viewModel.uiState.value.errorMessageResId)
                assertEquals(viewModel.uiState.value.history, expectedHistoryResult)
                assertEquals(viewModel.uiState.value.markets, expectedMarketResult)
            }
        }
    }

    companion object {
        @JvmStatic
        fun errorScenarios(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    Result.Error(IOException("Network error")),
                    Result.Success(testCoinMarketValues),
                ),
                Arguments.of(
                    Result.Success(testCoinHistoryValues),
                    Result.Error(IOException("Network error")),
                ),
                Arguments.of(
                    Result.Error(IOException("Network error")),
                    Result.Error(IOException("Network error")),
                ),
            )
    }
}
