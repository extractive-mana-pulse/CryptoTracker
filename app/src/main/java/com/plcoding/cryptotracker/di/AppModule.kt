package com.plcoding.cryptotracker.di

import androidx.room.Room
import com.plcoding.cryptotracker.core.data.network.HttpClientFactory
import com.plcoding.cryptotracker.cryto.data.network.RemoteCoinDataSource
import com.plcoding.cryptotracker.cryto.domain.CoinDataSource
import com.plcoding.cryptotracker.cryto.presentation.coin_list.CoinListViewModel
import com.plcoding.cryptotracker.settings.releases.data.remote.ReleaseServiceImpl
import com.plcoding.cryptotracker.settings.releases.domain.ReleaseService
import com.plcoding.cryptotracker.settings.releases.presentation.ReleaseViewModel
import com.plcoding.cryptotracker.widget.data.datasource.WidgetCoinLocalDataSource
import com.plcoding.cryptotracker.widget.data.datasource.WidgetPreferencesDataSource
import com.plcoding.cryptotracker.widget.data.db.WidgetDatabase
import com.plcoding.cryptotracker.widget.data.repository.WidgetCoinRepository
import com.plcoding.cryptotracker.widget.domain.repository.IWidgetCoinRepository
import com.plcoding.cryptotracker.widget.presentation.CoinChartWidgetViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    // --- Network ---
    single { CIO.create() }
    single(named("coincap")) { HttpClientFactory.create(get()) }
    single(named("github")) { HttpClientFactory.createGitHub(get()) }

    single<CoinDataSource> { RemoteCoinDataSource(get(named("coincap"))) }
    single<ReleaseService> { ReleaseServiceImpl(get(named("github"))) }

    // --- Room ---
    single {
        Room.databaseBuilder(
            androidContext(),
            WidgetDatabase::class.java,
            "widget_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<WidgetDatabase>().widgetCoinDao() }

    // --- Widget data sources ---
    single { WidgetCoinLocalDataSource(get()) }
    single { WidgetPreferencesDataSource(androidContext()) }

    // --- Widget repository ---
    single<IWidgetCoinRepository> { WidgetCoinRepository(get(), get()) }

    // --- ViewModels ---
    viewModelOf(::CoinListViewModel)
    viewModelOf(::ReleaseViewModel)
    viewModelOf(::CoinChartWidgetViewModel)
}