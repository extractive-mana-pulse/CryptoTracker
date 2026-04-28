package com.plcoding.cryptotracker.widget.presentation.helper

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider


internal object WidgetColors {
    private val TEXT_PRIMARY_LIGHT = Color(0xFF0F172A)
    private val TEXT_PRIMARY_DARK = Color(0xFFFFFFFF)

    private val TEXT_SECONDARY_LIGHT = Color(0xFF334155)
    private val TEXT_SECONDARY_DARK = Color(0xFFE2E8F0)

    private val TEXT_MUTED_LIGHT = Color(0xFF64748B)
    private val TEXT_MUTED_DARK = Color(0xFF94A3B8)

    private val POSITIVE_LIGHT = Color(0xFF16A34A)
    private val POSITIVE_DARK = Color(0xFF22C55E)
    private val NEGATIVE_LIGHT = Color(0xFFDC2626)
    private val NEGATIVE_DARK = Color(0xFFEF4444)

    val CHART_LINE_LIGHT = Color(0xFF2563EB)
    val CHART_LINE_DARK = Color(0xFF60A5FA)
    val CHART_SELECTED_LIGHT = Color(0xFF0EA5E9)
    val CHART_SELECTED_DARK = Color(0xFF34D399)
    val CHART_AXIS_LIGHT = Color(0xFF0F172A)
    val CHART_AXIS_DARK = Color(0xFFFFFFFF)

    internal fun primaryTextColor()   = ColorProvider(day = TEXT_PRIMARY_LIGHT, night = TEXT_PRIMARY_DARK)
    internal fun secondaryTextColor() = ColorProvider(day = TEXT_SECONDARY_LIGHT, night = TEXT_SECONDARY_DARK)
    internal fun mutedTextColor()     = ColorProvider(day = TEXT_MUTED_LIGHT, night = TEXT_MUTED_DARK)

    internal fun changeColor(change: Double) = ColorProvider(
        day   = if (change >= 0) POSITIVE_LIGHT else NEGATIVE_LIGHT,
        night = if (change >= 0) POSITIVE_DARK else NEGATIVE_DARK
    )
}