package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import com.example.capital_taxi.data.repository.graphhopper_response.Path
import com.example.capital_taxi.data.utils.DirectionsPrefs
import com.example.capital_taxi.domain.DirectionsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun captainToPassenger(navController: NavController,
                       onTripStarted:()->Unit,
driverId:String?=null ,
                       rating: String,
                       userId2: String?=null,

                       mapchangetoInPrograss:()->Unit,
                       context: Context, tripId: String,passengerName:String) {

    var distance by rememberSaveable { mutableStateOf<Double?>(null) }
    var duration by rememberSaveable { mutableStateOf<Double?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    // Firebase Firestore
    val db = FirebaseFirestore.getInstance()
    val tripRef = db.collection("trips")
    var remainingDistance by remember { mutableStateOf(0.0) }
    var remainingTime by remember { mutableStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val navigationBox = remember(tripId) {
        Top_Navigation_Box(tripId).apply {
            startTracking { distance, time ->
                remainingDistance = distance
                remainingTime = time
                isLoading = false
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            navigationBox.stopTracking()
        }
    }

    // الاستماع للتحديثات بناءً على TripId
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
                        distance = it["remaining_distance_dynamic"] as? Double
                        val durationValue = it["remaining_time_dynamic"] as? Long ?: 0L
                        duration = durationValue.toDouble() / 1000.0  // تحويل من milliseconds إلى seconds

                        Log.d("Firebase", "Data updated: Distance = $distance, Duration = $duration")
                    }
                }
            }
    }

    // تحويل المسافة إلى كيلومترات أو متر
    val formattedDistance = distance?.let {
        if (it >= 1000) {
            "${String.format("%.2f", it / 1000)} km" // تحويل للمتر إلى كيلومتر
        } else {
            "${it.toInt()}  m" // إبقاءها متر إذا كانت أقل من 1000 متر
        }
    } ?: "Loading..." // عرض Loading إذا كانت المسافة null

    // تحويل الوقت إلى ساعات ودقائق
    val formattedDuration = duration?.let {
        val hours = (it / 3600).toInt() // حساب الساعات
        val minutes = ((it % 3600) / 60).toInt() // حساب الدقائق
        if (hours > 0) {
            "$hours hour${if (hours > 1) "s" else ""} ${minutes} min"
        } else {
            "${minutes} min"
        }
    } ?: "Loading..." // عرض Loading إذا كانت المدة null
    fun formatDuration(millis: Long): String {
        val minutes = (millis / 60000).toInt()
        val seconds = (millis % 60000 / 1000).toInt()
        return String.format("%02d:%02d", minutes, seconds)
    }

      fun formatDistance(meters: Double): String {
        return if (meters >= 1000) {
            "${String.format("%.1f", meters / 1000)} km"
        } else {
            "${meters.toInt()} m"
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Loader: لو لسه البيانات ما وصلتش
        if (duration == null || distance == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
            DirectionsPrefs.clear(context)

        } else {
            Top_Navigation_Box(tripId)
            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                sheetContent = {


                        TripDetailsForDriver(
                            navController,

                            onTripStarted = onTripStarted,
                            tripId = tripId,

                            mapchangetoInPrograss = mapchangetoInPrograss,
                            chatId = tripId,
                            userId = driverId,
                            menu_close = { bottomSheetState.hide() },
                            passengerName = passengerName,
                            userId2 =userId2 ,
                            rating = rating,

                        )


                },
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        // عند الضغط على الأيقونة، نعرض الـ Bottom Sheet
                                        scope.launch { bottomSheetState.show() }
                                    },
                                    contentDescription = null,
                                    painter = painterResource(R.drawable.baseline_segment_24),
                                    tint = Color.Black
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = stringResource(R.string.meet_passenger),
                                    fontSize = responsiveTextSize(0.06f, 14.sp, 18.sp),
                                    fontFamily = CustomFontFamily,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f), // يوزع المساحة بالتساوي بين الأعمدة
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Time Left: \n${formattedDuration}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier.weight(1f), // نفس الشيء هنا
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Left Distance: \n ${formattedDistance}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
                }
            }
        }


