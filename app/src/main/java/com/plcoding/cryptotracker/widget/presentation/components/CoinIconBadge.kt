package com.plcoding.cryptotracker.widget.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.size
import com.plcoding.cryptotracker.core.presentation.util.getDrawableIdForCoin

@Composable
internal fun CoinIconBadge(coinSymbol: String, iconSize: Dp) {
    Image(
        provider = ImageProvider(getDrawableIdForCoin(coinSymbol)),
        contentDescription = coinSymbol,
        modifier = GlanceModifier.size(iconSize)
    )
}
