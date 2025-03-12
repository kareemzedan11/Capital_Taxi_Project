package com.example.capital_taxi.Presentation.ui.Driver.Screens.VerficationScreens.DocumentReview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.capital_taxi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentReviewScreen(navController: NavController) {
    // Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.review))

    // Lottie animation state
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                       "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.primary_color))
            )
        }
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.secondary_color))
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Lottie Animation
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Message to the user
        Text(
            text = "Your document is under review",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You will receive a response within 48 hours",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}}