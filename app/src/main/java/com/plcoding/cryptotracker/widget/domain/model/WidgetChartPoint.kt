package com.plcoding.cryptotracker.widget.domain.model

/**
 * Domain model for one point in the widget chart series.
 *
 * @property x X-axis value used for chart positioning.
 * @property y Y-axis value (price).
 * @property xLabel Human-readable label for the X-axis tick.
 */
data class WidgetChartPoint(
    val x: Float,
    val y: Float,
    val xLabel: String
)
