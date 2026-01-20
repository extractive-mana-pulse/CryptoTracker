package com.plcoding.cryptotracker.settings.releases.domain

import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result

interface ReleaseService {
    suspend fun getLatestRelease(): Result<MainGit, NetworkError>
}