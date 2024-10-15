package com.partokarwat.showcase.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.remote.MarketValue
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.usecases.GetCoinHistoryUseCase
import com.partokarwat.showcase.usecases.GetMarketVolumesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val COIN_ID_SAVED_STATE_KEY = "coinId"

@HiltViewModel
class CoinDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val coinDetailsRepository: CoinDetailsRepository,
        private val getCoinHistoryUseCase: GetCoinHistoryUseCase,
        private val getMarketVolumesUseCase: GetMarketVolumesUseCase,
    ) : ViewModel() {
        private val coinId: String = savedStateHandle.get<String>(COIN_ID_SAVED_STATE_KEY).orEmpty()

        val coin =
            coinDetailsRepository
                .getCoinById(coinId)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    null,
                )

        private val _coinHistory = MutableStateFlow<List<HistoryValue>?>(null)
        val coinHistory: Flow<List<HistoryValue>> get() = _coinHistory.filterNotNull()

        private val _coinMarkets = MutableStateFlow<List<MarketValue>?>(null)
        val coinMarkets: Flow<List<MarketValue>> get() = _coinMarkets.filterNotNull()

        private val _isInitError = MutableStateFlow(false)
        val isInitError: Flow<Boolean> get() = _isInitError.filterNotNull()

        init {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        _coinHistory.value = getCoinHistoryUseCase(coinId)
                        _coinMarkets.value = getMarketVolumesUseCase(coinId)
                    } catch (e: Exception) {
                        _isInitError.emit(true)
                        e.printStackTrace()
                    }
                }
            }
        }

        fun resetInitError() {
            _isInitError.value = false
        }
    }
