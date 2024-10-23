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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.ui.coinslist.CoinListViewModel.Companion.IS_ERROR_INITIAL_VALUE
import com.partokarwat.showcase.ui.coinslist.CoinListViewModel.Companion.IS_REFRESHING_INITIAL_VALUE
import com.partokarwat.showcase.ui.coinslist.CoinListViewModel.Companion.IS_TOP_GAINERS_INITIAL_VALUE
import com.partokarwat.showcase.ui.coinslist.CoinListViewModel.Companion.LIST_SIZE
import com.partokarwat.showcase.ui.compose.CoinListItem
import com.partokarwat.showcase.ui.compose.CoinListItemSkeleton
import com.partokarwat.showcase.ui.compose.Dimensions
import com.partokarwat.showcase.ui.compose.ShowcaseText
import com.valentinilk.shimmer.shimmer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoinListScreen(
    activity: Activity,
    onCoinClick: (Coin) -> Unit = {},
    coinListViewModel: CoinListViewModel = hiltViewModel(),
) {
    val items = coinListViewModel.items.observeAsState().value
    val lastListUpdateTimestamp = coinListViewModel.lastListUpdateTimestamp.observeAsState().value
    val isError = coinListViewModel.isError.collectAsStateWithLifecycle(IS_ERROR_INITIAL_VALUE).value
    val isRefreshing = coinListViewModel.isRefreshing.observeAsState().value
    val isTopGainers =
        coinListViewModel.isTopGainers
            .collectAsStateWithLifecycle(
                IS_TOP_GAINERS_INITIAL_VALUE,
            ).value

    ScreenContent(
        items,
        lastListUpdateTimestamp,
        isRefreshing ?: IS_REFRESHING_INITIAL_VALUE,
        isTopGainers,
        onCoinClick,
        coinListViewModel,
    )
    if (isError.first) {
        Toast
            .makeText(
                activity,
                activity.getString(isError.second),
                Toast.LENGTH_LONG,
            ).show()
        coinListViewModel.resetIsError()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    items: List<Coin>?,
    lastListUpdateTimestamp: Long?,
    isRefreshing: Boolean,
    isTopGainers: Boolean,
    onCoinClick: (Coin) -> Unit = {},
    coinListViewModel: CoinListViewModel,
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
        if (!items.isNullOrEmpty() && items.size == LIST_SIZE) {
            LoadedContent(
                contentPadding,
                items,
                lastListUpdateTimestamp,
                isRefreshing,
                isTopGainers,
                onCoinClick,
                coinListViewModel,
            )
        } else {
            LoadingScreen(contentPadding)
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
    onCoinClick: (Coin) -> Unit = {},
    coinListViewModel: CoinListViewModel,
) {
    val pullRefreshState = rememberPullToRefreshState()

    return PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { coinListViewModel.onSwipeToRefresh() },
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
            CoinListScreen(items, lastListUpdateTimestamp, isTopGainers, onCoinClick, coinListViewModel)
        }
    }
}

@Composable
private fun CoinListScreen(
    items: List<Coin>,
    lastListUpdateTimestamp: Long?,
    isTopGainers: Boolean,
    onCoinClick: (Coin) -> Unit = {},
    coinListViewModel: CoinListViewModel,
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
            ToggleAssetsByPerformanceButton(isTopGainers, coinListViewModel)
        }
    }
}

@Composable
private fun ToggleAssetsByPerformanceButton(
    isTopGainers: Boolean,
    coinListViewModel: CoinListViewModel,
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
        onClick = { coinListViewModel.toggleCoinListOrder() },
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LoadedScreenPreview() {
    MaterialTheme {
        ScreenContent(
            items =
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
            lastListUpdateTimestamp = 1536347807471L,
            isRefreshing = false,
            isTopGainers = true,
            {},
            hiltViewModel(),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoadingScreenPreview() {
    MaterialTheme {
        ScreenContent(
            items = null,
            lastListUpdateTimestamp = 1536347807471L,
            isRefreshing = false,
            isTopGainers = true,
            {},
            hiltViewModel(),
        )
    }
}
