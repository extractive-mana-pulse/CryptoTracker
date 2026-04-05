package com.plcoding.cryptotracker.widget.presentation

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.cryto.domain.CoinDataSource
import com.plcoding.cryptotracker.cryto.domain.model.Coin
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.widget.domain.model.WidgetCoinItem
import com.plcoding.cryptotracker.widget.domain.repository.IWidgetCoinRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

/**
 * ViewModel for the in-app widget configuration screen [CoinChartWidget].
 *
 * Responsibilities:
 * - Load the list of available coins from the network.
 * - Allow the user to select a coin, fetch its 7-day history, and persist it.
 * - Trigger a Glance widget refresh after each successful selection.
 *
 * Depends on [IWidgetCoinRepository] (domain interface) and [CoinDataSource].
 * No data-layer types leak into this class.
 */
class CoinChartWidgetViewModel(
    private val coinDataSource: CoinDataSource,
    private val repository: IWidgetCoinRepository,
    private val appContext: Context
) : ViewModel() {

    /**
     * The currently persisted selected coin, or null if none has been chosen.
     * Collected from the repository as a hot StateFlow.
     */
    val selectedCoin = repository.observeSelected()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())

    /** Live list of all coins available for selection, sorted by market rank. */
    val coins = _coins.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    /** True while a network request is in flight. */
    val isLoading = _isLoading.asStateFlow()

    init {
        loadCoins()
    }

    /** Fetches coins from the network and populates [coins]. */
    private fun loadCoins() {
        viewModelScope.launch {
            _isLoading.update { true }
            coinDataSource.getCoins().onSuccess { list ->
                _coins.update { list.sortedBy { it.rank } }
            }
            _isLoading.update { false }
        }
    }

    /**
     * Selects [coin] as the widget coin:
     * fetches 7-day history, persists it, marks the update timestamp,
     * and refreshes all active Glance widgets.
     */
    fun selectCoin(coin: Coin) {
        viewModelScope.launch {
            _isLoading.update { true }
            val end = ZonedDateTime.now()
            val start = end.minusDays(7)
            coinDataSource.getCoinHistory(coin.id, start, end).onSuccess { history ->
                repository.saveSelected(
                    coinId = coin.id,
                    coinName = coin.name,
                    coinSymbol = coin.symbol,
                    priceUsd = coin.priceUsd,
                    changePercent24Hr = coin.changePercent24Hr,
                    history = history
                )
                repository.markUpdated()
                refreshGlanceWidgets()
            }
            _isLoading.update { false }
        }
    }

    /**
     * Decodes the serialized chart data inside [coin] into a list of [DataPoint]
     * suitable for the Compose line chart.
     */
    fun decodeDataPoints(coin: WidgetCoinItem): List<DataPoint> =
        repository.decodeWidgetDataPoints(coin.dataPointsJson).map {
            DataPoint(x = it.x, y = it.y, xLabel = it.xLabel)
        }

    /** Notifies all active Glance widget instances to re-render. */
    private suspend fun refreshGlanceWidgets() {
        try {
            withContext(Dispatchers.IO) {
                val manager = GlanceAppWidgetManager(appContext)
                manager.getGlanceIds(CompactCoinWidget::class.java).forEach {
                    CompactCoinWidget().update(appContext, it)
                }
                manager.getGlanceIds(ChartCoinWidget::class.java).forEach {
                    ChartCoinWidget().update(appContext, it)
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.w("CoinChartWidgetViewModel", "Widget refresh failed", e)
        }
    }
}