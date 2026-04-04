package com.plcoding.cryptotracker.cryto.presentation.coin_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.plcoding.cryptotracker.cryto.presentation.coin_list.components.CoinListItem
import com.plcoding.cryptotracker.cryto.presentation.coin_list.components.previewCoin
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun CoinListScreen(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier,
    showWidgetAction: Boolean = false,
    widgetCoinId: String? = null
) {
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )

        val coinListContent: @Composable () -> Unit = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.coins) { coinUi ->
                    CoinListItem(
                        coinUiState = coinUi,
                        onClick = { onAction(CoinListAction.OnCoinClick(coinUi)) },
                        isFavorite = coinUi.id in state.favoriteCoinIds,
                        onToggleFavorite = { onAction(CoinListAction.OnToggleFavorite(coinUi)) },
                        showWidgetAction = showWidgetAction,
                        isWidgetCoin = widgetCoinId == coinUi.id,
                        onSetWidgetCoin = { onAction(CoinListAction.OnSetWidgetCoin(coinUi)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 2.dp)
                }
            }
        }
        Box(modifier = modifier.then(contentModifier)) {
            coinListContent()
        }
    }
}

@PreviewLightDark
@Composable
fun CoinListScreenPreview() {
    CryptoTrackerTheme {
        CoinListScreen(
            state = CoinListState(
                coins = (1..100).map {
                    previewCoin.copy(id = it.toString())
                }
            ),
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background
            ),
            onAction = {}
        )
    }
}