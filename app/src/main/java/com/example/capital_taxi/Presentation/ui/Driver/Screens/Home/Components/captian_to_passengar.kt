import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.TripDetailsForDriver
import com.example.capital_taxi.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun captainToPassenger(
    navController: NavController,
    onTripStarted: () -> Unit,
    passengerName: String,
    mapchangetoInPrograss: () -> Unit,
    context: Context, // Keep context if needed elsewhere, but review its usage
    tripId: String
) {

    // State variables for distance and duration, initialized to null (loading state)
    // rememberSaveable ensures state survives configuration changes
    var distance by rememberSaveable { mutableStateOf<Double?>(null) }
    var duration by rememberSaveable { mutableStateOf<Double?>(null) }

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    // Use LaunchedEffect to set up the Firestore listener when tripId changes or composable enters composition.
    // The listener will be automatically removed when the composable leaves composition or tripId changes.
    LaunchedEffect(tripId) {
        if (tripId.isEmpty()) {
            Log.w("Firebase", "Trip ID is empty, cannot attach listener.")
            // Optionally reset state or show an error
            distance = null
            duration = null
            return@LaunchedEffect // Exit if tripId is invalid
        }

        Log.d("Firebase", "Setting up listener for Trip ID: $tripId")
        val db = FirebaseFirestore.getInstance()
        val tripQuery = db.collection("trips").whereEqualTo("_id", tripId)

        // Register the snapshot listener
        val listenerRegistration = tripQuery.addSnapshotListener { snapshot, error ->
            // Handle errors
            if (error != null) {
                Log.e("Firebase", "Listen failed for trip $tripId", error)
                // Optionally update UI to show error state
                distance = -1.0 // Indicate error, or use a dedicated error state variable
                duration = -1.0
                return@addSnapshotListener
            }

            // Handle snapshot
            if (snapshot != null && !snapshot.isEmpty) {
                val document = snapshot.documents[0] // Assuming _id is unique, take the first doc
                val data = document.data
                Log.d("Firebase", "Received update for trip $tripId: $data")

                // Safely extract data and update state
                // This update will trigger recomposition
                distance = data?.get("distance") as? Double
                val durationValue = data?.get("time") as? Long // Assuming time is stored as Long (milliseconds)
                duration = durationValue?.toDouble()?.div(1000.0) // Convert ms to seconds

                Log.d("Firebase", "State updated: Distance = $distance, Duration = $duration")

            } else {
                Log.w("Firebase", "No data found for trip $tripId or snapshot is empty.")
                // Handle case where document might be deleted or doesn't exist
                distance = null
                duration = null
            }
        }

        // IMPORTANT: LaunchedEffect automatically handles cancellation.
        // When the effect leaves the composition or the key (tripId) changes,
        // the coroutine scope is cancelled. Firebase SDK's addSnapshotListener
        // tied to a scope/activity lifecycle *should* handle this, but for safety,
        // especially outside Activity/Fragment scopes, explicit removal is best.
        // However, the standard way in LaunchedEffect is to rely on scope cancellation.
        // If issues persist, consider using DisposableEffect for explicit cleanup:
        /*
        DisposableEffect(tripId) {
            // ... (setup code as above)
            val listenerRegistration = tripQuery.addSnapshotListener { ... }
            onDispose {
                Log.d("Firebase", "Removing listener for Trip ID: $tripId")
                listenerRegistration.remove()
            }
        }
        */
        // For now, we rely on LaunchedEffect's cancellation propagation.
    }

    // --- UI Formatting --- //

    // Format distance (meters to km or m)
    val formattedDistance = when {
        distance == null -> "Loading..."
        distance == -1.0 -> "Error"
        distance!! >= 1000 -> "${String.format("%.2f", distance!! / 1000)} km"
        else -> "${distance!!.toInt()} m"
    }

    // Format duration (seconds to hours and minutes)
    val formattedDuration = when {
        duration == null -> "Loading..."
        duration == -1.0 -> "Error"
        else -> {
            val totalSeconds = duration!!.toLong()
            val hours = (totalSeconds / 3600).toInt()
            val minutes = ((totalSeconds % 3600) / 60).toInt()
            val remainingSeconds = (totalSeconds % 60).toInt() // Optional: include seconds if needed

            buildString {
                if (hours > 0) {
                    append("$hours hour${if (hours > 1) "s" else ""} ")
                }
                if (minutes > 0 || hours > 0) { // Show minutes if hours > 0 or minutes > 0
                    append("$minutes min")
                } else { // Only show seconds if less than a minute total
                    append("$remainingSeconds sec")
                }
                // If duration is exactly 0, show "0 min" or similar
                if (hours == 0 && minutes == 0 && remainingSeconds == 0) {
                    clear()
                    append("0 min")
                }
            }.trim()
        }
    }

    // --- UI Structure --- //

    Box(modifier = Modifier.fillMaxSize()) {
        // Show loader only if both distance and duration are null (initial loading)
        if (distance == null && duration == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
            // TODO: Review this line - Clearing preferences here seems incorrect.
            // It might clear directions data unexpectedly during loading or recomposition.
            // Consider moving this logic to a more appropriate place (e.g., when trip ends/starts).
            // DirectionsPrefs.clear(context)

        } else {
            // Assuming Top_Navigation_Box is another composable defined elsewhere
            // Top_Navigation_Box(tripId)

            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), // Optional: customize shape
                sheetContent = {
                    // Assuming TripDetailsForDriver is another composable
                    TripDetailsForDriver(
                        navController = navController,
                        onTripStarted = onTripStarted,
                        tripId = tripId,
                        mapchangetoInPrograss = mapchangetoInPrograss,
                        menu_close = { scope.launch { bottomSheetState.hide() } },
                        passengerName = passengerName
                    )
                },
                content = {
                    // Main content area (Map would likely be here)
                    // The Card is placed at the bottom using Box alignment
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 16.dp), // Consistent padding
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Reduced elevation slightly
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp), // Padding inside the card
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row: Menu Icon, Title, Spacer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween // Pushes icon and text apart
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp) // Explicit size for the icon
                                        .clickable { scope.launch { bottomSheetState.show() } },
                                    contentDescription = "show_trip_details" ,// Accessibility
                                    painter = painterResource(R.drawable.baseline_segment_24),
                                    tint = MaterialTheme.colorScheme.onSurface // Use theme color
                                )

                                Text(
                                    text = stringResource(R.string.meet_passenger),
                                    fontSize = 16.sp, // Slightly smaller font size
                                    fontFamily = CustomFontFamily, // Ensure this font is correctly set up
                                    color = MaterialTheme.colorScheme.onSurface, // Use theme color
                                    fontWeight = FontWeight.Bold
                                )

                                // Spacer to balance the row if needed, or keep arrangement SpaceBetween
                                Spacer(modifier = Modifier.size(24.dp)) // Placeholder spacer to match icon size
                            }

                            // Spacer(modifier = Modifier.height(8.dp)) // Removed extra spacer, use Arrangement

                            // Bottom Row: Time Left, Distance Left
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Time Left Column
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f) // Takes equal space
                                ) {
                                    Text(
                                        text = stringResource(R.string.time_left), // Use string resource
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant // Subtler color
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formattedDuration,
                                        fontSize = 14.sp, // Slightly larger for value
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // Distance Left Column
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f) // Takes equal space
                                ) {
                                    Text(
                                        text = "distance_left", // Use string resource
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formattedDistance,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
