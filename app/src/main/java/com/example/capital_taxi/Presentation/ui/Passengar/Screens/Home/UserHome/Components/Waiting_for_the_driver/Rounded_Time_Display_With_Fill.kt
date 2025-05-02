
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text // Using androidx.compose.material.Text as in original
import androidx.compose.material3.CircularProgressIndicator // Using androidx.compose.material3.CircularProgressIndicator as in original
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R // Assuming R is correctly imported
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@Composable
fun RoundedTimeDisplayWithFill(tripId: String) {

    // State for time, initialized to null to represent loading state better
    var timeMillis by remember { mutableStateOf<Long?>(null) }
    // State to explicitly track loading, although timeMillis == null can also indicate loading
    var isLoading by remember { mutableStateOf(true) }
    // State for potential errors
    var errorOccurred by remember { mutableStateOf(false) }

    // Animation progress (remains the same)
    val progress = remember { Animatable(0f) }
    // Color resource (remains the same)
    val generalColor = colorResource(R.color.primary_color)
    val secondaryColor = colorResource(R.color.secondary_color)

    // Use LaunchedEffect to set up the Firestore listener for real-time updates
    LaunchedEffect(tripId) {
        if (tripId.isEmpty()) {
            Log.w("Firestore", "Trip ID is empty, cannot attach listener.")
            isLoading = false
            errorOccurred = true // Indicate error due to invalid ID
            timeMillis = null
            return@LaunchedEffect
        }

        isLoading = true
        errorOccurred = false
        Log.d("Firestore", "Setting up real-time listener for Trip ID: $tripId")
        val db = FirebaseFirestore.getInstance()
        val tripQuery = db.collection("trips").whereEqualTo("_id", tripId)

        // Register the snapshot listener for real-time updates
        val listenerRegistration = tripQuery.addSnapshotListener { snapshot, error ->
            // Handle errors
            if (error != null) {
                Log.e("Firestore", "Listen failed for trip $tripId", error)
                errorOccurred = true
                isLoading = false
                timeMillis = null // Reset time on error
                return@addSnapshotListener
            }

            // Handle snapshot
            if (snapshot != null && !snapshot.isEmpty) {
                val document = snapshot.documents[0] // Assuming _id is unique
                val data = document.data
                Log.d("Firestore", "Received update for trip $tripId: $data")

                // Safely extract time and update state
                val newTime = data?.get("time") as? Long
                if (newTime != null) {
                    timeMillis = newTime
                    Log.d("Firestore", "Time state updated: $timeMillis ms")
                } else {
                    // Handle case where 'time' field is missing or not a Long
                    Log.w("Firestore", "'time' field is missing or not a Long in document for trip $tripId")
                    timeMillis = null // Or set to a default/error value
                }
                errorOccurred = false // Clear error if update is successful
            } else {
                Log.w("Firestore", "No data found for trip $tripId or snapshot is empty.")
                // Handle case where document might be deleted or doesn't exist
                timeMillis = null
                errorOccurred = false // Not necessarily an error, could be deleted
            }
            isLoading = false // Data loaded or error occurred
        }

        // LaunchedEffect handles cancellation automatically when tripId changes or composable leaves composition.
        // The listener registration will be implicitly removed.
        // For explicit control, use DisposableEffect:
        /*
        DisposableEffect(tripId) {
             // ... (setup code as above)
             val listenerRegistration = tripQuery.addSnapshotListener { ... }
             onDispose {
                 Log.d("Firestore", "Removing listener for Trip ID: $tripId")
                 listenerRegistration.remove()
             }
         }
        */
    }

    // Function to format time (remains mostly the same, handles null)
    fun formatMillisecondsWithSeconds(milliseconds: Long?): String {
        if (milliseconds == null) return "--:--" // Placeholder for loading/error/null
        if (milliseconds < 0) return "Error" // Handle potential error state if needed

        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val hours = minutes / 60

        return when {
            hours > 0 -> String.format("%dh %02dm", hours, minutes % 60)
            minutes > 0 -> String.format("%dm %02ds", minutes, totalSeconds % 60)
            else -> String.format("%ds", totalSeconds)
        }.ifEmpty { "0s" } // Ensure something is shown if duration is 0
    }

    // TODO: Implement logic for updating the animation 'progress'
    // This current implementation doesn't seem to use the 'timeMillis' for the animation.
    // You might want to calculate progress based on initial time vs current time, etc.
    // For now, the animation logic is kept as it was in the original code.
    LaunchedEffect(Unit) { // Example: Simple fill animation on launch
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
        )
    }

    // UI Structure
    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 40.dp) // Slightly wider for potentially longer text
            .background(secondaryColor) // Use the variable defined above
            .padding(2.dp) // Add padding for the border effect if needed
    ) {
        // Animated Fill Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = generalColor, // Use the variable defined above
                topLeft = Offset(0f, 0f),
                size = Size(size.width * progress.value, size.height),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()) // Adjusted corner radius
            )
        }

        // Content Row (Indicator or Text)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp) // Padding inside the row
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp), // Slightly smaller indicator
                        strokeWidth = 2.dp
                    )
                }
                errorOccurred -> {
                    Text(
                        text = "Error",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.sp,
                            color = Color.Red, // Indicate error clearly
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                else -> {
                    Text(
                        text = formatMillisecondsWithSeconds(timeMillis),
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.sp,
                            color = Color.Black, // Ensure contrast with fill
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1 // Prevent text wrapping issues
                    )
                }
            }
        }
    }
}


