package com.example.capital_taxi.domain.driver.model
import android.os.Handler
import android.os.Looper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay
import android.util.Log

data class Instruction(
    val text: String = "",
    val distance: Double = 0.0,
    val time: Long = 0L,
    val sign: Int = 0,
    val street_name: String = "",
    val street_destination: String = "",
    val exit_number: Int = 0,
    val exited: Boolean = false,
    val interval: List<Int> = emptyList(),
    val last_heading: Double = 0.0,
    val turn_angle: Double = 0.0
)
fun getInstructionsFromFirebase(tripId: String, onResult: (List<Instruction>?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("trips")
        .whereEqualTo("_id", tripId)
        .get()
        .addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val doc = snapshot.documents.firstOrNull()
                val instructionsData = doc?.get("instructions") as? List<Map<String, Any>>

                val instructions = instructionsData?.map { map ->
                    Instruction(
                        text = map["text"] as? String ?: "",
                        distance = (map["distance"] as? Number)?.toDouble() ?: 0.0,
                        time = (map["time"] as? Number)?.toLong() ?: 0L,
                        sign = (map["sign"] as? Number)?.toInt() ?: 0,
                        street_name = map["street_name"] as? String ?: "",
                        street_destination = map["street_destination"] as? String ?: "",
                        exit_number = (map["exit_number"] as? Number)?.toInt() ?: 0,
                        exited = map["exited"] as? Boolean ?: false,
                        interval = (map["interval"] as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList(),
                        last_heading = (map["last_heading"] as? Number)?.toDouble() ?: 0.0,
                        turn_angle = (map["turn_angle"] as? Number)?.toDouble() ?: 0.0
                    )
                }

                onResult(instructions)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener {
            Log.e("Firebase", "Error fetching instructions: ${it.message}")
            onResult(null)
        }
}
