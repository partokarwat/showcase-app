package com.partokarwat.showcase.ui.coindetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.ui.compose.CoinListItem
import com.partokarwat.showcase.ui.compose.Dimensions
import com.partokarwat.showcase.ui.compose.MarketValueListItem
import com.partokarwat.showcase.ui.compose.ShowcaseText

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
        Column(
            Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            if (!coinHistory.isNullOrEmpty()) {
                CoinHistroyGraph(coinHistory)
            }
            if (coin != null) {
                CoinListItem(coin, Modifier.padding(bottom = Dimensions.spacingNormal), {})
            }
            if (!coinMarkets.isNullOrEmpty()) {
                ShowcaseText(
                    text = stringResource(R.string.title_markets_list),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(Dimensions.minimumTouchTarget)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                            ).padding(horizontal = Dimensions.spacingNormal)
                            .wrapContentHeight(align = Alignment.CenterVertically),
                )
                for (marketValue in coinMarkets) {
                    MarketValueListItem(marketValue)
                }
            }
        }
    }
}

@Composable
fun CoinHistroyGraph(coinHistory: List<HistoryValue>) {
    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(Dimensions.coinChartHeight)
                .padding(horizontal = Dimensions.spacingNormal)
                .padding(top = Dimensions.spacingNormal),
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val horizontalStep = canvasWidth / coinHistory.size
        val maxValue = coinHistory.maxBy { it.priceUsd }.priceUsd.toFloat()
        val path = Path()
        path.moveTo(0f, coinHistory.first().priceUsd.toFloat() * canvasHeight / maxValue)
        coinHistory.forEachIndexed { index, historyValue ->
            path.lineTo(horizontalStep * index, historyValue.priceUsd.toFloat() * canvasHeight / maxValue)
        }
        drawPath(
            path,
            Color.Blue,
            style = Stroke(width = 10f),
        )
    }
}
