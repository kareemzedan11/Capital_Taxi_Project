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
import androidx.compose.material.Icon
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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
fun getColorFromString(colorName: String): Color {
    return when (colorName.lowercase()) {
        "red" -> Color.Red
        "blue" -> Color.Blue
        "black" -> Color.Black
        "white" -> Color.White
        "gray", "grey" -> Color.Gray
        "green" -> Color.Green
        "yellow" -> Color.Yellow
        "orange" -> Color(0xFFFFA500)
        else -> Color.Gray
    }
}

@Composable
fun RideDetailsBottomSheetContent(
    onclick:()->Unit,
    navController: NavController, tripid: String,

    UserId:String,driverid:String,fare:Double) {
    val carType = remember { mutableStateOf("") }
    val carNumber = remember { mutableStateOf("") }
    val driverUsername = remember { mutableStateOf("") }
    val driverRating = remember { mutableStateOf<Double?>(null) }
    val carColor = remember { mutableStateOf("") }
    val tripsCount = remember { mutableStateOf(0) }
    val carColor2 = getColorFromString(carColor.value)

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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color(0x40000000)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        // Car Details Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Car Image with color border
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .border(
                                        width = 3.dp,
                                        color = carColor2,
                                        shape = RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.uber),
                                    contentDescription = "car image",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                if (carNumber.value.isEmpty() || carType.value.isEmpty()) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF4CAF50),
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                } else {
                                    // Car Type with icon
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_car_type),
                                            contentDescription = "Car Type",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            carType.value,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF333333)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Car Number with icon
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_plate_number),
                                            contentDescription = "Plate Number",
                                            tint = Color(0xFF2196F3),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            carNumber.value,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF555555)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Car Color with icon
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_color_palette),
                                            contentDescription = "Car Color",
                                            tint = carColor2,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            carColor.value,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF666666)
                                        )
                                    }
                                }
                            }
                        }





                        Spacer(modifier = Modifier.height(16.dp))

                        driverDetails(
                            driverusername = driverUsername.value,
                            rating = driverRating.value.toString(),
                            trips = tripsCount.value.toString(),
                            driverId = driverid
                        )

                        Spacer(modifier = Modifier.padding(top = 16.dp))

                        CallAndChat(
                            navController,
                            chatId = tripid,
                            userId = UserId,
                            tripid = tripid,
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
                    Payment_trip_cost(fare=fare)
                }

                Spacer(modifier = Modifier.padding(top = 15.dp))
                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                Spacer(modifier = Modifier.padding(top = 15.dp))

                HorizontalImageScroll()
            }
        }
    }
}