package com.plcoding.cryptotracker.widget.presentation

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.ChartStyle
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.widget.domain.model.defaultDataPoints
import com.plcoding.cryptotracker.widget.domain.repository.WidgetCoinRepository
import com.plcoding.cryptotracker.widget.presentation.components.CoinIconBadge
import com.plcoding.cryptotracker.widget.presentation.components.EmptyFavoritesContent
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.CHART_AXIS_DARK
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.CHART_AXIS_LIGHT
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.CHART_LINE_DARK
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.CHART_LINE_LIGHT
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.CHART_SELECTED_DARK
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.CHART_SELECTED_LIGHT
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.changeColor
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.mutedTextColor
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.primaryTextColor
import com.plcoding.cryptotracker.widget.presentation.helper.WidgetColors.secondaryTextColor
import com.plcoding.cryptotracker.widget.presentation.helper.formatPrice
import com.plcoding.cryptotracker.widget.presentation.helper.formatTrendFromAverage
import com.plcoding.cryptotracker.widget.presentation.helper.formatUpdatedAt
import org.koin.java.KoinJavaComponent.get
import java.util.Locale

class ChartCoinWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = get<WidgetCoinRepository>(WidgetCoinRepository::class.java)
        val favorites = repository.getFavorites()
        val preferredFavorite = repository.resolvePreferredFavorite(favorites)
        val updatedAtLabel = formatUpdatedAt(repository.getLastUpdatedMillis())

        val isNight = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val style = ChartStyle(
            chartLineColor = if (isNight) CHART_LINE_DARK else CHART_LINE_LIGHT,
            unselectedColor = if (isNight) CHART_AXIS_DARK else CHART_AXIS_LIGHT,
            selectedColor = if (isNight) CHART_SELECTED_DARK else CHART_SELECTED_LIGHT,
            helperLinesThicknessPx = 1f,
            axisLinesThicknessPx = 1f,
            labelFontSize = 10.sp,
            minYLabelSpacing = 18.dp,
            verticalPadding = 6.dp,
            horizontalPadding = 6.dp,
            xAxisLabelSpacing = 8.dp
        )

        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ImageProvider(R.drawable.widget_background))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    val coin = preferredFavorite ?: favorites.firstOrNull()

                    if (coin == null) {
                        EmptyFavoritesContent(updatedAtLabel)
                    } else {
                        val dataPoints = repository
                            .decodeWidgetDataPoints(coin.dataPointsJson)
                            .map { DataPoint(x = it.x, y = it.y, xLabel = it.xLabel) }
                            .ifEmpty { defaultDataPoints() }

                        val (trendArrow, trendPct) = formatTrendFromAverage(dataPoints)

                        Column(modifier = GlanceModifier.fillMaxSize()) {

                            Row(
                                modifier          = GlanceModifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Leading icon
                                CoinIconBadge(coinSymbol = coin.coinSymbol, iconSize = 48.dp)

                                Spacer(GlanceModifier.width(12.dp))

                                // Coin name + symbol
                                Column(modifier = GlanceModifier.defaultWeight()) {
                                    Text(
                                        text = coin.coinName,
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryTextColor(),
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    )
                                    Text(
                                        text = coin.coinSymbol,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = mutedTextColor(),
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    )
                                }

                                // Price + trend — trailing, right-aligned
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text  = formatPrice(coin.priceUsd),
                                        style = TextStyle(
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = primaryTextColor(),
                                            fontFamily = FontFamily.Serif
                                        )
                                    )

                                    // Trend badge: arrow + absolute percentage
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text  = trendArrow,
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = changeColor(trendPct),
                                            )
                                        )
                                        Spacer(GlanceModifier.width(3.dp))
                                        Text(
                                            text = String.format(
                                                Locale.US, "%.2f%%", kotlin.math.abs(trendPct)
                                            ),
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = changeColor(trendPct),
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                    }
                                }

                                Spacer(GlanceModifier.width(8.dp))

                                Image(
                                    provider = ImageProvider(R.drawable.twotone_refresh_24),
                                    contentDescription = null,
                                    modifier = GlanceModifier
                                        .size(24.dp)
                                        .clickable(actionRunCallback<RefreshWidgetCallback>())
                                )
                            }

                            GlanceLineChart(
                                dataPoints = dataPoints,
                                style = style,
                                visibleDataPointsIndices = dataPoints.indices,
                                unit = "$",
                                selectedDataPoint = dataPoints.last(),
                                showHelperLines = true,
                                widthPx = 1200,
                                heightPx = 380,
                                density = 3f,
                                xLabelLineSpacingPx = 6f,
                                xLabelTopPaddingPx = 4f,
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .defaultWeight()
                            )


                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Updated $updatedAtLabel",
                                    style = TextStyle(fontSize = 9.sp, color = mutedTextColor()),
                                    modifier = GlanceModifier.defaultWeight()
                                )
                                Text(
                                    text = "7D",
                                    style = TextStyle(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = secondaryTextColor()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}