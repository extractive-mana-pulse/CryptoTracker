package com.plcoding.cryptotracker.widget.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.cryptotracker.cryto.domain.model.Coin
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.ChartStyle
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.LineChart
import com.plcoding.cryptotracker.widget.data.db.WidgetCoin
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@Composable
fun CoinChartWidget(
    modifier: Modifier = Modifier,
    viewModel: CoinChartWidgetViewModel = koinViewModel()
) {
    val selectedCoin by viewModel.selectedCoin.collectAsStateWithLifecycle()
    val coins by viewModel.coins.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = "Home Screen Widget",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Select a coin to display on your widget",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        CoinDropdown(
            coins = coins,
            selectedId = selectedCoin?.coinId,
            onSelect = viewModel::selectCoin,
            isLoading = isLoading
        )

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            selectedCoin != null -> {
                CoinChartPreview(
                    coin = selectedCoin!!,
                    viewModel = viewModel
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No coin selected yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CoinDropdown(
    coins: List<Coin>,
    selectedId: String?,
    onSelect: (Coin) -> Unit,
    isLoading: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = coins.firstOrNull { it.id == selectedId }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .clickable(enabled = !isLoading && coins.isNotEmpty()) {
                    expanded = !expanded
                }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selected?.let { "${it.name}  (${it.symbol})" }
                    ?: if (coins.isEmpty()) "Loading coins…" else "Select cryptocurrency",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            coins.forEach { coin ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = coin.name,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = coin.symbol,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$${String.format("%,.2f", coin.priceUsd)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                val change = coin.changePercent24Hr
                                Text(
                                    text = "${if (change >= 0) "▲" else "▼"} ${
                                        String.format("%.2f", abs(change))
                                    }%",
                                    fontSize = 11.sp,
                                    color = if (change >= 0) Color(0xFF00C853) else Color(0xFFFF1744)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelect(coin)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CoinChartPreview(
    coin: WidgetCoin,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = coin.coinName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = coin.coinSymbol,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%,.2f", coin.priceUsd)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                val change = coin.changePercent24Hr
                Text(
                    text = "${if (change >= 0) "▲" else "▼"} ${
                        String.format("%.2f", abs(change))
                    }%",
                    fontSize = 12.sp,
                    color = if (change >= 0) Color(0xFF00C853) else Color(0xFFFF1744)
                )
            }
        }

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
            text = "7-day history  ·  widget updates on selection",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

