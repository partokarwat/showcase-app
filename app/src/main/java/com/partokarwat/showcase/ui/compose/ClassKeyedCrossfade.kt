package com.partokarwat.showcase.ui.compose

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * [Crossfade] that uses state class as a key to animate only between different state types, not instances.
 * Useful for when state is an immutable sealed class or interface when we want to animate between
 * descendants.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> ClassKeyedCrossfade(
    targetState: T,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    label: String = "Crossfade",
    content: @Composable (T) -> Unit,
) {
    val transition = updateTransition(targetState, label)
    transition.Crossfade(
        modifier = modifier,
        animationSpec = animationSpec,
        contentKey = { it?.let { it::class } ?: Unit },
        content = content,
    )
}