package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DirectionsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@Composable
fun captainToPassenger(navController: NavController) {
    val directionsViewModel: DirectionsViewModel = viewModel()

    val duration by directionsViewModel.duration.collectAsState()

    // التأكد من أن duration ليس null
    val hours = duration?.toInt() ?: 0 // إذا كانت duration null، استخدم 0
    val minutes = ((duration?.minus(hours ?: 0))?.times(60))?.toInt() ?: 0 // إذا كانت duration null، استخدم 0

    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.clickable { navController.navigate(Destination.TripDetailsForDriver.route) },
                    contentDescription = null,
                    painter = painterResource(R.drawable.baseline_segment_24)
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.meet_passenger),
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 14.sp,
                            maxSize = 18.sp
                        ),
                        fontFamily = CustomFontFamily,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@Composable
fun DriverTripAcceptedScreen(
    userName: String,
    userRating: Float,
    pickupLocation: String,
    dropoffLocation: String,
    etaToPickup: String,
    distanceToPickup: String,
    onNavigate: () -> Unit,
    onCallUser: () -> Unit,
    onMessageUser: () -> Unit,
    onCancelTrip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // User Photo and Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // User Photo
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.person),
                            contentDescription = "User Photo",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }

                    // User Name and Rating
                    Column {
                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_star_24),
                                contentDescription = "Rating",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Yellow
                            )
                            Text(
                                text = userRating.toString(),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Pickup and Dropoff Locations
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pickup Location
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.circle),
                            contentDescription = "Pickup",
                            modifier = Modifier.size(24.dp),
                            tint = colorResource(R.color.primary_color)
                        )
                        Text(
                            text = pickupLocation,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }

                    // Dropoff Location
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.travel),
                            contentDescription = "Dropoff",
                            modifier = Modifier.size(24.dp),
                            tint = colorResource(R.color.primary_color)
                        )
                        Text(
                            text = dropoffLocation,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Trip Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ETA to Pickup
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = "ETA",
                        modifier = Modifier.size(24.dp),
                        tint = colorResource(R.color.primary_color)
                    )
                    Text(
                        text = "ETA to Pickup: $etaToPickup",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                // Distance to Pickup
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.travel),
                        contentDescription = "Distance",
                        modifier = Modifier.size(24.dp),
                        tint = colorResource(R.color.primary_color)
                    )
                    Text(
                        text = "Distance: $distanceToPickup",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }

        // Navigation and Contact Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onNavigate,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Navigate", color = Color.White, fontSize = 16.sp)
            }

            Button(
                onClick = onCallUser,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                border = BorderStroke(1.dp, colorResource(R.color.primary_color)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Call User", color = colorResource(R.color.primary_color), fontSize = 16.sp)
            }
        }

        // Cancel Trip Button
        Button(
            onClick = onCancelTrip,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Cancel Trip", color = Color.White, fontSize = 16.sp)
        }
    }
}
