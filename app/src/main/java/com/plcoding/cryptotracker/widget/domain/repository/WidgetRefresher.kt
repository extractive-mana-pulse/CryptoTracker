package com.plcoding.cryptotracker.widget.domain.repository

/**
 * Triggers re-rendering of widget instances after widget data changes.
 */
interface WidgetRefresher {
    suspend fun refreshAll()
}

