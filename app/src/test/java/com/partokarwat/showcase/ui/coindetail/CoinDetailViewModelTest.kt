package com.partokarwat.showcase.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.usecases.GetCoinHistoryUseCase
import com.partokarwat.showcase.usecases.GetMarketVolumesUseCase
import com.partokarwat.showcase.utilities.testCoin
import com.partokarwat.showcase.utilities.testCoinHistoryValues
import com.partokarwat.showcase.utilities.testCoinMarketValues
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Before

class CoinDetailViewModelTest {
    private lateinit var viewModel: CoinDetailViewModel
    private val coinDetailsRepository = mockk<CoinDetailsRepository>(relaxed = true)
    private val getCoinHistoryUseCase = mockk<GetCoinHistoryUseCase>(relaxed = true)
    private val getMarketVolumesUseCase = mockk<GetMarketVolumesUseCase>(relaxed = true)

    @Before
    fun setUp() {
        val savedStateHandle: SavedStateHandle =
            SavedStateHandle().apply {
                set("coinId", testCoin.id)
            }
        coEvery { coinDetailsRepository.getCoinById(testCoin.id) } returns
            flow {
                testCoin
            }
        coEvery { getCoinHistoryUseCase(testCoin.id) } returns testCoinHistoryValues
        coEvery { getMarketVolumesUseCase(testCoin.id) } returns testCoinMarketValues
        viewModel =
            CoinDetailViewModel(
                savedStateHandle,
                coinDetailsRepository,
                getCoinHistoryUseCase,
                getMarketVolumesUseCase,
            )
    }
}
