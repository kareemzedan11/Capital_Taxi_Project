package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver.RideDetailsBottomSheetContent
import com.example.capital_taxi.R








@Composable
fun HorizontalImage() {
    Box(
        modifier = Modifier
            .size(300.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.container),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun HorizontalImageScroll() {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        repeat(3) {
            HorizontalImage()
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

