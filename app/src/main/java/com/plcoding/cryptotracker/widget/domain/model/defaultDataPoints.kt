package com.plcoding.cryptotracker.widget.domain.model

import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint

internal fun defaultDataPoints() = listOf(
    DataPoint(0f, 59200f, "M"), DataPoint(1f, 60800f, "T"),
    DataPoint(2f, 60100f, "W"), DataPoint(3f, 61500f, "T"),
    DataPoint(4f, 60900f, "F"), DataPoint(5f, 62100f, "S"),
    DataPoint(6f, 62430f, "S")
)