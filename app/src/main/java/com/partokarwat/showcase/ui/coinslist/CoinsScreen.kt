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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.ui.coinslist.CoinsViewModelContract.Event
import com.partokarwat.showcase.ui.coinslist.CoinsViewModelContract.Intent
import com.partokarwat.showcase.ui.coinslist.CoinsViewModelContract.State
import com.partokarwat.showcase.ui.compose.ClassKeyedCrossfade
import com.partokarwat.showcase.ui.compose.CoinListItem
import com.partokarwat.showcase.ui.compose.CoinListItemSkeleton
import com.partokarwat.showcase.ui.compose.Dimensions
import com.partokarwat.showcase.ui.compose.ShowcaseText
import com.partokarwat.showcase.ui.use
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun CoinsScreen(
    activity: Activity,
    modifier: Modifier = Modifier,
    viewModel: CoinsViewModel = hiltViewModel(),
) {
    val (state, intents, events) = use(viewModel = viewModel)

    Initializer(activity, intents, events)
    ScreenContent(state, intents)
}

@Composable
private fun Initializer(
    activity: Activity,
    intents: (Intent) -> Unit,
    events: SharedFlow<Event>,
) {
    LaunchedEffect(Unit) {
        collectEvents(activity, intents, events)
    }
    LaunchedEffect(Unit) {
        intents(Intent.ScreenCreated)
    }
}

private suspend fun collectEvents(
    activity: Activity,
    intents: (Intent) -> Unit,
    events: SharedFlow<Event>,
) {
    events.collectLatest {
        when (it) {
            is Event.ShowTechnicalError -> {
                Toast
                    .makeText(
                        activity,
                        activity.getString(it.errorMessageResId),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    state: State,
    intents: (Intent) -> Unit,
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
        ClassKeyedCrossfade(
            targetState = state,
            label = "Content",
        ) { newState ->
            when (newState) {
                is State.Loading -> LoadingScreen(contentPadding)
                is State.Loaded ->
                    LoadedContent(
                        contentPadding,
                        newState.isRefreshing,
                        newState,
                        intents,
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
        repeat(10) {
            CoinListItemSkeleton()
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.height(50.dp).width(180.dp).background(Color.LightGray, RoundedCornerShape(33)))
    }

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadedContent(
    contentPadding: PaddingValues,
    isRefreshing: Boolean,
    state: State.Loaded,
    intents: (Intent) -> Unit,
) {
    val listState = state.items.collectAsState(emptyList()).value

    val pullRefreshState =
        rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = {
                intents(Intent.OnSwipeToRefresh)
            },
        )

    return Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
                .pullRefresh(pullRefreshState),
    ) {
        AnimatedVisibility(
            visible = listState.isNotEmpty() && !isRefreshing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            CoinsScreen(state, intents)
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = Dimensions.spacingNormal),
        )
    }
}

@Composable
private fun CoinsScreen(
    state: State.Loaded,
    intents: (Intent) -> Unit,
) {
    val listState = state.items.collectAsState(emptyList()).value
    val timeStampState = state.lastListUpdateTimestamp.collectAsState(0L).value
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ShowcaseText(
                text =
                    stringResource(
                        R.string.last_list_update,
                        SimpleDateFormat("dd. MMM yyyy HH:mm").format(
                            Date(timeStampState),
                        ),
                    ),
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(listState.size) { itemIndex ->
                    CoinListItem(listState[itemIndex])
                }
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            ToggleAssetsByPerformanceButton(state, intents)
        }
    }
}

@Composable
private fun ToggleAssetsByPerformanceButton(
    state: State.Loaded,
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
                            if (state.isTopGainers) {
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
                        if (state.isTopGainers) {
                            Icons.AutoMirrored.Default.TrendingDown
                        } else {
                            Icons.AutoMirrored.Default.TrendingUp
                        },
                    contentDescription = null,
                )
            }
        },
        onClick = { intents(Intent.OnSwitchPerformanceButtonPressed) },
    )
}

@Preview(showSystemUi = true)
@Preview
@Composable
private fun LoadedScreenPreview() {
    MaterialTheme {
        LoadedContent(
            contentPadding = PaddingValues(0.dp),
            isRefreshing = false,
            State.Loaded(
                flow {
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
                    )
                },
                flow { 1536347807471L },
            ),
            {},
        )
    }
}

@Preview(showSystemUi = true)
@Preview
@Composable
private fun LoadingScreenPreview() {
    MaterialTheme {
        LoadingScreen(
            contentPadding = PaddingValues(0.dp),
        )
    }
}
