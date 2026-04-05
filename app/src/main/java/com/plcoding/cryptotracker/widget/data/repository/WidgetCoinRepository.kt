package com.plcoding.cryptotracker.widget.data.repository

import com.plcoding.cryptotracker.cryto.domain.model.CoinPrice
import com.plcoding.cryptotracker.widget.data.datasource.WidgetCoinLocalDataSource
import com.plcoding.cryptotracker.widget.data.datasource.WidgetPreferencesDataSource
import com.plcoding.cryptotracker.widget.data.mapper.WidgetCoinMapper
import com.plcoding.cryptotracker.widget.domain.model.WidgetCoinItem
import com.plcoding.cryptotracker.widget.domain.model.WidgetChartPoint
import com.plcoding.cryptotracker.widget.domain.repository.IWidgetCoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of [IWidgetCoinRepository].
 * Coordinates between [WidgetCoinLocalDataSource] (Room) and
 * [WidgetPreferencesDataSource] (SharedPreferences).
 * Contains no Android framework imports itself.
 */
class WidgetCoinRepository(
    private val localDataSource: WidgetCoinLocalDataSource,
    private val preferencesDataSource: WidgetPreferencesDataSource
) : IWidgetCoinRepository {

    /** Emits the currently selected coin mapped to domain model. */
    override fun observeSelected(): Flow<WidgetCoinItem?> =
        localDataSource.observeAny().map { it?.let(WidgetCoinMapper::toWidgetCoinItem) }

    /** Returns the current selected coin mapped to domain model. */
    override suspend fun getSelected(): WidgetCoinItem? =
        localDataSource.getAny()?.let(WidgetCoinMapper::toWidgetCoinItem)

    /** Emits all favorites mapped to domain models. */
    override fun observeFavorites(): Flow<List<WidgetCoinItem>> =
        localDataSource.observeFavorites().map { list ->
            list.map(WidgetCoinMapper::toWidgetCoinItem)
        }

    /** Returns all favorites mapped to domain models. */
    override suspend fun getFavorites(): List<WidgetCoinItem> =
        localDataSource.getFavorites().map(WidgetCoinMapper::toWidgetCoinItem)

    /** Returns all favorite coin ids. */
    override suspend fun getFavoriteCoinIds(): Set<String> =
        localDataSource.getFavorites().map { it.coinId }.toSet()

    /** True when the given [coinId] exists in favorites. */
    override suspend fun isFavorite(coinId: String): Boolean =
        localDataSource.getByCoinId(coinId) != null

    /** Returns one favorite by [coinId], mapped to domain model. */
    override suspend fun getFavoriteByCoinId(coinId: String): WidgetCoinItem? =
        localDataSource.getByCoinId(coinId)?.let(WidgetCoinMapper::toWidgetCoinItem)

    /** Deletes a favorite and clears preference if that favorite was pinned. */
    override suspend fun removeFavorite(coinId: String) {
        localDataSource.deleteByCoinId(coinId)
        if (preferencesDataSource.getPreferredCoinId() == coinId) {
            preferencesDataSource.clearPreferredCoinId()
        }
    }

    /** Persists one selected coin with its 7-day [history]. */
    override suspend fun saveSelected(
        coinId: String,
        coinName: String,
        coinSymbol: String,
        priceUsd: Double,
        changePercent24Hr: Double,
        history: List<CoinPrice>
    ) {
        val entity = WidgetCoinMapper.toEntity(
            coinId, coinName, coinSymbol, priceUsd, changePercent24Hr, history
        )
        localDataSource.upsert(entity)
    }

    /** Returns preferred widget coin id from preferences. */
    override fun getPreferredWidgetCoinId(): String? =
        preferencesDataSource.getPreferredCoinId()

    /** Stores preferred widget coin id in preferences. */
    override fun setPreferredWidgetCoinId(coinId: String) =
        preferencesDataSource.setPreferredCoinId(coinId)

    /** Clears preferred widget coin id in preferences. */
    override fun clearPreferredWidgetCoinId() =
        preferencesDataSource.clearPreferredCoinId()

    /** Returns last update timestamp from preferences. */
    override fun getLastUpdatedMillis(): Long =
        preferencesDataSource.getLastUpdatedMillis()

    /** Stores last update timestamp in preferences. */
    override fun markUpdated(nowMillis: Long) =
        preferencesDataSource.markUpdated(nowMillis)

    /** Resolves pinned favorite from [favorites], if present. */
    override fun resolvePreferredFavorite(favorites: List<WidgetCoinItem>): WidgetCoinItem? {
        val preferredId = preferencesDataSource.getPreferredCoinId() ?: return null
        return favorites.firstOrNull { it.coinId == preferredId }
    }

    /** Picks a rotating favorite every 15 minutes for compact widget display. */
    override fun pickCompactFavorite(
        favorites: List<WidgetCoinItem>,
        nowMillis: Long
    ): WidgetCoinItem? {
        if (favorites.isEmpty()) return null
        val slot = (nowMillis / 900_000L).toInt()
        return favorites[slot % favorites.size]
    }

    /** Decodes chart JSON into domain chart points. */
    override fun decodeWidgetDataPoints(json: String): List<WidgetChartPoint> =
        WidgetCoinMapper.decodeWidgetDataPoints(json)
}