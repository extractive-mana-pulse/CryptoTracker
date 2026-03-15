package com.plcoding.cryptotracker.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.plcoding.cryptotracker.widget.presentation.DeviceTempWidget

class CoinWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = DeviceTempWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.e("ErrorOnClick", "Button was clicked.")
    }
}