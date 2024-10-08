package com.partokarwat.showcase.ui

import androidx.navigation.NamedNavArgument

sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList(),
) {
    data object Coins : Screen("coins")
}
