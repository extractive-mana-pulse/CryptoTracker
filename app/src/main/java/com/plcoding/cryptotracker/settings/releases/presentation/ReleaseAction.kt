package com.plcoding.cryptotracker.settings.releases.presentation

sealed interface ReleaseAction {
    data class OnDownloadClick(val downloadUrl: String) : ReleaseAction
}