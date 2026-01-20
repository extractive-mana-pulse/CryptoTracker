package com.plcoding.cryptotracker.settings.releases.data.remote

import com.plcoding.cryptotracker.core.data.network.safeCall
import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import com.plcoding.cryptotracker.core.domain.util.map
import com.plcoding.cryptotracker.settings.releases.data.remote.dto.MainGitDTO
import com.plcoding.cryptotracker.settings.releases.domain.MainGit
import com.plcoding.cryptotracker.settings.releases.domain.ReleaseService
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class ReleaseServiceImpl(
    private val httpClient: HttpClient
): ReleaseService {

    override suspend fun getLatestRelease(): Result<MainGit, NetworkError> {
        return safeCall<MainGitDTO> {
            httpClient.get("https://api.github.com/repos/extractive-mana-pulse/CryptoTracker/releases/latest")
        }.map { dto ->
            dto.toMainGit()
        }
    }
}