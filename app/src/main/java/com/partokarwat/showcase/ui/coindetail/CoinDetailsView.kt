package com.partokarwat.showcase.ui.coindetail

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.remote.MarketValue
import com.partokarwat.showcase.ui.compose.CoinListItem
import com.partokarwat.showcase.ui.compose.Dimensions
import com.partokarwat.showcase.ui.compose.MarketValueListItem
import com.partokarwat.showcase.ui.compose.ShowcaseText
import com.valentinilk.shimmer.shimmer

@Composable
fun CoinDetailsScreen(
    activity: Activity,
    onBackClick: () -> Unit,
    coinDetailsViewModel: CoinDetailViewModel = hiltViewModel(),
) {
    val coin = coinDetailsViewModel.coin.collectAsStateWithLifecycle(null).value
    val coinHistory = coinDetailsViewModel.coinHistory.collectAsStateWithLifecycle(null).value
    val coinMarkets = coinDetailsViewModel.coinMarkets.collectAsStateWithLifecycle(null).value
    val isInitError = coinDetailsViewModel.isInitError.collectAsStateWithLifecycle(false).value

    Scaffold(
        topBar = {
            CoinDetailsTopBar(coin, onBackClick)
        },
    ) { contentPadding ->
        CoinDetailsContent(contentPadding, coinHistory, coin, coinMarkets)
        if (isInitError) {
            Toast
                .makeText(
                    activity,
                    activity.getString(R.string.init_coin_details_error_text),
                    Toast.LENGTH_LONG,
                ).show()
            coinDetailsViewModel.resetInitError()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CoinDetailsTopBar(
    coin: Coin?,
    onBackClick: () -> Unit,
) {
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
}

@Composable
private fun CoinDetailsContent(
    contentPadding: PaddingValues,
    coinHistory: List<HistoryValue>?,
    coin: Coin?,
    coinMarkets: List<MarketValue>?,
) {
    Column(
        Modifier
            .padding(contentPadding)
            .verticalScroll(rememberScrollState()),
    ) {
        if (!coinHistory.isNullOrEmpty()) {
            CoinHistroyGraph(coinHistory)
        } else {
            Box(
                Modifier
                    .shimmer()
                    .height(
                        Dimensions.coinChartHeight,
                    ).fillMaxWidth()
                    .padding(horizontal = Dimensions.spacingNormal)
                    .padding(top = Dimensions.spacingNormal)
                    .background(Color.LightGray),
            )
        }
        if (coin != null) {
            CoinListItem(coin, Modifier.padding(bottom = Dimensions.spacingNormal), {})
        }
        MarketValuesSection(coinMarkets)
    }
}

@Composable
private fun MarketValuesSection(coinMarkets: List<MarketValue>?) {
    AnimatedVisibility(
        visible = !coinMarkets.isNullOrEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        if (!coinMarkets.isNullOrEmpty()) {
            Column {
                MarketValueListHeader()
                for (marketValue in coinMarkets) {
                    MarketValueListItem(marketValue)
                }
            }
        }
    }
}

@Composable
private fun MarketValueListHeader() {
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
}

@Composable
private fun CoinHistroyGraph(coinHistory: List<HistoryValue>) {
    Box(modifier = Modifier.padding(horizontal = Dimensions.spacingNormal)) {
        HistoryGraphTimeRangeLabel()
        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Dimensions.coinChartHeight)
                    .padding(top = Dimensions.spacingNormal),
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val horizontalStep = canvasWidth / coinHistory.size
            val maxValue = coinHistory.maxBy { it.priceUsd }.priceUsd.toBigDecimal()
            val minValue = coinHistory.minBy { it.priceUsd }.priceUsd.toBigDecimal()
            val maximumOffset = maxValue - minValue
            val path = Path()
            val yFirstPointOffset = (maxValue - coinHistory.first().priceUsd.toBigDecimal()) / maximumOffset
            val yFirstPoint = yFirstPointOffset * canvasHeight.toBigDecimal()
            path.moveTo(0f, yFirstPoint.toFloat())
            coinHistory.forEachIndexed { index, historyValue ->
                val yOffset = (maxValue - historyValue.priceUsd.toBigDecimal()) / maximumOffset
                val y = yOffset * canvasHeight.toBigDecimal()
                path.lineTo(horizontalStep * index, y.toFloat())
            }
            drawPath(
                path,
                Color.Blue,
                style = Stroke(width = 10f),
            )
        }
    }
}

@Composable
private fun HistoryGraphTimeRangeLabel() {
    SuggestionChip(
        onClick = { }, // do nothing
        enabled = false,
        label = {
            ShowcaseText(
                stringResource(R.string.coin_history_graph_label),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
            )
        },
    )
}
