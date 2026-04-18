package com.plcoding.cryptotracker.widget.presentation

import android.content.Context
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
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.widget.domain.repository.WidgetCoinRepository
import com.plcoding.cryptotracker.widget.presentation.components.CoinIconBadge
import com.plcoding.cryptotracker.widget.presentation.helper.formatPrice
import com.plcoding.cryptotracker.widget.presentation.helper.mutedTextColor
import com.plcoding.cryptotracker.widget.presentation.helper.primaryTextColor
import org.koin.java.KoinJavaComponent.get

class CompactCoinWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = get<WidgetCoinRepository>(WidgetCoinRepository::class.java)
        val favorites = repository.getFavorites()
        val preferredFavorite = repository.resolvePreferredFavorite(favorites)

        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ImageProvider(R.drawable.widget_background))
                        .padding(24.dp)
                ) {
                    val coin = preferredFavorite ?: repository.pickCompactFavorite(favorites)

                    if (coin != null) {
                        Row(
                            modifier = GlanceModifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            CoinIconBadge(coinSymbol = coin.coinSymbol, iconSize = 36.dp)

                            Spacer(GlanceModifier.width(8.dp))

                            Column {
                                Text(
                                    text = formatPrice(coin.priceUsd),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryTextColor(),
                                        fontFamily = FontFamily.Serif
                                    )
                                )
                                Spacer(GlanceModifier.height(2.dp))
                                Text(
                                    text = coin.coinSymbol,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = mutedTextColor(),
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No coin",
                            style = TextStyle(fontSize = 14.sp, color = mutedTextColor())
                        )
                    }

                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.twotone_refresh_24),
                            contentDescription = null,
                            modifier = GlanceModifier
                                .size(24.dp)
                                .clickable(actionRunCallback<RefreshWidgetCallback>())
                        )
                    }
                }
            }
        }
    }
}