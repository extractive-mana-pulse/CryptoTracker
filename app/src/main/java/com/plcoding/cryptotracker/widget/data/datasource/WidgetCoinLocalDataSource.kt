package com.plcoding.cryptotracker.widget.data.datasource

import com.plcoding.cryptotracker.widget.data.db.WidgetCoin
import com.plcoding.cryptotracker.widget.data.db.WidgetCoinDao
import kotlinx.coroutines.flow.Flow

/**
 * Local data source backed by [WidgetCoinDao].
 *
 * Keeps Room access in one place so repository logic can stay storage-agnostic.
 */
class WidgetCoinLocalDataSource(
    private val dao: WidgetCoinDao
) {
    /** Emits one stored coin (first by name), or null if database is empty. */
    fun observeAny(): Flow<WidgetCoin?> = dao.observeAny()

    /** Returns one stored coin (first by name), or null if database is empty. */
    suspend fun getAny(): WidgetCoin? = dao.getAny()

    /** Emits all stored favorite coins ordered by name. */
    fun observeFavorites(): Flow<List<WidgetCoin>> = dao.observeFavorites()

    /** Returns all stored favorite coins ordered by name. */
    suspend fun getFavorites(): List<WidgetCoin> = dao.getFavorites()

    /** Returns the stored favorite matching [coinId], or null when not found. */
    suspend fun getByCoinId(coinId: String): WidgetCoin? = dao.getByCoinId(coinId)

    /** Deletes the stored favorite matching [coinId]. */
    suspend fun deleteByCoinId(coinId: String) = dao.deleteByCoinId(coinId)

    /** Inserts or replaces a stored [coin]. */
    suspend fun upsert(coin: WidgetCoin) = dao.upsert(coin)
}