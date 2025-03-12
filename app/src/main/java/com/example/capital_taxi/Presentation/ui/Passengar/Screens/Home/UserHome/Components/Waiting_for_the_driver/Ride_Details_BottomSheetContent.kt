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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
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
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.HorizontalImageScroll
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun RideDetailsBottomSheetContent(navController:NavController) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .padding(2.dp),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.meeting_location),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        RoundedTimeDisplayWithFill()
                    }
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                    Spacer(modifier = Modifier.padding(top = 10.dp))

                    RideDetailsCard()
                    Spacer(modifier = Modifier.padding(top = 10.dp))

                    HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                    Spacer(modifier = Modifier.padding(bottom = 10.dp))

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                            .shadow(20.dp, shape = RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(16.dp)
                            .padding(2.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(R.drawable.uber),
                                        contentDescription = "car image",
                                        modifier = Modifier
                                            .size(160.dp) // Set a fixed size
                                            .clip(RoundedCornerShape(12.dp)) // Optional rounded corners
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier.padding(16.dp),
                                    ) {
                                        Column {
                                            androidx.compose.material3.Text(
                                                "Car Number ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                color = Color.Black.copy(alpha = .3f)
                                            )
                                            Spacer(modifier = Modifier.padding(10.dp))

                                            androidx.compose.material3.Text(
                                                "Car Type",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.padding(top = 16.dp))
                                driverDetails()
                                Spacer(modifier = Modifier.padding(top = 16.dp))

                                callAndChat(navController )
                            }

                            Spacer(modifier = Modifier.padding(top = 16.dp))


                        }
                    }
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                    Spacer(modifier = Modifier.padding(top = 10.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .height(60.dp)
                            .border(0.dp, Color.Transparent)
                            .shadow(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFF2F2F2))
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
}
