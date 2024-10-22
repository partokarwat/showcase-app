package com.partokarwat.showcase.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.remote.MarketValue
import com.partokarwat.showcase.data.repository.CoinDetailsRepository
import com.partokarwat.showcase.data.util.Result
import com.partokarwat.showcase.ui.base.UniDirectionalViewModelContract
import com.partokarwat.showcase.ui.coindetail.CoinDetailsViewModelContract.Event
import com.partokarwat.showcase.ui.coindetail.CoinDetailsViewModelContract.Intent
import com.partokarwat.showcase.ui.coindetail.CoinDetailsViewModelContract.State
import com.partokarwat.showcase.usecases.GetCoinHistoryUseCase
import com.partokarwat.showcase.usecases.GetCoinMarketVolumesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
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
    ) : ViewModel(),
        CoinDetailsViewModelContract {
        private val coinId: String = savedStateHandle.get<String>(COIN_ID_SAVED_STATE_KEY).orEmpty()

        private val _state = MutableStateFlow(State())
        override val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<Event>()
        override val event = _event.asSharedFlow()

        override fun intent(intent: Intent) {
            when (intent) {
                is Intent.ScreenCreated -> loadCoinDetailsFromApi()
            }
        }

        init {
            viewModelScope.launch {
                _state.value = _state.value.copy(history = Result.Loading, markets = Result.Loading)
                val coin = coinDetailsRepository.getCoinById(coinId).first()
                _state.value = _state.value.copy(coin = coin)
            }
        }

        private fun loadCoinDetailsFromApi() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val coinHistoryResult = getCoinHistoryUseCase(coinId)
                        val coinMarketsResult = getCoinMarketVolumesUseCase(coinId)

                        _state.value =
                            _state.value.copy(
                                history = coinHistoryResult,
                                markets = coinMarketsResult,
                            )
                    } catch (e: Exception) {
                        handleError(e)
                    }
                }
            }
        }

        private suspend fun handleError(exception: Exception) {
            _state.value = _state.value.copy(history = Result.Error(exception), markets = Result.Error(exception))
            when (exception) {
                is HttpException -> _event.emit(Event.ShowError(R.string.init_coin_details_error_text))
                is IOException -> _event.emit(Event.ShowError(R.string.network_error_text))
                else -> _event.emit(Event.ShowError(R.string.unknown_error_text))
            }
        }
    }

interface CoinDetailsViewModelContract : UniDirectionalViewModelContract<State, Intent, Event> {
    data class State(
        val coin: Coin? = null,
        val history: Result<List<HistoryValue>> = Result.Loading,
        val markets: Result<List<MarketValue>> = Result.Loading,
    )

    sealed interface Event {
        data class ShowError(
            val messageResId: Int,
        ) : Event
    }

    sealed interface Intent {
        data object ScreenCreated : Intent
    }
}
