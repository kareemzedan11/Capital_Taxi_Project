package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_Rating

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R


@Composable
fun StarRating(rating: Float, onRatingSelected: (Float) -> Unit) {
    Row(horizontalArrangement = Arrangement.Center) {
        for (i in 1..5) {
            // Calculate the star state: full, half, or empty
            val isFullStar = rating >= i
            val isHalfStar = rating in (i - 0.5)..i.toDouble()

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        val newRating = if (rating >= i) i - 0.5f else i.toFloat()
                        onRatingSelected(newRating)
                    }
            ) {
                if (isFullStar) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_rate_24), // Full star icon
                        contentDescription = "Full Star",
                        tint = Color(0XFFFDCD10),
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (isHalfStar) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_half_24), // Half star icon
                        contentDescription = "Half Star",
                        tint = Color(0XFFFDCD10),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_24), // Empty star icon
                        contentDescription = "Empty Star",
                        tint = Color.Gray,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}