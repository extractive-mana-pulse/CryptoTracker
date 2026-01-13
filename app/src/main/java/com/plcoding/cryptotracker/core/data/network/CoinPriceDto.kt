package com.plcoding.cryptotracker.core.data.network

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceDto(
    val priceUsd: Double,
    val time: Long
)