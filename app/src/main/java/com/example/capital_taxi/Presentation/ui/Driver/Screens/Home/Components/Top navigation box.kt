package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import android.os.Handler
import android.os.Looper
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
                allInstructions = updateInstructionsStatus(it, driverLocation)
            }
        }

        val locationListener = listenToDriverLocation(tripId) { location ->
            driverLocation = location
            allInstructions = updateInstructionsStatus(allInstructions, location)
        }

        onDispose {
            instructionsListener.remove()
            locationListener!!.remove()
        }
    }
    // واجهة المستخدم
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

    return instructions.map { instruction ->
        val isExited = calculateDistance(location, instruction) < 20
        instruction.copy(exited = isExited)
    }
}

// دالة مساعدة لحساب المسافة بين موقعين
private fun calculateDistance(driverLocation: LatLng, instruction: Instruction): Double {
    val results = FloatArray(1)
    if (instruction.latitude != null && instruction.longitude != null) {
        android.location.Location.distanceBetween(
            driverLocation.latitude,
            driverLocation.longitude,
            instruction.latitude,
            instruction.longitude,
            results
        )
    }

    return results[0].toDouble()
}
private fun calculateCurrentAndNextInstructions(
    instructions: List<Instruction>,
    driverLocation: LatLng?
): Pair<Instruction?, Instruction?> {
    if (instructions.isEmpty()) return null to null

    return if (driverLocation == null) {
        instructions.first() to instructions.getOrNull(0)
    } else {
        val currentIndex = instructions.indexOfFirst { !it.exited }
        when {
            currentIndex < 0 -> null to null
            currentIndex == instructions.lastIndex -> instructions[currentIndex] to null
            else -> instructions[currentIndex] to instructions[currentIndex + 1]
        }
    }
}
@Composable
private fun DistanceColumn(distance: Double?) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .background(color = Color(0xfff1a104)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Distance",
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
    Column() {
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
                text = "Next: ${nextInstruction.text.take(30)}...",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp)
        } else {
            driverLocation?.let { location ->
                Text(
                    text = "Location: ${location.latitude.roundToDecimals(5)}, ${location.longitude.roundToDecimals(5)}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp)
            }
        }
    }
}

fun Double.roundToDecimals(decimals: Int): String {
    return "%.${decimals}f".format(this)
}
fun listenToDriverLocation(
    tripId: String,
    onUpdate: (LatLng?) -> Unit
): ListenerRegistration? {
    return try {
        FirebaseFirestore.getInstance()
            .collection("trips")
            .whereEqualTo("_id", tripId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DRIVER_LOCATION", "Error listening to location: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val doc = snapshot.documents.first()
                        val location = doc?.get("driverLocation") as? Map<String, Any>
                        if (location != null) {
                            val lat = location["latitude"] as? Double ?: 0.0
                            val lng = location["longitude"] as? Double ?: 0.0
                            onUpdate(LatLng(lat, lng))
                        }
                    } else {
                        Log.w("DRIVER_LOCATION", "No matching trip found for _id = $tripId")
                    }
                }
            }
    } catch (e: Exception) {
        Log.e("DRIVER_LOCATION", "Error setting up listener: ${e.message}")
        null
    }
}


data class LatLng(val latitude: Double, val longitude: Double)
fun listenToInstructionsByTripId(
    tripId: String,
    onUpdate: (List<Instruction>?) -> Unit
): ListenerRegistration {
    val db = FirebaseFirestore.getInstance()
    return db.collection("trips")
        .whereEqualTo("_id", tripId)
        .addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                Log.e("INSTRUCTION", "Listen failed: ${error.message}")
                onUpdate(null)
                return@addSnapshotListener
            }

            val doc = querySnapshot?.documents?.firstOrNull()
            val instructionsList = doc?.get("instructions") as? List<Map<String, Any>>
            val polyline = doc?.getString("points") ?: ""
            val decodedPoints = decodePolyline(polyline) // List<LatLng>

            if (instructionsList != null) {
                val instructionObjects = instructionsList.mapNotNull {
                    try {
                        val distance = (it["distance"] as? Number)?.toDouble() ?: return@mapNotNull null
                        val text = it["text"] as? String ?: return@mapNotNull null
                        val streetName = it["street_name"] as? String ?: ""
                        val exitNumber = (it["exit_number"] as? Number)?.toInt() ?: 0
                        val exited = it["exited"] as? Boolean ?: false
                        val lastHeading = (it["last_heading"] as? Number)?.toDouble() ?: 0.0
                        val sign = (it["sign"] as? Number)?.toInt() ?: 0
                        val streetDestination = it["street_destination"] as? String ?: ""
                        val time = (it["time"] as? Number)?.toLong() ?: 0L
                        val turnAngle = (it["turn_angle"] as? Number)?.toDouble() ?: 0.0

                        val interval = it["interval"] as? List<Number> ?: emptyList()
                        val latLng = if (interval.isNotEmpty()) {
                            val idx = interval.first().toInt().coerceIn(0, decodedPoints.lastIndex)
                            decodedPoints[idx]
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
                            latitude = latLng?.latitude,
                            longitude = latLng?.longitude
                        )

                    } catch (e: Exception) {
                        Log.e("INSTRUCTION", "Parsing error: ${e.message}")
                        null
                    }
                }

                onUpdate(instructionObjects.takeIf { it.isNotEmpty() })
            } else {
                onUpdate(null)
            }
        }
}

