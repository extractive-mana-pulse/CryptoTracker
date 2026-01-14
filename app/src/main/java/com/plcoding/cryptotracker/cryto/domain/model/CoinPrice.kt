package com.plcoding.cryptotracker.cryto.domain.model

import java.time.ZonedDateTime

data class CoinPrice(
    val priceUsd: Double,
    val dateTime: ZonedDateTime
)