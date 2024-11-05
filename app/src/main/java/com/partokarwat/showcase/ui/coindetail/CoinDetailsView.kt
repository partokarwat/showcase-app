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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.remote.HistoryValue
import com.partokarwat.showcase.data.remote.MarketValue
import com.partokarwat.showcase.data.util.Result
import com.partokarwat.showcase.ui.coindetail.CoinDetailsViewModelContract.Event
import com.partokarwat.showcase.ui.coindetail.CoinDetailsViewModelContract.Intent
import com.partokarwat.showcase.ui.coindetail.CoinDetailsViewModelContract.State
import com.partokarwat.showcase.ui.common.use
import com.partokarwat.showcase.ui.compose.CoinListItem
import com.partokarwat.showcase.ui.compose.Dimensions
import com.partokarwat.showcase.ui.compose.MarketValueListItem
import com.partokarwat.showcase.ui.compose.ShowcaseText
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal

@Composable
fun CoinDetailsScreen(
    activity: Activity,
    onBackClick: () -> Unit,
    coinDetailsViewModel: CoinDetailViewModel = hiltViewModel(),
) {
    val (state, intents, events) = use(viewModel = coinDetailsViewModel)

    Initializer(activity, intents, events)
    ScreenContent(state, onBackClick)
}

@Composable
private fun Initializer(
    activity: Activity,
    intents: (Intent) -> Unit,
    events: SharedFlow<Event>,
) {
    LaunchedEffect(activity, events) {
        collectEvents(activity, events)
    }
    LaunchedEffect(intents) {
        intents(Intent.ScreenCreated)
    }
}

private suspend fun collectEvents(
    activity: Activity,
    events: SharedFlow<Event>,
) {
    events.collectLatest {
        when (it) {
            is Event.ShowError ->
                Toast
                    .makeText(
                        activity,
                        activity.getString(it.messageResId),
                        Toast.LENGTH_SHORT,
                    ).show()
        }
    }
}

@Composable
fun ScreenContent(
    state: State,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            CoinDetailsTopBar(state.coin, onBackClick)
        },
    ) { contentPadding ->
        CoinDetailsContent(contentPadding, state.history.getOrNull(), state.coin, state.markets.getOrNull())
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
            CoinHistoryGraph(coinHistory)
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
private fun CoinHistoryGraph(coinHistory: List<HistoryValue>) {
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
            val maxValue = coinHistory.maxByOrNull { it.priceUsd.toBigDecimal() }?.priceUsd?.toBigDecimal() ?: BigDecimal.ZERO
            val minValue = coinHistory.minByOrNull { it.priceUsd.toBigDecimal() }?.priceUsd?.toBigDecimal() ?: BigDecimal.ZERO
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
        onClick = { },
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

@Preview
@Composable
private fun CoinDetailsScreenPreview() {
    ScreenContent(
        State(
            Coin("bitcoin", "Bitcoin", "BTC", 62157.5903, -2.23),
            Result.Success(
                arrayListOf(
                    HistoryValue("26781.2977671380416781", 1697068800000, "2023-10-12T00:00:00.000Z"),
                    HistoryValue("26829.7786353395618383", 1697155200000, "2023-10-13T00:00:00.000Z"),
                    HistoryValue("26905.3950924400433811", 1697241600000, "2023-10-14T00:00:00.000Z"),
                ),
            ),
            Result.Success(
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
                ),
            ),
        ),
        {},
    )
}
