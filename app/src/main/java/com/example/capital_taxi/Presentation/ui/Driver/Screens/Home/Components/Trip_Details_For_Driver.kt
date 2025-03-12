package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.capital_taxi.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsForDriver(navController: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val tripViewModel: dataTripViewModel = viewModel()
    val origin by tripViewModel.origin.collectAsState()
    val destination by tripViewModel.destination.collectAsState()
    LaunchedEffect(Unit) {
        tripViewModel.setTripDetails(origin, destination) // أدخل القيم الفعلية
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            CancellationReasons(navController)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.secondary_color))
    ) {
        Column(modifier = Modifier.background(colorResource(R.color.secondary_color))) {
            // Back Icon and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Transparent)
                            .border(2.dp, color = Color.Black, RoundedCornerShape(30.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.trip_details),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.padding(vertical = 20.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.elevatedCardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // User Details
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material.Icon(
                                    modifier = Modifier.size(50.dp),
                                    painter = painterResource(R.drawable.person),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "Ahmed",
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row {
                                        Text(
                                            text = "4.5",
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                        Spacer(Modifier.width(5.dp))
                                        Icon(
                                            tint = Color.Unspecified,
                                            contentDescription = null,
                                            painter = painterResource(R.drawable.baseline_star_rate_24)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.LightGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )

                            // Ride Details
                            RidePointDetails(
                                Locationicon = R.drawable.circle,
                                Destinationicon = R.drawable.travel,
                                LocationText = origin,
                                DestinationText = destination,
                                isDestance = false,
                                onClick = { }
                            )

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.LightGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )

                            // Chat and Call Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { /* Handle chat */ },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.message),
                                        contentDescription = "Chat",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Chat",
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = { /* Handle call */ },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.primary_color))
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_phone_24),
                                        contentDescription = "Call",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Call",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Cancel Trip Button
                            Button(
                                onClick = { showBottomSheet = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                            ) {
                                Text(
                                    text = stringResource(R.string.cancel_trip),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Stop Accepting Trips Button
                Button(
                    onClick = { /* Handle invite */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text(
                        text = stringResource(R.string.stop_accept_trips),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
class dataTripViewModel : ViewModel() {
    private val _origin = MutableStateFlow("")
    val origin: StateFlow<String> = _origin

    private val _destination = MutableStateFlow("")
    val destination: StateFlow<String> = _destination

    fun setTripDetails(origin: String, destination: String) {
        _origin.value = origin
        _destination.value = destination
    }
}
@Composable
fun RideRequestCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Profile picture placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Color.Gray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "R",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // User details
            Column {
                Text(text = "Ruth Favour Doe", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Luxury Ride", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Ride price and distance
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "$16.76", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "24km", color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider(color = Color.LightGray, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Pickup and drop-off points
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = android.R.drawable.presence_online),
                    contentDescription = "Pickup Point",
                    tint = Color.Green,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "New Jersey, Delaware 2673", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = android.R.drawable.presence_busy),
                    contentDescription = "Drop-off Point",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Nezer Building, Addibas 3476", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Decline action */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
            ) {
                Text(text = "Decline", color = Color.Black)
            }

            Button(
                onClick = { /* Accept action */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
            ) {
                Text(text = "Accept", color = Color.White)
            }
        }
    }
}

@Composable
fun CancellationReasons(navController: NavController) {
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CancelTripDialog(
            onConfirm = {
                println("Trip canceled for reason: $selectedReason")
                showDialog = false
                // Navigate back or handle the cancellation
            },
            onDismiss = {
                showDialog = false
                println("Cancellation dismissed.")
            }
        )
    }

    // Retrieve cancellation reasons from string resources
    val reasons = stringArrayResource(id = R.array.reasons)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.cancellation_reasons),
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(reasons) { reason ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedReason = reason
                            showDialog = true
                        }
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = reason,
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CancelTripDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    androidx.compose.material.AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = stringResource(id = R.string.cancel_trip_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.cancel_trip_dialog_message))
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
                onClick = { onConfirm() }) {
                Text(stringResource(id = R.string.cancel_trip_button_confirm))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text(stringResource(id = R.string.cancel_trip_button_dismiss))
            }
        }
    )
}

