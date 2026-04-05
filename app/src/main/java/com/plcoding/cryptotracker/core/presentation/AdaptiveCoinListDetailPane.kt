@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.plcoding.cryptotracker.core.presentation

import android.widget.Toast
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.cryptotracker.core.presentation.util.ObserveAsEvents
import com.plcoding.cryptotracker.core.presentation.util.toString
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.CoinDetailScreen
import com.plcoding.cryptotracker.cryto.presentation.coin_list.CoinListAction
import com.plcoding.cryptotracker.cryto.presentation.coin_list.CoinListEvent
import com.plcoding.cryptotracker.cryto.presentation.coin_list.CoinListScreen
import com.plcoding.cryptotracker.cryto.presentation.coin_list.CoinListViewModel
import com.plcoding.cryptotracker.cryto.presentation.coin_list.FavoritesCoinListScreen
import org.koin.androidx.compose.koinViewModel

/**
 * Adaptive list-detail container for the coin flow.
 *
 * Displays a coin list pane and a coin detail pane using Material adaptive navigation.
 * In favorites mode, the list pane is filtered to only favorited coins.
 *
 * User actions are forwarded to [viewModel]. When a coin is clicked, navigation
 * moves to the detail pane.
 *
 * @param modifier Optional modifier applied to the scaffold root.
 * @param showFavoritesOnly When true, only favorited coins are shown in the list pane.
 * @param viewModel State holder and action handler for list/detail content.
 */
@Composable
fun AdaptiveCoinListDetailPane(
    modifier: Modifier = Modifier,
    showFavoritesOnly: Boolean = false,
    viewModel: CoinListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val visibleState = if (showFavoritesOnly) {
        state.copy(coins = state.coins.filter { it.id in state.favoriteCoinIds })
    } else {
        state
    }
    val context = LocalContext.current
    ObserveAsEvents(events = viewModel.events) { event ->
        when(event) {
            is CoinListEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.toString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                val handleAction: (CoinListAction) -> Unit = { action ->
                    viewModel.onAction(action)
                    if (action is CoinListAction.OnCoinClick) {
                        navigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail
                        )
                    }
                }

                if (showFavoritesOnly) {
                    FavoritesCoinListScreen(
                        state = visibleState,
                        onAction = handleAction
                    )
                } else {
                    CoinListScreen(
                        state = visibleState,
                        onAction = handleAction
                    )
                }
            }
        },
        detailPane = {
            AnimatedPane {
                CoinDetailScreen(state = visibleState)
            }
        },
        modifier = modifier
    )
}