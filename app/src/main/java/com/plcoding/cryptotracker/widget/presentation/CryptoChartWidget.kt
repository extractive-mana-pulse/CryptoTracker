package com.plcoding.cryptotracker.widget.presentation

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.BackgroundModifier
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
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
import androidx.glance.unit.ColorProvider
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.ChartStyle
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint

class DeviceTempWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_SQUARE = DpSize(109.dp, 56.dp)
        private val MEDIUM_RECT = DpSize(109.dp, 110.dp)
        private val LARGE_SQUARE = DpSize(250.dp, 250.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SMALL_SQUARE, MEDIUM_RECT, LARGE_SQUARE)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dataPoints = listOf(
            DataPoint(x = 0f, y = 59200f, xLabel = "M"),
            DataPoint(x = 1f, y = 60800f, xLabel = "T"),
            DataPoint(x = 2f, y = 60100f, xLabel = "W"),
            DataPoint(x = 3f, y = 61500f, xLabel = "T"),
            DataPoint(x = 4f, y = 60900f, xLabel = "F"),
            DataPoint(x = 5f, y = 62100f, xLabel = "S"),
            DataPoint(x = 6f, y = 62430f, xLabel = "S"),
        )

        val style = ChartStyle(
            chartLineColor = Color(0xFF00C853),
            unselectedColor = Color(0x88FFFFFF),
            selectedColor = Color(0xFFFFFFFF),
            helperLinesThicknessPx = 1f,
            axisLinesThicknessPx = 1f,
            labelFontSize = 12.sp,
            minYLabelSpacing = 25.dp,
            verticalPadding = 8.dp,
            horizontalPadding = 8.dp,
            xAxisLabelSpacing = 8.dp
        )

        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ImageProvider(R.drawable.widget_background))
                        .padding(16.dp)
                ) {
                    Column(modifier = GlanceModifier.fillMaxSize()) {

                        // ── Header ──────────────────────────────────────────
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Coin icon
                            Image(
                                provider = ImageProvider(R.drawable.btc),
                                contentDescription = "Bitcoin",
                                modifier = GlanceModifier.size(28.dp)
                            )

                            Spacer(GlanceModifier.width(8.dp))

                            // Coin name + symbol
                            Column(modifier = GlanceModifier.defaultWeight()) {
                                Text(
                                    text = "Bitcoin",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "BTC",
                                    style = TextStyle(
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            // Price + change
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$62,430",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "▲ 2.4%",
                                    style = TextStyle(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(GlanceModifier.width(8.dp))

                            // Refresh button
                            Image(
                                provider = ImageProvider(R.drawable.twotone_refresh_24),
                                contentDescription = "Refresh",
                                modifier = GlanceModifier
                                    .size(24.dp)
                                    .clickable(actionRunCallback<RefreshWidgetCallback>())
                            )
                        }

                        Spacer(GlanceModifier.height(10.dp))

                        // ── Chart ────────────────────────────────────────────
                        GlanceLineChart(
                            dataPoints = dataPoints,
                            style = style,
                            visibleDataPointsIndices = 0..6,
                            unit = "$",
                            selectedDataPoint = dataPoints.last(),
                            showHelperLines = true,
                            widthPx = 700,
                            heightPx = 260,
                            density = 3f,
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .defaultWeight()
                        )

                        Spacer(GlanceModifier.height(6.dp))

                        // ── Footer ───────────────────────────────────────────
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Updated just now",
                                style = TextStyle(
                                    fontSize = 8.sp
                                ),
                                modifier = GlanceModifier.defaultWeight()
                            )
                            Text(
                                text = "7D",
                                style = TextStyle(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getErrorIntent(context: Context, throwable: Throwable): PendingIntent {
        val intent = Intent(context, DeviceTempWidget::class.java)
        intent.action = "widgetError"
        Log.d("Error widget", "Error Message: `${throwable.message}`")
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable
    ) {
        val rv = RemoteViews(context.packageName, R.layout.error_layout)
        rv.setTextViewText(
            R.id.error_text_view,
            "Error Message: `${throwable.message}`"
        )
        Log.d("Error widget", "Error Message: `${throwable.message}`")
        rv.setOnClickPendingIntent(R.id.error_icon, getErrorIntent(context, throwable))
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, rv)
    }
}