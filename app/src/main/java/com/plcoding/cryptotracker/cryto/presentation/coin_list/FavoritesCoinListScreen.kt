package com.plcoding.cryptotracker.cryto.presentation.coin_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FavoritesCoinListScreen(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        WidgetSelectionSection(
            state = state,
            onAction = onAction,
            modifier = Modifier
                .fillMaxWidth()
                // Keep padding consistent with the list container in CoinListScreen
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CoinListScreen(
                state = state,
                onAction = onAction,
                showWidgetAction = true,
                widgetCoinId = state.widgetCoinId,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun WidgetSelectionSection(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val widgetCoin = state.coins.firstOrNull { it.id == state.widgetCoinId }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            // Match CoinListScreen container
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Widget coin",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = widgetCoin?.let { "${it.name} (${it.symbol}) will be prioritized on widget." }
                    ?: "Choose one favorite coin to prioritize on your widget chart.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            widgetCoin?.let { coin ->
                Text(
                    text = "Refresh widget coin",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { onAction(CoinListAction.OnSetWidgetCoin(coin)) }
                )
            }
        }
    }
}

