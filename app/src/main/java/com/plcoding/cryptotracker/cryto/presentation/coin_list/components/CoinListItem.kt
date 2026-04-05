package com.plcoding.cryptotracker.cryto.presentation.coin_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.cryto.domain.model.Coin
import com.plcoding.cryptotracker.cryto.presentation.model.CoinUiState
import com.plcoding.cryptotracker.cryto.presentation.model.toCoinUiState
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun CoinListItem(
    coinUiState: CoinUiState,
    onClick: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    showWidgetAction: Boolean = false,
    isWidgetCoin: Boolean = false,
    onSetWidgetCoin: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = coinUiState.iconRes),
            contentDescription = coinUiState.name,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(85.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = coinUiState.symbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = coinUiState.symbol,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = contentColor
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showWidgetAction) {
                    IconButton(onClick = onSetWidgetCoin) {
                        Icon(
                            painter = painterResource(R.drawable.ic_widget_24),
                            contentDescription = if (isWidgetCoin) "Selected as widget coin" else "Set as widget coin",
                            tint = if (isWidgetCoin) MaterialTheme.colorScheme.primary else contentColor
                        )
                    }
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color(0xFFFFD54F) else contentColor
                    )
                }
            }
            Text(
                text = "$ ${coinUiState.priceUsd.formatted}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            PriceChange(
                change = coinUiState.changePercent24Hr,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CoinListItemPreview() {
    CryptoTrackerTheme {
        CoinListItem(
            coinUiState = previewCoin,
            onClick = {},
            isFavorite = true,
            onToggleFavorite = {},
            showWidgetAction = true,
            isWidgetCoin = true,
            onSetWidgetCoin = {},
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background
            )
        )
    }
}

internal val previewCoin = Coin(
    id = "bitcoin",
    rank = 1,
    name = "Bitcoin",
    symbol = "BTC",
    marketCapUsd = 124127124325215.76,
    priceUsd = 62282.13,
    changePercent24Hr = -0.1
).toCoinUiState()