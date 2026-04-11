package com.plcoding.cryptotracker.widget.domain.repository

import com.plcoding.cryptotracker.cryto.domain.model.CoinPrice
import com.plcoding.cryptotracker.widget.domain.model.WidgetCoinItem
import com.plcoding.cryptotracker.widget.domain.model.WidgetChartPoint
import kotlinx.coroutines.flow.Flow

/**
 * Contract for all widget coin data operations.
 * Lives in the domain layer — no Android or framework imports.
 */
interface WidgetCoinRepository {

    // --- Observation ---

    /** Emits the currently selected widget coin, or null if none. */
    fun observeSelected(): Flow<WidgetCoinItem?>

    /** Returns the currently selected widget coin, or null if none. */
    suspend fun getSelected(): WidgetCoinItem?

    /** Emits the full list of favorited coins in real time. */
    fun observeFavorites(): Flow<List<WidgetCoinItem>>

    /** Returns the full list of favorited coins. */
    suspend fun getFavorites(): List<WidgetCoinItem>

    // --- Queries ---

    /** Returns the set of coin IDs that are currently favorited. */
    suspend fun getFavoriteCoinIds(): Set<String>

    /** Returns true if the coin with [coinId] is favorited. */
    suspend fun isFavorite(coinId: String): Boolean

    /** Returns the favorited coin matching [coinId], or null. */
    suspend fun getFavoriteByCoinId(coinId: String): WidgetCoinItem?

    // --- Mutations ---

    /** Removes the coin with [coinId] from favorites and clears preferred if it matches. */
    suspend fun removeFavorite(coinId: String)

    /**
     * Persists a coin with its price history as the selected widget coin.
     * Existing entry for [coinId] is replaced.
     */
    suspend fun saveSelected(
        coinId: String,
        coinName: String,
        coinSymbol: String,
        priceUsd: Double,
        changePercent24Hr: Double,
        history: List<CoinPrice>
    )

    // --- Preferences ---

    /** Returns the coin ID the user has pinned as their preferred widget coin. */
    fun getPreferredWidgetCoinId(): String?

    /** Pins [coinId] as the preferred widget coin. */
    fun setPreferredWidgetCoinId(coinId: String)

    /** Clears the pinned preferred widget coin. */
    fun clearPreferredWidgetCoinId()

    /** Returns the epoch millis of the last widget data refresh. */
    fun getLastUpdatedMillis(): Long

    /** Records the current time as the last widget data refresh timestamp. */
    fun markUpdated(nowMillis: Long = System.currentTimeMillis())

    // --- Business logic ---

    /**
     * Finds the user's pinned favorite from [favorites], or null if no pin is set
     * or the pinned coin is no longer in the list.
     */
    fun resolvePreferredFavorite(favorites: List<WidgetCoinItem>): WidgetCoinItem?

    /**
     * Picks a coin from [favorites] using a time-based round-robin slot,
     * rotating every 15 minutes. Returns null if [favorites] is empty.
     */
    fun pickCompactFavorite(
        favorites: List<WidgetCoinItem>,
        nowMillis: Long = System.currentTimeMillis()
    ): WidgetCoinItem?

    /**
     * Decodes a JSON string of data points into a list of [WidgetChartPoint].
     * Returns an empty list if decoding fails.
     */
    fun decodeWidgetDataPoints(json: String): List<WidgetChartPoint>

    /** Refreshes all active widget instances after widget data changes. */
    suspend fun refreshWidgets()
}