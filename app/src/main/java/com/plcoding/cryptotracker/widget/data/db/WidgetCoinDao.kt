package com.plcoding.cryptotracker.widget.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for widget coin persistence.
 *
 * Exposes both one-shot suspend APIs and reactive [Flow]-based APIs
 * used by widget and settings screens.
 */
@Dao
interface WidgetCoinDao {

    /**
     * Inserts or replaces a [coin] entry.
     * Existing row with the same primary key is overwritten.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(coin: WidgetCoin)

    /** Emits all stored favorites ordered by coin name. */
    @Query("SELECT * FROM widget_coin ORDER BY coinName ASC")
    fun observeFavorites(): Flow<List<WidgetCoin>>

    /** Returns all stored favorites ordered by coin name. */
    @Query("SELECT * FROM widget_coin ORDER BY coinName ASC")
    suspend fun getFavorites(): List<WidgetCoin>

    /** Emits one arbitrary stored coin (first by name), or null if empty. */
    @Query("SELECT * FROM widget_coin ORDER BY coinName ASC LIMIT 1")
    fun observeAny(): Flow<WidgetCoin?>

    /** Returns one arbitrary stored coin (first by name), or null if empty. */
    @Query("SELECT * FROM widget_coin ORDER BY coinName ASC LIMIT 1")
    suspend fun getAny(): WidgetCoin?

    /** Returns the coin matching [coinId], or null when not found. */
    @Query("SELECT * FROM widget_coin WHERE coinId = :coinId LIMIT 1")
    suspend fun getByCoinId(coinId: String): WidgetCoin?

    /** Deletes the coin row matching [coinId]. */
    @Query("DELETE FROM widget_coin WHERE coinId = :coinId")
    suspend fun deleteByCoinId(coinId: String)
}