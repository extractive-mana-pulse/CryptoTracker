package com.plcoding.cryptotracker.cryto.presentation.coin_list

import androidx.compose.runtime.Immutable
import com.plcoding.cryptotracker.cryto.presentation.model.CoinUiState

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUiState> = emptyList(),
    val selectedCoin: CoinUiState? = null,
    val favoriteCoinIds: Set<String> = emptySet(),
    val widgetCoinId: String? = null
)