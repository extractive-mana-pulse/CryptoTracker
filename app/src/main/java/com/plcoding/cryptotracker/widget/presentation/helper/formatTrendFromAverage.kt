package com.plcoding.cryptotracker.widget.presentation.helper

import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint
import kotlin.collections.map

internal fun formatTrendFromAverage(dataPoints: List<DataPoint>): Pair<String, Double> {
    if (dataPoints.isEmpty()) return "" to 0.0
    val avg  = dataPoints.map { it.y.toDouble() }.average()
    val last = dataPoints.last().y.toDouble()
    if (avg == 0.0) return "" to 0.0
    val pct   = ((last - avg) / avg) * 100.0
    val arrow = if (pct >= 0) "▲" else "▼"
    return arrow to pct
}
