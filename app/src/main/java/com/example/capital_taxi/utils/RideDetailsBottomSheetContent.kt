package com.example.capital_taxi.utils

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.capital_taxi.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Data class for driver information
data class DriverInfo(
    val id: String,
    val name: String,
    val profileImage: String,
    val rating: Float,
    val tripCount: Int,
    val carModel: String,
    val licensePlate: String
)

@Composable
fun RideDetailsBottomSheetContent2(
    driverInfo: DriverInfo,
    onCancel: () -> Unit,
    distance: Double,
    duration: Int,
    price: Double,
    navController:NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
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
                    .width(40.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Gray.copy(alpha = 0.5f)))
        }

        // Main scrollable content
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            // Meeting location and time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "We will meet after",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                RoundedTimeDisplayWithFill2(duration = formatTime(duration))
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Ride details card
            RideDetailsCard(
                distance = formatDistance(distance),
                duration = formatTime(duration),
                price = formatPrice(price)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Car and driver details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left side: Driver details
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Car plate with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.uber),
                            contentDescription = "Car",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = driverInfo.licensePlate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    // Car model
                    Text(
                        text = driverInfo.carModel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Driver details
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = driverInfo.profileImage,
                                contentDescription = "Driver",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                text = driverInfo.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // Rating and trip count
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "%.1f".format(driverInfo.rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "•",
                                color = Color.Gray
                            )

                            Text(
                                text = "${driverInfo.tripCount}+ Trips",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Right side: Car image and buttons
                Column {
                    // Car image (placeholder)
                    Image(
                        painter = painterResource(id = R.drawable.uber), // Replace with your image
                        contentDescription = "Car",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    // Call and chat buttons
                    CallAndChatButtons()
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Cancel button
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text(
                    text = "Cancel Ride",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RoundedTimeDisplayWithFill2(duration: String) {
    Box(
        modifier = Modifier
            .size(70.dp, 40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE8F5E9)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = duration,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun RideDetailsCard(
    distance: String,
    duration: String,
    price: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Distance",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = distance,
                fontSize = 14.sp
            )
        }

        Divider(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp),
            color = Color.Gray
        )

        Column {
            Text(
                text = "Duration",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = duration,
                fontSize = 14.sp
            )
        }

        Divider(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp),
            color = Color.Gray
        )

        Column {
            Text(
                text = "Price",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = price,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CallAndChatButtons() {
    Row {
        Button(
            onClick = { /* Call action */ },
            modifier = Modifier.size(43.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Button(
            onClick = { /* Chat action */ },
            modifier = Modifier.size(43.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_chat_24),
                contentDescription = "Chat",
                tint = Color.White,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@Composable
fun DriverArrivedBottomSheet(
    driverInfo: DriverInfo,
    onCancel: () -> Unit,
    distance: Double,
    duration: Int,
    price: Double
) {
    var timeRemaining by remember { mutableStateOf(600) } // 10 minutes in seconds

    LaunchedEffect(key1 = Unit) {
        while (timeRemaining > 0) {
            delay(1000L)
            timeRemaining--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
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
                    .width(40.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Gray.copy(alpha = 0.5f))
            )
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Arrival message with icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Arrived",
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = "Driver has arrived",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Please proceed to the vehicle",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Countdown timer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Waiting time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = timeRemaining / 600f,
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 6.dp,
                        color = Color.Blue,
                        backgroundColor = Color.Gray.copy(alpha = 0.2f)
                    )

                    Text(
                        text = timeFormatted(timeRemaining),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Ride details card
            RideDetailsCard(
                distance = formatDistance(distance),
                duration = formatTime(duration),
                price = formatPrice(price)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Driver info section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Car plate with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.uber),
                            contentDescription = "Car",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = driverInfo.licensePlate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    // Car model
                    Text(
                        text = driverInfo.carModel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Driver details
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = driverInfo.profileImage,
                                contentDescription = "Driver",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                text = driverInfo.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // Rating and trip count
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "%.1f".format(driverInfo.rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "•",
                                color = Color.Gray
                            )

                            Text(
                                text = "${driverInfo.tripCount}+ Trips",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Column {
                    // Car image
                    Image(
                        painter = painterResource(id = R.drawable.uber),
                        contentDescription = "Car",
                        modifier = Modifier
                            .size(100.dp, 60.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    // Call and chat buttons
                    CallAndChatButtons()
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Cancel button
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text(
                    text = "Cancel Ride",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RideInProgressBottomSheet(
    driverInfo: DriverInfo,
    onCancel: () -> Unit,
    distance: Double,
    duration: Int,
    price: Double
) {
    val currentTime by produceState(initialValue = Date()) {
        while (true) {
            delay(1000L)
            value = Date()
        }
    }

    val estimatedArrivalTime = remember(currentTime, duration) {
        val calendar = Calendar.getInstance()
        calendar.time = currentTime
        calendar.add(Calendar.MINUTE, duration)
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
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
                    .width(40.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Gray.copy(alpha = 0.5f)))
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Ride in progress message
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.uber),
                    contentDescription = "In progress",
                    tint = Color.Blue,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = "Ride in Progress",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Estimated arrival at $estimatedArrivalTime",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Time and distance info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Time remaining",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            text = formatTime(duration),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column {
                        Text(
                            text = "Distance",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            text = formatDistance(distance),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Price",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            text = formatPrice(price),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Empty space to balance layout
                    Column {
                        Text(text = " ")
                        Text(text = " ")
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Driver info section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Car plate with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.uber),
                            contentDescription = "Car",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = driverInfo.licensePlate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    // Car model
                    Text(
                        text = driverInfo.carModel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Driver details
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = driverInfo.profileImage,
                                contentDescription = "Driver",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                text = driverInfo.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // Rating and trip count
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "%.1f".format(driverInfo.rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "•",
                                color = Color.Gray
                            )

                            Text(
                                text = "${driverInfo.tripCount}+ Trips",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Column {
                    // Car image
                    Image(
                        painter = painterResource(id = R.drawable.uber),
                        contentDescription = "Car",
                        modifier = Modifier
                            .size(100.dp, 60.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    // Call and chat buttons
                    CallAndChatButtons()
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Cancel button
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text(
                    text = "Cancel Ride",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RideRatingBottomSheet(
    driverInfo: DriverInfo,
    onComplete: () -> Unit,
    distance: Double,
    duration: Int,
    price: Double
) {
    var rating by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    var showComplaintForm by remember { mutableStateOf(false) }
    var complaintText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
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
                    .width(40.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Gray.copy(alpha = 0.5f))
            )
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Ride completion message
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color.Green,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Ride Completed",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Please rate your experience",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Rating stars
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How was your trip?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) Color.Yellow else Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = i }
                        )
                    }
                }
            }

            // Feedback text field
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Add feedback (optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                TextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Complaint button
            Button(
                onClick = { showComplaintForm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "File a complaint",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Red
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Complaint",
                        tint = Color.Red
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Ride details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ride Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Distance",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatDistance(distance),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Column {
                        Text(
                            text = "Duration",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatTime(duration),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Column {
                        Text(
                            text = "Price",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatPrice(price),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Driver info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Driver",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = driverInfo.profileImage,
                        contentDescription = "Driver",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Text(
                            text = driverInfo.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "%.1f".format(driverInfo.rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Text(
                                text = "•",
                                color = Color.Gray
                            )

                            Text(
                                text = "${driverInfo.tripCount}+ Trips",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.uber),
                        contentDescription = "Car",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )

                    Column {
                        Text(
                            text = driverInfo.carModel,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = driverInfo.licensePlate,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Submit button
            Button(
                onClick = { submitRating(driverInfo, rating, onComplete) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                enabled = rating > 0,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (rating > 0) Color.Blue else Color.Gray
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Submit Rating",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showComplaintForm) {
        ComplaintForm(
            complaintText = complaintText,
            onComplaintTextChange = { complaintText = it },
            onSubmit = {
                submitComplaint(complaintText)
                showComplaintForm = false
            },
            onCancel = { showComplaintForm = false }
        )
    }
}

@Composable
fun ComplaintForm(
    complaintText: String,
    onComplaintTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("File Complaint") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Describe your complaint",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            TextField(
                value = complaintText,
                onValueChange = onComplaintTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Gray.copy(alpha = 0.1f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = complaintText.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (complaintText.isNotEmpty()) Color.Red else Color.Gray
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Submit Complaint",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Helper functions
private fun formatDistance(distance: Double): String {
    return if (distance < 1000) {
        "%.0f m".format(distance)
    } else {
        "%.1f km".format(distance / 1000)
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    return if (minutes > 0) {
        "${minutes}m"
    } else {
        "${seconds}s"
    }
}

private fun formatPrice(price: Double): String {
    return "%.2f EGP".format(price)
}

private fun timeFormatted(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

private fun submitRating(driverInfo: DriverInfo, rating: Int, onComplete: () -> Unit) {
    // Implement Firebase rating submission
    // This is a placeholder - you'll need to implement the actual Firebase logic
    println("Submitting rating $rating for driver ${driverInfo.id}")
    onComplete()
}

private fun submitComplaint(complaintText: String) {
    // Implement complaint submission
    println("Submitted complaint: $complaintText")
}

// Custom CircularProgressIndicator
@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = color.copy(alpha = 0.2f)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = backgroundColor,
                radius = size.minDimension / 2 - strokeWidth.toPx() / 2,
                style = Stroke(strokeWidth.toPx())
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height),
                topLeft = Offset.Zero
            )
        }
    }
}