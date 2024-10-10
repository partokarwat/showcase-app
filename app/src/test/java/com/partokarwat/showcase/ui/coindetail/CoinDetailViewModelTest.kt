package com.partokarwat.showcase.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import org.junit.Before

class CoinDetailViewModelTest {
    private lateinit var viewModel: CoinDetailViewModel

    @Before
    fun setUp() {
        val savedStateHandle: SavedStateHandle =
            SavedStateHandle().apply {
                set("coinId", testPlant.plantId)
            }
        viewModel = PlantDetailViewModel(savedStateHandle, plantRepository, gardenPlantingRepository)
    }
}
