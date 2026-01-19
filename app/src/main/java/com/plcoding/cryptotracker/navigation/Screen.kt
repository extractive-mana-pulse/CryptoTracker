package com.plcoding.cryptotracker.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    object CoinList : Screen
    @Serializable
    object Settings : Screen
    @Serializable
    object Releases : Screen
}