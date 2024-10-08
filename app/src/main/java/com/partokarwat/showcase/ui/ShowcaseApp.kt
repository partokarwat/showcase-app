package com.partokarwat.showcase.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.partokarwat.showcase.ui.coinslist.CoinsScreen

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
            CoinsScreen(activity)
        }
    }
}
