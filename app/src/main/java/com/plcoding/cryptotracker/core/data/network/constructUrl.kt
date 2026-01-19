package com.plcoding.cryptotracker.core.data.network

import com.plcoding.cryptotracker.BuildConfig

fun constructUrl(url: String, baseUrl: String = BuildConfig.BASE_URL): String {
    return when {
        url.contains(baseUrl) -> url
        else -> {
            val normalizedBase = baseUrl.removeSuffix("/")
            val normalizedUrl = url.removePrefix("/")
            "$normalizedBase/$normalizedUrl"
        }
    }
}