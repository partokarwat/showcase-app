package com.partokarwat.showcase.utilities

import com.partokarwat.showcase.data.db.Coin

/**
 * [Coin] objects used for tests.
 */
val testCoins =
    arrayListOf(
        Coin("bitcoin", "Bitcoin", "BTC", 62157.5903, -2.23),
        Coin("ethereum", "Ethereum", "ETH", 2510.16464, 3.57),
        Coin("binance-coin", "BNB", "BNB", 552.61, -3.30),
    )
val testCoin = Coin("solana", "Solana", "SOL", 147.98545, -4.46)
val updatedTestCoin = Coin("binance-coin", "BNB", "BNB", 621.61, 5.30)
