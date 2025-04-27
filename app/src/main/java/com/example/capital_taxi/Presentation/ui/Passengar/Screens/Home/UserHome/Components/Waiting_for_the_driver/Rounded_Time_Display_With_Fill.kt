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

    LaunchedEffect(tripId) {
        isDataLoading = true
        try {
            val tripDoc = FirebaseFirestore.getInstance()
                .collection("trips")
                .whereEqualTo("_id", tripId)
                .get()
                .await()

            if (!tripDoc.isEmpty) {
                val document = tripDoc.documents.first()
                Time = document.get("time") as? Long ?: 0
                Log.d("Firestore", "تم جلب الوقت: $Time مللي ثانية")
            } else {
                Log.e("Firestore", "لم يتم العثور على الرحلة!")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "خطأ في جلب البيانات: ${e.message}")
        }
        isDataLoading = false
    }


    fun formatMillisecondsWithSeconds(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        return if (hours > 0) {
            "${hours}h ${minutes % 60}m  "  // مثال: 1h 48m 22s
        } else if (minutes > 0) {
            "${minutes}m "  // مثال: 108m 22s
        } else {
            "${seconds}s"  // مثال: 6502s
        }
    }
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
                text = formatMillisecondsWithSeconds(Time),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,  // قلل الحجم إذا كان النص لا يظهر
                    color = Color.Black,  // غيِّر اللون إذا كان غير مرئي
                    fontWeight = FontWeight.Bold
                )
            )}
        }
    }
}
