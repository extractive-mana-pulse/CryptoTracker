package com.plcoding.cryptotracker.widget.presentation

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.plcoding.cryptotracker.widget.domain.repository.WidgetRefresher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of [WidgetRefresher] backed by Glance widgets.
 */
class GlanceWidgetRefresher(
    private val appContext: Context
) : WidgetRefresher {

    override suspend fun refreshAll() {
        withContext(Dispatchers.IO) {
            val manager = GlanceAppWidgetManager(appContext)
            manager.getGlanceIds(CompactCoinWidget::class.java).forEach {
                CompactCoinWidget().update(appContext, it)
            }
            manager.getGlanceIds(ChartCoinWidget::class.java).forEach {
                ChartCoinWidget().update(appContext, it)
            }
        }
    }
}

