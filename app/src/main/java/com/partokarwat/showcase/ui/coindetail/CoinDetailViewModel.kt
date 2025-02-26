package com.partokarwat.showcase.ui.coindetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.remote.MarketValue
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.util.Result
import com.partokarwat.showcase.ui.common.getErrorStringRes
import com.partokarwat.showcase.usecases.GetCoinHistoryUseCase
import com.partokarwat.showcase.usecases.GetCoinMarketVolumesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
        private val getCoinMarketVolumesUseCase: GetCoinMarketVolumesUseCase,
    ) : ViewModel() {
        private val coinId: String = savedStateHandle.get<String>(COIN_ID_SAVED_STATE_KEY).orEmpty()

        private val _uiState = MutableStateFlow(UiState())
        val uiState: StateFlow<UiState> = _uiState.asStateFlow()

        init {
            loadCoinDetails()
        }

        private fun loadCoinDetails() {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    _uiState.value = UiState(history = Result.Loading, markets = Result.Loading)
                    val coin = coinDetailsRepository.getCoinById(coinId).first()
                    _uiState.value = _uiState.value.copy(coin = coin)
                    val coinHistoryResult =
                        try {
                            getCoinHistoryUseCase(coinId)
                        } catch (e: Exception) {
                            Result.Error(e)
                        }
                    val coinMarketsResult =
                        try {
                            getCoinMarketVolumesUseCase(coinId)
                        } catch (e: Exception) {
                            Result.Error(e)
                        }
                    if (coinMarketsResult is Result.Error) {
                        showError(coinMarketsResult.getErrorOrNull())
                    } else if (coinHistoryResult is Result.Error) {
                        showError(coinHistoryResult.getErrorOrNull())
                    }
                    _uiState.value =
                        _uiState.value.copy(
                            history = coinHistoryResult,
                            markets = coinMarketsResult,
                        )
                }
            }
        }

        private fun showError(exception: Throwable?) {
            Log.e(CoinDetailViewModel::class.java.simpleName, "Error: ", exception)
            _uiState.value = _uiState.value.copy(errorMessageResId = getErrorStringRes(exception))
        }

        fun resetErrorMessageResId() {
            _uiState.value = _uiState.value.copy(errorMessageResId = null)
        }

        data class UiState(
            val coin: Coin? = null,
            val history: Result<List<HistoryValue>> = Result.Loading,
            val markets: Result<List<MarketValue>> = Result.Loading,
            val errorMessageResId: Int? = null,
        )
    }
