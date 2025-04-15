package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.During_the_trip

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
@Composable
fun DriverArrivalCard(
    tripId: String,
    onTripCancelled: () -> Unit,
    modifier: Modifier = Modifier
) {
    var remainingTime by remember { mutableStateOf(10 * 60) } // 10 minutes in seconds
    var cancellationInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(remainingTime) {
        while (remainingTime > 0) {
            delay(1000) // Update every second
            remainingTime--
        }

        // When timer reaches 0, cancel the trip
        if (remainingTime == 0 && !cancellationInProgress) {
            cancellationInProgress = true
            updateTripStatusToCancelled(tripId = tripId,
                onSuccess = {
                    cancellationInProgress = false
                    onTripCancelled()
                },
                onError = { error ->
                    cancellationInProgress = false
                    println("Failed to update trip status: $error")
                    onTripCancelled() // Still notify UI even if Firebase update fails
                }
            )
        }
    }

    val minutes = TimeUnit.SECONDS.toMinutes(remainingTime.toLong())
    val seconds = remainingTime % 60
 Box(Modifier.fillMaxSize()){

     Card(
         modifier = modifier
             .fillMaxWidth()
             .align(Alignment.BottomCenter)
             .padding(16.dp),
         colors = CardDefaults.cardColors(
             containerColor = MaterialTheme.colorScheme.surfaceVariant,
             contentColor = MaterialTheme.colorScheme.onSurfaceVariant
         )
     ) {
         Column(
             modifier = Modifier.padding(16.dp),
             horizontalAlignment = Alignment.CenterHorizontally
         ) {
             Text(
                 text = "Driver Has Arrived",
                 style = MaterialTheme.typography.headlineSmall.copy(
                     fontWeight = FontWeight.Bold
                 )
             )

             Spacer(modifier = Modifier.height(16.dp))

             Text(
                 text = "Your driver is now at the meeting point. " +
                         "Please proceed to the vehicle within the next 10 minutes " +
                         "or the trip will be automatically cancelled.",
                 style = MaterialTheme.typography.bodyMedium
             )

             Spacer(modifier = Modifier.height(24.dp))

             // Countdown timer with progress indicator if cancelling
             if (cancellationInProgress) {
                 CircularProgressIndicator()
                 Spacer(modifier = Modifier.height(16.dp))
                 Text("Cancelling trip...")
             } else {
                 Text(
                     text = String.format("%02d:%02d", minutes, seconds),
                     style = MaterialTheme.typography.displaySmall.copy(
                         fontSize = 48.sp,
                         color = when {
                             remainingTime <= 60 -> Color.Red
                             remainingTime <= 180 -> Color(0xFFFFA500) // Orange
                             else -> MaterialTheme.colorScheme.primary
                         }
                     )
                 )

                 Spacer(modifier = Modifier.height(8.dp))

                 Text(
                     text = "Time remaining",
                     style = MaterialTheme.typography.labelMedium
                 )
             }
         }
     }
 }
}fun updateTripStatusToCancelled(
    tripId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    FirebaseFirestore.getInstance()
        .collection("trips")
        .whereEqualTo("_id", tripId)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                onError("Trip not found")
                return@addOnSuccessListener
            }

            val document = querySnapshot.documents.first()
            document.reference.update("status", "cancelled")
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.message ?: "Update failed") }
        }
        .addOnFailureListener { e ->
            onError(e.message ?: "Failed to fetch trip")
        }
}