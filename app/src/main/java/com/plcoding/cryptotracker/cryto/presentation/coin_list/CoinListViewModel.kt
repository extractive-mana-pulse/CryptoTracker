package com.plcoding.cryptotracker.cryto.presentation.coin_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.cryto.domain.CoinDataSource
import com.plcoding.cryptotracker.cryto.presentation.model.toCoinUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
): ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state = _state
        .onStart { loadCoins() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            CoinListState()
        )

    fun onAction(action: CoinListAction) {
        when(action) {
            is CoinListAction.OnCoinClick -> {

            }
        }
    }
    private fun loadCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            coinDataSource.getCoins().onSuccess { coins ->
                // Log the received coins
                Log.d("CoinListViewModel", "Received coins: $coins")
                _state.update { it.copy(isLoading = false, coins = coins.map { it.toCoinUiState() }) }
            }.onError { error ->
                Log.e("CoinListViewModel", "Error fetching coins: $error")
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}