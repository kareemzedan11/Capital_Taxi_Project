package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.model.content.CircleShape
import com.example.capital_taxi.Helper.PermissionViewModel
import com.example.capital_taxi.R
import android.content.Context
import android.content.Intent
import android.provider.Settings
@Composable
fun EnableLocationServices(permissionViewModel: PermissionViewModel, context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.Start) {

            // Lottie Animation
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.findlocation))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(250.dp)
                    .background(Color.Transparent)
                    .clip(CircleShape)
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)) {
                        append("Enable Location Services\n")
                    }

                    withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.W600)) {
                        append("Please enable location services")
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                color = Color.Gray
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    // Open location settings screen
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 30.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color))
            ) {
                Text("Enable in Settings",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

        }
    }
}