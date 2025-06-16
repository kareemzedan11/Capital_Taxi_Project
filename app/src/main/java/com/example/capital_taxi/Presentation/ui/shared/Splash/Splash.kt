package com.example.capital_taxi.Navigation

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    var progress by remember { mutableStateOf(0f) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val context = LocalContext.current
    // Detect the current layout direction (LTR or RTL)
    val layoutDirection = LocalLayoutDirection.current

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(20)
            progress += 0.005f
        }

        // اقرأ حالة الدخول من SharedPreferences

        val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
        val driverToken = sharedPreferences.getString("driver_token", null)
        val userToken = sharedPreferences.getString("USER_TOKEN", null)
        val userType = sharedPreferences.getString("user_type", null)

        val driverid = sharedPreferences.getString("driver_id", null)

        val isLoggedIn = (!driverToken.isNullOrEmpty() || !userToken.isNullOrEmpty())
        if (isLoggedIn) {
            when (userType) {
                "driver" -> {
                    shouldForcePhotoVerification(driverid!!) { shouldVerify ->
                        if (shouldVerify) {
                            navController.navigate(Destination.DriverPhotoValidationScreen.route) {
                                popUpTo(Destination.SplashScreen.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Destination.DriverHomeScreen.route) {
                                popUpTo(Destination.SplashScreen.route) { inclusive = true }
                            }
                        }
                    }
                }
                "rider" -> {
                    navController.navigate(Destination.UserHomeScreen.route) {
                        popUpTo(Destination.SplashScreen.route) { inclusive = true }
                    }
                }
                else -> {
                    navController.navigate(Destination.StartScreen.route) {
                        popUpTo(Destination.SplashScreen.route) { inclusive = true }
                    }
                }
            }
        } else {
            navController.navigate(Destination.StartScreen.route) {
                popUpTo(Destination.SplashScreen.route) { inclusive = true }
            }
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



fun shouldForcePhotoVerification(
    driverId: String,
    onResult: (Boolean) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("drivers")
        .whereEqualTo("id", driverId)
        .limit(1)
        .get()
        .addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val doc = snapshot.documents[0]
                val lastCheck = doc.getTimestamp("lastPhotoCheck")
                val now = Timestamp.now()

                if (lastCheck == null) {
                    onResult(true) // أول مرة يتحقق
                } else {
                    val diff = now.seconds - lastCheck.seconds
                    val hours = diff / 3600
                    onResult(hours >= 24)
                }
            } else {
                onResult(true) // لو مفيش document، نطلب تحقق
            }
        }
        .addOnFailureListener {
            onResult(true) // في حالة فشل القراءة، نطلب تحقق
        }
}
