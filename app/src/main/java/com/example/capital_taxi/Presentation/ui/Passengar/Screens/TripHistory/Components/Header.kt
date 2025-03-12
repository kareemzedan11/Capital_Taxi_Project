package com.example.capital_taxi.Presentation.ui.Passengar.Screens.TripHistory.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun Header(navController: NavController, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(backgroundColor)
    ) {


        Box(modifier = Modifier .align(Alignment.TopStart)) {

            CloseButton(navController = navController)
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            Title()
        }
    }
}