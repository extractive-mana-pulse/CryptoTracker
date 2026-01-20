package com.plcoding.cryptotracker.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.core.presentation.util.CryptoTrackerTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoTrackerDefaultScreen(
    showTopBar: Boolean,
    topBarTitle: String? = null,
    onNavigateUp: (() -> Unit)? = null,
    onNavigateToSettings: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (showTopBar && topBarTitle != null) {
                CryptoTrackerTopBar(
                    topBarTitle = topBarTitle,
                    action = {
                        if (topBarTitle == "Crypto Tracker") {
                            IconButton(
                                onClick = onNavigateToSettings
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_settings_24),
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    navigationIcon = if (onNavigateUp != null)
                        painterResource(R.drawable.outline_arrow_back_24)
                    else null,
                    onNavigationIconClick = { onNavigateUp?.invoke() },
                    scrollBehavior = scrollBehavior
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .padding(padding)
        ) {
            content()
        }
    }
}
