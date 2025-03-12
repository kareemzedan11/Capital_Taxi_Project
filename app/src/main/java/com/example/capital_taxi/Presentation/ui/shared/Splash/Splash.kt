package com.example.capital_taxi.Navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import com.example.capital_taxi.Presentation.ui.shared.Splash.Components.SplashLogo
import com.example.capital_taxi.Presentation.ui.shared.Splash.Components.SplashProgressBar
import com.example.capital_taxi.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    var progress by remember { mutableStateOf(0f) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Detect the current layout direction (LTR or RTL)
    val layoutDirection = LocalLayoutDirection.current

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(20)
            progress += 0.005f
        }

        navController.navigate(Destination.StartScreen.route) {
            popUpTo(Destination.SplashScreen.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        colorResource(R.color.primary_color).copy(alpha = 0.7f),
                        colorResource(R.color.primary_color).copy(alpha = 0.3f),
                        colorResource(R.color.primary_color).copy(alpha = 0.7f),
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Use the SplashLogo composable
            SplashLogo(progress)

            Spacer(modifier = Modifier.weight(1f))

            // Use the SplashProgressBar composable
            SplashProgressBar(progress, layoutDirection, screenWidth)

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}



