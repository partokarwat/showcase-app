package com.partokarwat.showcase.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partokarwat.showcase.R
import com.partokarwat.showcase.data.db.Coin
import com.partokarwat.showcase.data.remote.MarketValue
import com.valentinilk.shimmer.shimmer
import java.util.Locale

@Composable
fun CoinListItem(
    item: Coin,
    modifier: Modifier = Modifier,
    onCoinClick: (Coin) -> Unit = {},
) {
    Box(
        modifier
            .padding(horizontal = Dimensions.spacingNormal)
            .padding(top = Dimensions.spacingNormal)
            .clickable { onCoinClick(item) },
    ) {
        Column {
            Row {
                ShowcaseText(item.name)
                Spacer(Modifier.weight(1f))
                ShowcaseText(
                    String.format(Locale.GERMANY, "%.2f", item.changePercent24Hr) + "%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                )
            }
            Row {
                ShowcaseText(item.symbol, fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                ShowcaseText(
                    "€ " + String.format(Locale.GERMANY, "%f", item.priceEur),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    color = colorResource(R.color.teal_700),
                )
            }
        }
    }
}

@Composable
fun CoinListItemSkeleton() {
    Box(
        Modifier
            .shimmer()
            .padding(horizontal = Dimensions.spacingNormal)
            .padding(top = Dimensions.spacingNormal),
    ) {
        Column {
            Row {
                Box(
                    Modifier
                        .padding(bottom = 2.dp)
                        .height(20.dp)
                        .width(100.dp)
                        .background(Color.LightGray),
                )
                Spacer(Modifier.weight(1f))
                Box(Modifier.height(16.dp).width(100.dp).background(Color.LightGray))
            }
            Row {
                Box(Modifier.height(14.dp).width(50.dp).background(Color.LightGray))
                Spacer(Modifier.weight(1f))
                Box(Modifier.height(16.dp).width(50.dp).background(colorResource(R.color.teal_700)))
            }
        }
    }
}

@Composable
fun MarketValueListItem(item: MarketValue) {
    Box(
        Modifier
            .padding(horizontal = Dimensions.spacingNormal)
            .padding(top = Dimensions.spacingNormal),
    ) {
        Column {
            Row {
                ShowcaseText(item.exchangeId)
                Spacer(Modifier.weight(1f))
                ShowcaseText(
                    String.format(Locale.GERMANY, "%.2f", item.volumePercent.toDouble()) + "%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                )
            }
            Row {
                ShowcaseText(
                    "${item.baseSymbol}/${item.quoteSymbol}",
                    fontSize = 14.sp,
                )
                Spacer(Modifier.weight(1f))
                ShowcaseText(
                    "€ " + String.format(Locale.GERMANY, "%f", item.volumeUsd24Hr.toDouble()),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    color = colorResource(R.color.teal_700),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CoinListItemPreview() {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        CoinListItem(
            Coin(
                "1",
                "Bitcoin",
                "BTC",
                1000000.3300,
                123.45,
            ),
        )
    }
}

@Preview
@Composable
private fun CoinListItemSkeletonPreview() {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        CoinListItemSkeleton()
    }
}

@Preview
@Composable
private fun MarketValueListItemPreview() {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        MarketValueListItem(
            MarketValue(
                "Crypto.com Exchange",
                "1694772140.48677032",
                "62267.6968255180129234",
                "9.99636699417193",
                "BTC",
                "USDC"
            ),
        )
    }
}
