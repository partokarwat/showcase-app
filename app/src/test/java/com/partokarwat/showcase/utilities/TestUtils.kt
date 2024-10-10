package com.partokarwat.showcase.utilities

import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.remote.MarketValue

/**
 * [Coin] objects used for tests.
 */
val testCoins =
    arrayListOf(
        Coin("bitcoin", "Bitcoin", "BTC", 62157.5903, -2.23),
        Coin("ethereum", "Ethereum", "ETH", 2510.16464, 3.57),
        Coin("binance-coin", "BNB", "BNB", 552.61, -3.30),
    )
val testCoin = testCoins[0]

/**
 * HistoryValues for testCoin.
 */
val testCoinHistoryValues =
    arrayListOf(
        HistoryValue("26781.2977671380416781", 1697068800000, "2023-10-12T00:00:00.000Z"),
        HistoryValue("26829.7786353395618383", 1697155200000, "2023-10-13T00:00:00.000Z"),
        HistoryValue("26905.3950924400433811", 1697241600000, "2023-10-14T00:00:00.000Z"),
    )

/**
 * MarketValues for testCoin.
 */
val testCoinMarketValues =
    arrayListOf(
        MarketValue(
            "Crypto.com Exchange",
            "1694772140.4867703284109239",
            "62267.6968255180129234",
            "9.9963669941719350",
            "BTC",
            "USDT",
        ),
        MarketValue(
            "Binance",
            "1329954436.7461967692016879",
            "62262.8289498239104600",
            "7.8445428253403535",
            "BTC",
            "USDT",
        ),
    )
