package com.plcoding.cryptotracker.cryto.presentation.coin_list

import com.plcoding.cryptotracker.cryto.presentation.model.CoinUiState

sealed interface CoinListAction {
    data class OnCoinClick(val coinUi: CoinUiState): CoinListAction
}