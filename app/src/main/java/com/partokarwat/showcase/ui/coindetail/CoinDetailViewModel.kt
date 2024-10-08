package com.partokarwat.showcase.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val COIN_ID_SAVED_STATE_KEY = "coinId"

@HiltViewModel
class CoinDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val coinDetailsRepository: CoinDetailsRepository,
    ) : ViewModel() {
        val coinId: String = savedStateHandle.get<String>(COIN_ID_SAVED_STATE_KEY)!!

        val coin = coinDetailsRepository.getCoinById(coinId)
    }
