package com.plcoding.cryptotracker.widget.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_coin")
data class WidgetCoin(
    @PrimaryKey val id: String,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val priceUsd: Double,
    val changePercent24Hr: Double,
    val dataPointsJson: String
)