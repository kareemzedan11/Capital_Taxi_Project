package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

data class Instruction(
    val distance: Double,
    val text: String,
    val street_name: String,
    val exit_number: Int,
    val exited: Boolean,
    val last_heading: Double,
    val sign: Int,
    val street_destination: String,
    val time: Long,
    val turn_angle: Double,
    val latitude: Double?,
    val longitude: Double?
)

class Top_Navigation_Box(private val tripId: String) {
    private var allInstructions: List<Instruction> = emptyList()
    private var driverLocation: LatLng? = null
    private var previousLocation: LatLng? = null
    private var currentInstructionIndex: Int? = null

    private var instructionsListener: ListenerRegistration? = null
    private var locationListener: ListenerRegistration? = null

    private var onProgressUpdate: ((remainingDistance: Double, remainingTime: Long) -> Unit)? = null

    fun startTracking(
        onProgressUpdate: (remainingDistance: Double, remainingTime: Long) -> Unit
    ) {
        this.onProgressUpdate = onProgressUpdate

        instructionsListener = listenToInstructionsByTripId(tripId) { instructions ->
            instructions?.let {
                allInstructions = updateInstructionsStatus(it, driverLocation)
                currentInstructionIndex = null
                calculateAndUpdateProgress()
            }
        }

        locationListener = listenToDriverLocation(tripId) { location ->
            val distanceCovered = previousLocation?.let { prevLoc ->
                calculateDistance(prevLoc, location ?: return@listenToDriverLocation)
            } ?: 0.0

            previousLocation = location
            driverLocation = location
            allInstructions = updateInstructionsStatus(allInstructions, location)

            calculateAndUpdateProgress(distanceCovered)
        }
    }

    fun stopTracking() {
        instructionsListener?.remove()
        locationListener?.remove()
        onProgressUpdate = null
    }

    private fun calculateAndUpdateProgress(distanceCovered: Double = 0.0) {
        if (allInstructions.isEmpty()) {
            updateTripProgress(tripId, 0.0, 0L)
            onProgressUpdate?.invoke(0.0, 0L)
            return
        }

        val (currentInstruction, _) = calculateCurrentAndNextInstructions(allInstructions, driverLocation)

        currentInstruction?.let { currInstr ->
            val newIndex = allInstructions.indexOfFirst { it == currInstr }

            if (currentInstructionIndex == null || currentInstructionIndex != newIndex) {
                currentInstructionIndex = newIndex
            }

            currentInstructionIndex?.let { index ->
                if (index >= 0 && index < allInstructions.size) {
                    val currentInstr = allInstructions[index]
                    val newRemainingDistance = maxOf(0.0, currentInstr.distance - distanceCovered)

                    allInstructions = allInstructions.toMutableList().apply {
                        set(index, currentInstr.copy(distance = newRemainingDistance))
                    }

                    val totalDistance = allInstructions.sumOf { it.distance }
                    val totalTime = allInstructions.sumOf { it.time }
                    val completedDistance = allInstructions.filter { it.exited }.sumOf { it.distance }
                    val completedTime = allInstructions.filter { it.exited }.sumOf { it.time }
                    val remainingDistance = maxOf(0.0, totalDistance - completedDistance)
                    val remainingTime = maxOf(0L, totalTime - completedTime)

                    updateTripProgress(tripId, remainingDistance, remainingTime)
                    onProgressUpdate?.invoke(remainingDistance, remainingTime)

                    // ✅ التحديث الديناميكي الجديد هنا
                    val originalTotalDistance = totalDistance + completedDistance
                    val isDriverMoving = distanceCovered > 5 // السائق اتحرك أكتر من 5 متر

                    if (isDriverMoving && originalTotalDistance > 0) {
                        val ratio = completedDistance / originalTotalDistance
                        val dynamicRemainingDistance = maxOf(0.0, originalTotalDistance - completedDistance)
                        val dynamicRemainingTime = maxOf(0L, ((1 - ratio) * (totalTime + completedTime)).toLong())

                        updateTripDynamicProgress(tripId, dynamicRemainingDistance, dynamicRemainingTime)
                    }
                }
            }
        } ?: run {
            updateTripProgress(tripId, 0.0, 0L)
            updateTripDynamicProgress(tripId, 0.0, 0L)
            onProgressUpdate?.invoke(0.0, 0L)
        }
    }

    private fun updateInstructionsStatus(
        instructions: List<Instruction>,
        location: LatLng?
    ): List<Instruction> {
        if (location == null || instructions.isEmpty()) return instructions

        return instructions.map { instruction ->
            if (!instruction.exited && instruction.latitude != null && instruction.longitude != null &&
                calculateDistance(location, LatLng(instruction.latitude, instruction.longitude)) < 20
            ) {
                instruction.copy(exited = true)
            } else {
                instruction
            }
        }
    }

    private fun calculateCurrentAndNextInstructions(
        instructions: List<Instruction>,
        driverLocation: LatLng?
    ): Pair<Instruction?, Instruction?> {
        if (instructions.isEmpty()) return null to null

        val currentIndex = instructions.indexOfFirst { !it.exited }

        return when {
            currentIndex < 0 -> null to null
            currentIndex == instructions.lastIndex -> instructions[currentIndex] to null
            else -> instructions[currentIndex] to instructions[currentIndex + 1]
        }
    }

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

    private fun listenToDriverLocation(
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
                        onUpdate(null)
                        return@addSnapshotListener
                    }

                    val doc = snapshot?.documents?.firstOrNull()
                    val locationMap = doc?.get("driverLocation") as? Map<*, *>
                    val lat = (locationMap?.get("latitude") as? Number)?.toDouble()
                    val lng = (locationMap?.get("longitude") as? Number)?.toDouble()

                    if (lat != null && lng != null) {
                        onUpdate(LatLng(lat, lng))
                    } else {
                        onUpdate(null)
                    }
                }
        } catch (e: Exception) {
            Log.e("DRIVER_LOCATION", "Listener setup failed: ${e.message}")
            onUpdate(null)
            null
        }
    }

    private fun listenToInstructionsByTripId(
        tripId: String,
        onUpdate: (List<Instruction>?) -> Unit
    ): ListenerRegistration {
        return FirebaseFirestore.getInstance()
            .collection("trips")
            .whereEqualTo("_id", tripId)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("INSTRUCTION", "Listen failed: ${error.message}")
                    onUpdate(null)
                    return@addSnapshotListener
                }

                val doc = querySnapshot?.documents?.firstOrNull()
                if (doc == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val instructionsList = doc.get("instructions") as? List<Map<String, Any>>
                val instructionObjects = instructionsList?.mapNotNull { instructionMap ->
                    try {
                        Instruction(
                            distance = (instructionMap["distance"] as? Number)?.toDouble() ?: 0.0,
                            text = instructionMap["text"] as? String ?: "",
                            street_name = instructionMap["street_name"] as? String ?: "",
                            exit_number = (instructionMap["exit_number"] as? Number)?.toInt() ?: 0,
                            exited = instructionMap["exited"] as? Boolean ?: false,
                            last_heading = (instructionMap["last_heading"] as? Number)?.toDouble() ?: 0.0,
                            sign = (instructionMap["sign"] as? Number)?.toInt() ?: 0,
                            street_destination = instructionMap["street_destination"] as? String ?: "",
                            time = (instructionMap["time"] as? Number)?.toLong() ?: 0L,
                            turn_angle = (instructionMap["turn_angle"] as? Number)?.toDouble() ?: 0.0,
                            latitude = (instructionMap["latitude"] as? Number)?.toDouble(),
                            longitude = (instructionMap["longitude"] as? Number)?.toDouble()
                        )
                    } catch (e: Exception) {
                        Log.e("INSTRUCTION", "Instruction parse error: ${e.message}")
                        null
                    }
                } ?: emptyList()

                onUpdate(instructionObjects)
            }
    }

    private fun updateTripProgress(tripId: String, remainingDistance: Double, remainingTime: Long) {
        FirebaseFirestore.getInstance()
            .collection("trips")
            .whereEqualTo("_id", tripId)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.firstOrNull()?.reference?.update(
                    mapOf(
                        "remaining_distance_dynamic" to remainingDistance,
                        "remaining_time_dynamic" to remainingTime
                    )
                )?.addOnFailureListener { e ->
                    Log.e("UPDATE_PROGRESS", "Failed to update dynamic fields: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UPDATE_PROGRESS", "Failed to fetch trip: ${e.message}")
            }
    }

    // ✅ دالة التحديث الديناميكي للوقت والمسافة
    private fun updateTripDynamicProgress(tripId: String, remainingDistance: Double, remainingTime: Long) {
        FirebaseFirestore.getInstance()
            .collection("trips")
            .whereEqualTo("_id", tripId)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.firstOrNull()?.reference?.update(
                    mapOf(
                        "remaining_distance_dynamic" to remainingDistance,
                        "remaining_time_dynamic" to remainingTime
                    )
                )?.addOnFailureListener { e ->
                    Log.e("DYNAMIC_PROGRESS", "Failed to update dynamic: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("DYNAMIC_PROGRESS", "Failed to fetch trip for dynamic: ${e.message}")
            }
    }

    private fun decodePolyline(polyline: String): List<LatLng> {
        // Implement polyline decoding if needed
        return emptyList()
    }
}
