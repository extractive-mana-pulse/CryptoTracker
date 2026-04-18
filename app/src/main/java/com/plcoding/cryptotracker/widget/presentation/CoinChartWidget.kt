package com.plcoding.cryptotracker.widget.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.cryptotracker.widget.presentation.components.CoinChartPreview
import com.plcoding.cryptotracker.widget.presentation.components.CoinDropdown
import com.plcoding.cryptotracker.widget.presentation.components.EmptySelectionWidget
import com.plcoding.cryptotracker.widget.presentation.components.LoadingWidget
import org.koin.androidx.compose.koinViewModel

@Composable
fun CoinChartWidget(
    modifier: Modifier = Modifier,
    viewModel: CoinChartWidgetViewModel = koinViewModel()
) {
    val selectedCoin by viewModel.selectedCoin.collectAsStateWithLifecycle()
    val coins by viewModel.coins.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Home Screen Widget",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Select a coin to display on your widget",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!isLoading) {
                    Icon(
                        imageVector = Icons.Rounded.Sync,
                        contentDescription = "Refresh coin data",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(16.dp))

            CoinDropdown(
                coins = coins,
                selectedId = selectedCoin?.coinId,
                onSelect = viewModel::selectCoin,
                isLoading = isLoading
            )

            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> LoadingWidget()

                selectedCoin != null -> CoinChartPreview(
                    coin = selectedCoin!!,
                    viewModel = viewModel
                )

                else -> EmptySelectionWidget()
            }
        }
    }
}
@PreviewLightDark
@Composable
private fun CoinChartWidgetPreview() {
    MaterialTheme {
        CoinChartWidget()
    }
}