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
import com.partokarwat.showcase.usecases.FetchAllCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
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
        private val _isTopGainers = MutableStateFlow(IS_TOP_GAINERS_INITIAL_VALUE)
        val isTopGainers: Flow<Boolean> get() = _isTopGainers.filterNotNull()

        val items: LiveData<List<Coin>> =
            isTopGainers
                .flatMapLatest { isTopGainers ->
                    if (isTopGainers) {
                        coinListRepository.getTopGainersCoins(LIST_SIZE)
                    } else {
                        coinListRepository.getTopLoserCoins(LIST_SIZE)
                    }
                }.asLiveData()

        val lastListUpdateTimestamp = coinListRepository.getLastDataUpdateTimestamp().asLiveData()

        private val _isError = MutableStateFlow(IS_ERROR_INITIAL_VALUE)
        val isError: Flow<Pair<Boolean, Int>> get() = _isError.filterNotNull()

        private val _isRefreshing = MutableLiveData(IS_REFRESHING_INITIAL_VALUE)
        val isRefreshing: LiveData<Boolean>
            get() = _isRefreshing

        init {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    try {
                        fetchAllCoinsUseCase()
                    } catch (e: Exception) {
                        _isError.emit(Pair(true, R.string.loading_data_from_network_error_text))
                        Log.d(CoinListViewModel::class.java.simpleName, e.toString())
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
                    Log.d(CoinListViewModel::class.java.simpleName, e.toString())
                }
            }
        }

        fun toggleCoinListOrder() {
            viewModelScope.launch {
                try {
                    _isTopGainers.emit(!_isTopGainers.value)
                } catch (e: Exception) {
                    _isError.value = Pair(true, R.string.technical_error_text)
                    Log.d(CoinListViewModel::class.java.simpleName, e.toString())
                }
            }
        }

        fun resetIsError() {
            _isError.value = IS_ERROR_INITIAL_VALUE
        }

        companion object {
            val IS_ERROR_INITIAL_VALUE = Pair(false, 0)
            const val IS_REFRESHING_INITIAL_VALUE = false
            const val IS_TOP_GAINERS_INITIAL_VALUE = true
            const val LIST_SIZE = 100
        }
    }
