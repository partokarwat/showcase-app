package com.partokarwat.showcase.ui.coinslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.partokarwat.showcase.data.repository.CoinListRepository
import com.partokarwat.showcase.ui.coinslist.CoinListViewModelContract.Intent
import com.partokarwat.showcase.usecases.FetchAllCoinsUseCase
import com.partokarwat.showcase.utilities.MainCoroutineRule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
class CoinListViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private val coinListRepository = mockk<CoinListRepository>(relaxed = true)
    private val fetchAllCoinsUseCase = mockk<FetchAllCoinsUseCase>(relaxed = true)
    private lateinit var viewModel: CoinListViewModel

    @Before
    fun setUp() {
        viewModel =
            CoinListViewModel(
                coinListRepository,
                fetchAllCoinsUseCase,
            )
    }

    @Test
    fun `given viewModel when swipe to refresh is started then isRefreshing is updated accordingly`() =
        runTest {
            viewModel.state.test {
                // given
                val initialValue = awaitItem().isRefreshing

                // when
                viewModel.intent(Intent.OnSwipeToRefresh)
                val refreshingValue = awaitItem().isRefreshing
                val finishedValue = awaitItem().isRefreshing

                // then
                assertEquals(refreshingValue, true)
                assertEquals(initialValue, finishedValue)
            }
        }

    @Test
    fun `given viewModel when switchCoinListByPerformance then isGainers is toggled`() =
        runTest {
            viewModel.state.test {
                // given
                val initialValue = awaitItem().isTopGainers

                // when
                viewModel.intent(Intent.ToggleCoinListOrder)
                val updatedValue = awaitItem().isTopGainers

                // then
                assertEquals(initialValue, true)
                assertEquals(updatedValue, false)
            }
        }
}
