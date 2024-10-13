package com.partokarwat.showcase.ui.coindetail

import com.partokarwat.showcase.data.remote.CoinCapApi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CoinDetailViewModelTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var coinCapApi: CoinCapApi

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testCoinDetailViewModel() {
        assertNotNull(coinCapApi)
    }
}
