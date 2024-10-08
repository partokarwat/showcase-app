package com.partokarwat.showcase.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun ShowcaseText(
    text: String,
    color: Color = Color.Black,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp,
    fontFamily: FontFamily = FontFamily.SansSerif,
    fontWeight: FontWeight = FontWeight.Medium,
) {
    Text(
        text = text,
        color = color,
        modifier = modifier,
        fontSize = fontSize,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
    )
}

@Preview
@Composable
private fun ShowcaseTextPreview() {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        ShowcaseText(text = "Text text")
    }
}

@Composable
fun <INTENT> ShowcaseTextModel<INTENT>.Component() {
    ShowcaseText(text, color, modifier, fontSize, fontFamily, fontWeight)
}

data class ShowcaseTextModel<INTENT>(
    val text: String,
    val color: Color = Color.Black,
    val modifier: Modifier = Modifier,
    val fontSize: TextUnit = 20.sp,
    val fontFamily: FontFamily = FontFamily.SansSerif,
    val fontWeight: FontWeight = FontWeight.Medium,
)
