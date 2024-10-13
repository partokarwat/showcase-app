package com.partokarwat.showcase.ui.coindetail

import androidx.test.filters.SmallTest
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.usecases.GetCoinHistoryUseCase
import com.partokarwat.showcase.usecases.GetMarketVolumesUseCase
import io.mockk.MockKAnnotations
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@SmallTest
class CoinDetailViewModelTest {
    private val coinDetailsRepository = mockk<CoinDetailsRepository>(relaxed = true)
    private val getCoinHistoryUseCase = mockk<GetCoinHistoryUseCase>(relaxed = true)
    private val getMarketVolumesUseCase = mockk<GetMarketVolumesUseCase>(relaxed = true)

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun testCoinDetailViewModel() {
        assertNotNull(coinDetailsRepository)
    }
}
