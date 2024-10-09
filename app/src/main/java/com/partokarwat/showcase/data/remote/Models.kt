package com.partokarwat.showcase.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: String?,
    val changePercent24Hr: String?,
)

@Serializable
data class AssetResponse(
    val data: List<Asset>,
    val timestamp: Long,
)

@Serializable
data class HistoryValue(
    val priceUsd: String,
    val time: Long,
    val date: String,
)

@Serializable
data class CoinHistoryResponse(
    val data: List<HistoryValue>,
    val timestamp: Long,
)

@Serializable
data class MarketValue(
    val exchangeId: String,
    var volumeUsd24Hr: String,
    val priceUsd: String,
    val volumePercent: String,
)

@Serializable
data class CoinMarketsResponse(
    val data: List<MarketValue>,
    val timestamp: Long,
)

@Serializable
data class Rate(
    val rateUsd: String,
)

@Serializable
data class RateResponse(
    val data: Rate,
)
