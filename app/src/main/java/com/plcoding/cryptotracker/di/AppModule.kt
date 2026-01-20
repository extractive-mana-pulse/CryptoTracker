package com.plcoding.cryptotracker.di

import com.plcoding.cryptotracker.core.data.network.HttpClientFactory
import com.plcoding.cryptotracker.cryto.data.network.RemoteCoinDataSource
import com.plcoding.cryptotracker.cryto.domain.CoinDataSource
import com.plcoding.cryptotracker.cryto.presentation.coin_list.CoinListViewModel
import com.plcoding.cryptotracker.settings.releases.data.remote.ReleaseServiceImpl
import com.plcoding.cryptotracker.settings.releases.domain.ReleaseService
import com.plcoding.cryptotracker.settings.releases.presentation.ReleaseViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    // Create CIO engine once
    single { CIO.create() }

    // CoinCap HttpClient (with auth)
    single(named("coincap")) {
        HttpClientFactory.create(get())
    }

    // GitHub HttpClient (without auth)
    single(named("github")) {
        HttpClientFactory.createGitHub(get())
    }

    // Data sources
    single<CoinDataSource> {
        RemoteCoinDataSource(get(named("coincap")))
    }

    single<ReleaseService> {
        ReleaseServiceImpl(get(named("github")))
    }

    // ViewModels
    viewModelOf(::CoinListViewModel)
    viewModelOf(::ReleaseViewModel)
}