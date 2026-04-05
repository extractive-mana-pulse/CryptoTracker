package com.plcoding.cryptotracker.widget.domain.model

data class WidgetCoinItem(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val priceUsd: Double,
    val changePercent24Hr: Double,
    val dataPointsJson: String
)