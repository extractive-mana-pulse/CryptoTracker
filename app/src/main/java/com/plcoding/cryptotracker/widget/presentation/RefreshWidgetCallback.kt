package com.plcoding.cryptotracker.widget.presentation

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.cryto.domain.CoinDataSource
import com.plcoding.cryptotracker.widget.data.WidgetCoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.get
import java.time.ZonedDateTime

class RefreshWidgetCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        withContext(Dispatchers.IO) {
            val repository = get<WidgetCoinRepository>(WidgetCoinRepository::class.java)
            val coinDataSource = get<CoinDataSource>(CoinDataSource::class.java)
            val favorites = repository.getFavorites()

            if (favorites.isNotEmpty()) {
                val latestCoinsById = coinDataSource.getCoins()
                    .onSuccess { }
                    .let { result ->
                        when (result) {
                            is com.plcoding.cryptotracker.core.domain.util.Result.Success -> {
                                result.data.associateBy { it.id }
                            }
                            is com.plcoding.cryptotracker.core.domain.util.Result.Error -> emptyMap()
                        }
                    }

                val end = ZonedDateTime.now()
                val start = end.minusDays(7)
                favorites.forEach { favorite ->
                    coinDataSource.getCoinHistory(favorite.coinId, start, end)
                        .onSuccess { history ->
                            val latest = latestCoinsById[favorite.coinId]
                            repository.saveSelected(
                                coinId = favorite.coinId,
                                coinName = favorite.coinName,
                                coinSymbol = favorite.coinSymbol,
                                priceUsd = latest?.priceUsd ?: favorite.priceUsd,
                                changePercent24Hr = latest?.changePercent24Hr ?: favorite.changePercent24Hr,
                                history = history
                            )
                        }
                }
                repository.markUpdated()
            }
        }
        CompactCoinWidget().update(context, glanceId)
        ChartCoinWidget().update(context, glanceId)
    }
}