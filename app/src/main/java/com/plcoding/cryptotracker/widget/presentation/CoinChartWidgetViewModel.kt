package com.plcoding.cryptotracker.widget.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.cryto.domain.CoinDataSource
import com.plcoding.cryptotracker.cryto.domain.model.Coin
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.widget.data.WidgetCoinRepository
import com.plcoding.cryptotracker.widget.data.db.WidgetCoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class CoinChartWidgetViewModel(
    private val coinDataSource: CoinDataSource,
    private val repository: WidgetCoinRepository,
    private val appContext: Context
) : ViewModel() {

    val selectedCoin = repository.observeSelected()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    val coins = _coins.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _isLoading.update { true }
            coinDataSource.getCoins().onSuccess { list ->
                _coins.update { list.sortedBy { it.rank } }
            }
            _isLoading.update { false }
        }
    }

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
                refreshGlanceWidget()
            }
            _isLoading.update { false }
        }
    }

    fun decodeDataPoints(coin: WidgetCoin): List<DataPoint> =
        repository.decodeDataPoints(coin.dataPointsJson)

    private suspend fun refreshGlanceWidget() {
        try {
            val manager = androidx.glance.appwidget.GlanceAppWidgetManager(appContext)
            manager.getGlanceIds(CompactCoinWidget::class.java).forEach { 
                CompactCoinWidget().update(appContext, it) 
            }
            manager.getGlanceIds(ChartCoinWidget::class.java).forEach { 
                ChartCoinWidget().update(appContext, it) 
            }
        } catch (_: Exception) {}
    }
}