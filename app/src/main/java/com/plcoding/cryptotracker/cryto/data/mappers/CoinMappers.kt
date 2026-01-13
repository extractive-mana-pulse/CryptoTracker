package com.plcoding.cryptotracker.cryto.data.mappers

import com.plcoding.cryptotracker.core.data.network.CoinPriceDto
import com.plcoding.cryptotracker.cryto.data.network.dto.CoinDTO
import com.plcoding.cryptotracker.cryto.domain.model.Coin
import com.plcoding.cryptotracker.cryto.domain.model.CoinPrice
import java.time.Instant
import java.time.ZoneId

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

fun CoinPriceDto.toCoinPrice(): CoinPrice {
    return CoinPrice(
        priceUsd = priceUsd,
        dateTime = Instant
            .ofEpochMilli(time)
            .atZone(ZoneId.systemDefault())
    )
}