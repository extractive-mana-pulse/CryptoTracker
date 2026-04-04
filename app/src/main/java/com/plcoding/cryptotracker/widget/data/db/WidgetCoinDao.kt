package com.plcoding.cryptotracker.widget.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetCoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(coin: WidgetCoin)

    @Query("SELECT * FROM widget_coin ORDER BY coinName ASC")
    fun observeFavorites(): Flow<List<WidgetCoin>>

    @Query("SELECT * FROM widget_coin ORDER BY coinName ASC")
    suspend fun getFavorites(): List<WidgetCoin>

    @Query("SELECT * FROM widget_coin ORDER BY coinName ASC LIMIT 1")
    fun observeAny(): Flow<WidgetCoin?>

    @Query("SELECT * FROM widget_coin ORDER BY coinName ASC LIMIT 1")
    suspend fun getAny(): WidgetCoin?

    @Query("SELECT * FROM widget_coin WHERE coinId = :coinId LIMIT 1")
    suspend fun getByCoinId(coinId: String): WidgetCoin?

    @Query("DELETE FROM widget_coin WHERE coinId = :coinId")
    suspend fun deleteByCoinId(coinId: String)
}