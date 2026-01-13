package com.plcoding.cryptotracker.core.data.network

import kotlinx.serialization.Serializable

@Serializable
data class CoinHistoryDto(
    val data: List<CoinPriceDto>
)