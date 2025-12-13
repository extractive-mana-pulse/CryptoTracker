package com.plcoding.cryptotracker.cryto.mappers

import com.plcoding.cryptotracker.cryto.data.network.dto.CoinDTO
import com.plcoding.cryptotracker.cryto.domain.model.Coin

fun CoinDTO.toCoin(): Coin {
    return Coin(
        id = id,
        rank = rank,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr,
    )
}