package com.partokarwat.showcase.ui.coinslist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.repository.CoinListRepository
import com.partokarwat.showcase.ui.coinslist.CoinListViewModelContract.Event
import com.partokarwat.showcase.ui.coinslist.CoinListViewModelContract.Intent
import com.partokarwat.showcase.ui.coinslist.CoinListViewModelContract.State
import com.partokarwat.showcase.ui.common.UniDirectionalViewModelContract
import com.partokarwat.showcase.usecases.FetchAllCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel
    @Inject
    constructor(
        private val coinListRepository: CoinListRepository,
        private val fetchAllCoinsUseCase: FetchAllCoinsUseCase,
    ) : ViewModel(),
        CoinListViewModelContract {
        private val _state =
            MutableStateFlow(
                State(
                    items = MutableLiveData(),
                    lastListUpdateTimestamp = coinListRepository.getLastDataUpdateTimestamp().asLiveData(),
                ),
            )
        override val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<Event>()
        override val event = _event.asSharedFlow()

        override fun intent(intent: Intent) {
            when (intent) {
                Intent.OnSwipeToRefresh -> onSwipeToRefresh()
                Intent.ToggleCoinListOrder -> toggleCoinListOrder()
            }
        }

        init {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    _state.value =
                        _state.value.copy(
                            items =
                                if (_state.value.isTopGainers) {
                                    coinListRepository.getTopGainersCoins(_state.value.listSize)
                                } else {
                                    coinListRepository.getTopLoserCoins(_state.value.listSize)
                                }.asLiveData(),
                        )
                    try {
                        fetchAllCoinsUseCase()
                    } catch (e: Exception) {
                        _event.emit(Event.ShowError(R.string.loading_data_from_network_error_text))
                        Log.d(CoinListViewModel::class.java.simpleName, e.toString())
                    }
                }
            }
        }

        private fun onSwipeToRefresh() {
            viewModelScope.launch {
                try {
                    _state.value =
                        _state.value.copy(
                            isRefreshing = true,
                        )
                    withContext(Dispatchers.Default) {
                        fetchAllCoinsUseCase()
                    }
                } catch (e: Exception) {
                    _event.emit(Event.ShowError(R.string.pull_to_refresh_error_text))
                    Log.d(CoinListViewModel::class.java.simpleName, e.toString())
                }
                _state.value =
                    _state.value.copy(
                        isRefreshing = false,
                    )
            }
        }

        private fun toggleCoinListOrder() {
            viewModelScope.launch {
                try {
                    val isTopGainers = !_state.value.isTopGainers
                    _state.value =
                        _state.value.copy(
                            isTopGainers = isTopGainers,
                            items =
                                if (isTopGainers) {
                                    coinListRepository.getTopGainersCoins(_state.value.listSize)
                                } else {
                                    coinListRepository.getTopLoserCoins(_state.value.listSize)
                                }.asLiveData(),
                        )
                } catch (e: Exception) {
                    _event.emit(Event.ShowError(R.string.technical_error_text))
                    Log.d(CoinListViewModel::class.java.simpleName, e.toString())
                }
            }
        }
    }

interface CoinListViewModelContract : UniDirectionalViewModelContract<State, Intent, Event> {
    data class State(
        val listSize: Int = 100,
        val isTopGainers: Boolean = true,
        val items: LiveData<List<Coin>>,
        val lastListUpdateTimestamp: LiveData<Long>,
        val isRefreshing: Boolean = false,
    )

    sealed interface Event {
        data class ShowError(
            val messageResId: Int,
        ) : Event
    }

    sealed interface Intent {
        data object OnSwipeToRefresh : Intent

        data object ToggleCoinListOrder : Intent
    }
}
