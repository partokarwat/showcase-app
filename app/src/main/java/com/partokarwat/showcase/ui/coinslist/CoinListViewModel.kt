package com.partokarwat.showcase.ui.coinslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.repository.CoinListRepository
import com.partokarwat.showcase.usecases.FetchAllCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel
    @Inject
    constructor(
        private val coinListRepository: CoinListRepository,
        private val fetchAllCoinsUseCase: FetchAllCoinsUseCase,
    ) : ViewModel() {
        private val _items = MutableStateFlow(emptyList<Coin>())
        val items: StateFlow<List<Coin>?> = _items

        private val _lastListUpdateTimestamp = MutableStateFlow(0L)
        val lastListUpdateTimestamp: StateFlow<Long?> = _lastListUpdateTimestamp

        private val _isError = MutableStateFlow(IS_ERROR_INITIAL_VALUE)
        val isError: Flow<Pair<Boolean, Int>> get() = _isError.filterNotNull()

        private val _isRefreshing = MutableStateFlow(IS_REFRESHING_INITIAL_VALUE)
        val isRefreshing: Flow<Boolean> get() = _isRefreshing.filterNotNull()

        private val _isTopGainers = MutableStateFlow(IS_TOP_GAINERS_INITIAL_VALUE)
        val isTopGainers: Flow<Boolean> get() = _isTopGainers.filterNotNull()

        init {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    try {
                        fetchAllCoinsUseCase()
                    } catch (e: Exception) {
                        _isError.value = Pair(true, R.string.loading_data_from_network_error_text)
                        e.printStackTrace()
                    }
                    try {
                        val coinListFlow =
                            if (_isTopGainers.value) {
                                coinListRepository.getTop100GainersCoins()
                            } else {
                                coinListRepository
                                    .getTop100LoserCoins()
                            }
                        coinListFlow.collect {
                            _items.value = it
                        }

                        coinListRepository.getLastDataUpdateTimestamp().collect {
                            _lastListUpdateTimestamp.value = it
                        }
                        _isError.value = IS_ERROR_INITIAL_VALUE
                    } catch (e: Exception) {
                        _isError.value = Pair(true, R.string.loading_data_from_database_error_text)
                        e.printStackTrace()
                    }
                }
            }
        }

        fun onSwipeToRefresh() {
            viewModelScope.launch {
                try {
                    _isRefreshing.value = true
                    withContext(Dispatchers.Default) {
                        fetchAllCoinsUseCase()
                    }
                    _isRefreshing.value = false
                } catch (e: Exception) {
                    _isError.value = Pair(true, R.string.pull_to_refresh_error_text)
                    _isRefreshing.value = false
                    e.printStackTrace()
                }
            }
        }

        fun switchCoinListByPerformance() {
            viewModelScope.launch {
                try {
                    if (!_isTopGainers.value) {
                        coinListRepository.getTop100GainersCoins().collect {
                            _items.value = it
                        }
                    } else {
                        coinListRepository.getTop100LoserCoins().collect {
                            _items.value = it
                        }
                    }
                    _isTopGainers.value = !_isTopGainers.value
                } catch (e: Exception) {
                    _isError.value = Pair(true, R.string.technical_error_text)
                    e.printStackTrace()
                }
            }
        }

        companion object {
            val IS_ERROR_INITIAL_VALUE = Pair(false, 0)
            const val IS_REFRESHING_INITIAL_VALUE = false
            const val IS_TOP_GAINERS_INITIAL_VALUE = true
        }
    }
