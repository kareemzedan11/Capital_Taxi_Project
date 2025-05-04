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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@Composable
fun Top_Navigation_Box(tripId: String) {

    var instruction by remember { mutableStateOf<Instruction?>(null) }

    // جلب البيانات عند تحديث الـ tripId أو عند بداية تشغيل الـ Composable
    DisposableEffect(tripId) {
        val listener = listenToInstructionsByTripId(tripId) { instructions ->
            if (instructions != null && instructions.isNotEmpty()) {
                instruction = instructions[0] // أو أي index مناسب
                Log.d("INSTRUCTION", "Live updated: ${instructions.size} instructions")
            } else {
                Log.e("INSTRUCTION", "No valid instructions found")
            }
        }

        // تنظيف الليسنر عند إلغاء Composable أو تغيير tripId
        onDispose {
            listener.remove()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xffce8907)),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Distance column
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .fillMaxHeight()
                        .background(color = Color(0xfff1a104)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Distance",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (instruction != null) "${instruction!!.distance.toInt()} m" else "-- m",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Direction column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 5.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = instruction?.text ?: "Waiting...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = instruction?.street_name ?: "No street info",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}


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
                            turn_angle = turnAngle
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

