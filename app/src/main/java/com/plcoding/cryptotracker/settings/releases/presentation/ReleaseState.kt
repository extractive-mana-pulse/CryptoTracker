package com.plcoding.cryptotracker.settings.releases.presentation

import androidx.compose.runtime.Immutable
import com.plcoding.cryptotracker.settings.releases.domain.MainGit

@Immutable
data class ReleaseState(
    val isLoading: Boolean = false,
    val releases: List<MainGit> = emptyList(),
    val selectedCoin: MainGit? = null
)