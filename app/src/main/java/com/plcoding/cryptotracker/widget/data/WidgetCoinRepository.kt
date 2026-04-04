package com.plcoding.cryptotracker.widget.data

import android.content.Context
import com.plcoding.cryptotracker.cryto.domain.model.CoinPrice
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.widget.data.db.WidgetCoin
import com.plcoding.cryptotracker.widget.data.db.WidgetCoinDao
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WidgetCoinRepository(
    private val dao: WidgetCoinDao,
    private val appContext: Context
) {

    companion object {
        private const val PREFS_NAME = "widget_coin_prefs"
        private const val KEY_LAST_UPDATED_AT = "last_updated_at"
        private const val KEY_PREFERRED_WIDGET_COIN_ID = "preferred_widget_coin_id"
    }

    @Serializable
    private data class DataPointDto(val x: Float, val y: Float, val xLabel: String)

    private val prefs by lazy {
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun observeSelected(): Flow<WidgetCoin?> = dao.observeAny()

    suspend fun getSelected(): WidgetCoin? = dao.getAny()

    fun observeFavorites(): Flow<List<WidgetCoin>> = dao.observeFavorites()

    suspend fun getFavorites(): List<WidgetCoin> = dao.getFavorites()

    suspend fun getFavoriteCoinIds(): Set<String> = dao.getFavorites().map { it.coinId }.toSet()

    suspend fun isFavorite(coinId: String): Boolean = dao.getByCoinId(coinId) != null

    suspend fun removeFavorite(coinId: String) {
        dao.deleteByCoinId(coinId)
        if (getPreferredWidgetCoinId() == coinId) {
            clearPreferredWidgetCoinId()
        }
    }

    suspend fun getFavoriteByCoinId(coinId: String): WidgetCoin? = dao.getByCoinId(coinId)

    fun getPreferredWidgetCoinId(): String? =
        prefs.getString(KEY_PREFERRED_WIDGET_COIN_ID, null)

    fun setPreferredWidgetCoinId(coinId: String) {
        prefs.edit().putString(KEY_PREFERRED_WIDGET_COIN_ID, coinId).apply()
    }

    fun clearPreferredWidgetCoinId() {
        prefs.edit().remove(KEY_PREFERRED_WIDGET_COIN_ID).apply()
    }

    fun resolvePreferredFavorite(favorites: List<WidgetCoin>): WidgetCoin? {
        val preferredId = getPreferredWidgetCoinId() ?: return null
        return favorites.firstOrNull { it.coinId == preferredId }
    }

    suspend fun saveSelected(
        coinId: String,
        coinName: String,
        coinSymbol: String,
        priceUsd: Double,
        changePercent24Hr: Double,
        history: List<CoinPrice>
    ) {
        val formatter = DateTimeFormatter.ofPattern("MM/dd\nHH:mm")
        val dtoList = history.mapIndexed { i, cp ->
            DataPointDto(
                x = i.toFloat(),
                y = cp.priceUsd.toFloat(),
                xLabel = cp.dateTime.format(formatter)
            )
        }
        dao.upsert(
            WidgetCoin(
                id = coinId,
                coinId = coinId,
                coinName = coinName,
                coinSymbol = coinSymbol,
                priceUsd = priceUsd,
                changePercent24Hr = changePercent24Hr,
                dataPointsJson = Json.encodeToString(dtoList)
            )
        )
    }

    fun pickCompactFavorite(favorites: List<WidgetCoin>, nowMillis: Long = System.currentTimeMillis()): WidgetCoin? {
        if (favorites.isEmpty()) return null
        val slot = (nowMillis / 900_000L).toInt()
        return favorites[slot % favorites.size]
    }

    fun getLastUpdatedMillis(): Long = prefs.getLong(KEY_LAST_UPDATED_AT, 0L)

    fun markUpdated(nowMillis: Long = System.currentTimeMillis()) {
        prefs.edit().putLong(KEY_LAST_UPDATED_AT, nowMillis).apply()
    }

    fun getLastUpdatedLabel(): String {
        val saved = getLastUpdatedMillis()
        if (saved <= 0L) return "Never"
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return formatter.format(Instant.ofEpochMilli(saved).atZone(ZoneId.systemDefault()))
    }

    fun decodeDataPoints(json: String): List<DataPoint> {
        return Json.decodeFromString<List<DataPointDto>>(json).map {
            DataPoint(x = it.x, y = it.y, xLabel = it.xLabel)
        }
    }
}