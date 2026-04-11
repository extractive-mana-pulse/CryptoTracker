package com.plcoding.cryptotracker.widget.presentation

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.plcoding.cryptotracker.core.domain.util.Result
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.cryto.domain.CoinDataSource
import com.plcoding.cryptotracker.widget.domain.repository.WidgetCoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.get
import java.time.ZonedDateTime

/**
 * Glance [ActionCallback] triggered when the user taps the refresh button on any widget.
 *
 * For each favorited coin it:
 * 1. Fetches the latest 7-day price history.
 * 2. Updates the persisted price using freshly fetched coin data where available.
 * 3. Marks the update timestamp.
 * 4. Re-renders both [CompactCoinWidget] and [ChartCoinWidget].
 *
 * Depends on [WidgetCoinRepository] — the concrete implementation is never referenced.
 */
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
                // Fetch latest prices once, then use them for each favorite
                val latestCoinsById = when (val result = coinDataSource.getCoins()) {
                    is Result.Success -> result.data.associateBy { it.id }
                    is Result.Error -> emptyMap()
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
                                changePercent24Hr = latest?.changePercent24Hr
                                    ?: favorite.changePercent24Hr,
                                history = history
                            )
                        }
                }
                repository.markUpdated()
            }
        }

        // Re-render widgets outside IO dispatcher (Glance requires main-safe context)
        CompactCoinWidget().update(context, glanceId)
        ChartCoinWidget().update(context, glanceId)
    }
}