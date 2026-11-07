package com.obrit.obrit.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import com.obrit.android.core.designsystem.theme.LocalOBRitColor

private const val NAVIGATION_TRANSITION_DURATION_MILLIS = 320
private const val NAVIGATION_FADE_DURATION_MILLIS = 220
private const val NAVIGATION_SLIDE_DISTANCE_DIVISOR = 5

@Composable
internal fun <T : Any> OBRitNavDisplay(
    backStack: List<T>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    entryProvider: (key: T) -> NavEntry<T>,
) {
    val colors = LocalOBRitColor.current

    NavDisplay(
        backStack = backStack,
        modifier = modifier.background(colors.gray900),
        onBack = onBack,
        transitionSpec = obritForwardTransitionSpec(),
        popTransitionSpec = obritPopTransitionSpec(),
        predictivePopTransitionSpec = obritPredictivePopTransitionSpec(),
        entryProvider = entryProvider,
    )
}

private typealias NavigationTransitionSpec<T> =
    AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform

private typealias PredictiveNavigationTransitionSpec<T> =
    AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform

private fun <T : Any> obritForwardTransitionSpec(): NavigationTransitionSpec<T> = { sharedAxisTransition(enterFromEnd = true) }

private fun <T : Any> obritPopTransitionSpec(): NavigationTransitionSpec<T> = { sharedAxisTransition(enterFromEnd = false) }

private fun <T : Any> obritPredictivePopTransitionSpec(): PredictiveNavigationTransitionSpec<T> =
    { swipeEdge -> sharedAxisTransition(enterFromEnd = swipeEdge == NavigationEvent.EDGE_RIGHT) }

private fun sharedAxisTransition(enterFromEnd: Boolean): ContentTransform {
    val enterDirection = if (enterFromEnd) 1 else -1
    val exitDirection = -enterDirection

    return targetEnterTransition(enterDirection) togetherWith initialExitTransition(exitDirection)
}

private fun targetEnterTransition(direction: Int): EnterTransition =
    slideInHorizontally(
        animationSpec =
            tween(
                durationMillis = NAVIGATION_TRANSITION_DURATION_MILLIS,
                easing = FastOutSlowInEasing,
            ),
        initialOffsetX = { fullWidth ->
            direction * fullWidth / NAVIGATION_SLIDE_DISTANCE_DIVISOR
        },
    ) +
        fadeIn(
            animationSpec =
                tween(
                    durationMillis = NAVIGATION_FADE_DURATION_MILLIS,
                    easing = FastOutSlowInEasing,
                ),
        )

private fun initialExitTransition(direction: Int): ExitTransition =
    slideOutHorizontally(
        animationSpec =
            tween(
                durationMillis = NAVIGATION_TRANSITION_DURATION_MILLIS,
                easing = FastOutSlowInEasing,
            ),
        targetOffsetX = { fullWidth ->
            direction * fullWidth / NAVIGATION_SLIDE_DISTANCE_DIVISOR
        },
    ) +
        fadeOut(
            animationSpec =
                tween(
                    durationMillis = NAVIGATION_FADE_DURATION_MILLIS,
                    easing = FastOutSlowInEasing,
                ),
        )
