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

@Composable
fun Top_Navigation_Box(tripId: String) {

    var instruction by remember { mutableStateOf<Instruction?>(null) }

    // جلب البيانات عند تحديث الـ tripId أو عند بداية تشغيل الـ Composable
    LaunchedEffect(tripId) {
        fetchInstructionsWithRetry(tripId) { instructions ->
            if (instructions != null && instructions.isNotEmpty()) {
                // نحدث الـ instruction بالقيمة الأولى من القائمة
                instruction = instructions[0]
                Log.d("INSTRUCTION", "Fetched ${instructions.size} instructions")
            } else {
                Log.e("INSTRUCTION", "Failed to fetch after retries")
            }
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

fun getInstructionsByTripId(tripId: String, onResult: (List<Instruction>?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("trips") // ← غيّر دي لاسم الكولكشن بتاعك
        .whereEqualTo("_id", tripId)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                val instructionsList = doc["instructions"] as? List<Map<String, Any>>

                if (instructionsList != null) {
                    val instructionObjects = instructionsList.mapNotNull {
                        val distance = (it["distance"] as? Number)?.toDouble() ?: return@mapNotNull null
                        val text = it["text"] as? String ?: return@mapNotNull null
                        val streetName = it["street_name"] as? String ?: ""

                        Instruction(
                            distance = distance,
                            text = text,
                            street_name = streetName
                        )
                    }

                    // إذا كانت الـ instructionObjects فارغة، هذا معناه إن فيه بيانات ناقصة أو مش كاملة
                    if (instructionObjects.isEmpty()) {
                        Log.e("INSTRUCTION", "Data is incomplete or invalid!")
                    }

                    onResult(instructionObjects.takeIf { it.isNotEmpty() })
                } else {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener {
            Log.e("INSTRUCTION", "Failed to get instructions: ${it.message}")
            onResult(null)
        }
}

fun fetchInstructionsWithRetry(
    tripId: String,
    maxAttempts: Int = 5,
    delayMillis: Long = 1000,
    onResult: (List<Instruction>?) -> Unit
) {
    var attempt = 0

    fun tryFetch() {
        Log.d("INSTRUCTION", "Attempt ${attempt + 1}")
        getInstructionsByTripId(tripId) { instructions ->
            if (instructions != null) {
                Log.d("INSTRUCTION", "Success on attempt ${attempt + 1}")
                onResult(instructions)
            } else {
                Log.e("INSTRUCTION", "Failed to get instructions on attempt ${attempt + 1}")
                attempt++
                if (attempt < maxAttempts) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        tryFetch()
                    }, delayMillis)
                } else {
                    Log.e("INSTRUCTION", "Giving up after $maxAttempts attempts")
                    onResult(null)
                }
            }
        }
    }

    tryFetch()
}
