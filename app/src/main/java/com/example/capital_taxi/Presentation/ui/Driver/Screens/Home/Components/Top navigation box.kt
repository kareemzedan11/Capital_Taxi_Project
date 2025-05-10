package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.domain.driver.model.Instruction
import com.example.capital_taxi.domain.shared.decodePolyline
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


@Composable
fun Top_Navigation_Box(tripId: String) {
    var allInstructions by remember { mutableStateOf<List<Instruction>>(emptyList()) }
    var driverLocation by remember { mutableStateOf<LatLng?>(null) }

    val (currentInstruction, nextInstruction) = remember(allInstructions, driverLocation) {
        calculateCurrentAndNextInstructions(allInstructions, driverLocation)
    }

    DisposableEffect(tripId) {
        val instructionsListener = listenToInstructionsByTripId(tripId) { instructions ->
            instructions?.let {
                // Update instruction status first when instructions are loaded/updated
                allInstructions = updateInstructionsStatus(it, driverLocation)
            }
        }

        val locationListener = listenToDriverLocation(tripId) { location ->
            driverLocation = location
            // Update instruction status based on new location
            allInstructions = updateInstructionsStatus(allInstructions, location)

            if (allInstructions.isNotEmpty()) { // Ensure instructions are loaded
                // Calculate total distance and time
                val totalDistance = allInstructions.sumOf { it.distance }
                val totalTime = allInstructions.sumOf { it.time }

                // Calculate completed distance and time
                val completedDistance = allInstructions.filter { it.exited }.sumOf { it.distance }
                val completedTime = allInstructions.filter { it.exited }.sumOf { it.time }

                // Calculate remaining distance and time
                val remainingDistance = totalDistance - completedDistance
                val remainingTime = totalTime - completedTime

                // Update Firestore with REMAINING distance and time
                // Ensure non-negative values, although logically distance/time shouldn't be negative
                updateTripProgress(tripId, maxOf(0.0, remainingDistance), maxOf(0L, remainingTime))
            } else {
                // If no instructions, update progress to 0
                updateTripProgress(tripId, 0.0, 0L)
            }
        }

        onDispose {
            instructionsListener.remove()
            locationListener?.remove() // Make locationListener nullable and check before removing
        }
    }

    // واجهة المستخدم (UI remains the same)
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp) // زيادة الارتفاع لعرض معلومات إضافية
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xffce8907))
            ) {
                // صف المعلومات الأساسية
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    // مسافة العمود
                    // Display current instruction distance or remaining trip distance?
                    // Assuming current instruction distance based on original code.
                    DistanceColumn(currentInstruction?.distance)

                    Spacer(modifier = Modifier.width(16.dp))

                    // عمود الاتجاه
                    DirectionColumn(
                        text = currentInstruction?.text ?: "Waiting...",
                        street = currentInstruction?.street_name ?: "No street info"
                    )
                }

                // معلومات إضافية (الخطوة التالية أو الموقع)
                NextStepOrLocationInfo(
                    nextInstruction = nextInstruction,
                    driverLocation = driverLocation,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

private fun updateInstructionsStatus(
    instructions: List<Instruction>,
    location: LatLng?
): List<Instruction> {
    if (location == null || instructions.isEmpty()) return instructions

    // Use mapIndexedNotNull to handle potential index issues if needed, though map seems fine here.
    return instructions.map { instruction ->
        // Check if not already exited and if close enough to mark as exited
        if (!instruction.exited && instruction.latitude != null && instruction.longitude != null &&
            calculateDistance(location, LatLng(instruction.latitude, instruction.longitude)) < 20) { // Assuming 20 meters threshold
            instruction.copy(exited = true)
        } else {
            instruction
        }
    }
}

// دالة مساعدة لحساب المسافة بين موقعين (Using LatLng for both arguments for consistency)
private fun calculateDistance(loc1: LatLng, loc2: LatLng): Double {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        loc1.latitude,
        loc1.longitude,
        loc2.latitude,
        loc2.longitude,
        results
    )
    return results[0].toDouble()
}

private fun calculateCurrentAndNextInstructions(
    instructions: List<Instruction>,
    driverLocation: LatLng? // driverLocation is not actually used here, logic depends only on 'exited' status
): Pair<Instruction?, Instruction?> {
    if (instructions.isEmpty()) return null to null

    // Find the first instruction that is not marked as 'exited'
    val currentIndex = instructions.indexOfFirst { !it.exited }

    return when {
        currentIndex < 0 -> null to null // All instructions completed
        currentIndex == instructions.lastIndex -> instructions[currentIndex] to null // Current is the last one
        else -> instructions[currentIndex] to instructions[currentIndex + 1] // Current and next
    }
}

@Composable
private fun DistanceColumn(distance: Double?) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight() // Fill height for better visual balance
            .background(color = Color(0xfff1a104)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp, // Consider a more appropriate navigation icon
            contentDescription = "Distance to next maneuver",
            tint = Color.White,
            modifier = Modifier.size(24.dp))

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = distance?.let { "${it.toInt()} m" } ?: "-- m",
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DirectionColumn(text: String, street: String) {
    // Use Expanded to take remaining space if needed, or adjust padding
    Column(modifier = Modifier.padding(start = 8.dp)) { // Added padding for separation
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 2)

        Text(
            text = street,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun NextStepOrLocationInfo(
    nextInstruction: Instruction?,
    driverLocation: LatLng?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (nextInstruction != null) {
            Text(
                text = "Next: ${nextInstruction.text.take(30)}...", // Consider showing distance/time to next
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp)
        } else if (driverLocation != null) { // Show location only if no next instruction
            Text(
                text = "Location: ${driverLocation.latitude.roundToDecimals(5)}, ${driverLocation.longitude.roundToDecimals(5)}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp)
        } else {
            // Placeholder if neither is available
            Text(text = "", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

// Extension function for rounding Doubles
fun Double.roundToDecimals(decimals: Int): String {
    return "%.${decimals}f".format(this)
}

// Listener for driver location updates from Firestore
fun listenToDriverLocation(
    tripId: String,
    onUpdate: (LatLng?) -> Unit
): ListenerRegistration? { // Return nullable ListenerRegistration
    return try {
        FirebaseFirestore.getInstance()
            .collection("trips")
            .whereEqualTo("_id", tripId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DRIVER_LOCATION", "Error listening to location for trip $tripId: ${error.message}")
                    onUpdate(null) // Notify with null on error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    // Use safe casting and provide defaults
                    val locationMap = doc.get("driverLocation") as? Map<*, *>
                    val lat = (locationMap?.get("latitude") as? Number)?.toDouble()
                    val lng = (locationMap?.get("longitude") as? Number)?.toDouble()

                    if (lat != null && lng != null) {
                        onUpdate(LatLng(lat, lng))
                    } else {
                        Log.w("DRIVER_LOCATION", "Location data missing or invalid for trip $tripId")
                        onUpdate(null) // Notify with null if data is invalid
                    }
                } else {
                    Log.w("DRIVER_LOCATION", "No matching trip found for _id = $tripId or snapshot is null")
                    onUpdate(null) // Notify with null if no trip found
                }
            }
    } catch (e: Exception) {
        Log.e("DRIVER_LOCATION", "Exception setting up location listener for trip $tripId: ${e.message}", e)
        onUpdate(null) // Notify with null on exception
        null // Return null if listener setup fails
    }
}

// Custom LatLng data class (consider using com.google.android.gms.maps.model.LatLng directly if possible)
// data class LatLng(val latitude: Double, val longitude: Double)

// Listener for instruction updates from Firestore
fun listenToInstructionsByTripId(
    tripId: String,
    onUpdate: (List<Instruction>?) -> Unit
): ListenerRegistration {
    val db = FirebaseFirestore.getInstance()
    return db.collection("trips")
        .whereEqualTo("_id", tripId)
        .addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                Log.e("INSTRUCTION", "Listen failed for trip $tripId: ${error.message}")
                onUpdate(null)
                return@addSnapshotListener
            }

            val doc = querySnapshot?.documents?.firstOrNull()
            if (doc == null) {
                Log.w("INSTRUCTION", "No trip document found for _id = $tripId")
                onUpdate(emptyList()) // Return empty list if no doc found
                return@addSnapshotListener
            }

            val instructionsList = doc.get("instructions") as? List<Map<String, Any>>
            val polyline = doc.getString("points") ?: ""
            val decodedPoints = try {
                decodePolyline(polyline) // Assuming decodePolyline returns List<com.google.android.gms.maps.model.LatLng>
            } catch (e: Exception) {
                Log.e("INSTRUCTION", "Error decoding polyline for trip $tripId: ${e.message}")
                emptyList<com.google.android.gms.maps.model.LatLng>() // Use GMS LatLng
            }

            if (instructionsList == null) {
                Log.w("INSTRUCTION", "Instructions list is null for trip $tripId")
                onUpdate(emptyList())
                return@addSnapshotListener
            }

            val instructionObjects = instructionsList.mapNotNull { instructionMap ->
                try {
                    val distance = (instructionMap["distance"] as? Number)?.toDouble()
                    val text = instructionMap["text"] as? String
                    val streetName = instructionMap["street_name"] as? String ?: ""
                    val time = (instructionMap["time"] as? Number)?.toLong()

                    // Basic validation: distance, text, and time are essential
                    if (distance == null || text == null || time == null) {
                        Log.w("INSTRUCTION", "Skipping instruction due to missing essential fields: $instructionMap")
                        return@mapNotNull null
                    }

                    val exitNumber = (instructionMap["exit_number"] as? Number)?.toInt() ?: 0
                    val exited = instructionMap["exited"] as? Boolean ?: false
                    val lastHeading = (instructionMap["last_heading"] as? Number)?.toDouble() ?: 0.0
                    val sign = (instructionMap["sign"] as? Number)?.toInt() ?: 0
                    val streetDestination = instructionMap["street_destination"] as? String ?: ""
                    val turnAngle = (instructionMap["turn_angle"] as? Number)?.toDouble() ?: 0.0

                    val interval = instructionMap["interval"] as? List<Number> ?: emptyList()
                    val latLng: LatLng? = if (interval.isNotEmpty() && decodedPoints.isNotEmpty()) {
                        val startIndex = interval.firstOrNull()?.toInt()
                        if (startIndex != null && startIndex >= 0 && startIndex < decodedPoints.size) {
                            decodedPoints[startIndex] as? LatLng  // Explicit cast to LatLng
                        } else {
                            Log.w("INSTRUCTION", "Invalid interval start index: $startIndex for decoded points size: ${decodedPoints.size}")
                            null
                        }
                    } else null

                    Instruction(
                        distance = distance,
                        text = text,
                        street_name = streetName,
                        exit_number = exitNumber,
                        exited = exited,
                        last_heading = lastHeading,
                        sign = sign,
                        street_destination = streetDestination,
                        time = time,
                        turn_angle = turnAngle,
                        latitude = latLng?.latitude,  // Now safe to access
                        longitude = latLng?.longitude
                    )

                } catch (e: Exception) {
                    Log.e("INSTRUCTION", "Parsing error for instruction in trip $tripId: ${e.message}", e)
                    null // Skip instruction on error
                }
            }
            Log.d("INSTRUCTION", "Parsed ${instructionObjects.size} instructions for trip $tripId")
            onUpdate(instructionObjects)
        }
}

// This function seems unused based on the provided code context.
// If needed, it should likely calculate remaining time, not total/completed/remaining.
/*
fun calculateTripTimeStats(instructions: List<Instruction>): Triple<Long, Long, Long> {
    val totalTime = instructions.sumOf { it.time }
    val completedTime = instructions.filter { it.exited }.sumOf { it.time }
    val remainingTime = totalTime - completedTime
    return Triple(totalTime, completedTime, remainingTime)
}
*/

// Updates Firestore with the provided distance and time (intended to be remaining values)
fun updateTripProgress(tripId: String, newRemainingDistance: Double, newRemainingTime: Long) {
    val db = FirebaseFirestore.getInstance()
    val tripRef = db.collection("trips").whereEqualTo("_id", tripId)

    tripRef.get().addOnSuccessListener { snapshot ->
        val doc = snapshot.documents.firstOrNull()
        if (doc == null) {
            Log.w("UPDATE_PROGRESS", "No trip document found for _id = $tripId during update.")
            return@addOnSuccessListener
        }

        // Get current values from Firestore for comparison (optional, but good practice)
        val currentDistance = doc.getDouble("distance")
        val currentTime = doc.getLong("time")

        // Update only if values have changed
        if (newRemainingDistance != currentDistance || newRemainingTime != currentTime) {
            doc.reference.update(
                mapOf(
                    "distance" to newRemainingDistance,
                    "time" to newRemainingTime
                )
            ).addOnSuccessListener {
                Log.d("UPDATE_PROGRESS", "Trip $tripId progress updated: Dist=$newRemainingDistance, Time=$newRemainingTime")
            }.addOnFailureListener {
                Log.e("UPDATE_PROGRESS", "Failed to update progress for trip $tripId", it)
            }
        } else {
            Log.d("UPDATE_PROGRESS", "Trip $tripId progress unchanged. No update needed.")
        }

    }.addOnFailureListener {
        Log.e("UPDATE_PROGRESS", "Failed to fetch trip $tripId for progress update", it)
    }
}


