package com.plcoding.cryptotracker.settings.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.plcoding.cryptotracker.R

@PreviewLightDark
@Composable
internal fun SettingsItem(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    itemLeadingIcon: Painter = painterResource(R.drawable.outline_arrow_back_24),
    itemHeadlineText: String = "Settings",
    itemSupportingText: String = "hello world!"
) {
    ListItem(
        modifier = modifier
            .clickable(onClick = onItemClick)
            ,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        leadingContent = {
            Icon(
                painter = itemLeadingIcon,
                contentDescription = null
            )
        },
        headlineContent = {
            Text(
                text = itemHeadlineText
            )
        },
        supportingContent = {
            Text(
                text = itemSupportingText
            )
        }
    )
}