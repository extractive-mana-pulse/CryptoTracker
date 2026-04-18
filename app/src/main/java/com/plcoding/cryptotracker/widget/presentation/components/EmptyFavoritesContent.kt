package com.plcoding.cryptotracker.widget.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.widget.presentation.RefreshWidgetCallback
import com.plcoding.cryptotracker.widget.presentation.mutedTextColor
import com.plcoding.cryptotracker.widget.presentation.primaryTextColor
import com.plcoding.cryptotracker.widget.presentation.secondaryTextColor

@Composable
fun EmptyFavoritesContent(updatedAtLabel: String) {
    Column(modifier = GlanceModifier.fillMaxSize().padding(4.dp)) {

        // Header row
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "No favourites",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor()
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Image(
                provider = ImageProvider(R.drawable.twotone_refresh_24),
                contentDescription = "Refresh",
                modifier = GlanceModifier
                    .size(24.dp)
                    .clickable(actionRunCallback<RefreshWidgetCallback>())
            )
        }

        Spacer(GlanceModifier.height(8.dp))

        Text(
            text = "Open the app and mark a coin as favourite.",
            style = TextStyle(fontSize = 12.sp, color = secondaryTextColor())
        )

        Spacer(GlanceModifier.defaultWeight())

        Text(
            text = "Updated $updatedAtLabel",
            style = TextStyle(fontSize = 9.sp, color = mutedTextColor())
        )
    }
}