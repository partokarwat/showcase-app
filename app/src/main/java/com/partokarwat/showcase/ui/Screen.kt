package com.partokarwat.showcase.ui

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.partokarwat.showcase.ui.coindetail.COIN_ID_SAVED_STATE_KEY

sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList(),
) {
    data object Coins : Screen("coins")

    data object CoinDetail : Screen(
        route = "coinDetail/{coinId}",
        navArguments =
            listOf(
                navArgument(COIN_ID_SAVED_STATE_KEY) {
                    type = NavType.StringType
                },
            ),
    ) {
        fun createRoute(coinId: String) = "coinDetail/$coinId"
    }
}
