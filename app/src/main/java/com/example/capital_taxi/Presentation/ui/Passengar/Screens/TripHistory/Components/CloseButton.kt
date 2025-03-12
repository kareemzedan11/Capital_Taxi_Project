package com.example.capital_taxi.Presentation.ui.Passengar.Screens.TripHistory.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun CloseButton(navController: NavController) {
    Icon(
        modifier = Modifier
            .padding(start = 16.dp, top = 60.dp)
            .size(40.dp)

            .clip(CircleShape)
            .background(Color.White)
            .clickable { navController.popBackStack() },
        imageVector = Icons.Default.Close,
        contentDescription =  "close",
        tint = Color.Black
    )
}