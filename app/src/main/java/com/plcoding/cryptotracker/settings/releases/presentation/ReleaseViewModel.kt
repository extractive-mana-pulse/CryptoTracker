package com.plcoding.cryptotracker.settings.releases.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.settings.releases.domain.ReleaseService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReleaseViewModel(
    private val releaseService: ReleaseService
): ViewModel() {

    private val _state = MutableStateFlow(ReleaseState())
    val state = _state
        .onStart { loadRelease() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ReleaseState()
        )

    private val _events = Channel<ReleaseEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: ReleaseAction) {
        when(action) {
            is ReleaseAction.OnDownloadClick -> {
                viewModelScope.launch {
                    _events.send(ReleaseEvent.OnDownload(assets = action.downloadUrl))
                }
            }
        }
    }

    private fun loadRelease() {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }

            releaseService
                .getLatestRelease()
                .onSuccess { release ->
                    _state.update { it.copy(
                        isLoading = false,
                        releases = listOf(release)
                    ) }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ReleaseEvent.Error(error))
                }
        }
    }
}