package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DirectionsViewModel


@Composable
fun RoundedTimeDisplayWithFill() {
    val directionsViewModel: DirectionsViewModel = viewModel()

    val duration by directionsViewModel.duration.collectAsState()

    val totalTimeMillis = (duration?.toLong() ?: 0L) * 60 * 1000L


    val progress = remember { Animatable(0f) }
var generalColor= colorResource(R.color.primary_color)
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = totalTimeMillis.toInt(), easing = LinearEasing)
        )
    }

    Box(
        modifier = Modifier
            .size(width = 70.dp, height = 40.dp)
            .background( colorResource(R.color.secondary_color)) // Background color
    ) {
        // Animated Fill
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = generalColor,
                topLeft = Offset(0f, 0f),
                size = Size(size.width * progress.value, size.height),
                cornerRadius = CornerRadius(25.dp.toPx(), 25.dp.toPx())
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {

            Text(
                text = "${duration?.toInt()} M",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
