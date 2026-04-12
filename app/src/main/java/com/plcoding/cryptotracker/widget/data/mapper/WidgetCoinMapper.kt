package com.plcoding.cryptotracker.widget.data.mapper

import com.plcoding.cryptotracker.cryto.domain.model.CoinPrice
import com.plcoding.cryptotracker.widget.data.db.WidgetCoin
import com.plcoding.cryptotracker.widget.data.model.DataPointDto
import com.plcoding.cryptotracker.widget.domain.model.WidgetCoinItem
import com.plcoding.cryptotracker.widget.domain.model.WidgetChartPoint
import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter

/**
 * Mapping helpers between widget data-layer entities/DTOs and domain models.
 */
object WidgetCoinMapper {

    private val formatter = DateTimeFormatter.ofPattern("MM/dd\nHH:mm")

    /** Converts API [history] into serializable DTO points for persistence. */
    fun toDataPointDtoList(history: List<CoinPrice>): List<DataPointDto> {
        return history.mapIndexed { i, cp ->
            DataPointDto(
                x = i.toFloat(),
                y = cp.priceUsd.toFloat(),
                xLabel = cp.dateTime.format(formatter)
            )
        }
    }

    /**
     * Decodes persisted chart-point JSON into domain points.
     * Returns an empty list if decoding fails.
     */
    fun decodeWidgetDataPoints(json: String): List<WidgetChartPoint> {
        return runCatching {
            Json.decodeFromString<List<DataPointDto>>(json).map {
                WidgetChartPoint(x = it.x, y = it.y, xLabel = it.xLabel)
            }
        }.getOrElse { emptyList() }
    }

    /** Maps Room [entity] to domain [WidgetCoinItem]. */
    fun toWidgetCoinItem(entity: WidgetCoin): WidgetCoinItem {
        return WidgetCoinItem(
            coinId = entity.coinId,
            coinName = entity.coinName,
            coinSymbol = entity.coinSymbol,
            priceUsd = entity.priceUsd,
            changePercent24Hr = entity.changePercent24Hr,
            dataPointsJson = entity.dataPointsJson
        )
    }

    /** Creates a Room entity from raw coin properties and [history]. */
    fun toEntity(
        coinId: String,
        coinName: String,
        coinSymbol: String,
        priceUsd: Double,
        changePercent24Hr: Double,
        history: List<CoinPrice>
    ): WidgetCoin {
        val dtoList = toDataPointDtoList(history)
        return WidgetCoin(
            id = coinId,
            coinId = coinId,
            coinName = coinName,
            coinSymbol = coinSymbol,
            priceUsd = priceUsd,
            changePercent24Hr = changePercent24Hr,
            dataPointsJson = Json.encodeToString(dtoList)
        )
    }
}