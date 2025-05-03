package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

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
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DirectionsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@Composable
fun RoundedTimeDisplayWithFill(tripId:String) {

    var Time by remember { mutableStateOf(0L) }
    var isDataLoading by remember { mutableStateOf(true) }


    val progress = remember { Animatable(0f) }
var generalColor= colorResource(R.color.primary_color)
    val db = FirebaseFirestore.getInstance()
    val tripRef = db.collection("trips")
    LaunchedEffect(tripId) {
        tripRef.whereEqualTo("_id", tripId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firebase", "Error fetching trip data: ${e.message}")
                    return@addSnapshotListener
                }

                // تحقق من وجود البيانات
                if (snapshot != null && !snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    val data = document.data

                    // التأكد من أن البيانات موجودة وتحديث المسافة والوقت
                    data?.let {
                        val durationValue = it["time"] as? Long ?: 0L
                        Time = (durationValue.toDouble() / 1000.0).toLong()  // تحويل من milliseconds إلى seconds
                        isDataLoading = false
                    }
                }
            }
    }


    // تحويل الوقت إلى ساعات ودقائق
    val formattedDuration = Time?.let {
        val hours = (it / 3600).toInt() // حساب الساعات
        val minutes = ((it % 3600) / 60).toInt() // حساب الدقائق
        if (hours > 0) {
            "$hours hour${if (hours > 1) "s" else ""} ${minutes} min"
        } else {
            "${minutes} min"
        }
    } ?: "Loading..." // عرض Loading إذا كانت المدة null
    Box(
        modifier = Modifier
            .size(width = 70.dp, height = 40.dp)
            .background( colorResource(R.color.secondary_color)) // Background color
    ) {
        // Animated Fill
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = generalColor,
                topLeft = Offset(0f, 0f),
                size = Size(size.width * progress.value, size.height),
                cornerRadius = CornerRadius(25.dp.toPx(), 25.dp.toPx())
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) { if (isDataLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {

            Text(
                text =formattedDuration,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,  // قلل الحجم إذا كان النص لا يظهر
                    color = Color.Black,  // غيِّر اللون إذا كان غير مرئي
                    fontWeight = FontWeight.Bold
                )
            )}
        }
    }
}
