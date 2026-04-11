package com.plcoding.cryptotracker.cryto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.cryto.domain.CoinDataSource
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.cryto.presentation.model.CoinUiState
import com.plcoding.cryptotracker.cryto.presentation.model.toCoinUiState
import com.plcoding.cryptotracker.widget.domain.repository.WidgetCoinRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    private val coinDataSource: CoinDataSource,
    private val widgetCoinRepository: WidgetCoinRepository
): ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state = _state
        .onStart { loadCoins() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            CoinListState()
        )

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CoinListAction) {
        when(action) {
            is CoinListAction.OnCoinClick -> {
                selectCoin(action.coinUi)
            }
            is CoinListAction.OnToggleFavorite -> {
                toggleFavorite(action.coinUi)
            }
            is CoinListAction.OnSetWidgetCoin -> {
                setWidgetCoin(action.coinUi)
            }
        }
    }

    private fun setWidgetCoin(coinUi: CoinUiState) {
        viewModelScope.launch {
            val isFavorite = widgetCoinRepository.isFavorite(coinUi.id)
            if (!isFavorite) return@launch

            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id,
                    start = ZonedDateTime.now().minusDays(7),
                    end = ZonedDateTime.now()
                )
                .onSuccess { history ->
                    val dataPoints = history
                        .sortedBy { it.dateTime }
                        .map {
                            DataPoint(
                                x = it.dateTime.hour.toFloat(),
                                y = it.priceUsd.toFloat(),
                                xLabel = DateTimeFormatter
                                    .ofPattern("ha\nM/d")
                                    .format(it.dateTime)
                            )
                        }
                    widgetCoinRepository.saveSelected(
                        coinId = coinUi.id,
                        coinName = coinUi.name,
                        coinSymbol = coinUi.symbol,
                        priceUsd = coinUi.priceUsd.value,
                        changePercent24Hr = coinUi.changePercent24Hr.value,
                        history = history
                    )
                    widgetCoinRepository.setPreferredWidgetCoinId(coinUi.id)
                    widgetCoinRepository.markUpdated()
                    _state.update {
                        val withHistory = it.coins.map { coin ->
                            if (coin.id == coinUi.id) coin.copy(coinPriceHistory = dataPoints) else coin
                        }
                        it.copy(
                            coins = withHistory,
                            widgetCoinId = coinUi.id,
                            selectedCoin = withHistory.firstOrNull { coin -> coin.id == coinUi.id }
                        )
                    }
                    widgetCoinRepository.refreshWidgets()
                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    private fun toggleFavorite(coinUi: CoinUiState) {
        viewModelScope.launch {
            val isFavorite = widgetCoinRepository.isFavorite(coinUi.id)
            if (isFavorite) {
                widgetCoinRepository.removeFavorite(coinUi.id)
                widgetCoinRepository.markUpdated()
                _state.update {
                    it.copy(
                        favoriteCoinIds = it.favoriteCoinIds - coinUi.id,
                        widgetCoinId = if (it.widgetCoinId == coinUi.id) null else it.widgetCoinId
                    )
                }
                widgetCoinRepository.refreshWidgets()
                return@launch
            }

            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id,
                    start = ZonedDateTime.now().minusDays(7),
                    end = ZonedDateTime.now()
                )
                .onSuccess { history ->
                    widgetCoinRepository.saveSelected(
                        coinId = coinUi.id,
                        coinName = coinUi.name,
                        coinSymbol = coinUi.symbol,
                        priceUsd = coinUi.priceUsd.value,
                        changePercent24Hr = coinUi.changePercent24Hr.value,
                        history = history
                    )
                    widgetCoinRepository.markUpdated()
                    _state.update {
                        val nextFavorites = it.favoriteCoinIds + coinUi.id
                        val preferred = it.widgetCoinId ?: coinUi.id
                        widgetCoinRepository.setPreferredWidgetCoinId(preferred)
                        it.copy(
                            favoriteCoinIds = nextFavorites,
                            widgetCoinId = preferred
                        )
                    }
                    widgetCoinRepository.refreshWidgets()
                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    private fun selectCoin(coinUi: CoinUiState) {
        _state.update { it.copy(selectedCoin = coinUi) }

        viewModelScope.launch {
            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id,
                    start = ZonedDateTime.now().minusDays(5),
                    end = ZonedDateTime.now()
                )
                .onSuccess { history ->
                    val dataPoints = history
                        .sortedBy { it.dateTime }
                        .map {
                            DataPoint(
                                x = it.dateTime.hour.toFloat(),
                                y = it.priceUsd.toFloat(),
                                xLabel = DateTimeFormatter
                                    .ofPattern("ha\nM/d")
                                    .format(it.dateTime)
                            )
                        }

                    _state.update {
                        it.copy(
                            selectedCoin = it.selectedCoin?.copy(
                                coinPriceHistory = dataPoints
                            )
                        )
                    }
                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    favoriteCoinIds = widgetCoinRepository.getFavoriteCoinIds(),
                    widgetCoinId = widgetCoinRepository.getPreferredWidgetCoinId()
                )
            }
            _state.update { it.copy(
                isLoading = true
            ) }

            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    val widgetCoinId = _state.value.widgetCoinId
                    val widgetCoin = widgetCoinId?.let { id ->
                        widgetCoinRepository.getFavoriteByCoinId(id)
                    }
                    val widgetHistory = widgetCoin?.let {
                        widgetCoinRepository.decodeWidgetDataPoints(it.dataPointsJson).map { point ->
                            DataPoint(x = point.x, y = point.y, xLabel = point.xLabel)
                        }
                    } ?: emptyList()

                    _state.update { it.copy(
                        isLoading = false,
                        coins = coins.map { coin ->
                            val ui = coin.toCoinUiState()
                            if (coin.id == widgetCoinId) {
                                ui.copy(coinPriceHistory = widgetHistory)
                            } else {
                                ui
                            }
                        }
                    ) }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

}
