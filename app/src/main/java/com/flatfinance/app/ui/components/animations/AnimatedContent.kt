package com.flatfinance.app.ui.components.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedSlideContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    initiallyVisible: Boolean = false,
    slideDirection: SlideDirection = SlideDirection.UP,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        initiallyVisible = initiallyVisible,
        modifier = modifier,
        enter = slideInAnimation(slideDirection) + fadeIn(animationSpec = tween(300)),
        exit = slideOutAnimation(slideDirection) + fadeOut(animationSpec = tween(300)),
        content = content
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedFadeContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    initiallyVisible: Boolean = false,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        initiallyVisible = initiallyVisible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)),
        content = content
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedScaleContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    initiallyVisible: Boolean = false,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        initiallyVisible = initiallyVisible,
        modifier = modifier,
        enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
        exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)),
        content = content
    )
}

enum class SlideDirection {
    UP, DOWN, LEFT, RIGHT
}

private fun slideInAnimation(direction: SlideDirection): EnterTransition {
    return when (direction) {
        SlideDirection.UP -> slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(300)
        )
        SlideDirection.DOWN -> slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(300)
        )
        SlideDirection.LEFT -> slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        )
        SlideDirection.RIGHT -> slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        )
    }
}

private fun slideOutAnimation(direction: SlideDirection): ExitTransition {
    return when (direction) {
        SlideDirection.UP -> slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(300)
        )
        SlideDirection.DOWN -> slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(300)
        )
        SlideDirection.LEFT -> slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        )
        SlideDirection.RIGHT -> slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        )
    }
}