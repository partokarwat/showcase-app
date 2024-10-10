package com.partokarwat.showcase.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.partokarwat.showcase.ui.coindetail.CoinDetailsScreen
import com.partokarwat.showcase.ui.coinslist.CoinListScreen

@Composable
fun ShowcaseApp() {
    val navController = rememberNavController()
    ShowcaseNavHost(
        navController = navController,
    )
}

@Composable
fun ShowcaseNavHost(navController: NavHostController) {
    val activity = (LocalContext.current as Activity)
    NavHost(navController = navController, startDestination = Screen.Coins.route) {
        composable(route = Screen.Coins.route) {
            CoinListScreen(
                activity,
                onCoinClick = {
                    navController.navigate(
                        Screen.CoinDetail.createRoute(
                            coinId = it.id,
                        ),
                    )
                },
            )
        }
        composable(
            route = Screen.CoinDetail.route,
            arguments = Screen.CoinDetail.navArguments,
        ) {
            CoinDetailsScreen(
                activity,
                onBackClick = { navController.navigateUp() },
            )
        }
    }
}
