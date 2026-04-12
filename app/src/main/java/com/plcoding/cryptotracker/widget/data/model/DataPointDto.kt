package com.plcoding.cryptotracker.widget.data.model

import kotlinx.serialization.Serializable

/**
 * Serialized DTO used for storing widget chart points in JSON.
 *
 * @property x X-axis value used by the chart.
 * @property y Y-axis value (price).
 * @property xLabel Label rendered on the X-axis.
 */
@Serializable
data class DataPointDto(
    val x: Float,
    val y: Float,
    val xLabel: String
)
