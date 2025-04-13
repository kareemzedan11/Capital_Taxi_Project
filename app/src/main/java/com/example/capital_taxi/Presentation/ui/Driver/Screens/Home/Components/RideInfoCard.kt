package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.capital_taxi.R
import com.example.capital_taxi.data.utils.AudioRecorder
import com.example.capital_taxi.data.utils.FirebaseUploader
import kotlinx.coroutines.delay

@Composable
fun RideInfoCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${stringResource(R.string.From)} :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Lotfy lapip strt",
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "1 sec ago",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Destination Section
            Text(
                text = "${stringResource(R.string.To)} :",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Abbas El-Akkad Strt",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.Gray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Price Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.Price)} :",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "30.00 EGP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(R.color.primary_color)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(
                        onClick = { /* Handle Chat Action */ }, modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(Color.Blue)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_chat_24),
                            contentDescription = "Chat Icon",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    IconButton(
                        onClick = { /* Handle Call Action */ },
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(colorResource(R.color.primary_color))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_call_24),
                            contentDescription = "Call Icon",
                            tint = Color.White
                        )
                    }
                }

                Row {
                    Button(
                        onClick = { /* Handle I am there action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = "I am there", color = Color.Black)
                    }

                    Button(
                        onClick = { /* Handle Cancel action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text(text = stringResource(R.string.cancel_trip), color = Color.White)
                    }
                }
            }
        }
    }}

    @Composable
   fun DriverArrivedCard() {
        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }
        var isRecording by remember { mutableStateOf(false) }
        var showUploadProgress by remember { mutableStateOf(false) }
        var uploadError by remember { mutableStateOf<String?>(null) }
        var uploadSuccess by remember { mutableStateOf(false) }

        val audioRecorder = remember { AudioRecorder(context) }
        val firebaseUploader = remember { FirebaseUploader(context) }

        // Error Dialog
        if (uploadError != null) {
            AlertDialog(
                onDismissRequest = { uploadError = null },
                title = { Text("Upload Failed") },
                text = { Text(uploadError!!) },
                confirmButton = {
                    Button(onClick = { uploadError = null }) {
                        Text("OK")
                    }
                }
            )
        }

        // Success Snackbar
        if (uploadSuccess) {
            LaunchedEffect(Unit) {
                delay(2000)
                uploadSuccess = false
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Upload successful!", color = Color.Green)
            }
        }
Box(modifier = Modifier.fillMaxSize()) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Arrived At:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Abbas El-Akkad Strt",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "Just now",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Driver Info Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.person),
                        contentDescription = "Passenger",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Your Passenger:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "John Doe",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_star_24),
                        contentDescription = "Rating",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.5",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.uber),
                            contentDescription = "Start Ride",
                            modifier = Modifier.size(26.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Ride",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
                var showCancelDialog by remember { mutableStateOf(false) }
                var selectedReason by remember { mutableStateOf<String?>(null) }
                var customReason by remember { mutableStateOf("") }

// الأسباب الخاصة بالسائق
                val driverReasons = listOf(
                    "Passenger didn't show up" to Icons.Default.Person,
                    "Vehicle issue" to Icons.Default.LocationOn,
                    "Emergency situation" to Icons.Default.Warning,
                    "Wrong location" to Icons.Default.LocationOn,
                    "Unsafe behavior" to Icons.Default.Info,
                    "Other reason" to Icons.Default.Edit
                )

                Button(
                    onClick = { showCancelDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_cancel_24),
                            contentDescription = "Cancel Ride",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cancel Ride",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                if (showCancelDialog) {
                    Dialog(
                        onDismissRequest = { showCancelDialog = false }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                           ,
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                // Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Cancel Trip",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE53935)
                                    )
                                    IconButton(
                                        onClick = { showCancelDialog = false }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close")
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Reasons List
                                Text(
                                    text = "Please select the reason:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyColumn {
                                    items(driverReasons) { (reason, icon) ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable {
                                                    selectedReason = if (reason == selectedReason) null else reason
                                                    if (reason != "Other reason") customReason = ""
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(
                                                1.dp,
                                                if (selectedReason == reason) Color(0xFFE53935) else Color.LightGray
                                            ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (selectedReason == reason)
                                                    Color(0x22E53935) else Color.Transparent
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = reason,
                                                    tint = if (selectedReason == reason)
                                                        Color(0xFFE53935) else Color.Gray
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = reason,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                RadioButton(
                                                    selected = selectedReason == reason,
                                                    onClick = {
                                                        selectedReason = if (reason == selectedReason) null else reason
                                                        if (reason != "Other reason") customReason = ""
                                                    },
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = Color(0xFFE53935)
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                // Custom Reason Field
                                if (selectedReason == "Other reason") {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = customReason,
                                        onValueChange = { customReason = it },
                                        label = { Text("Please specify...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color(0xFFE53935),
                                            focusedLabelColor = Color(0xFFE53935)
                                        ),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                        singleLine = true
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Confirm Button
                                Button(
                                    onClick = {
                                        val reason = if (selectedReason == "Other reason")
                                            customReason else selectedReason
                                        println("Trip cancelled. Reason: $reason")
                                        showCancelDialog = false
                                        // Add your cancellation logic here
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = selectedReason != null &&
                                            (selectedReason != "Other reason" || customReason.isNotBlank()),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE53935),
                                        disabledContainerColor = Color(0xFFE53935).copy(alpha = 0.5f)
                                    )
                                ) {
                                    Text(
                                        text = "Confirm Cancellation",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }}}}

    // Recording Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Important Information") },
            text = {
                Text("We will record the trip for safety. Please position your phone to capture both faces during the journey.")
            },
            confirmButton = {
                Button(
                    onClick = {

                        showDialog = false

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red else Color(0xFF4CAF50)
                    )
                ) {
                    if (showUploadProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(if (isRecording) "Stop & Confirm" else "Start Recording")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}
}sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}
