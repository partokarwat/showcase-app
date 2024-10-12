package com.partokarwat.showcase.ui.coindetail

import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CoinDetailViewModelTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var coin: CoinDetailsRepository

    @Test
    fun testCoinDetailViewModel() {
        hiltRule.inject()
        assertNotNull(coin)
    }
}
