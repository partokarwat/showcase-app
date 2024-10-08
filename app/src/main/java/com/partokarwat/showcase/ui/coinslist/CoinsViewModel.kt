package com.partokarwat.showcase.ui.coinslist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.repository.CoinRepository
import com.partokarwat.showcase.ui.UniDirectionalViewModelContract
import com.partokarwat.showcase.ui.coinslist.CoinsViewModelContract.Event
import com.partokarwat.showcase.ui.coinslist.CoinsViewModelContract.Intent
import com.partokarwat.showcase.ui.coinslist.CoinsViewModelContract.State
import com.partokarwat.showcase.usecases.FetchAllCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel
    @Inject
    constructor(
        private val fetchAllCoinsUseCase: FetchAllCoinsUseCase,
        private val coinRepository: CoinRepository,
    ) : ViewModel(),
        CoinsViewModelContract {
        private val _state = MutableStateFlow<State>(State.Loading(false))
        override val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<Event>()
        override val event = _event.asSharedFlow()

        override fun intent(intent: Intent) {
            when (intent) {
                is Intent.ScreenCreated -> createLoadedScreen()
                is Intent.OnSwipeToRefresh -> onSwipeToRefresh()
                is Intent.OnSwitchPerformanceButtonPressed -> switchCoinListByPerformance()
            }
        }

        private fun createLoadedScreen() {
            viewModelScope.launch {
                if (_state.value is State.Loading) {
                    try {
                        withContext(Dispatchers.Default) {
                            fetchAllCoinsUseCase()
                        }
                    } catch (e: Exception) {
                        Log.d(CoinsViewModel::class.simpleName, e.toString())
                        _event.emit(Event.ShowTechnicalError(R.string.loading_data_from_network_error_text))
                    }
                    try {
                        showCoinsFromDatabase(
                            coinRepository.getTop100GainersCoins(),
                            isRefreshing = false,
                            isTopGainers = true,
                        )
                    } catch (e: Exception) {
                        Log.d(CoinsViewModel::class.simpleName, e.toString())
                        _event.emit(Event.ShowTechnicalError(R.string.loading_data_from_database_error_text))
                    }
                }
            }
        }

        private suspend fun showCoinsFromDatabase(
            assets: Flow<List<Coin>>,
            isRefreshing: Boolean,
            isTopGainers: Boolean,
        ) {
            val timeStamp = coinRepository.getLastDataUpdateTimestamp()
            _state.emit(
                State.Loaded(
                    items = assets,
                    lastListUpdateTimestamp = timeStamp,
                    isRefreshing = isRefreshing,
                    isTopGainers = isTopGainers,
                ),
            )
        }

        private fun onSwipeToRefresh() {
            viewModelScope.launch {
                try {
                    val state = _state.value as State.Loaded
                    _state.emit(
                        state.copy(isRefreshing = true),
                    )
                    withContext(Dispatchers.Default) {
                        fetchAllCoinsUseCase()
                    }
                    _state.emit(
                        state.copy(isRefreshing = false),
                    )
                } catch (e: Exception) {
                    Log.d(CoinsViewModel::class.simpleName, e.toString())
                    _event.emit(Event.ShowTechnicalError(R.string.pull_to_refresh_error_text))
                    val state = _state.value as State.Loaded
                    _state.emit(
                        state.copy(isRefreshing = false),
                    )
                }
            }
        }

        private fun switchCoinListByPerformance() {
            val state = _state.value as State.Loaded
            viewModelScope.launch {
                try {
                    _state.emit(State.Loading(false))
                    val assets =
                        if (!state.isTopGainers) coinRepository.getTop100GainersCoins() else coinRepository.getTop100LoserCoins()
                    showCoinsFromDatabase(assets, state.isRefreshing, !state.isTopGainers)
                } catch (e: Exception) {
                    Log.d(CoinsViewModel::class.simpleName, e.toString())
                    _event.emit(Event.ShowTechnicalError())
                }
            }
        }
    }

interface CoinsViewModelContract : UniDirectionalViewModelContract<State, Intent, Event> {
    sealed class State {
        data class Loading(
            val showAlertDialog: Boolean = false,
        ) : State()

        data class Loaded(
            val items: Flow<List<Coin>>,
            val lastListUpdateTimestamp: Flow<Long>,
            val isRefreshing: Boolean = false,
            val isTopGainers: Boolean = true,
        ) : State()
    }

    sealed interface Event {
        data class ShowTechnicalError(
            val errorMessageResId: Int = R.string.technical_error_text,
        ) : Event
    }

    sealed interface Intent {
        data object ScreenCreated : Intent

        data object OnSwipeToRefresh : Intent

        data object OnSwitchPerformanceButtonPressed : Intent
    }
}
