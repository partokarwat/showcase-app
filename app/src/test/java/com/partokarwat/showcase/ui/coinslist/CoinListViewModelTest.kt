package com.partokarwat.showcase.ui.coinslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.partokarwat.showcase.data.repository.CoinListRepository
import com.partokarwat.showcase.usecases.FetchAllCoinsUseCase
import com.partokarwat.showcase.utilities.MainCoroutineRule
import com.partokarwat.showcase.utilities.getOrAwaitValue
import com.partokarwat.showcase.utilities.observeForTesting
import io.mockk.mockk
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
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
    fun `given viewModel when swipe to refresh is started then isRefreshing changes to true`() =
        runTest {
            val initialValue = viewModel.isRefreshing.getOrAwaitValue()
            assertEquals(initialValue, false)
            viewModel.onSwipeToRefresh()
            viewModel.isRefreshing.observeForTesting {
                yield()
                val valueIsRefreshing = viewModel.isRefreshing.value
                assertEquals(valueIsRefreshing, true)
                runCurrent()
            }
        }

    @Test
    fun `given viewModel when swipe to refresh is fully performed then isRefreshing is false after refresh`() =
        runTest {
            val initialValue = viewModel.isRefreshing.getOrAwaitValue()
            assertEquals(initialValue, false)
            viewModel.onSwipeToRefresh()
            val valueAfterRefresh = viewModel.isRefreshing.getOrAwaitValue()
            assertEquals(valueAfterRefresh, false)
        }

    @Test
    fun `given viewModel when switchCoinListByPerformance then isGainers is toggled`() =
        runTest {
            val initialValue = viewModel.isTopGainers.first()
            assertEquals(initialValue, true)
            viewModel.switchCoinListByPerformance()
            val valueAfterRefresh = viewModel.isTopGainers.drop(1).first()
            assertEquals(valueAfterRefresh, false)
        }
}
