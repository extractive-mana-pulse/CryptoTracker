package com.plcoding.cryptotracker.settings.releases.presentation

import com.plcoding.cryptotracker.core.domain.util.NetworkError

sealed interface ReleaseEvent {

    data class Error(val error: NetworkError): ReleaseEvent

    data class OnDownload(val assets: String) : ReleaseEvent

}