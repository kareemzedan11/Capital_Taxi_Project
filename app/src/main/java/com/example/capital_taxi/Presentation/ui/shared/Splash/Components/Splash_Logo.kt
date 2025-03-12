package com.example.capital_taxi.Presentation.ui.shared.Splash.Components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R

@Composable
fun SplashLogo(progress: Float) {
    val logoScale by animateFloatAsState(
        targetValue = if (progress < 1f) 1f else 0.8f,
        animationSpec = tween(durationMillis = 100)
    )

    val logoAlpha by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 100)
    )

    Icon(
        modifier = Modifier
            .size(320.dp)
            .graphicsLayer(
                scaleX = logoScale,
                scaleY = logoScale,
                alpha = logoAlpha
            ),
        painter = painterResource(R.drawable.logo),
        contentDescription = "Logo"
    )
}