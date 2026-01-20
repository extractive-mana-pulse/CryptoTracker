package com.plcoding.cryptotracker.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.settings.presentation.components.SettingsItem

@Composable
fun SettingsRoot(
    onNavigateToReleases: () -> Unit
) {
    SettingsScreen(
        onNavigateToReleases = onNavigateToReleases
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    onNavigateToReleases: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingsItem(
            modifier = Modifier,
            itemLeadingIcon = painterResource(R.drawable.update_ic),
            onItemClick = onNavigateToReleases,
            itemHeadlineText = "Releases",
            itemSupportingText = "Check for new releases"
        )
        HorizontalDivider()
    }
}