import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.plcoding.cryptotracker.core.presentation.AdaptiveCoinListDetailPane
import com.plcoding.cryptotracker.navigation.CryptoTrackerDefaultScreen
import com.plcoding.cryptotracker.navigation.Screen
import com.plcoding.cryptotracker.settings.presentation.SettingsRoot
import com.plcoding.cryptotracker.settings.releases.presentation.ReleaseRoot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val (showTopBar, title, showBack) = when {
        destination?.hasRoute<Screen.CoinList>() == true ->
            Triple(true, "Crypto Tracker", false)

        destination?.hasRoute<Screen.Settings>() == true ->
            Triple(true, "Settings", true)

        destination?.hasRoute<Screen.Releases>() == true ->
            Triple(true, "Releases", true)

        else -> Triple(false, null, false)
    }

    CryptoTrackerDefaultScreen(
        showTopBar = showTopBar,
        topBarTitle = title,
        onNavigateToSettings = {
            navController.navigate(Screen.Settings)
        },
        onNavigateUp = if (showBack) {
            { navController.navigateUp() }
        } else null,
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.CoinList
        ) {
            composable<Screen.CoinList> {
                AdaptiveCoinListDetailPane()
            }

            composable<Screen.Settings> {
                SettingsRoot(
                    onNavigateToReleases = {
                        navController.navigate(Screen.Releases)
                    }
                )
            }

            composable<Screen.Releases> {
                ReleaseRoot()
            }
        }
    }
}
