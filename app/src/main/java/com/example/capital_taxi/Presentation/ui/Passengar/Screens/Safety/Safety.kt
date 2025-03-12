package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Safety


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.capital_taxi.R


@Composable
fun SafetyScreen( navController: NavController) {
//    // Load the Lottie animation from raw resource
//    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.contacts))
//
//    // Animate the Lottie animation in an infinite loop
//    val progress by animateLottieCompositionAsState(
//        composition,
//        iterations = LottieConstants.IterateForever // Loop infinitely
//    )
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Your Safety is Our Priority",
//            style = MaterialTheme.typography.titleMedium
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//
//
//        Button(
//            onClick = {
//                // Handle SOS button press
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(60.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//        ) {
//            // Lottie Animation inside Button
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .wrapContentWidth(Alignment.CenterHorizontally)
//            ) {
//                LottieAnimation(composition, progress, modifier = Modifier.size(40.dp)) // Adjust size
//            }
//            Spacer(modifier = Modifier.width(8.dp)) // Space between Lottie icon and text
//            Text(text = "Emergency SOS")
//        }
//    }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {   },
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
//        ) {
//            Text(text = "Report an Issue")
//        }
//    }
//
//
//@Composable
//fun SafetyFeatureCard(title: String, description: String) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//        ) {
//            Text(
//                text = title,
//                style = MaterialTheme.typography.titleMedium
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(text = description)
//        }
//    }
}