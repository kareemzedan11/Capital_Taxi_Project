package com.example.capital_taxi.Presentation.ui.shared.Splash.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.capital_taxi.R


@Composable
fun SplashProgressBar(
    progress: Float,
    layoutDirection: LayoutDirection,
    screenWidth: Dp
) {
    // Change car icon based on layout direction (RTL or LTR)
    val carIcon = if (layoutDirection == LayoutDirection.Rtl) {
        // Use a different icon for RTL (Arabic)
        R.drawable.ar_car
    } else {
        // Use the default car icon for LTR (English)
        R.drawable.splashcar
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 64.dp)
            .height(10.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color.White)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = androidx.compose.ui.graphics.lerp(
                    start = colorResource(R.color.primary_color),
                    stop = colorResource(R.color.primary_color),
                    fraction = progress
                )
            )

            Icon(
                painter = painterResource(carIcon),
                contentDescription = "Car Icon",
                tint = Color.Black,
                modifier = Modifier
                    .size(80.dp)

                    .offset(x = (progress * (screenWidth - 128.dp)) - 32.dp)
            )
        }
    }
}
