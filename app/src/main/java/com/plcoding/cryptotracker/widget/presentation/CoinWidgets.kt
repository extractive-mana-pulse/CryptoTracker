package com.plcoding.cryptotracker.widget.presentation

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.core.presentation.util.getDrawableIdForCoin
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.ChartStyle
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.widget.domain.repository.IWidgetCoinRepository
import org.koin.java.KoinJavaComponent.get
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TEXT_PRIMARY_LIGHT = Color(0xFF020617)
private val TEXT_PRIMARY_DARK = Color(0xFFFFFFFF)
private val TEXT_SECONDARY_LIGHT = Color(0xFF334155)
private val TEXT_SECONDARY_DARK = Color(0xFFE2E8F0)
private val POSITIVE = Color(0xFF22C55E)
private val NEGATIVE = Color(0xFFEF4444)
private val CHART_LINE_LIGHT = Color(0xFF2563EB)
private val CHART_LINE_DARK = Color(0xFF60A5FA)
private val CHART_SELECTED_LIGHT = Color(0xFF0EA5E9)
private val CHART_SELECTED_DARK = Color(0xFF34D399)
// For chart labels + helper lines: pure black in light mode, pure white in dark mode.
private val CHART_LABELS_LIGHT = Color(0xFF000000)
private val CHART_LABELS_DARK = Color(0xFFFFFFFF)

fun primaryTextColor() = ColorProvider(day = TEXT_PRIMARY_LIGHT, night = TEXT_PRIMARY_DARK)
fun secondaryTextColor() = ColorProvider(day = TEXT_SECONDARY_LIGHT, night = TEXT_SECONDARY_DARK)
fun changeTextColor(change: Double) = ColorProvider(day = if (change >= 0) POSITIVE else NEGATIVE, night = if (change >= 0) POSITIVE else NEGATIVE)

fun formatPrice(priceUsd: Double): String = "$" + String.format(Locale.US, "%,.2f", priceUsd)
fun formatTrendFromAverage(dataPoints: List<DataPoint>): Pair<String, Double> {
    if (dataPoints.isEmpty()) return "" to 0.0
    val avg = dataPoints.map { it.y.toDouble() }.average()
    val last = dataPoints.last().y.toDouble()
    if (avg == 0.0) return "" to 0.0
    val pct = ((last - avg) / avg) * 100.0
    val arrow = if (pct >= 0) "▲" else "▼"
    return arrow to pct
}

private fun formatUpdatedAtLabel(lastUpdatedMillis: Long): String {
    if (lastUpdatedMillis <= 0L) return "Never"
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(Instant.ofEpochMilli(lastUpdatedMillis).atZone(ZoneId.systemDefault()))
}

@Composable
fun CoinIconBadge(coinSymbol: String, iconSize: androidx.compose.ui.unit.Dp) {
    Image(
        provider = ImageProvider(getDrawableIdForCoin(coinSymbol)),
        contentDescription = coinSymbol,
        modifier = GlanceModifier.size(iconSize)
    )
}

@Composable
fun EmptyFavoritesContent(updatedAtLabel: String) {
    Column(modifier = GlanceModifier.fillMaxSize()) {
        Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(text = "No favorites", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryTextColor()))
            }
            Image(
                provider = ImageProvider(R.drawable.twotone_refresh_24),
                contentDescription = "Refresh",
                modifier = GlanceModifier.size(24.dp).clickable(actionRunCallback<RefreshWidgetCallback>())
            )
        }
        Spacer(GlanceModifier.height(12.dp))
        Text(text = "Add a coin to favorites from the app.", style = TextStyle(fontSize = 11.sp, color = secondaryTextColor()))
        Spacer(GlanceModifier.defaultWeight())
        Text(text = "Updated $updatedAtLabel", style = TextStyle(fontSize = 8.sp, color = secondaryTextColor()))
    }
}

private fun defaultDataPoints() = listOf(
    DataPoint(0f, 59200f, "M"), DataPoint(1f, 60800f, "T"), DataPoint(2f, 60100f, "W"), DataPoint(3f, 61500f, "T"),
    DataPoint(4f, 60900f, "F"), DataPoint(5f, 62100f, "S"), DataPoint(6f, 62430f, "S")
)

class CompactCoinWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = get<IWidgetCoinRepository>(IWidgetCoinRepository::class.java)
        val favorites = repository.getFavorites()
        val preferredFavorite = repository.resolvePreferredFavorite(favorites)

        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_background)).padding(8.dp)
                ) {
                    val coin = preferredFavorite ?: repository.pickCompactFavorite(favorites)
                    if (coin != null) {
                        Row(
                            modifier = GlanceModifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            CoinIconBadge(coinSymbol = coin.coinSymbol, iconSize = 22.dp)
                            Spacer(GlanceModifier.width(8.dp))
                            Text(text = formatPrice(coin.priceUsd), style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryTextColor()))
                        }
                    } else {
                        Text(text = "No coin", style = TextStyle(color = primaryTextColor()))
                    }
                }
            }
        }
    }
}

class ChartCoinWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = get<IWidgetCoinRepository>(IWidgetCoinRepository::class.java)
        val favorites = repository.getFavorites()
        val preferredFavorite = repository.resolvePreferredFavorite(favorites)
        val updatedAtLabel = formatUpdatedAtLabel(repository.getLastUpdatedMillis())
        val isNightMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val style = ChartStyle(
            chartLineColor = if (isNightMode) CHART_LINE_DARK else CHART_LINE_LIGHT,
            unselectedColor = if (isNightMode) CHART_LABELS_DARK else CHART_LABELS_LIGHT,
            selectedColor = if (isNightMode) CHART_SELECTED_DARK else CHART_SELECTED_LIGHT,
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
                    modifier = GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_background)).padding(6.dp)
                ) {
                    val coin = preferredFavorite ?: favorites.firstOrNull()
                    if (coin == null) {
                        EmptyFavoritesContent(updatedAtLabel)
                    } else {
                        val dataPoints = repository.decodeWidgetDataPoints(coin.dataPointsJson)
                            .map { point -> DataPoint(x = point.x, y = point.y, xLabel = point.xLabel) }
                            .ifEmpty { defaultDataPoints() }
                        val (trendArrow, trendPct) = formatTrendFromAverage(dataPoints)
                        
                        Column(modifier = GlanceModifier.fillMaxSize()) {
                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = GlanceModifier.defaultWeight()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CoinIconBadge(coinSymbol = coin.coinSymbol, iconSize = 18.dp)
                                        Spacer(GlanceModifier.width(6.dp))
                                        Text(text = coin.coinName, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryTextColor()))
                                    }
                                    Text(text = coin.coinSymbol, style = TextStyle(fontSize = 10.sp, color = secondaryTextColor()))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = formatPrice(coin.priceUsd), style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryTextColor()))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = trendArrow,
                                            style = TextStyle(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = changeTextColor(trendPct)
                                            )
                                        )
                                        Spacer(GlanceModifier.width(4.dp))
                                        Text(
                                            text = String.format(Locale.US, "%.2f%%", kotlin.math.abs(trendPct)),
                                            style = TextStyle(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = changeTextColor(trendPct)
                                            )
                                        )
                                    }
                                }
                                Spacer(GlanceModifier.width(8.dp))
                                Image(
                                    provider = ImageProvider(R.drawable.twotone_refresh_24),
                                    contentDescription = "Refresh",
                                    modifier = GlanceModifier
                                        .size(24.dp)
                                        .clickable(actionRunCallback<RefreshWidgetCallback>())
                                )
                            }
                            Spacer(GlanceModifier.height(4.dp))
                            GlanceLineChart(
                                dataPoints = dataPoints, style = style, visibleDataPointsIndices = dataPoints.indices, unit = "$",
                                selectedDataPoint = dataPoints.last(), showHelperLines = true, widthPx = 1200, heightPx = 380,
                                density = 3f, xLabelLineSpacingPx = 6f, xLabelTopPaddingPx = 4f, modifier = GlanceModifier.fillMaxWidth().defaultWeight()
                            )
                            Spacer(GlanceModifier.height(2.dp))
                            Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Updated $updatedAtLabel", style = TextStyle(fontSize = 8.sp, color = secondaryTextColor()), modifier = GlanceModifier.defaultWeight())
                                Text(text = "7D", style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = secondaryTextColor()))
                            }
                        }
                    }
                }
            }
        }
    }
}
