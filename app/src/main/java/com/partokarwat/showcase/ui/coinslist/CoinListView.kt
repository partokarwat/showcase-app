package com.partokarwat.showcase.ui.coinslist

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.ui.coinslist.CoinListViewModelContract.Event
import com.partokarwat.showcase.ui.coinslist.CoinListViewModelContract.Intent
import com.partokarwat.showcase.ui.coinslist.CoinListViewModelContract.State
import com.partokarwat.showcase.ui.common.use
import com.partokarwat.showcase.ui.compose.CoinListItem
import com.partokarwat.showcase.ui.compose.CoinListItemSkeleton
import com.partokarwat.showcase.ui.compose.Dimensions
import com.partokarwat.showcase.ui.compose.ShowcaseText
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoinListScreen(
    activity: Activity,
    onCoinClick: (Coin) -> Unit = {},
    coinListViewModel: CoinListViewModel = hiltViewModel(),
) {
    val (state, intents, events) = use(viewModel = coinListViewModel)

    Initializer(activity, intents, events)
    ScreenContent(state, intents, onCoinClick)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    state: State,
    intents: (Intent) -> Unit,
    onCoinClick: (Coin) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ShowcaseText(
                        stringResource(R.string.coins_fragment_label),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
            )
        },
    ) { contentPadding ->
        when (state) {
            State.Loading -> LoadingScreen(contentPadding)
            is State.Loaded -> {
                val items =
                    state.items
                        .observeAsState()
                        .value
                        .orEmpty()
                val lastListUpdateTimestamp = state.lastListUpdateTimestamp.observeAsState().value
                LoadedContent(
                    contentPadding,
                    items,
                    lastListUpdateTimestamp,
                    state.isRefreshing,
                    state.isTopGainers,
                    intents,
                    onCoinClick,
                )
            }
        }
    }
}

@Composable
fun LoadingScreen(contentPadding: PaddingValues) =
    Column(
        modifier =
            Modifier
                .shimmer()
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.height(14.dp).width(200.dp).background(Color.LightGray))
        repeat(20) {
            CoinListItemSkeleton()
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.height(50.dp).width(180.dp).background(Color.LightGray, RoundedCornerShape(33)))
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadedContent(
    contentPadding: PaddingValues,
    items: List<Coin>,
    lastListUpdateTimestamp: Long?,
    isRefreshing: Boolean,
    isTopGainers: Boolean,
    intents: (Intent) -> Unit,
    onCoinClick: (Coin) -> Unit = {},
) {
    val pullRefreshState = rememberPullToRefreshState()

    return PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { intents(Intent.OnSwipeToRefresh) },
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding()),
    ) {
        AnimatedVisibility(
            visible = items.isNotEmpty() && !isRefreshing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            CoinListScreen(items, lastListUpdateTimestamp, isTopGainers, intents, onCoinClick)
        }
    }
}

@Composable
private fun CoinListScreen(
    items: List<Coin>,
    lastListUpdateTimestamp: Long?,
    isTopGainers: Boolean,
    intents: (Intent) -> Unit,
    onCoinClick: (Coin) -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (lastListUpdateTimestamp != null) {
                ShowcaseText(
                    text =
                        stringResource(
                            R.string.last_list_update,
                            SimpleDateFormat("dd. MMM yyyy HH:mm", Locale.GERMANY).format(
                                Date(lastListUpdateTimestamp),
                            ),
                        ),
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(items.size) { itemIndex ->
                    CoinListItem(items[itemIndex], Modifier, onCoinClick)
                }
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            ToggleAssetsByPerformanceButton(isTopGainers, intents)
        }
    }
}

@Composable
private fun ToggleAssetsByPerformanceButton(
    isTopGainers: Boolean,
    intents: (Intent) -> Unit,
) {
    ExtendedFloatingActionButton(
        modifier =
            Modifier
                .wrapContentWidth()
                .padding(horizontal = Dimensions.spacingNormal)
                .padding(bottom = Dimensions.spacingSmall)
                .height(Dimensions.minimumTouchTarget),
        content = {
            Row {
                ShowcaseText(
                    text =
                        stringResource(
                            if (isTopGainers) {
                                R.string.switch_performance_button_worst_assets
                            } else {
                                R.string.switch_performance_button_best_assets
                            },
                        ),
                    color = colorResource(R.color.purple_700),
                )
                Icon(
                    modifier = Modifier.padding(start = Dimensions.spacingSmall),
                    imageVector =
                        if (isTopGainers) {
                            Icons.AutoMirrored.Default.TrendingDown
                        } else {
                            Icons.AutoMirrored.Default.TrendingUp
                        },
                    contentDescription = null,
                )
            }
        },
        onClick = { intents(Intent.ToggleCoinListOrder) },
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LoadedScreenPreview() {
    MaterialTheme {
        ScreenContent(
            state =
                State.Loaded(
                    items =
                        MutableLiveData(
                            listOf(
                                Coin(
                                    "1",
                                    "Bitcoin",
                                    "BTC",
                                    1000000.3300,
                                    123.45,
                                ),
                                Coin(
                                    "2",
                                    "Ethereum",
                                    "ETH",
                                    500000.0000,
                                    67.85,
                                ),
                                Coin(
                                    "3",
                                    "BNB",
                                    "BNB",
                                    123.0230,
                                    90.74,
                                ),
                            ),
                        ),
                    lastListUpdateTimestamp = MutableLiveData(1536347807471L),
                ),
            {},
            {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoadingScreenPreview() {
    MaterialTheme {
        ScreenContent(
            state =
                State.Loading,
            {},
            {},
        )
    }
}
