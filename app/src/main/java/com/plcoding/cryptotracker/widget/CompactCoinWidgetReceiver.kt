package com.plcoding.cryptotracker.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.plcoding.cryptotracker.widget.presentation.CompactCoinWidget

class CompactCoinWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CompactCoinWidget()
}

