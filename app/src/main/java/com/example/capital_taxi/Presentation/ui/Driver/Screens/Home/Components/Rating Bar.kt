package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    Row(horizontalArrangement = Arrangement.Start) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.Star,
                contentDescription = "Rating Star",
                tint = if (i <= rating) Color.Yellow else Color.Gray, // Yellow for selected stars
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clickable { onRatingChanged(i.toFloat()) }
            )
        }
    }
}
