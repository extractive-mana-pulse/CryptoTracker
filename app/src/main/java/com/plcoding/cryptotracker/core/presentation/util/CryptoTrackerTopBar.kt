@file:OptIn(ExperimentalMaterial3Api::class)
package com.plcoding.cryptotracker.core.presentation.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun CryptoTrackerTopBar(
    topBarTitle: String,
    navigationIcon: Painter? = null,
    action: @Composable () -> Unit = {},
    onNavigationIconClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = topBarTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        },
        navigationIcon = {
            navigationIcon?.let { icon ->
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        painter = icon,
                        contentDescription = null
                    )
                }
            }
        },
        actions = { action() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        scrollBehavior = scrollBehavior
    )
}