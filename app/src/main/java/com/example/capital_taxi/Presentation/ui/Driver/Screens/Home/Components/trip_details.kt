package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import androidx.compose.runtime.Composable
import android.Manifest
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.assignDriver
import com.example.capital_taxi.domain.shared.TripViewModel
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder

import android.os.Looper
import androidx.compose.runtime.State
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


import android.location.Location
import android.media.MediaPlayer
import androidx.annotation.OptIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.TripViewModel4
import com.example.capital_taxi.domain.driver.model.Instruction
import com.example.capital_taxi.domain.driver.model.getInstructionsFromFirebase
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.util.Locale

@OptIn(UnstableApi::class)
@Composable
fun TripDetailsCard(
    light: Boolean,
    trip: Trip,
    availableTrips: List<Trip>,
    tripViewModel: TripViewModel,
    onTripAccepted: () -> Unit,
    onTripCancelled: () -> Unit
) {
    if (availableTrips.isEmpty()) return
    val tripViewModel2: TripViewModel = viewModel()
    val StatusTripViewModel: StatusTripViewModel = viewModel()
    val driverlocation: driverlocation = viewModel()
    val trip = availableTrips.first()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // متغيرات لإدارة الرسالة الوامضة
    var isMessageBlinking by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf<String?>(null) }

     var previousMessage by remember { mutableStateOf<String?>(null) }
    // الدالة لتشغيل الصوت

    fun playNotificationSoundSlow(context: Context) {
        val exoPlayer = ExoPlayer.Builder(context).build()

        val rawUri = RawResourceDataSource.buildRawResourceUri(R.raw.notification_sound)
        val mediaItem = MediaItem.fromUri(rawUri)
        exoPlayer.setMediaItem(mediaItem)

        exoPlayer.setPlaybackParameters(PlaybackParameters(0.7f))
        exoPlayer.prepare()
        exoPlayer.play()

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    exoPlayer.release()
                }
            }
        })
    }

    val scrollState = rememberScrollState()





    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            // تأثير الوميض للخلفية
            var blinkState by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                while (true) {
                    blinkState = !blinkState
                    delay(700L)
                }
            }

            if (light) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(.7f)
                        .fillMaxWidth()
                        .background(
                            color = if (blinkState) colorResource(R.color.secondary_color) else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState) // ✅ أضف هذا السطر للتمرير
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

//                playNotificationSoundSlow(
//                    context =context
//                )
                isMessageBlinking = true
                // Trip Type and Price
                Text(
                    text = "Comfort",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
                Text(
                    text = "${trip.fare} EGP",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Comprehensive Graphical Service",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                // Progress Bar
                var progress by remember { mutableStateOf(1f) }
                LaunchedEffect(Unit) {
                    val totalDuration = 30000L
                    val frameDuration = 16L
                    while (progress > 0f) {
                        progress -= frameDuration.toFloat() / totalDuration
                        delay(frameDuration)
                    }
                    progress = 0f
                }
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = colorResource(R.color.primary_color),
                    trackColor = Color(0XFFF2F2F2)
                )

                // Rating & Passenger Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(R.drawable.person),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4.5", // هنا يمكن لاحقًا استخدام تقييم الراكب إذا كان متاحًا
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }

                // Ride Details
                RidePointDetails(
                    Locationicon = R.drawable.circle,
                    Destinationicon = R.drawable.travel,
                    LocationText = trip.origin,
                    DestinationText = trip.destination,

                    distance2 = "${trip.distanceInKm} km",
                    isDestance = true,
                    onClick = { }
                )

                val context = LocalContext.current
                val sharedPreferences =
                    context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                val authToken = sharedPreferences.getString("driver_token", "") ?: ""
                val driver_id = sharedPreferences.getString("driver_id", "") ?: ""


                val tripViewModel3: TripViewModel4 = viewModel()
                fun getCoordinatesFromAddress(
                    context: Context,
                    address: String,
                    onSuccess: (GeoPoint) -> Unit,
                    onError: (String) -> Unit
                ) {
                    val geocoder = Geocoder(context, Locale.getDefault())

                    try {
                        val addresses = geocoder.getFromLocationName(address, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val location = addresses[0]
                            val geoPoint =
                                GeoPoint(location.latitude, location.longitude)
                            onSuccess(geoPoint)
                        } else {
                            onError("🚫 لم يتم العثور على الإحداثيات لهذا العنوان")
                        }
                    } catch (e: IOException) {
                        onError("❌ خطأ في تحويل العنوان إلى إحداثيات: ${e.message}")
                    }
                }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(2000) // تحديث كل ثانيتين

                        trip._id.let {
                            tripViewModel.getTripOriginById(it, onSuccess = { originAddress ->
                                getCoordinatesFromAddress(
                                    context,
                                    originAddress,
                                    onSuccess = { geoPoint ->
                                        tripViewModel3.updateTripLocation(geoPoint) // ✅ تحديث الحالة بعد التحويل


                                    },
                                    onError = { errorMessage ->
                                        Log.e(
                                            "Origin",
                                            "❌ خطأ في تحويل العنوان إلى إحداثيات: $errorMessage"
                                        )
                                    })
                            }, onError = { errorMessage ->
                                Log.e("Origin", "❌ خطأ في جلب الحالة: $errorMessage")
                            })
                        }
                    }
                }





                val instructionState = remember { mutableStateOf<Instruction?>(null) }

                Button(
                    onClick = {
                        val fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(context)
                        val permissionGranted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (permissionGranted) {
                            val locationRequest = LocationRequest.create().apply {
                                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                                interval = 10000 // 10 seconds
                                fastestInterval = 5000 // 5 seconds
                            }
                        }

                        // تخصيص السائق للرحلة
                        assignDriver(
                            tripId = trip._id,
                            driverId = driver_id,
                            token = authToken,
                            coroutineScope = coroutineScope,
                            onSuccess = {


                                StatusTripViewModel.updateTripId(trip._id, "accepted")
                                Log.d("TripDetailsCard", "✅ Driver assigned successfully")
                            },
                            onError = { errorMessage ->
                                Log.e("TripDetailsCard", "❌ Error assigning driver: $errorMessage")
                            }
                        )
                        getInstructionsFromFirebase(trip._id!!) { instructions ->
                            if (instructions != null) {
                                instructions.forEach {
                                    Log.d("INSTRUCTION", it.text)
                                }

                                // مثلاً: خزن أول Instruction في State لعرضها في الواجهة
                                instructionState.value = instructions.firstOrNull()
                            } else {
                                Log.e("INSTRUCTION", "Failed to get instructions")
                            }
                        }

                        // قبول الرحلة
                        tripViewModel.acceptTrip(
                            trip._id,
                            onSuccess = {
                                onTripAccepted()
                                startUpdatingDriverLocation(trip._id, driver_id, context)
                            },
                            onError = { Log.e("TripDetailsCard", "❌ Error accepting trip: $it") }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Accept Trip", color = Color.White, fontSize = 16.sp)
                }


                // Cancel Trip Button
                // Inside the TripDetailsCard Composable
                Button(
                    onClick = {
                        onTripCancelled()
                        // Cancel the trip for this specific driver
                        tripViewModel.cancelTripForDriver(trip._id) // Call the method in ViewModel
                        Log.d("TripDetailsCard", "Trip canceled for driver.")

                        // Additional logic (like updating the UI or notifying other components) can go here
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    border = BorderStroke(1.dp, colorResource(R.color.primary_color)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Cancel Trip",
                        color = colorResource(R.color.primary_color),
                        fontSize = 16.sp
                    )
                }


            }
            notificationMessage?.let { message ->
                Text(
                    text = message,
                    color = if (isMessageBlinking) Color.Red else Color.Black,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun startUpdatingDriverLocation(tripId: String, driverId: String, context: Context) {
    val db = FirebaseFirestore.getInstance()
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 2000 // ⏳ تحديث كل ثانيتين
        fastestInterval = 2000
    }

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Log.e("LocationUpdate", "❌ لا يوجد إذن للوصول إلى الموقع")
        return
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                val driverLocation = mapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                    "timestamp" to System.currentTimeMillis()
                )

                // ✅ البحث عن الرحلة بواسطة `_id`
                db.collection("trips")
                    .whereEqualTo("_id", tripId)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            for (document in documents) {
                                // ✅ تحديث `driver` لأول مرة إذا كان `null`
                                val tripRef = document.reference
                                tripRef.update("driver", driverId)
                                    .addOnSuccessListener { Log.d("TripUpdate", "✅ تم تعيين السائق بنجاح!") }
                                    .addOnFailureListener { e -> Log.e("TripUpdate", "❌ فشل في تعيين السائق: ${e.message}") }

                                // ✅ تحديث `driverLocation` كل ثانيتين
                                tripRef.update("driverLocation", driverLocation)
                                    .addOnSuccessListener { Log.d("TripUpdate", "✅ تم تحديث موقع السائق داخل الرحلة!") }
                                    .addOnFailureListener { e -> Log.e("TripUpdate", "❌ فشل في تحديث الموقع: ${e.message}") }
                            }
                        } else {
                            Log.e("TripUpdate", "❌ لم يتم العثور على الرحلة!")
                        }
                    }
                    .addOnFailureListener { e -> Log.e("TripUpdate", "❌ فشل في البحث عن الرحلة: ${e.message}") }
            }
        }
    }, Looper.getMainLooper())
}



class driverlocation : ViewModel() {
    // متغير لتخزين الموقع
    private val _driverLocation = mutableStateOf<Location?>(null)
    val driverLocation: State<Location?> = _driverLocation

    fun updateDriverLocation(location: Location) {
        _driverLocation.value = location
    }
}

class TripViewModel : ViewModel() {
    var tripLocation = mutableStateOf<com.example.capital_taxi.domain.Location?>(null)
        private set

    fun updateTripLocation(location: com.example.capital_taxi.domain.Location) {
        tripLocation.value = location
    }
}

@Composable
fun RidePointDetails(
    distance1: String? = null,
    distance2: String? = null,
    isDestance: Boolean,
    Locationicon: Int,
    Destinationicon: Int,
    onClick: () -> Unit,
    LocationText: String,
    DestinationText: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Location
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(Locationicon),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                distance1?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Text(
                    text = LocationText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.Black
                )
            }
        }

        // Destination
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(Destinationicon),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                distance2?.let {
                    Text(
                        text = "${it.take(5)} km",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primary_color)
                    )
                }
                    Text(
                    text = DestinationText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.Black
                )
            }
        }
    }
}


// ✅ تعديل الـ ViewModel لإضافة `updateTripStatus`
class StatusTripViewModel : ViewModel() {

    private val _currentTripId = MutableLiveData<String?>()
    val currentTripId: LiveData<String?> = _currentTripId

    private val _tripStatus = MutableLiveData<String>()
    val tripStatus: LiveData<String> = _tripStatus

    fun updateTripId(tripId: String, status: String) {
        _currentTripId.value = tripId
        _tripStatus.value = status
    }

    fun updateTripStatus(newStatus: String) {
        _tripStatus.value = newStatus
    }
}
