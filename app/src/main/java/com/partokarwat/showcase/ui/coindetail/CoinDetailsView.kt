package com.partokarwat.showcase.ui.coindetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.partokarwat.showcase.ui.compose.ShowcaseText
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailsScreen(
    onBackClick: () -> Unit,
    coinDetailsViewModel: CoinDetailViewModel = hiltViewModel(),
) {
    val coin = coinDetailsViewModel.coin.collectAsState(null).value
    val coinHistory = coinDetailsViewModel.coinHistory.collectAsState(null).value
    val coinMarkets = coinDetailsViewModel.coinMarkets.collectAsState(null).value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ShowcaseText(
                        coin?.name.orEmpty(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
            )
        },
    ) { contentPadding ->
        Box(Modifier.padding(contentPadding)) {
            Column {
                ShowcaseText(coinHistory?.first()?.date.orEmpty())
                ShowcaseText(coinMarkets?.first()?.exchangeId.orEmpty())
            }
        }
    }
}
