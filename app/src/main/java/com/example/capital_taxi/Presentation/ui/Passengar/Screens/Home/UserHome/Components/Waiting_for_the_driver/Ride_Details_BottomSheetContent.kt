package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capital_taxi.Presentation.ui.Driver.Components.fetchDriverCarDetails
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.HorizontalImageScroll
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RideDetailsBottomSheetContent(
    onclick:()->Unit,
    navController: NavController, tripid: String,

    UserId:String) {
    val carType = remember { mutableStateOf("") }
    val carNumber = remember { mutableStateOf("") }
    val driverUsername = remember { mutableStateOf("") }
    val driverRating = remember { mutableStateOf<Double?>(null) }
    val carColor = remember { mutableStateOf("") }
    val tripsCount = remember { mutableStateOf(0) }

    LaunchedEffect(tripid) {
        fetchDriverCarDetails(
            tripId = tripid,
            onResult = { type: String, number: String, username: String, rating: Double?, color: String, trips: Int ->
                carType.value = type
                carNumber.value = number
                driverUsername.value = username
                driverRating.value = rating
                carColor.value = color
                tripsCount.value = trips
            }
        )
    }



    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
    ) {
        // Top indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(5.dp)
                    .background(Color.Gray, CircleShape)
            )
        }

        // تم إزالة Box الخارجية واستخدام LazyColumn مباشرة
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.meeting_location),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    RoundedTimeDisplayWithFill(tripid)
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                Spacer(modifier = Modifier.padding(top = 10.dp))

                RideDetailsCard(tripid,
                    onclick=onclick,
                    navController )
                Spacer(modifier = Modifier.padding(top = 10.dp))

                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                Spacer(modifier = Modifier.padding(bottom = 10.dp))

                // Card for car details
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(20.dp, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.uber),
                                contentDescription = "car image",
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                if (carNumber.value.isEmpty() || carType.value.isEmpty()) {
                                    CircularProgressIndicator(
                                        color = Color.Blue,
                                        strokeWidth = 4.dp
                                    )
                                } else {
                                    Text(
                                        carNumber.value,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.Black.copy(alpha = 0.3f)
                                    )
                                    Spacer(modifier = Modifier.padding(10.dp))
                                    Text(
                                        carType.value,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        driverDetails(
                            driverusername = driverUsername.value,
                            rating = driverRating.value.toString(),
                            trips = tripsCount.value.toString(),
                            driverId = UserId
                        )

                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        CallAndChat(
                            navController,
                            chatId = tripid,
                            userId = UserId,
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(top = 16.dp))
                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                Spacer(modifier = Modifier.padding(top = 10.dp))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F2F2))
                ) {
                    Payment_trip_cost()
                }

                Spacer(modifier = Modifier.padding(top = 15.dp))
                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                Spacer(modifier = Modifier.padding(top = 15.dp))

                HorizontalImageScroll()
            }
        }
    }
}