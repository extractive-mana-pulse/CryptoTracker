package com.plcoding.cryptotracker.cryto.presentation.coin_list

import com.plcoding.cryptotracker.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError): CoinListEvent
}