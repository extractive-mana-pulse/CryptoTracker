package com.plcoding.cryptotracker.widget.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.ChartStyle
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.LineChart
import com.plcoding.cryptotracker.widget.domain.model.WidgetCoinItem
import com.plcoding.cryptotracker.widget.presentation.CoinChartWidgetViewModel
import java.util.Locale
import kotlin.math.abs

/**
 * Preview card shown below the dropdown once a [coin] has been selected.
 * Renders the coin's price, 24h change, and a 7-day line chart.
 *
 * Receives [viewModel] solely to decode the chart data points - no business
 * logic is invoked here.
 */
@Composable
internal fun CoinChartPreview(
    coin: WidgetCoinItem,
    viewModel: CoinChartWidgetViewModel
) {
    val dataPoints = remember(coin.dataPointsJson) {
        viewModel.decodeDataPoints(coin)
    }

    val chartStyle = ChartStyle(
        chartLineColor = Color(0xFF00C853),
        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedColor = MaterialTheme.colorScheme.primary,
        helperLinesThicknessPx = 1f,
        axisLinesThicknessPx = 1f,
        labelFontSize = 11.sp,
        minYLabelSpacing = 25.dp,
        verticalPadding = 8.dp,
        horizontalPadding = 8.dp,
        xAxisLabelSpacing = 8.dp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        CoinChartPreviewHeader(coin)

        Spacer(Modifier.height(12.dp))

        if (dataPoints.isNotEmpty()) {
            LineChart(
                dataPoints = dataPoints,
                style = chartStyle,
                visibleDataPointsIndices = dataPoints.indices,
                unit = "$",
                selectedDataPoint = dataPoints.last(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "7-day history  .  widget updates on selection",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Header row for [CoinChartPreview] showing coin name, symbol, current price and 24h change.
 */
@Composable
private fun CoinChartPreviewHeader(coin: WidgetCoinItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = coin.coinName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                text = coin.coinSymbol,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$${String.format(Locale.US, "%,.2f", coin.priceUsd)}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            val change = coin.changePercent24Hr
            Text(
                text = "${if (change >= 0) "▲" else "▼"} ${String.format(Locale.US, "%.2f", abs(change))}%",
                fontSize = 12.sp,
                color = if (change >= 0) Color(0xFF00C853) else Color(0xFFFF1744)
            )
        }
    }
}
