package com.plcoding.cryptotracker.cryto.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinsResponseDto(
    val data: List<CoinDTO>,
)