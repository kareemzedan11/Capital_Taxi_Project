package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home

import TopBar
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Driver.Components.DriverControls
import com.example.capital_taxi.Presentation.ui.Driver.Components.InProgressMap
import com.example.capital_taxi.Presentation.ui.Driver.Components.MapStateViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.DriverNavigationDrawer
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.TripViewModel4
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.StartTrip
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.TripArrivedCard2
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.TripDetailsCard
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.captainToPassenger
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.dataTripViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverHomeScreenContent
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverlocation
import com.example.capital_taxi.Presentation.ui.Passengar.Components.StateTripViewModel
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.DirectionsApi
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationDataStore
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationViewModel5
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.ResultWrapper
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.TrackDriverScreen
import com.example.capital_taxi.domain.DirectionsViewModel
import com.example.capital_taxi.domain.Location
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.driver.model.acceptTripViewModel
import com.example.capital_taxi.domain.fetchTripDirections
import com.example.capital_taxi.domain.shared.TripViewModel
import com.example.capital_taxi.domain.shared.saveDriverLocationToRealtimeDatabase
import com.example.capital_taxi.domain.storedPoints
import com.example.capital_taxi.utils.DirectionsUpdater
import com.example.myapplication.DriverMapView
import com.example.myapplication.interpolateLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("UnrememberedMutableState")
@Composable
fun driverHomeScreen(navController: NavController) {
    val stateTripViewModel: StateTripViewModel = viewModel()
    val tripState by stateTripViewModel.uiState

    val scope = rememberCoroutineScope()
    val tripViewModel = remember { TripViewModel() }
    var availableTrips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var startPoint = remember { mutableStateOf<GeoPoint?>(null) }
    var endPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("driver_token", "") ?: ""
    val driver_id = sharedPreferences.getString("driver_id", "") ?: ""
    val directionsViewModel: DirectionsViewModel = viewModel()
    val DriverViewModel: DriverViewModel = viewModel()



    val viewmodel: driverlocation = viewModel()
    var tripListener by remember { mutableStateOf<ListenerRegistration?>(null) }

    var passengerID by remember { mutableStateOf<String?>(null) }
    var passengerName by remember { mutableStateOf<String?>(null) }
    var destination by remember { mutableStateOf<String?>(null) }
    var fare by remember { mutableStateOf<Double?>(null) }
    var distance by remember { mutableStateOf<Double?>(null) }
    var tripId by remember { mutableStateOf<String?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val firestore = FirebaseFirestore.getInstance()
    val driverId = driver_id
    var driverLocation = viewmodel.driverLocation.value
    var driverLocationState2 by remember { mutableStateOf<GeoPoint?>(null) }
    var driverLocationState by remember { mutableStateOf<GeoPoint?>(null) }
    var previousLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var carBearing by remember { mutableStateOf(0f) }
    val locationDataStore = LocationDataStore(context)
    val accepttrip: acceptTripViewModel = viewModel()
    val accepttripViewModel by accepttrip.isTripAccepted
    val startTrip by accepttrip.isTripStarted
    val EndTrip by accepttrip.isTripCompleted
    var showCancellationDialog by remember { mutableStateOf(false) }
    var rawLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var smoothedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var passengerLocation2 by remember { mutableStateOf(GeoPoint(30.0444, 31.2357)) }
    var rating by remember { mutableStateOf<Double?>(null) }
    var showNavigationButton by remember { mutableStateOf(false) }
    var originPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var destinationPoint by remember { mutableStateOf<GeoPoint?>(null) }
    val sharedPref = context.getSharedPreferences("trip_prefs", Context.MODE_PRIVATE)
    var tripStarted by remember { mutableStateOf(sharedPref.getBoolean("trip_started", false)) }
    // Handle trip state changes
    LaunchedEffect(tripState) {
        when {
            tripState.isCancelled -> {
                showCancellationDialog = true
            }
            tripState.isEnd -> {
                // Trip completed logic
            }
        }
    }
    LaunchedEffect(Unit) {
        Log.d("TripCheck", "🚀 LaunchedEffect started")

        val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
        val activeTripId = sharedPreferences.getString("active_trip_id", null)
           passengerID = sharedPreferences.getString("passenger_id", null)

        Log.d("TripCheck", "🧠 activeTripId = $activeTripId")

        if (activeTripId == null) {
            Log.d("TripCheck", "⚠️ No active trip found in SharedPreferences")
            return@LaunchedEffect
        }
        val sharedPref = context.getSharedPreferences("trip_prefs", Context.MODE_PRIVATE)
        val fareStr = sharedPref.getString("fare", "0.0") ?: "0.0"
          fare = fareStr.toDoubleOrNull() ?: 0.0

        // خزن القيمة في متغير tripId اللي بتستخدمه في Compose
        tripId = activeTripId

        try {
            val querySnapshot = withContext(Dispatchers.IO) {
                Log.d("TripCheck", "🔎 Fetching trip from Firestore")
                FirebaseFirestore.getInstance().collection("trips")
                    .whereEqualTo("_id", activeTripId)
                    .get()
                    .await()
            }

            val document = querySnapshot.documents.firstOrNull()
            Log.d("TripCheck", "📄 Document fetched: ${document?.data}")

            if (document != null) {
                val status = document.getString("status") ?: ""
                Log.d("TripCheck", "📌 Trip status = $status")

                if (status in listOf("accepted", "InProgress", "Started")) {
                    Log.d("TripCheck", "✅ Active trip detected, updating state")

                    // تحويل الحالة حسب التدرج
                    val updatedStatus = when (status) {
                        "accepted" -> {
                            stateTripViewModel.setStart(true)
                            "Started"

                        }
                        "InProgress" -> {
                            stateTripViewModel.setStart(true)
                            "InProgress"
                        }
                        "Started" -> {
                            stateTripViewModel.setStart(true)
                            "InProgress"
                        }

                        else -> status // لو هي Started تفضل زي ما هي
                    }
                    Log.d("TripCheck", "📌 Updated status: $updatedStatus")
                    stateTripViewModel.updateTripStatus(updatedStatus)
                }

                else {
                    Log.d("TripCheck", "🛑 Trip ended or cancelled, clearing active trip ID")
                    sharedPreferences.edit().remove("active_trip_id").apply()
                    stateTripViewModel.resetAll()
                    tripId = null // برضه نزّل المتغير لو الرحلة خلصت
                }
            } else {
                Log.d("TripCheck", "❌ No document found with this trip ID")
                sharedPreferences.edit().remove("active_trip_id").apply()
                stateTripViewModel.resetAll()
                tripId = null
            }
        } catch (e: Exception) {
            Log.e("TripCheck", "🔥 Exception while fetching trip: ${e.message}", e)
            sharedPreferences.edit().remove("active_trip_id").apply()
            stateTripViewModel.resetAll()
            tripId = null
        }
    }

    // Update map and get directions periodically
    LaunchedEffect(tripState.isAccepted, startPoint.value, endPoint.value) {
        while (tripState.isAccepted && startPoint.value != null && endPoint.value != null) {
            delay(2000)
            val origin = Location(startPoint.value!!.latitude, startPoint.value!!.longitude)
            val destination = Location(endPoint.value!!.latitude, endPoint.value!!.longitude)

            val token = sharedPreferences.getString("driver-token", null)
            if (token != null) {
                fetchTripDirections(
                    token = token,
                    origin = origin,
                    destination = destination,
                    directionsViewModel = directionsViewModel,
                    onSuccess = { directionsResponse ->
                        Log.d("TripDirections", "Successfully fetched directions: $directionsResponse")
                    },
                    onError = { errorMessage ->
                        Log.e("TripDirections", "Error fetching directions: $errorMessage")
                    }
                )
            }
        }
    }
    val directionsUpdater = remember(tripId) {
        tripId?.let {
            DirectionsUpdater(
                tripId = it,
                graphHopperApiKey = "c69abe50-60d2-43bc-82b1-81cbdcebeddc"
            )
        }
    }
    var originStr by remember { mutableStateOf<String?>(null) }
    var destinationStr by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tripState.isTripBegin, tripState.inProgress, directionsUpdater, driverLocationState, passengerLocation2, originStr, destinationStr) {
        Log.d("TripLog", "LaunchedEffect triggered")
        Log.d("TripLog", "isTripBegin: ${tripState.isTripBegin}, inProgress: ${tripState.inProgress}")

        val shouldUseLiveTracking = tripState.inProgress
        val driverLocation = driverLocationState
        val passengerLocation = passengerLocation2

        if (directionsUpdater != null) {
            if (shouldUseLiveTracking && driverLocation != null && passengerLocation != null) {
                val origin = "${driverLocation.latitude},${driverLocation.longitude}"
                val destination = "${passengerLocation.latitude},${passengerLocation.longitude}"
                Log.d("TripLog", "Live tracking: driver to passenger")
                directionsUpdater.setRoute(originStr = origin, destinationStr = destination)
                directionsUpdater.start()
            } else if (tripState.isTripBegin && originStr != null && destinationStr != null) {
                Log.d("TripLog", "Trip begin: using static origin/destination")
                directionsUpdater.setRoute(originStr = originStr!!, destinationStr = destinationStr!!)
                directionsUpdater.start()
            } else {
                Log.d("TripLog", "Stopping directionsUpdater")
                directionsUpdater.stop()
            }
        }
    }


    DisposableEffect(tripId) {
        var documentListener: ListenerRegistration? = null

        if (tripId != null) {
            val query = firestore.collection("trips")
                .whereEqualTo("_id", tripId)

            val registration = query.addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("TripStatus", "Error listening to trip", error)
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    Log.d("TripData", "Document found: ${document.id}")

                    val originMap = document.get("originMap") as? Map<String, Any>
                    Log.d("TripData", "originMap: $originMap")

                    val originLat = originMap?.get("lat") as? Double
                    val originLng = originMap?.get("lng") as? Double
                    Log.d("TripData", "originLat: $originLat, originLng: $originLng")

                    val destinationMap = document.get("destinationMap") as? Map<String, Any>
                    Log.d("TripData", "destinationMap: $destinationMap")

                    val destinationLat = destinationMap?.get("lat") as? Double
                    val destinationLng = destinationMap?.get("lng") as? Double
                    Log.d("TripData", "destinationLat: $destinationLat, destinationLng: $destinationLng")

                    passengerID = document.get("userId") as? String
                    Log.d("TripData", "passengerID: $passengerID")

                    if (originLat != null && originLng != null) {
                        passengerLocation2 = GeoPoint(originLat, originLng)
                        originPoint = GeoPoint(originLat, originLng)
                        Log.d("TripData", "passengerLocation2 set: $passengerLocation2")
                    }

                    if (destinationLat != null && destinationLng != null) {
                        destinationPoint = GeoPoint(destinationLat, destinationLng)
                    }
                    if (originLat != null && originLng != null) {
                        originStr = "$originLat,$originLng"
                    }
                    if (destinationLat != null && destinationLng != null) {
                        destinationStr = "$destinationLat,$destinationLng"
                    }

                    passengerID?.let { id ->
                        firestore.collection("users")
                            .whereEqualTo("id", id)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { query ->
                                val userDoc = query.documents.firstOrNull()
                                Log.d("UserData", "User document: $userDoc")

                                val ratingMap = userDoc?.get("rating") as? Map<*, *>
                                Log.d("UserData", "Rating map: $ratingMap")

                                val count = (ratingMap?.get("count") as? Number)?.toInt() ?: 0
                                val total = (ratingMap?.get("total") as? Number)?.toInt() ?: 0
                                val averageRating = if (count > 0) total.toDouble() / count else null
                                rating = averageRating
                                Log.d("UserData", "count: $count, total: $total, averageRating: $averageRating")

                                passengerName = userDoc?.getString("name") ?: "مستخدم غير معروف"
                                Log.d("UserData", "passengerName: $passengerName")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error fetching user name", e)
                                passengerName = "مستخدم غير معروف"
                            }
                    }

                    documentListener = document.reference.addSnapshotListener { snapshot, error2 ->
                        if (error2 != null) {
                            Log.e("TripStatus", "Error listening to trip status", error2)
                            return@addSnapshotListener
                        }

                        snapshot?.let { doc ->
                            val status = doc.getString("status") ?: "pending"
                            Log.d("TripStatus", "Trip status: $status")

                            if (status == "Cancelled" && !tripState.isCancelled) {
                                stateTripViewModel.setCancelled()
                                Toast.makeText(context, "Trip cancelled by passenger", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Log.w("TripData", "No trip document found for tripId: $tripId")
                }
            }

            tripListener = registration
        }

        onDispose {
            tripListener?.remove()
            documentListener?.remove()
        }
    }

    fun updateLocationAndStatus(driverId: String, location: GeoPoint) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
                if (location != null) {
                    val database = FirebaseDatabase.getInstance().getReference("drivers").child(driverId)
                    driverLocationState = GeoPoint(location.latitude, location.longitude)
                    val locationMap = mapOf(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude
                    )

                    database.child("location").setValue(locationMap)
                        .addOnSuccessListener {
                            Log.d("RealtimeDB", "Location stored successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("RealtimeDB", "Failed to store location", e)
                        }
                }
            }
        } else {
            Log.e("Permission", "Location permission not granted")
        }
    }

    fun requestLocationUpdates(
        fusedLocationClient: FusedLocationProviderClient,
        firestore: FirebaseFirestore,
        driverId: String,
        context: Context
    ) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { location ->
                    val newLocation = GeoPoint(location.latitude, location.longitude)
                    previousLocation = newLocation
                    driverLocationState = newLocation
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    updateLocationAndStatus(driverId, geoPoint)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    var previousLocation2 by remember { mutableStateOf<GeoPoint?>(null) }
    var currentLocation2 by remember { mutableStateOf<GeoPoint?>(null) }

    val fusedLocationClient2 = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Request location updates
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.create().apply {
                interval = 2000
                fastestInterval = 1000
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let { location ->
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        previousLocation2 = currentLocation2
                        currentLocation2 = newPoint
                    }
                }
            }

            fusedLocationClient2.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            println("Location permission not granted")
        }
    }
    val realStreetPath = listOf(
        GeoPoint( 30.827140, 30.534610),
        GeoPoint( 30.827800, 30.534440),
        GeoPoint( 30.827840, 30.534710),
        GeoPoint( 30.827850, 30.534880),
        GeoPoint( 30.827830, 30.535200),
        GeoPoint( 30.827830, 30.535300),
        GeoPoint( 30.827840, 30.535310),
        GeoPoint( 30.827770, 30.535400),
        GeoPoint( 30.827750, 30.535460),
        GeoPoint( 30.827740, 30.535880),
        GeoPoint( 30.827740, 30.536030),
        GeoPoint( 30.827760, 30.536180),
        GeoPoint( 30.827780, 30.536330),
        GeoPoint( 30.827810, 30.536480),
        GeoPoint( 30.827860, 30.536620),
        GeoPoint( 30.826630, 30.537780),
        GeoPoint( 30.826510, 30.537900),
        GeoPoint( 30.826300, 30.538160),
        GeoPoint( 30.826020, 30.538600),
        GeoPoint( 30.825950, 30.538750),
        GeoPoint( 30.825850, 30.539000),
        GeoPoint( 30.825820, 30.539080),
        GeoPoint( 30.825360, 30.540030),
        GeoPoint( 30.824970, 30.540880),
        GeoPoint( 30.824670, 30.541520),
        GeoPoint( 30.824360, 30.542120),
        GeoPoint( 30.823570, 30.543810),
        GeoPoint( 30.822510, 30.545990),
        GeoPoint( 30.822140, 30.546760),
        GeoPoint( 30.822040, 30.547030),
        GeoPoint( 30.821850, 30.547600),
        GeoPoint( 30.820940, 30.550660),
        GeoPoint( 30.820710, 30.551380),
        GeoPoint( 30.820610, 30.551690),
        GeoPoint( 30.820510, 30.551870),
        GeoPoint( 30.820370, 30.552080),
        GeoPoint( 30.818770, 30.554170),
        GeoPoint( 30.818570, 30.554390),
        GeoPoint( 30.818350, 30.554600),
        GeoPoint( 30.817250, 30.555520),
        GeoPoint( 30.816240, 30.556300),
        GeoPoint( 30.815660, 30.556800),
        GeoPoint( 30.814640, 30.557600),
        GeoPoint( 30.814350, 30.557810),
        GeoPoint( 30.814050, 30.558000),
        GeoPoint( 30.813170, 30.558510),
        GeoPoint( 30.812950, 30.558680),
        GeoPoint( 30.812840, 30.558770),
        GeoPoint( 30.812650, 30.558990),
        GeoPoint( 30.811910, 30.559990),
        GeoPoint( 30.811680, 30.560270),
        GeoPoint( 30.811550, 30.560400),
        GeoPoint( 30.811360, 30.560550),
        GeoPoint( 30.811150, 30.560670),
        GeoPoint( 30.810210, 30.561110),
        GeoPoint( 30.809540, 30.561370),
        GeoPoint( 30.809030, 30.561540),
        GeoPoint( 30.808130, 30.561800),
        GeoPoint( 30.807760, 30.561880),
        GeoPoint( 30.807270, 30.562040),
        GeoPoint( 30.807310, 30.562290),
        GeoPoint( 30.807340, 30.562610),
        GeoPoint( 30.807340, 30.564880),
        GeoPoint( 30.807260, 30.568840),
        GeoPoint( 30.807190, 30.570420),
        GeoPoint( 30.807170, 30.571380),
        GeoPoint( 30.807090, 30.575210),
        GeoPoint( 30.807020, 30.576920),
        GeoPoint( 30.806940, 30.578000),
        GeoPoint( 30.806850, 30.578910),
        GeoPoint( 30.806720, 30.580050),
        GeoPoint( 30.806630, 30.580910),
        GeoPoint( 30.806430, 30.582760),
        GeoPoint( 30.806390, 30.582960),
        GeoPoint( 30.806330, 30.583170),
        GeoPoint( 30.806020, 30.584130),
        GeoPoint( 30.805880, 30.584490),
        GeoPoint( 30.805730, 30.584760),
        GeoPoint( 30.805510, 30.585110),
        GeoPoint( 30.805270, 30.585450),
        GeoPoint( 30.804970, 30.585810),
        GeoPoint( 30.804470, 30.586330),
        GeoPoint( 30.804390, 30.586440),
        GeoPoint( 30.804320, 30.586600),
        GeoPoint( 30.804270, 30.586780),
        GeoPoint( 30.804220, 30.586970),
        GeoPoint( 30.804200, 30.587250),
        GeoPoint( 30.804160, 30.588180),
        GeoPoint( 30.804080, 30.590960),
        GeoPoint( 30.804050, 30.592780),
        GeoPoint( 30.803870, 30.599850),
        GeoPoint( 30.803850, 30.600270),
        GeoPoint( 30.803830, 30.600870),
        GeoPoint( 30.803750, 30.602310),
        GeoPoint( 30.803700, 30.603710),
        GeoPoint( 30.803510, 30.607020),
        GeoPoint( 30.803170, 30.614280),
        GeoPoint( 30.803020, 30.615800),
        GeoPoint( 30.802940, 30.616490),
        GeoPoint( 30.802890, 30.616980),
        GeoPoint( 30.802670, 30.619610),
        GeoPoint( 30.802440, 30.621650),
        GeoPoint( 30.802350, 30.622490),
        GeoPoint( 30.802290, 30.623200),
        GeoPoint( 30.801610, 30.629480),
        GeoPoint( 30.801110, 30.632680),
        GeoPoint( 30.800980, 30.633620),
        GeoPoint( 30.800910, 30.633980),
        GeoPoint( 30.800860, 30.634180),
        GeoPoint( 30.800760, 30.634490),
        GeoPoint( 30.800650, 30.634740),
        GeoPoint( 30.800540, 30.634970),
        GeoPoint( 30.800360, 30.635320),
        GeoPoint( 30.800010, 30.635880),
        GeoPoint( 30.799500, 30.636580),
        GeoPoint( 30.798510, 30.638110),
        GeoPoint( 30.797670, 30.639610),
        GeoPoint( 30.797060, 30.640760),
        GeoPoint( 30.795840, 30.642940),
        GeoPoint( 30.795380, 30.643720),
        GeoPoint( 30.795100, 30.644250),
        GeoPoint( 30.794730, 30.644900),
        GeoPoint( 30.794510, 30.645390),
        GeoPoint( 30.794410, 30.645660),
        GeoPoint( 30.794070, 30.646730),
        GeoPoint( 30.793040, 30.649760),
        GeoPoint( 30.792610, 30.651100),
        GeoPoint( 30.792220, 30.652210),
        GeoPoint( 30.791700, 30.653800),
        GeoPoint( 30.791200, 30.656180),
        GeoPoint( 30.791110, 30.656550),
        GeoPoint( 30.790350, 30.659600),
        GeoPoint( 30.790020, 30.661050),
        GeoPoint( 30.789060, 30.664980),
        GeoPoint( 30.788620, 30.666840),
        GeoPoint( 30.788370, 30.667940),
        GeoPoint( 30.788270, 30.668450),
        GeoPoint( 30.787840, 30.670500),
        GeoPoint( 30.787790, 30.670710),
        GeoPoint( 30.787720, 30.670930),
        GeoPoint( 30.787610, 30.671170),
        GeoPoint( 30.787540, 30.671320),
        GeoPoint( 30.787330, 30.671650),
        GeoPoint( 30.787090, 30.671990),
        GeoPoint( 30.786940, 30.672160),
        GeoPoint( 30.786800, 30.672300),
        GeoPoint( 30.786640, 30.672430),
        GeoPoint( 30.786190, 30.672740),
        GeoPoint( 30.785800, 30.672990),
        GeoPoint( 30.785030, 30.673540),
        GeoPoint( 30.784690, 30.673790),
        GeoPoint( 30.784120, 30.674190),
        GeoPoint( 30.783790, 30.674450),
        GeoPoint( 30.783520, 30.674700),
        GeoPoint( 30.783000, 30.675270),
        GeoPoint( 30.782840, 30.675490),
        GeoPoint( 30.782710, 30.675690),
        GeoPoint( 30.782480, 30.676140),
        GeoPoint( 30.782380, 30.676410),
        GeoPoint( 30.782340, 30.676570),
        GeoPoint( 30.782190, 30.676900),
        GeoPoint( 30.782080, 30.677090),
        GeoPoint( 30.781940, 30.677270),
        GeoPoint( 30.781680, 30.677510),
        GeoPoint( 30.781520, 30.677630),
        GeoPoint( 30.781360, 30.677710),
        GeoPoint( 30.781150, 30.677790),
        GeoPoint( 30.781010, 30.677830),
        GeoPoint( 30.780790, 30.677840),
        GeoPoint( 30.780480, 30.677810),
        GeoPoint( 30.779600, 30.677620),
        GeoPoint( 30.779380, 30.677560),
        GeoPoint( 30.778060, 30.677310),
        GeoPoint( 30.777620, 30.677200),
        GeoPoint( 30.777250, 30.677220),
        GeoPoint( 30.776970, 30.677270),
        GeoPoint( 30.776640, 30.677360),
        GeoPoint( 30.776360, 30.677380),
        GeoPoint( 30.776000, 30.677370),
        GeoPoint( 30.775780, 30.677720),
        GeoPoint( 30.775560, 30.677960),
        GeoPoint( 30.775440, 30.678090),
        GeoPoint( 30.775320, 30.678310),
        GeoPoint( 30.775180, 30.678660),
        GeoPoint( 30.775110, 30.678880),
        GeoPoint( 30.775090, 30.678990),
        GeoPoint( 30.775070, 30.679150),
        GeoPoint( 30.775080, 30.679300),
        GeoPoint( 30.775110, 30.679460),
        GeoPoint( 30.775190, 30.679810),
        GeoPoint( 30.775350, 30.680670),
        GeoPoint( 30.775480, 30.681120),
        GeoPoint( 30.775580, 30.681560),
        GeoPoint( 30.775810, 30.682690),
        GeoPoint( 30.775840, 30.682880),
        GeoPoint( 30.775850, 30.683110),
        GeoPoint( 30.775910, 30.683670),
        GeoPoint( 30.776010, 30.684220),
        GeoPoint( 30.776050, 30.684480),
        GeoPoint( 30.776050, 30.684680),
        GeoPoint( 30.776030, 30.684800),
        GeoPoint( 30.775950, 30.684970),
        GeoPoint( 30.775650, 30.685460),
        GeoPoint( 30.775600, 30.685500),
        GeoPoint( 30.775250, 30.685710),
        GeoPoint( 30.774290, 30.686190),
        GeoPoint( 30.773820, 30.686380),
        GeoPoint( 30.773310, 30.686570),
        GeoPoint( 30.772720, 30.686720),
        GeoPoint( 30.772420, 30.686780),
        GeoPoint( 30.772100, 30.686820),
        GeoPoint( 30.771430, 30.686870),
        GeoPoint( 30.770900, 30.686860),
        GeoPoint( 30.770010, 30.686730),
        GeoPoint( 30.768620, 30.686500),
        GeoPoint( 30.769160, 30.688450),
        GeoPoint( 30.769580, 30.690020),
        GeoPoint( 30.769780, 30.690670),
        GeoPoint( 30.769970, 30.691350),
        GeoPoint( 30.770190, 30.692180),
        GeoPoint( 30.770360, 30.692900),
        GeoPoint( 30.770450, 30.693260),
        GeoPoint( 30.770640, 30.694580),
        GeoPoint( 30.770710, 30.695200),
        GeoPoint( 30.770790, 30.696570),
        GeoPoint( 30.770850, 30.698110),
        GeoPoint( 30.770940, 30.699890),
        GeoPoint( 30.771120, 30.702880),
        GeoPoint( 30.771230, 30.704650),
        GeoPoint( 30.771250, 30.705350),
        GeoPoint( 30.771250, 30.706280),
        GeoPoint( 30.771510, 30.708940),
        GeoPoint( 30.771540, 30.709680),
        GeoPoint( 30.771540, 30.710160),
        GeoPoint( 30.771540, 30.710430),
        GeoPoint( 30.771490, 30.710970),
        GeoPoint( 30.771420, 30.711420),
        GeoPoint( 30.771260, 30.712100),
        GeoPoint( 30.770330, 30.715820),
        GeoPoint( 30.770290, 30.715910),
        GeoPoint( 30.770230, 30.716010),
        GeoPoint( 30.770170, 30.716070),
        GeoPoint( 30.770050, 30.716130),
        GeoPoint( 30.769900, 30.716180),
        GeoPoint( 30.769660, 30.716220),
        GeoPoint( 30.769360, 30.716230),
        GeoPoint( 30.769720, 30.716580),
        GeoPoint( 30.770140, 30.717010),
        GeoPoint( 30.770250, 30.717150),
        GeoPoint( 30.770590, 30.717600),
        GeoPoint( 30.771070, 30.718360),
        GeoPoint( 30.771250, 30.718670),
        GeoPoint( 30.771410, 30.718990),
        GeoPoint( 30.771530, 30.719280),
        GeoPoint( 30.771670, 30.719730),
        GeoPoint( 30.772070, 30.721120),
        GeoPoint( 30.772670, 30.723180),
        GeoPoint( 30.772720, 30.723360),
        GeoPoint( 30.772750, 30.723520),
        GeoPoint( 30.772770, 30.723730),
        GeoPoint( 30.772740, 30.724570),
        GeoPoint( 30.772690, 30.725300),
        GeoPoint( 30.772610, 30.725880),
        GeoPoint( 30.772510, 30.726470),
        GeoPoint( 30.772500, 30.726590),
        GeoPoint( 30.772500, 30.726730),
        GeoPoint( 30.772560, 30.727270),
        GeoPoint( 30.772720, 30.728260),
        GeoPoint( 30.772860, 30.728930),
        GeoPoint( 30.772890, 30.729150),
        GeoPoint( 30.772890, 30.729630),
        GeoPoint( 30.772840, 30.730310),
        GeoPoint( 30.772830, 30.730580),
        GeoPoint( 30.772850, 30.730940),
        GeoPoint( 30.773330, 30.733640),
        GeoPoint( 30.773340, 30.733770),
        GeoPoint( 30.773300, 30.734290),
        GeoPoint( 30.773110, 30.735700),
        GeoPoint( 30.773010, 30.736350),
        GeoPoint( 30.772900, 30.736890),
        GeoPoint( 30.772830, 30.737320),
        GeoPoint( 30.772800, 30.737660),
        GeoPoint( 30.772810, 30.738000),
        GeoPoint( 30.772910, 30.738910),
        GeoPoint( 30.772990, 30.739370),
        GeoPoint( 30.773020, 30.739490),
        GeoPoint( 30.773090, 30.739700),
        GeoPoint( 30.773170, 30.739910),
        GeoPoint( 30.773620, 30.739800),
        GeoPoint( 30.773920, 30.739760),
        GeoPoint( 30.774200, 30.739780),
        GeoPoint( 30.774440, 30.739850),
        GeoPoint( 30.774670, 30.740000),
        GeoPoint( 30.776570, 30.741710),
        GeoPoint( 30.777750, 30.742800),
        GeoPoint( 30.778100, 30.743210),
        GeoPoint( 30.778340, 30.743570),
        GeoPoint( 30.778610, 30.744060),
        GeoPoint( 30.779040, 30.744770),
        GeoPoint( 30.779480, 30.745330),
        GeoPoint( 30.779890, 30.745710),
        GeoPoint( 30.780140, 30.745890),
        GeoPoint( 30.780270, 30.745960),
        GeoPoint( 30.780410, 30.746020),
        GeoPoint( 30.780570, 30.746070),
        GeoPoint( 30.780770, 30.746110),
        GeoPoint( 30.780950, 30.746120),
        GeoPoint( 30.781480, 30.746090),
        GeoPoint( 30.781820, 30.746110),
        GeoPoint( 30.782120, 30.746180),
        GeoPoint( 30.782410, 30.746310),
        GeoPoint( 30.783330, 30.747000),
        GeoPoint( 30.783620, 30.747150),
        GeoPoint( 30.783890, 30.747220),
        GeoPoint( 30.784160, 30.747200),
        GeoPoint( 30.784460, 30.747110),
        GeoPoint( 30.785140, 30.746890),
        GeoPoint( 30.785410, 30.746840),
        GeoPoint( 30.785610, 30.746810),
        GeoPoint( 30.785850, 30.746810),
        GeoPoint( 30.786050, 30.746840),
        GeoPoint( 30.786280, 30.746890),
        GeoPoint( 30.786880, 30.747070),
        GeoPoint( 30.788000, 30.747450),
        GeoPoint( 30.788260, 30.747520),
        GeoPoint( 30.788940, 30.747770),
        GeoPoint( 30.789390, 30.747880),
        GeoPoint( 30.789640, 30.747900),
        GeoPoint( 30.789910, 30.747880),
        GeoPoint( 30.791570, 30.747720),
        GeoPoint( 30.791900, 30.747730),
        GeoPoint( 30.792140, 30.747770),
        GeoPoint( 30.792370, 30.747850),
        GeoPoint( 30.792930, 30.748070),
        GeoPoint( 30.794050, 30.748540),
        GeoPoint( 30.794950, 30.748890),
        GeoPoint( 30.795100, 30.748930),
        GeoPoint( 30.795670, 30.748970),
        GeoPoint( 30.796020, 30.748940),
        GeoPoint( 30.796280, 30.748860),
        GeoPoint( 30.796660, 30.748670),
        GeoPoint( 30.797340, 30.748210),
        GeoPoint( 30.798010, 30.747730),
        GeoPoint( 30.798730, 30.747270),
        GeoPoint( 30.798980, 30.747180),
        GeoPoint( 30.799240, 30.747110),
        GeoPoint( 30.799610, 30.747080),
        GeoPoint( 30.800000, 30.747090),
        GeoPoint( 30.800310, 30.747180),
        GeoPoint( 30.800650, 30.747360),
        GeoPoint( 30.800910, 30.747510),
        GeoPoint( 30.801320, 30.747830),
        GeoPoint( 30.801800, 30.748230),
        GeoPoint( 30.802570, 30.748860),
        GeoPoint( 30.803970, 30.749960),
        GeoPoint( 30.804690, 30.750560),
        GeoPoint( 30.805340, 30.751210),
        GeoPoint( 30.806230, 30.752170),
        GeoPoint( 30.807110, 30.753150),
        GeoPoint( 30.807900, 30.754020),
        GeoPoint( 30.808420, 30.754530),
        GeoPoint( 30.808780, 30.754830),
        GeoPoint( 30.808930, 30.754940),
        GeoPoint( 30.809240, 30.755120),
        GeoPoint( 30.809740, 30.755360),
        GeoPoint( 30.809790, 30.755260),
        GeoPoint( 30.810450, 30.752840),
        GeoPoint( 30.810560, 30.752620),
        GeoPoint( 30.810700, 30.752470),
        GeoPoint( 30.810550, 30.752600),
        GeoPoint( 30.810430, 30.752780),
        GeoPoint( 30.810310, 30.752880),
        GeoPoint( 30.810280, 30.753010),
        GeoPoint( 30.810290, 30.753110),
        GeoPoint( 30.810340, 30.753170),
        GeoPoint( 30.810420, 30.753210),
        GeoPoint( 30.810530, 30.753220),
        GeoPoint( 30.810660, 30.753170),
        GeoPoint( 30.811030, 30.752970),
        GeoPoint( 30.811690, 30.752550),
        GeoPoint( 30.813400, 30.752150),
        GeoPoint( 30.815140, 30.751920),
        GeoPoint( 30.816390, 30.751760),
        GeoPoint( 30.817470, 30.751720),
        GeoPoint( 30.817920, 30.751700),
        GeoPoint( 30.818220, 30.751700),
        GeoPoint( 30.818640, 30.751750),
        GeoPoint( 30.819380, 30.751920),
        GeoPoint( 30.820370, 30.752150),
        GeoPoint( 30.821170, 30.753030),
        GeoPoint( 30.823910, 30.753650),
        GeoPoint( 30.825790, 30.753830),
        GeoPoint( 30.826330, 30.753940),
        GeoPoint( 30.826750, 30.755380),
        GeoPoint( 30.833330, 30.757190),
        GeoPoint( 30.841560, 30.757390),
        GeoPoint( 30.842390, 30.757810),
        GeoPoint( 30.844370, 30.758780),
        GeoPoint( 30.848680, 30.759460),
        GeoPoint( 30.851830, 30.759900),
        GeoPoint( 30.853760, 30.760030),
        GeoPoint( 30.854390, 30.760170),
        GeoPoint( 30.855220, 30.760220),
        GeoPoint( 30.855850, 30.760250),
        GeoPoint( 30.856330, 30.760260),
        GeoPoint( 30.856890, 30.760250),
        GeoPoint( 30.857280, 30.760180),
        GeoPoint( 30.857980, 30.760130),
        GeoPoint( 30.858310, 30.760020),
        GeoPoint( 30.858840, 30.759800),
        GeoPoint( 30.859510, 30.759670),
        GeoPoint( 30.859840, 30.759520),
        GeoPoint( 30.860190, 30.759310),
        GeoPoint( 30.860600, 30.758970),
        GeoPoint( 30.861200, 30.758800),
        GeoPoint( 30.861450, 30.758450),
        GeoPoint( 30.862020, 30.757970),
        GeoPoint( 30.862680, 30.755420),
        GeoPoint( 30.866410, 30.754280),
        GeoPoint( 30.868110, 30.751040),
        GeoPoint( 30.872900, 30.750890),
        GeoPoint( 30.872540, 30.751460),
        GeoPoint( 30.872310, 30.751850),
        GeoPoint( 30.871980, 30.752520),
        GeoPoint( 30.871450, 30.753630),
        GeoPoint( 30.870790, 30.754960),
        GeoPoint( 30.868930, 30.758570),
        GeoPoint( 30.866060, 30.764480),
        GeoPoint( 30.865300, 30.766060),
        GeoPoint( 30.864760, 30.767150),
        GeoPoint( 30.864400, 30.767850),
        GeoPoint( 30.864030, 30.768500),
        GeoPoint( 30.863510, 30.769320),
        GeoPoint( 30.862830, 30.770280),
        GeoPoint( 30.861530, 30.772050),
        GeoPoint( 30.860520, 30.773460),
        GeoPoint( 30.860170, 30.773960),
        GeoPoint( 30.859590, 30.774750),
        GeoPoint( 30.858750, 30.775910),
        GeoPoint( 30.857920, 30.777080),
        GeoPoint( 30.857700, 30.777370),
        GeoPoint( 30.857390, 30.777810),
        GeoPoint( 30.857130, 30.778250),
        GeoPoint( 30.856880, 30.778800),
        GeoPoint( 30.856740, 30.779200),
        GeoPoint( 30.856610, 30.779590),
        GeoPoint( 30.856410, 30.780210),
        GeoPoint( 30.854610, 30.785680),
        GeoPoint( 30.853980, 30.787630),
        GeoPoint( 30.852910, 30.790880),
        GeoPoint( 30.852080, 30.793460),
        GeoPoint( 30.851830, 30.794210),
        GeoPoint( 30.850940, 30.796960),
        GeoPoint( 30.848840, 30.803420),
        GeoPoint( 30.848440, 30.804580),
        GeoPoint( 30.848120, 30.805410),
        GeoPoint( 30.847930, 30.805860),
        GeoPoint( 30.847630, 30.806490),
        GeoPoint( 30.846850, 30.807850),
        GeoPoint( 30.845470, 30.810310),
        GeoPoint( 30.843530, 30.813840),
        GeoPoint( 30.841620, 30.817210),
        GeoPoint( 30.841350, 30.817650),
        GeoPoint( 30.840980, 30.818330),
        GeoPoint( 30.840500, 30.819380),
        GeoPoint( 30.840230, 30.820180),
        GeoPoint( 30.839140, 30.823920),
        GeoPoint( 30.838790, 30.824860),
        GeoPoint( 30.838140, 30.826310),
        GeoPoint( 30.836620, 30.829610),
        GeoPoint( 30.834680, 30.833760),
        GeoPoint( 30.833460, 30.836430),
        GeoPoint( 30.832620, 30.838180),
        GeoPoint( 30.829370, 30.845260),
        GeoPoint( 30.828470, 30.847180),
        GeoPoint( 30.826380, 30.851690),
        GeoPoint( 30.825680, 30.853140),
        GeoPoint( 30.823020, 30.858800),
        GeoPoint( 30.822230, 30.860460),
        GeoPoint( 30.821250, 30.862550),
        GeoPoint( 30.819230, 30.866810),
        GeoPoint( 30.818950, 30.867460),
        GeoPoint( 30.818750, 30.867970),
        GeoPoint( 30.818600, 30.868430),
        GeoPoint( 30.817810, 30.871510),
        GeoPoint( 30.817580, 30.872460),
        GeoPoint( 30.817500, 30.872730),
        GeoPoint( 30.817310, 30.873520),
        GeoPoint( 30.817190, 30.873970),
        GeoPoint( 30.817010, 30.874520),
        GeoPoint( 30.816920, 30.874800),
        GeoPoint( 30.816660, 30.875380),
        GeoPoint( 30.816490, 30.875740),
        GeoPoint( 30.816240, 30.876190),
        GeoPoint( 30.816080, 30.876440),
        GeoPoint( 30.815720, 30.876970),
        GeoPoint( 30.815450, 30.877300),
        GeoPoint( 30.815130, 30.877650),
        GeoPoint( 30.814970, 30.877820),
        GeoPoint( 30.814650, 30.878130),
        GeoPoint( 30.813170, 30.879440),
        GeoPoint( 30.810960, 30.881440),
        GeoPoint( 30.809390, 30.882820),
        GeoPoint( 30.808390, 30.883710),
        GeoPoint( 30.807810, 30.884230),
        GeoPoint( 30.807020, 30.884910),
        GeoPoint( 30.805890, 30.885960),
        GeoPoint( 30.805630, 30.886180),
        GeoPoint( 30.803970, 30.887920),
        GeoPoint( 30.802670, 30.889350),
        GeoPoint( 30.801520, 30.890570),
        GeoPoint( 30.799680, 30.892540),
        GeoPoint( 30.797200, 30.895170),
        GeoPoint( 30.796160, 30.896260),
        GeoPoint( 30.795810, 30.896640),
        GeoPoint( 30.794620, 30.897880),
        GeoPoint( 30.793640, 30.898910),
        GeoPoint( 30.792560, 30.900060),
        GeoPoint( 30.792250, 30.900370),
        GeoPoint( 30.791870, 30.900790),
        GeoPoint( 30.790950, 30.901780),
        GeoPoint( 30.790790, 30.901960),
        GeoPoint( 30.790650, 30.902130),
        GeoPoint( 30.790250, 30.902640),
        GeoPoint( 30.789860, 30.903180),
        GeoPoint( 30.786360, 30.908400),
        GeoPoint( 30.783460, 30.912580),
        GeoPoint( 30.782840, 30.913280),
        GeoPoint( 30.782500, 30.913770),
        GeoPoint( 30.781990, 30.914670),
        GeoPoint( 30.781740, 30.915190),
        GeoPoint( 30.778260, 30.920270),
        GeoPoint( 30.777270, 30.921710),
        GeoPoint( 30.776820, 30.922390),
        GeoPoint( 30.776580, 30.922800),
        GeoPoint( 30.776310, 30.923340),
        GeoPoint( 30.776190, 30.923640),
        GeoPoint( 30.776070, 30.923970),
        GeoPoint( 30.775870, 30.924550),
        GeoPoint( 30.775780, 30.924890),
        GeoPoint( 30.775680, 30.925380),
        GeoPoint( 30.775580, 30.926100),
        GeoPoint( 30.775550, 30.926620),
        GeoPoint( 30.775550, 30.926870),
        GeoPoint( 30.775570, 30.927910),
        GeoPoint( 30.775620, 30.928990),
        GeoPoint( 30.775660, 30.929580),
        GeoPoint( 30.775690, 30.930220),
        GeoPoint( 30.775720, 30.931090),
        GeoPoint( 30.775810, 30.932580),
        GeoPoint( 30.775840, 30.933750),
        GeoPoint( 30.776140, 30.939690),
        GeoPoint( 30.776290, 30.942540),
        GeoPoint( 30.776400, 30.944640),
        GeoPoint( 30.776460, 30.945660),
        GeoPoint( 30.776560, 30.947710),
        GeoPoint( 30.776640, 30.949100),
        GeoPoint( 30.776670, 30.949830),
        GeoPoint( 30.776700, 30.950290),
        GeoPoint( 30.776780, 30.952140),
        GeoPoint( 30.776970, 30.955810),
        GeoPoint( 30.776980, 30.956100),
        GeoPoint( 30.777010, 30.956470),
        GeoPoint( 30.777080, 30.958000),
        GeoPoint( 30.777120, 30.958560),
        GeoPoint( 30.777240, 30.960850),
        GeoPoint( 30.777300, 30.961700),
        GeoPoint( 30.777330, 30.962680),
        GeoPoint( 30.777540, 30.966400),
        GeoPoint( 30.777600, 30.967730),
        GeoPoint( 30.777800, 30.971050),
        GeoPoint( 30.777720, 30.971440),
        GeoPoint( 30.777670, 30.971600),
        GeoPoint( 30.777540, 30.971940),
        GeoPoint( 30.777460, 30.972100),
        GeoPoint( 30.777280, 30.972360),
        GeoPoint( 30.777170, 30.972480),
        GeoPoint( 30.776940, 30.972690),
        GeoPoint( 30.775940, 30.973220),
        GeoPoint( 30.774870, 30.973780),
        GeoPoint( 30.774780, 30.973860),
        GeoPoint( 30.774650, 30.974020),
        GeoPoint( 30.772290, 30.975260),
        GeoPoint( 30.770910, 30.976000),
        GeoPoint( 30.767930, 30.977560),
        GeoPoint( 30.763300, 30.980010),
        GeoPoint( 30.742150, 30.991320),
        GeoPoint( 30.733950, 30.995680),
        GeoPoint( 30.732920, 30.996200),
        GeoPoint( 30.731650, 30.996880),
        GeoPoint( 30.724630, 31.000610),
        GeoPoint( 30.722780, 31.001650),
        GeoPoint( 30.721900, 31.002250),
        GeoPoint( 30.721430, 31.002610),
        GeoPoint( 30.721140, 31.002860),
        GeoPoint( 30.720160, 31.003760),
        GeoPoint( 30.719510, 31.004350),
        GeoPoint( 30.718050, 31.005850),
        GeoPoint( 30.717500, 31.006400),
        GeoPoint( 30.716020, 31.007940),
        GeoPoint( 30.714300, 31.009610),
        GeoPoint( 30.712750, 31.011180),
        GeoPoint( 30.707530, 31.016500),
        GeoPoint( 30.706970, 31.017080),
        GeoPoint( 30.706440, 31.017660),
        GeoPoint( 30.704950, 31.019240),
        GeoPoint( 30.702860, 31.021420),
        GeoPoint( 30.702050, 31.022250),
        GeoPoint( 30.701050, 31.023240),
        GeoPoint( 30.697880, 31.026320),
        GeoPoint( 30.697250, 31.026950),
        GeoPoint( 30.695270, 31.028930),
        GeoPoint( 30.692920, 31.031330),
        GeoPoint( 30.688660, 31.035610),
        GeoPoint( 30.688230, 31.035990),
        GeoPoint( 30.688000, 31.036190),
        GeoPoint( 30.687710, 31.036420),
        GeoPoint( 30.687280, 31.036800),
        GeoPoint( 30.686730, 31.037220),
        GeoPoint( 30.686520, 31.037370),
        GeoPoint( 30.685510, 31.038040),
        GeoPoint( 30.684800, 31.038450),
        GeoPoint( 30.684110, 31.038810),
        GeoPoint( 30.678110, 31.041850),
        GeoPoint( 30.669160, 31.046470),
        GeoPoint( 30.664520, 31.048850),
        GeoPoint( 30.663640, 31.049280),
        GeoPoint( 30.660910, 31.050700),
        GeoPoint( 30.658960, 31.051680),
        GeoPoint( 30.657830, 31.052260),
        GeoPoint( 30.656790, 31.052820),
        GeoPoint( 30.655410, 31.053510),
        GeoPoint( 30.652080, 31.055190),
        GeoPoint( 30.649640, 31.056440),
        GeoPoint( 30.646630, 31.057990),
        GeoPoint( 30.636620, 31.063120),
        GeoPoint( 30.633670, 31.064640),
        GeoPoint( 30.633140, 31.064930),
        GeoPoint( 30.629160, 31.066920),
        GeoPoint( 30.627230, 31.067960),
        GeoPoint( 30.624360, 31.069420),
        GeoPoint( 30.621490, 31.070930),
        GeoPoint( 30.619280, 31.072050),
        GeoPoint( 30.617250, 31.073100),
        GeoPoint( 30.617020, 31.073230),
        GeoPoint( 30.616820, 31.073360),
        GeoPoint( 30.616500, 31.073610),
        GeoPoint( 30.616310, 31.073800),
        GeoPoint( 30.616120, 31.074010),
        GeoPoint( 30.615910, 31.074300),
        GeoPoint( 30.615290, 31.075200),
        GeoPoint( 30.614480, 31.076560),
        GeoPoint( 30.614280, 31.076850),
        GeoPoint( 30.614050, 31.077130),
        GeoPoint( 30.613780, 31.077430),
        GeoPoint( 30.613400, 31.077780),
        GeoPoint( 30.613230, 31.077910),
        GeoPoint( 30.613010, 31.078080),
        GeoPoint( 30.612840, 31.078190),
        GeoPoint( 30.612680, 31.078280),
        GeoPoint( 30.612460, 31.078390),
        GeoPoint( 30.612190, 31.078490),
        GeoPoint( 30.611790, 31.078630),
        GeoPoint( 30.611490, 31.078720),
        GeoPoint( 30.611340, 31.078760),
        GeoPoint( 30.611190, 31.078780),
        GeoPoint( 30.610880, 31.078800),
        GeoPoint( 30.610480, 31.078780),
        GeoPoint( 30.609660, 31.078680),
        GeoPoint( 30.608440, 31.078480),
        GeoPoint( 30.608190, 31.078450),
        GeoPoint( 30.607830, 31.078440),
        GeoPoint( 30.607600, 31.078440),
        GeoPoint( 30.607170, 31.078480),
        GeoPoint( 30.606830, 31.078540),
        GeoPoint( 30.606690, 31.078590),
        GeoPoint( 30.606480, 31.078670),
        GeoPoint( 30.606250, 31.078780),
        GeoPoint( 30.605000, 31.079410),
        GeoPoint( 30.604070, 31.079910),
        GeoPoint( 30.602070, 31.080960),
        GeoPoint( 30.601680, 31.081160),
        GeoPoint( 30.600660, 31.081710),
        GeoPoint( 30.594400, 31.084940),
        GeoPoint( 30.586910, 31.088830),
        GeoPoint( 30.578440, 31.093240),
        GeoPoint( 30.578050, 31.093460),
        GeoPoint( 30.577970, 31.093470),
        GeoPoint( 30.577820, 31.093500),
        GeoPoint( 30.576930, 31.093970),
        GeoPoint( 30.574820, 31.095060),
        GeoPoint( 30.573940, 31.095520),
        GeoPoint( 30.572480, 31.096260),
        GeoPoint( 30.571640, 31.096670),
        GeoPoint( 30.568640, 31.098210),
        GeoPoint( 30.567640, 31.098700),
        GeoPoint( 30.566300, 31.099400),
        GeoPoint( 30.565400, 31.099900),
        GeoPoint( 30.565300, 31.099970),
        GeoPoint( 30.565240, 31.100030),
        GeoPoint( 30.565190, 31.100090),
        GeoPoint( 30.564740, 31.100320),
        GeoPoint( 30.563900, 31.100810),
        GeoPoint( 30.563090, 31.101280),
        GeoPoint( 30.562950, 31.101420),
        GeoPoint( 30.562880, 31.101480),
        GeoPoint( 30.561760, 31.102240),
        GeoPoint( 30.561210, 31.102590),
        GeoPoint( 30.560650, 31.102880),
        GeoPoint( 30.559710, 31.103410),
        GeoPoint( 30.558590, 31.103970),
        GeoPoint( 30.558510, 31.104040),
        GeoPoint( 30.558430, 31.104130),
        GeoPoint( 30.558340, 31.104250),
        GeoPoint( 30.558260, 31.104400),
        GeoPoint( 30.558170, 31.104630),
        GeoPoint( 30.558160, 31.104720),
        GeoPoint( 30.558140, 31.104970),
        GeoPoint( 30.558150, 31.105140),
        GeoPoint( 30.558190, 31.105310),
        GeoPoint( 30.558250, 31.105470),
        GeoPoint( 30.558330, 31.105620),
        GeoPoint( 30.558410, 31.105740),
        GeoPoint( 30.558540, 31.105900),
        GeoPoint( 30.558680, 31.105990),
        GeoPoint( 30.558830, 31.106060),
        GeoPoint( 30.558990, 31.106100),
        GeoPoint( 30.559310, 31.106120),
        GeoPoint( 30.559620, 31.106180),
        GeoPoint( 30.559830, 31.106250),
        GeoPoint( 30.560040, 31.106340),
        GeoPoint( 30.560300, 31.106490),
        GeoPoint( 30.560500, 31.106650),
        GeoPoint( 30.560650, 31.106790),
        GeoPoint( 30.560790, 31.106940),
        GeoPoint( 30.560880, 31.107100),
        GeoPoint( 30.560930, 31.107270),
        GeoPoint( 30.561050, 31.107430),
        GeoPoint( 30.561140, 31.107560),
        GeoPoint( 30.561260, 31.107840),
        GeoPoint( 30.561390, 31.108240),
        GeoPoint( 30.561510, 31.108670),
        GeoPoint( 30.561590, 31.108870),
        GeoPoint( 30.561680, 31.109020),
        GeoPoint( 30.561840, 31.109670),
        GeoPoint( 30.561960, 31.110320),
        GeoPoint( 30.562070, 31.110970),
        GeoPoint( 30.562160, 31.111630),
        GeoPoint( 30.562220, 31.112300),
        GeoPoint( 30.562260, 31.112970),
        GeoPoint( 30.562270, 31.113650),
        GeoPoint( 30.562260, 31.114320),
        GeoPoint( 30.562230, 31.114990),
        GeoPoint( 30.562180, 31.115650),
        GeoPoint( 30.562050, 31.116490),
        GeoPoint( 30.561900, 31.117310),
        GeoPoint( 30.561700, 31.118110),
        GeoPoint( 30.561470, 31.118910),
        GeoPoint( 30.561230, 31.119660),
        GeoPoint( 30.560940, 31.120370),
        GeoPoint( 30.560640, 31.121050),
        GeoPoint( 30.560170, 31.121940),
        GeoPoint( 30.559980, 31.122270),
        GeoPoint( 30.557440, 31.126380),
        GeoPoint( 30.555160, 31.129830),
        GeoPoint( 30.554430, 31.131020),
        GeoPoint( 30.554070, 31.131640),
        GeoPoint( 30.552990, 31.133550),
        GeoPoint( 30.549530, 31.139560),
        GeoPoint( 30.548400, 31.141390),
        GeoPoint( 30.546500, 31.144340),
        GeoPoint( 30.544780, 31.147050),
        GeoPoint( 30.543960, 31.148550),
        GeoPoint( 30.543360, 31.149570),
        GeoPoint( 30.542710, 31.150560),
        GeoPoint( 30.540760, 31.153280),
        GeoPoint( 30.539860, 31.154620),
        GeoPoint( 30.538240, 31.157110),
        GeoPoint( 30.536950, 31.158980),
        GeoPoint( 30.536750, 31.159220),
        GeoPoint( 30.536530, 31.159450),
        GeoPoint( 30.536300, 31.159650),
        GeoPoint( 30.536060, 31.159830),
        GeoPoint( 30.535940, 31.159910),
        GeoPoint( 30.535820, 31.159970),
        GeoPoint( 30.535690, 31.160010),
        GeoPoint( 30.535460, 31.160060),
        GeoPoint( 30.535230, 31.160080),
        GeoPoint( 30.534990, 31.160080),
        GeoPoint( 30.534760, 31.160040),
        GeoPoint( 30.534530, 31.159980),
        GeoPoint( 30.534310, 31.159890),
        GeoPoint( 30.533620, 31.159500),
        GeoPoint( 30.533230, 31.159120),
        GeoPoint( 30.531350, 31.157390),
        GeoPoint( 30.520690, 31.147390),
        GeoPoint( 30.518610, 31.145450),
        GeoPoint( 30.518370, 31.145240),
        GeoPoint( 30.518130, 31.145050),
        GeoPoint( 30.517880, 31.144870),
        GeoPoint( 30.517620, 31.144700),
        GeoPoint( 30.516960, 31.144310),
        GeoPoint( 30.516560, 31.144110),
        GeoPoint( 30.515830, 31.143810),
        GeoPoint( 30.514740, 31.143470),
        GeoPoint( 30.513440, 31.143110),
        GeoPoint( 30.511710, 31.142590),
        GeoPoint( 30.511280, 31.142440),
        GeoPoint( 30.510850, 31.142260),
        GeoPoint( 30.510560, 31.142110),
        GeoPoint( 30.510280, 31.141950),
        GeoPoint( 30.510000, 31.141770),
        GeoPoint( 30.509730, 31.141580),
        GeoPoint( 30.509470, 31.141370),
        GeoPoint( 30.509220, 31.141160),
        GeoPoint( 30.508980, 31.140920),
        GeoPoint( 30.508740, 31.140680),
        GeoPoint( 30.508520, 31.140430),
        GeoPoint( 30.508300, 31.140160),
        GeoPoint( 30.508010, 31.139820),
        GeoPoint( 30.507720, 31.139510),
        GeoPoint( 30.507420, 31.139220),
        GeoPoint( 30.507110, 31.138940),
        GeoPoint( 30.506780, 31.138670),
        GeoPoint( 30.506450, 31.138420),
        GeoPoint( 30.506110, 31.138190),
        GeoPoint( 30.505760, 31.137980),
        GeoPoint( 30.505410, 31.137780),
        GeoPoint( 30.505050, 31.137600),
        GeoPoint( 30.504680, 31.137440),
        GeoPoint( 30.504300, 31.137300),
        GeoPoint( 30.503950, 31.137190),
        GeoPoint( 30.503600, 31.137100),
        GeoPoint( 30.503240, 31.137020),
        GeoPoint( 30.502880, 31.136970),
        GeoPoint( 30.501910, 31.136850),
        GeoPoint( 30.497010, 31.136380),
        GeoPoint( 30.496510, 31.136360),
        GeoPoint( 30.496000, 31.136350),
        GeoPoint( 30.495490, 31.136350),
        GeoPoint( 30.494990, 31.136370),
        GeoPoint( 30.494650, 31.136390),
        GeoPoint( 30.494310, 31.136430),
        GeoPoint( 30.493970, 31.136480),
        GeoPoint( 30.493630, 31.136540),
        GeoPoint( 30.493290, 31.136610),
        GeoPoint( 30.492960, 31.136700),
        GeoPoint( 30.492620, 31.136790),
        GeoPoint( 30.487860, 31.138300),
        GeoPoint( 30.487510, 31.138420),
        GeoPoint( 30.487180, 31.138560),
        GeoPoint( 30.486850, 31.138720),
        GeoPoint( 30.486520, 31.138890),
        GeoPoint( 30.485850, 31.139280),
        GeoPoint( 30.485490, 31.139530),
        GeoPoint( 30.485130, 31.139810),
        GeoPoint( 30.484780, 31.140100),
        GeoPoint( 30.484450, 31.140400),
        GeoPoint( 30.484130, 31.140730),
        GeoPoint( 30.483820, 31.141080),
        GeoPoint( 30.482720, 31.142350),
        GeoPoint( 30.482400, 31.142700),
        GeoPoint( 30.482070, 31.143030),
        GeoPoint( 30.481730, 31.143340),
        GeoPoint( 30.481370, 31.143630),
        GeoPoint( 30.474500, 31.148790),
        GeoPoint( 30.473200, 31.149750),
        GeoPoint( 30.472380, 31.150340),
        GeoPoint( 30.471550, 31.150910),
        GeoPoint( 30.470700, 31.151450),
        GeoPoint( 30.469670, 31.152050),
        GeoPoint( 30.468950, 31.152440),
        GeoPoint( 30.468220, 31.152820),
        GeoPoint( 30.467490, 31.153170),
        GeoPoint( 30.466750, 31.153510),
        GeoPoint( 30.466000, 31.153820),
        GeoPoint( 30.464950, 31.154240),
        GeoPoint( 30.463880, 31.154620),
        GeoPoint( 30.462810, 31.154960),
        GeoPoint( 30.461730, 31.155260),
        GeoPoint( 30.460640, 31.155510),
        GeoPoint( 30.459330, 31.155800),
        GeoPoint( 30.458660, 31.155950),
        GeoPoint( 30.458000, 31.156160),
        GeoPoint( 30.457360, 31.156420),
        GeoPoint( 30.456740, 31.156730),
        GeoPoint( 30.456440, 31.156890),
        GeoPoint( 30.447490, 31.161900),
        GeoPoint( 30.446910, 31.162230),
        GeoPoint( 30.446350, 31.162580),
        GeoPoint( 30.445800, 31.162960),
        GeoPoint( 30.444400, 31.164030),
        GeoPoint( 30.443470, 31.164670),
        GeoPoint( 30.442840, 31.165060),
        GeoPoint( 30.442200, 31.165420),
        GeoPoint( 30.441540, 31.165750),
        GeoPoint( 30.441030, 31.165990),
        GeoPoint( 30.440510, 31.166210),
        GeoPoint( 30.439600, 31.166540),
        GeoPoint( 30.439160, 31.166680),
        GeoPoint( 30.437670, 31.167110),
        GeoPoint( 30.436620, 31.167430),
        GeoPoint( 30.435440, 31.167820),
        GeoPoint( 30.432300, 31.168950),
        GeoPoint( 30.431400, 31.169250),
        GeoPoint( 30.430510, 31.169520),
        GeoPoint( 30.429600, 31.169760),
        GeoPoint( 30.428690, 31.169980),
        GeoPoint( 30.427780, 31.170180),
        GeoPoint( 30.423500, 31.170960),
        GeoPoint( 30.423050, 31.171060),
        GeoPoint( 30.422610, 31.171190),
        GeoPoint( 30.422170, 31.171330),
        GeoPoint( 30.421740, 31.171500),
        GeoPoint( 30.421310, 31.171690),
        GeoPoint( 30.420890, 31.171890),
        GeoPoint( 30.420270, 31.172250),
        GeoPoint( 30.418890, 31.173130),
        GeoPoint( 30.418440, 31.173410),
        GeoPoint( 30.417980, 31.173660),
        GeoPoint( 30.417510, 31.173900),
        GeoPoint( 30.417030, 31.174110),
        GeoPoint( 30.416550, 31.174300),
        GeoPoint( 30.415560, 31.174630),
        GeoPoint( 30.415000, 31.174760),
        GeoPoint( 30.414440, 31.174860),
        GeoPoint( 30.413880, 31.174930),
        GeoPoint( 30.413310, 31.174970),
        GeoPoint( 30.412740, 31.174980),
        GeoPoint( 30.412170, 31.174960),
        GeoPoint( 30.411430, 31.174870),
        GeoPoint( 30.410910, 31.174790),
        GeoPoint( 30.410400, 31.174680),
        GeoPoint( 30.409890, 31.174540),
        GeoPoint( 30.409390, 31.174380),
        GeoPoint( 30.408290, 31.173950),
        GeoPoint( 30.407130, 31.173480),
        GeoPoint( 30.406470, 31.173230),
        GeoPoint( 30.405810, 31.172980),
        GeoPoint( 30.403740, 31.172120),
        GeoPoint( 30.402340, 31.171530),
        GeoPoint( 30.400950, 31.170910),
        GeoPoint( 30.399560, 31.170260),
        GeoPoint( 30.396470, 31.168770),
        GeoPoint( 30.394910, 31.168070),
        GeoPoint( 30.392900, 31.167210),
        GeoPoint( 30.385470, 31.163770),
        GeoPoint( 30.384470, 31.163310),
        GeoPoint( 30.382870, 31.162540),
        GeoPoint( 30.381260, 31.161800),
        GeoPoint( 30.379640, 31.161070),
        GeoPoint( 30.374060, 31.158480),
        GeoPoint( 30.373340, 31.158160),
        GeoPoint( 30.372290, 31.157650),
        GeoPoint( 30.371230, 31.157170),
        GeoPoint( 30.370170, 31.156710),
        GeoPoint( 30.369740, 31.156550),
        GeoPoint( 30.368810, 31.156200),
        GeoPoint( 30.367830, 31.155890),
        GeoPoint( 30.366840, 31.155630),
        GeoPoint( 30.365840, 31.155420),
        GeoPoint( 30.362770, 31.154880),
        GeoPoint( 30.348060, 31.152410),
        GeoPoint( 30.346620, 31.152150),
        GeoPoint( 30.345580, 31.151980),
        GeoPoint( 30.344620, 31.151840),
        GeoPoint( 30.343660, 31.151720),
        GeoPoint( 30.342700, 31.151630),
        GeoPoint( 30.341730, 31.151560),
        GeoPoint( 30.340850, 31.151520),
        GeoPoint( 30.339960, 31.151510),
        GeoPoint( 30.339070, 31.151530),
        GeoPoint( 30.338220, 31.151560),
        GeoPoint( 30.337380, 31.151610),
        GeoPoint( 30.335690, 31.151790),
        GeoPoint( 30.334850, 31.151910),
        GeoPoint( 30.334000, 31.152060),
        GeoPoint( 30.332640, 31.152360),
        GeoPoint( 30.331400, 31.152690),
        GeoPoint( 30.330480, 31.152960),
        GeoPoint( 30.329560, 31.153270),
        GeoPoint( 30.328650, 31.153600),
        GeoPoint( 30.327750, 31.153950),
        GeoPoint( 30.326850, 31.154330),
        GeoPoint( 30.325960, 31.154740),
        GeoPoint( 30.325090, 31.155190),
        GeoPoint( 30.319320, 31.158210),
        GeoPoint( 30.318810, 31.158470),
        GeoPoint( 30.318300, 31.158710),
        GeoPoint( 30.317780, 31.158940),
        GeoPoint( 30.317240, 31.159150),
        GeoPoint( 30.316710, 31.159340),
        GeoPoint( 30.316160, 31.159510),
        GeoPoint( 30.315620, 31.159660),
        GeoPoint( 30.315070, 31.159800),
        GeoPoint( 30.314510, 31.159910),
        GeoPoint( 30.313960, 31.160000),
        GeoPoint( 30.313400, 31.160070),
        GeoPoint( 30.312840, 31.160130),
        GeoPoint( 30.312280, 31.160160),
        GeoPoint( 30.311720, 31.160170),
        GeoPoint( 30.311150, 31.160160),
        GeoPoint( 30.310590, 31.160140),
        GeoPoint( 30.310030, 31.160090),
        GeoPoint( 30.309470, 31.160020),
        GeoPoint( 30.308700, 31.159910),
        GeoPoint( 30.301990, 31.158910),
        GeoPoint( 30.301320, 31.158830),
        GeoPoint( 30.300660, 31.158780),
        GeoPoint( 30.299990, 31.158760),
        GeoPoint( 30.299330, 31.158770),
        GeoPoint( 30.298660, 31.158810),
        GeoPoint( 30.298000, 31.158880),
        GeoPoint( 30.297370, 31.158960),
        GeoPoint( 30.296730, 31.159060),
        GeoPoint( 30.294750, 31.159420),
        GeoPoint( 30.290490, 31.160160),
        GeoPoint( 30.289930, 31.160240),
        GeoPoint( 30.289360, 31.160340),
        GeoPoint( 30.288800, 31.160450),
        GeoPoint( 30.288240, 31.160580),
        GeoPoint( 30.286640, 31.161040),
        GeoPoint( 30.285800, 31.161260),
        GeoPoint( 30.284960, 31.161450),
        GeoPoint( 30.284120, 31.161610),
        GeoPoint( 30.283270, 31.161740),
        GeoPoint( 30.282410, 31.161840),
        GeoPoint( 30.280850, 31.161970),
        GeoPoint( 30.280430, 31.162040),
        GeoPoint( 30.280010, 31.162130),
        GeoPoint( 30.279600, 31.162240),
        GeoPoint( 30.279190, 31.162370),
        GeoPoint( 30.278790, 31.162530),
        GeoPoint( 30.278390, 31.162710),
        GeoPoint( 30.278000, 31.162900),
        GeoPoint( 30.277620, 31.163120),
        GeoPoint( 30.277250, 31.163360),
        GeoPoint( 30.275740, 31.164430),
        GeoPoint( 30.274600, 31.165260),
        GeoPoint( 30.274090, 31.165560),
        GeoPoint( 30.273590, 31.165880),
        GeoPoint( 30.273100, 31.166220),
        GeoPoint( 30.272310, 31.166820),
        GeoPoint( 30.271190, 31.167660),
        GeoPoint( 30.270320, 31.168280),
        GeoPoint( 30.269740, 31.168750),
        GeoPoint( 30.268650, 31.169550),
        GeoPoint( 30.267550, 31.170270),
        GeoPoint( 30.266970, 31.170750),
        GeoPoint( 30.264440, 31.172560),
        GeoPoint( 30.263570, 31.173160),
        GeoPoint( 30.263210, 31.173370),
        GeoPoint( 30.262850, 31.173560),
        GeoPoint( 30.262480, 31.173730),
        GeoPoint( 30.262100, 31.173880),
        GeoPoint( 30.261720, 31.174010),
        GeoPoint( 30.261340, 31.174130),
        GeoPoint( 30.260950, 31.174220),
        GeoPoint( 30.260550, 31.174300),
        GeoPoint( 30.257110, 31.174820),
        GeoPoint( 30.256450, 31.174940),
        GeoPoint( 30.255780, 31.175080),
        GeoPoint( 30.255120, 31.175260),
        GeoPoint( 30.254470, 31.175460),
        GeoPoint( 30.253820, 31.175690),
        GeoPoint( 30.252720, 31.176120),
        GeoPoint( 30.247250, 31.178300),
        GeoPoint( 30.246420, 31.178610),
        GeoPoint( 30.246030, 31.178790),
        GeoPoint( 30.245650, 31.178980),
        GeoPoint( 30.245280, 31.179190),
        GeoPoint( 30.244920, 31.179430),
        GeoPoint( 30.244560, 31.179680),
        GeoPoint( 30.244220, 31.179940),
        GeoPoint( 30.243880, 31.180230),
        GeoPoint( 30.243540, 31.180540),
        GeoPoint( 30.243100, 31.181010),
        GeoPoint( 30.242830, 31.181320),
        GeoPoint( 30.242570, 31.181650),
        GeoPoint( 30.242330, 31.181990),
        GeoPoint( 30.242040, 31.182440),
        GeoPoint( 30.241910, 31.182660),
        GeoPoint( 30.241580, 31.183270),
        GeoPoint( 30.241270, 31.183890),
        GeoPoint( 30.240970, 31.184520),
        GeoPoint( 30.240070, 31.186350),
        GeoPoint( 30.239700, 31.187080),
        GeoPoint( 30.239500, 31.187440),
        GeoPoint( 30.239200, 31.187950),
        GeoPoint( 30.238930, 31.188370),
        GeoPoint( 30.238650, 31.188780),
        GeoPoint( 30.238350, 31.189180),
        GeoPoint( 30.238040, 31.189570),
        GeoPoint( 30.237540, 31.190150),
        GeoPoint( 30.236440, 31.191390),
        GeoPoint( 30.236190, 31.191680),
        GeoPoint( 30.235930, 31.191960),
        GeoPoint( 30.235670, 31.192230),
        GeoPoint( 30.235390, 31.192480),
        GeoPoint( 30.235100, 31.192710),
        GeoPoint( 30.234800, 31.192930),
        GeoPoint( 30.234490, 31.193140),
        GeoPoint( 30.233920, 31.193500),
        GeoPoint( 30.233650, 31.193650),
        GeoPoint( 30.233370, 31.193780),
        GeoPoint( 30.233080, 31.193900),
        GeoPoint( 30.232790, 31.193990),
        GeoPoint( 30.232490, 31.194070),
        GeoPoint( 30.232190, 31.194120),
        GeoPoint( 30.231880, 31.194160),
        GeoPoint( 30.231580, 31.194170),
        GeoPoint( 30.231280, 31.194170),
        GeoPoint( 30.230520, 31.194100),
        GeoPoint( 30.230080, 31.194030),
        GeoPoint( 30.229630, 31.193940),
        GeoPoint( 30.229230, 31.193830),
        GeoPoint( 30.228900, 31.193720),
        GeoPoint( 30.228570, 31.193600),
        GeoPoint( 30.227940, 31.193310),
        GeoPoint( 30.225970, 31.192380),
        GeoPoint( 30.225180, 31.192040),
        GeoPoint( 30.224390, 31.191720),
        GeoPoint( 30.222150, 31.190890),
        GeoPoint( 30.221600, 31.190720),
        GeoPoint( 30.221050, 31.190570),
        GeoPoint( 30.220490, 31.190450),
        GeoPoint( 30.219700, 31.190320),
        GeoPoint( 30.219480, 31.190290),
        GeoPoint( 30.219260, 31.190290),
        GeoPoint( 30.219090, 31.190230),
        GeoPoint( 30.218910, 31.190190),
        GeoPoint( 30.218460, 31.190160),
        GeoPoint( 30.216390, 31.190060),
        GeoPoint( 30.215990, 31.190030),
        GeoPoint( 30.215900, 31.190000),
        GeoPoint( 30.215750, 31.189930),
        GeoPoint( 30.215670, 31.189890),
        GeoPoint( 30.215500, 31.189760),
        GeoPoint( 30.215350, 31.189600),
        GeoPoint( 30.215230, 31.189410),
        GeoPoint( 30.215180, 31.189310),
        GeoPoint( 30.215110, 31.189110),
        GeoPoint( 30.215030, 31.188760),
        GeoPoint( 30.214950, 31.188530),
        GeoPoint( 30.214840, 31.188330),
        GeoPoint( 30.214700, 31.188140),
        GeoPoint( 30.214540, 31.187990),
        GeoPoint( 30.214430, 31.187930),
        GeoPoint( 30.214100, 31.187710),
        GeoPoint( 30.214010, 31.187620),
        GeoPoint( 30.213830, 31.187430),
        GeoPoint( 30.213690, 31.187190),
        GeoPoint( 30.213560, 31.186760),
        GeoPoint( 30.213590, 31.185050),
        GeoPoint( 30.213560, 31.184840),
        GeoPoint( 30.213540, 31.184720),
        GeoPoint( 30.213510, 31.184610),
        GeoPoint( 30.213430, 31.184390),
        GeoPoint( 30.213570, 31.179100),
        GeoPoint( 30.213590, 31.176440),
        GeoPoint( 30.213600, 31.175230),
        GeoPoint( 30.213490, 31.172830),
        GeoPoint( 30.213390, 31.171420),
        GeoPoint( 30.213320, 31.169960),
        GeoPoint( 30.213100, 31.167480),
        GeoPoint( 30.212910, 31.165440),
        GeoPoint( 30.212860, 31.164700),
        GeoPoint( 30.212920, 31.164500),
        GeoPoint( 30.212960, 31.164390),
        GeoPoint( 30.213050, 31.164260),
        GeoPoint( 30.213200, 31.164120),
        GeoPoint( 30.213250, 31.164090),
        GeoPoint( 30.213370, 31.164060),
        GeoPoint( 30.213430, 31.164060),
        GeoPoint( 30.213490, 31.164060),
        GeoPoint( 30.213620, 31.164110),
        GeoPoint( 30.213720, 31.164180),
        GeoPoint( 30.213770, 31.164240),
        GeoPoint( 30.213810, 31.164290),
        GeoPoint( 30.213840, 31.164360),
        GeoPoint( 30.213870, 31.164460),
        GeoPoint( 30.213890, 31.164680),
        GeoPoint( 30.213870, 31.164940),
        GeoPoint( 30.213850, 31.165050),
        GeoPoint( 30.213790, 31.165250),
        GeoPoint( 30.213710, 31.165400),
        GeoPoint( 30.213590, 31.165540),
        GeoPoint( 30.213460, 31.165640),
        GeoPoint( 30.213230, 31.165740),
        GeoPoint( 30.212120, 31.165890),
        GeoPoint( 30.211320, 31.166040),
        GeoPoint( 30.209610, 31.166370),
        GeoPoint( 30.209300, 31.166470),
        GeoPoint( 30.209030, 31.166540),
        GeoPoint( 30.208230, 31.166700),
        GeoPoint( 30.207750, 31.166770),
        GeoPoint( 30.207410, 31.166830),
        GeoPoint( 30.206340, 31.167050),
        GeoPoint( 30.205310, 31.167220),
        GeoPoint( 30.204720, 31.167340),
        GeoPoint( 30.204100, 31.167500),
        GeoPoint( 30.202880, 31.167870),
        GeoPoint( 30.202020, 31.168190),
        GeoPoint( 30.200940, 31.168660),
        GeoPoint( 30.200180, 31.169030),
        GeoPoint( 30.199910, 31.169170),
        GeoPoint( 30.198680, 31.169910),
        GeoPoint( 30.198330, 31.170140),
        GeoPoint( 30.197160, 31.170980),
        GeoPoint( 30.196220, 31.171710),
        GeoPoint( 30.194610, 31.173000),
        GeoPoint( 30.193680, 31.173780),
        GeoPoint( 30.193230, 31.174150),
        GeoPoint( 30.191710, 31.175270),
        GeoPoint( 30.191040, 31.175850),
        GeoPoint( 30.190360, 31.176520),
        GeoPoint( 30.188810, 31.177870),
        GeoPoint( 30.188070, 31.178460),
        GeoPoint( 30.187790, 31.178670),
        GeoPoint( 30.187500, 31.178850),
        GeoPoint( 30.187200, 31.179020),
        GeoPoint( 30.186620, 31.179280),
        GeoPoint( 30.186240, 31.179440),
        GeoPoint( 30.185990, 31.179530),
        GeoPoint( 30.185390, 31.179670),
        GeoPoint( 30.185240, 31.179680),
        GeoPoint( 30.185080, 31.179670),
        GeoPoint( 30.184920, 31.179640),
        GeoPoint( 30.182270, 31.178900),
        GeoPoint( 30.182080, 31.178830),
        GeoPoint( 30.181710, 31.178650),
        GeoPoint( 30.181060, 31.178280),
        GeoPoint( 30.180760, 31.178080),
        GeoPoint( 30.180220, 31.177760),
        GeoPoint( 30.179860, 31.177480),
        GeoPoint( 30.179730, 31.177390),
        GeoPoint( 30.179610, 31.177330),
        GeoPoint( 30.179560, 31.177330),
        GeoPoint( 30.179420, 31.177370),
        GeoPoint( 30.178980, 31.177600),
        GeoPoint( 30.178320, 31.177920),
        GeoPoint( 30.177620, 31.178230),
        GeoPoint( 30.176450, 31.178730),
        GeoPoint( 30.175750, 31.179060),
        GeoPoint( 30.175010, 31.179350),
        GeoPoint( 30.174840, 31.179400),
        GeoPoint( 30.174480, 31.179500),
        GeoPoint( 30.174290, 31.179540),
        GeoPoint( 30.173570, 31.179650),
        GeoPoint( 30.172820, 31.179750),
        GeoPoint( 30.172000, 31.179940),
        GeoPoint( 30.170860, 31.180300),
        GeoPoint( 30.170230, 31.180500),
        GeoPoint( 30.170080, 31.180540),
        GeoPoint( 30.169870, 31.180580),
        GeoPoint( 30.168130, 31.180730),
        GeoPoint( 30.168060, 31.180720),
        GeoPoint( 30.167930, 31.180690),
        GeoPoint( 30.167820, 31.180630),
        GeoPoint( 30.167690, 31.180510),
        GeoPoint( 30.166790, 31.179160),
        GeoPoint( 30.166670, 31.178950),
        GeoPoint( 30.166270, 31.178290),
        GeoPoint( 30.165540, 31.177240),
        GeoPoint( 30.165150, 31.176730),
        GeoPoint( 30.164810, 31.176330),
        GeoPoint( 30.164320, 31.176080),
        GeoPoint( 30.163780, 31.175850),
        GeoPoint( 30.162770, 31.175320),
        GeoPoint( 30.160820, 31.174230),
        GeoPoint( 30.158990, 31.173130),
        GeoPoint( 30.157880, 31.172540),
        GeoPoint( 30.156430, 31.171830),
        GeoPoint( 30.154870, 31.171010),
        GeoPoint( 30.153600, 31.170200),
        GeoPoint( 30.153430, 31.170120),
        GeoPoint( 30.152110, 31.169640),
        GeoPoint( 30.150740, 31.169240),
        GeoPoint( 30.150110, 31.169000),
        GeoPoint( 30.149770, 31.168860),
        GeoPoint( 30.149620, 31.168810),
        GeoPoint( 30.148120, 31.168380),
        GeoPoint( 30.147700, 31.168230),
        GeoPoint( 30.147420, 31.168110),
        GeoPoint( 30.147150, 31.167970),
        GeoPoint( 30.146070, 31.167360),
        GeoPoint( 30.145640, 31.167160),
        GeoPoint( 30.145130, 31.166980),
        GeoPoint( 30.144910, 31.166920),
        GeoPoint( 30.144460, 31.166860),
        GeoPoint( 30.144030, 31.166830),
        GeoPoint( 30.143840, 31.166810),
        GeoPoint( 30.143640, 31.166770),
        GeoPoint( 30.143450, 31.166710),
        GeoPoint( 30.143210, 31.166620),
        GeoPoint( 30.142810, 31.166440),
        GeoPoint( 30.141340, 31.165670),
        GeoPoint( 30.139490, 31.164670),
        GeoPoint( 30.138570, 31.164080),
        GeoPoint( 30.137990, 31.163750),
        GeoPoint( 30.137750, 31.163630),
        GeoPoint( 30.137600, 31.163560),
        GeoPoint( 30.137450, 31.163510),
        GeoPoint( 30.137310, 31.163470),
        GeoPoint( 30.137010, 31.163410),
        GeoPoint( 30.136150, 31.163340),
        GeoPoint( 30.135920, 31.163310),
        GeoPoint( 30.135690, 31.163280),
        GeoPoint( 30.134830, 31.163110),
        GeoPoint( 30.134100, 31.162970),
        GeoPoint( 30.133500, 31.162890),
        GeoPoint( 30.132540, 31.162820),
        GeoPoint( 30.132010, 31.162820),
        GeoPoint( 30.130710, 31.162900),
        GeoPoint( 30.129480, 31.163230),
        GeoPoint( 30.129300, 31.163270),
        GeoPoint( 30.129110, 31.163290),
        GeoPoint( 30.128920, 31.163290),
        GeoPoint( 30.128730, 31.163270),
        GeoPoint( 30.128110, 31.163140),
        GeoPoint( 30.127190, 31.163040),
        GeoPoint( 30.126940, 31.163070),
        GeoPoint( 30.125810, 31.163310),
        GeoPoint( 30.124830, 31.163440),
        GeoPoint( 30.123500, 31.163810),
        GeoPoint( 30.121940, 31.164290),
        GeoPoint( 30.121350, 31.164450),
        GeoPoint( 30.120460, 31.164650),
        GeoPoint( 30.120000, 31.164770),
        GeoPoint( 30.119750, 31.164850),
        GeoPoint( 30.119430, 31.164960),
        GeoPoint( 30.118830, 31.165200),
        GeoPoint( 30.118410, 31.165390),
        GeoPoint( 30.117290, 31.165860),
        GeoPoint( 30.116290, 31.166320),
        GeoPoint( 30.115450, 31.166670),
        GeoPoint( 30.115230, 31.166740),
        GeoPoint( 30.114940, 31.166800),
        GeoPoint( 30.113380, 31.167230),
        GeoPoint( 30.113060, 31.167330),
        GeoPoint( 30.112740, 31.167400),
        GeoPoint( 30.112260, 31.167430),
        GeoPoint( 30.111830, 31.167450),
        GeoPoint( 30.110930, 31.167450),
        GeoPoint( 30.110760, 31.167470),
        GeoPoint( 30.110390, 31.167540),
        GeoPoint( 30.109870, 31.167570),
        GeoPoint( 30.109260, 31.167630),
        GeoPoint( 30.108970, 31.167590),
        GeoPoint( 30.108200, 31.167580),
        GeoPoint( 30.107960, 31.167580),
        GeoPoint( 30.107280, 31.167660),
        GeoPoint( 30.107090, 31.167660),
        GeoPoint( 30.106370, 31.167590),
        GeoPoint( 30.105530, 31.167560),
        GeoPoint( 30.105280, 31.167520),
        GeoPoint( 30.105070, 31.167480),
        GeoPoint( 30.104390, 31.167260),
        GeoPoint( 30.104120, 31.167120),
        GeoPoint( 30.103810, 31.166930),
        GeoPoint( 30.102400, 31.166270),
        GeoPoint( 30.102100, 31.166120),
        GeoPoint( 30.101880, 31.166010),
        GeoPoint( 30.101030, 31.165630),
        GeoPoint( 30.100360, 31.165310),
        GeoPoint( 30.100050, 31.165210),
        GeoPoint( 30.099770, 31.165210),
        GeoPoint( 30.099080, 31.165320),
        GeoPoint( 30.098700, 31.165350),
        GeoPoint( 30.098200, 31.165380),
        GeoPoint( 30.097710, 31.165440),
        GeoPoint( 30.097490, 31.165500),
        GeoPoint( 30.097320, 31.165560),
        GeoPoint( 30.095910, 31.166160),
        GeoPoint( 30.094920, 31.166570),
        GeoPoint( 30.094500, 31.166760),
        GeoPoint( 30.094360, 31.166810),
        GeoPoint( 30.093670, 31.166910),
        GeoPoint( 30.093360, 31.166970),
        GeoPoint( 30.093100, 31.167030),
        GeoPoint( 30.092830, 31.167080),
        GeoPoint( 30.091550, 31.167250),
        GeoPoint( 30.091200, 31.167240),
        GeoPoint( 30.090950, 31.167210),
        GeoPoint( 30.090810, 31.167180),
        GeoPoint( 30.088340, 31.166650),
        GeoPoint( 30.086770, 31.166270),
        GeoPoint( 30.086550, 31.166180),
        GeoPoint( 30.086370, 31.166130),
        GeoPoint( 30.085990, 31.166050),
        GeoPoint( 30.083420, 31.165620),
        GeoPoint( 30.082390, 31.165430),
        GeoPoint( 30.082070, 31.165350),
        GeoPoint( 30.081720, 31.165250),
        GeoPoint( 30.081330, 31.165140),
        GeoPoint( 30.075200, 31.163920),
        GeoPoint( 30.074500, 31.163770),
        GeoPoint( 30.074100, 31.163660),
        GeoPoint( 30.072620, 31.163110),
        GeoPoint( 30.072230, 31.162980),
        GeoPoint( 30.071560, 31.162840),
        GeoPoint( 30.070670, 31.162640),
        GeoPoint( 30.070160, 31.162470),
        GeoPoint( 30.069960, 31.162430),
        GeoPoint( 30.069740, 31.162390),
        GeoPoint( 30.069540, 31.162380),
        GeoPoint( 30.069150, 31.162370),
        GeoPoint( 30.068950, 31.162380),
        GeoPoint( 30.068420, 31.162420),
        GeoPoint( 30.067760, 31.162530),
        GeoPoint( 30.067410, 31.162610),
        GeoPoint( 30.067090, 31.162710),
        GeoPoint( 30.066620, 31.162890),
        GeoPoint( 30.065840, 31.163210),
        GeoPoint( 30.064650, 31.163650),
        GeoPoint( 30.063500, 31.164040),
        GeoPoint( 30.062270, 31.164350),
        GeoPoint( 30.062090, 31.164390),
        GeoPoint( 30.062000, 31.164390),
        GeoPoint( 30.059310, 31.164310),
        GeoPoint( 30.058700, 31.164310),
        GeoPoint( 30.057290, 31.164280),
        GeoPoint( 30.056880, 31.164290),
        GeoPoint( 30.056340, 31.164390),
        GeoPoint( 30.055990, 31.164440),
        GeoPoint( 30.055500, 31.164530),
        GeoPoint( 30.055250, 31.164580),
        GeoPoint( 30.054030, 31.164960),
        GeoPoint( 30.053640, 31.165070),
        GeoPoint( 30.053240, 31.165160),
        GeoPoint( 30.052830, 31.165230),
        GeoPoint( 30.052480, 31.165280),
        GeoPoint( 30.052080, 31.165310),
        GeoPoint( 30.049910, 31.165440),
        GeoPoint( 30.049510, 31.165480),
        GeoPoint( 30.049120, 31.165530),
        GeoPoint( 30.048740, 31.165610),
        GeoPoint( 30.048350, 31.165710),
        GeoPoint( 30.047760, 31.165910),
        GeoPoint( 30.047340, 31.166090),
        GeoPoint( 30.047020, 31.166240),
        GeoPoint( 30.046600, 31.166460),
        GeoPoint( 30.044100, 31.168030),
        GeoPoint( 30.043680, 31.168290),
        GeoPoint( 30.043250, 31.168520),
        GeoPoint( 30.042700, 31.168760),
        GeoPoint( 30.042370, 31.168940),
        GeoPoint( 30.039870, 31.170140),
        GeoPoint( 30.039310, 31.170360),
        GeoPoint( 30.037100, 31.171130),
        GeoPoint( 30.036780, 31.171260),
        GeoPoint( 30.036460, 31.171410),
        GeoPoint( 30.036150, 31.171570),
        GeoPoint( 30.035840, 31.171770),
        GeoPoint( 30.035550, 31.171980),
        GeoPoint( 30.035270, 31.172210),
        GeoPoint( 30.033040, 31.174240),
        GeoPoint( 30.031550, 31.175580),
        GeoPoint( 30.031260, 31.175870),
        GeoPoint( 30.030890, 31.176300),
        GeoPoint( 30.030530, 31.176750),
        GeoPoint( 30.028270, 31.179780),
        GeoPoint( 30.027980, 31.180190),
        GeoPoint( 30.027670, 31.180570),
        GeoPoint( 30.027350, 31.180940),
        GeoPoint( 30.027010, 31.181290),
        GeoPoint( 30.026660, 31.181610),
        GeoPoint( 30.026290, 31.181920),
        GeoPoint( 30.025510, 31.182560),
        GeoPoint( 30.022740, 31.184760),
        GeoPoint( 30.022090, 31.185320),
        GeoPoint( 30.021840, 31.185560),
        GeoPoint( 30.021720, 31.185690),
        GeoPoint( 30.021210, 31.186300),
        GeoPoint( 30.020510, 31.187210),
        GeoPoint( 30.019340, 31.188700),
        GeoPoint( 30.019540, 31.188930),
        GeoPoint( 30.019720, 31.189220),
        GeoPoint( 30.020130, 31.189990),
        GeoPoint( 30.021150, 31.191820),
        GeoPoint( 30.021110, 31.191850),
        GeoPoint( 30.021070, 31.191910),
        GeoPoint( 30.021060, 31.191970),
        GeoPoint( 30.021070, 31.192050),
        GeoPoint( 30.021100, 31.192120),
        GeoPoint( 30.021140, 31.192180),
        GeoPoint( 30.021260, 31.192240),
        GeoPoint( 30.021320, 31.192260),
        GeoPoint( 30.021380, 31.192250),
        GeoPoint( 30.021640, 31.192730),
        GeoPoint( 30.022070, 31.193530),
        GeoPoint( 30.022840, 31.192910),
        GeoPoint( 30.023800, 31.193330),
        GeoPoint( 30.023930, 31.193230),

        )

    var index by remember { mutableStateOf(0) }
    var current by remember { mutableStateOf<GeoPoint?>(null) }
    var previous by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            if (index < realStreetPath.size - 1) {
                previous = realStreetPath[index]
                current = realStreetPath[index + 1]
                index++
            } else {
                index = 0
            }
            delay(2000L) // كل خطوة 1.5 ثانية
        }
    }

    fun parseGeoPoint(destination: String): GeoPoint {
        val parts = destination.split(",")
        return if (parts.size == 2) {
            try {
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                GeoPoint(latitude, longitude)
            } catch (e: NumberFormatException) {
                Log.e("parseGeoPoint", "Invalid coordinates format: $destination")
                GeoPoint(30.0444, 31.2357)
            }
        } else {
            Log.e("parseGeoPoint", "Invalid destination format: $destination")
            GeoPoint(30.0444, 31.2357)
        }
    }

    // Function to open Google Maps with navigation
    fun openGoogleMapsNavigation(origin: GeoPoint, destination: GeoPoint) {
        try {
            val uri = Uri.parse("https://www.google.com/maps/dir/?api=1" +
                    "&origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&travelmode=driving")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse("https://www.google.com/maps/dir/?api=1" +
                    "&origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&travelmode=driving")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    }

    // Show navigation button when trip is accepted
    LaunchedEffect(tripState.isAccepted) {
        showNavigationButton = tripState.isAccepted && originPoint != null && destinationPoint != null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DriverNavigationDrawer(navController)
                }
            },
            gesturesEnabled = drawerState.isOpen,
            modifier = Modifier.fillMaxSize()
        ) {
            driverHomeScreenContent(navController)
            val passengerLocation: LocationViewModel5 = viewModel()
            val tripViewModel4: TripViewModel4 = viewModel()
            val tripLocation by tripViewModel4.tripLocation

            LaunchedEffect(Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                    val savedLocation = locationDataStore.getLocation()
                    savedLocation?.let { (lat, lng) ->
                        val latLng = LatLng(lat, lng)
                        passengerLocation.updatePassengerLocation(latLng)
                    }
                }
            }

            val mapStateViewModel: MapStateViewModel = viewModel()
            val shouldShowTracking by mapStateViewModel.shouldShowTracking
            val db = FirebaseFirestore.getInstance()
            val directions = remember { mutableStateListOf<GeoPoint>() }
            var isDataLoading by remember { mutableStateOf(true) }
            var driverLocation2 by remember { mutableStateOf(GeoPoint(30.0444, 31.2357)) }

            LaunchedEffect(mapStateViewModel.isTripInProgress.value) {
                if (mapStateViewModel.isTripInProgress.value) {
                    isDataLoading = true
                    try {
                        val querySnapshot = db.collection("trips")
                            .whereEqualTo("_id", tripId)
                            .get()
                            .await()

                        if (!querySnapshot.isEmpty) {
                            val document = querySnapshot.documents.first()
                            destination = document.get("destination") as? String
                              passengerID = document.get("userId") as? String

                            if (passengerID != null) {
                                val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                                sharedPreferences.edit()
                                    .putString("passenger_id", passengerID)
                                    .apply() // أو .commit() لو عايز تنتظر الحفظ
                                Log.d("TripCheck", "💾 passengerID saved in SharedPreferences: $passengerID")
                            } else {
                                Log.d("TripCheck", "⚠️ passengerID is null, not saved.")
                            }

                            passengerID?.let { id ->
                                try {
                                    val query = FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .whereEqualTo("id", id)
                                        .limit(1)
                                        .get()
                                        .await()

                                    passengerName = query.documents.firstOrNull()
                                        ?.getString("name") ?: "مستخدم غير معروف"
                                } catch (e: Exception) {
                                    Log.e("Firestore", "Error fetching user name", e)
                                    passengerName = "مستخدم غير معروف"
                                }
                            }

                            fare = document.get("fare") as? Double ?: 0.0
                            val sharedPref = context.getSharedPreferences("trip_prefs", Context.MODE_PRIVATE)
                            sharedPref.edit().putString("fare", fare.toString()).apply()

                            distance = document.get("distanceInKm") as? Double ?: 0.0

                            val originMap = document.get("originMap") as? Map<String, Any>
                            val originLat = originMap?.get("lat") as? Double
                            val originLng = originMap?.get("lng") as? Double

                            val destinationMap = document.get("destinationMap") as? Map<String, Any>
                            val destinationLat = destinationMap?.get("lat") as? Double
                            val destinationLng = destinationMap?.get("lng") as? Double

                            if (originLat != null && originLng != null && destinationLat != null && destinationLng != null) {
                                passengerLocation2 = GeoPoint(originLat, originLng)
                                driverLocation2 = GeoPoint(destinationLat, destinationLng)

                                val result = DirectionsApi.getDirections(
                                    start = passengerLocation2,
                                    end = driverLocation2,
                                    apiKey = "c69abe50-60d2-43bc-82b1-81cbdcebeddc",
                                    context = context,
                                    tripId = tripId!!
                                )

                                when (result) {
                                    is ResultWrapper.Success -> {
                                        val response = result.value
                                        val encodedPolyline = response.paths.firstOrNull()?.points

                                        if (encodedPolyline != null) {
                                            val decodedPoints = PolyUtil.decode(encodedPolyline).map {
                                                GeoPoint(it.latitude, it.longitude)
                                            }
                                            directions.clear()
                                            directions.addAll(decodedPoints)
                                            Log.d("Polyline", "Encoded polyline: $encodedPolyline")
                                            Log.d("time", "Encoded polyline: ${response.paths.firstOrNull()?.time}")
                                            Log.d("instructions", "Encoded polyline: ${response.paths.firstOrNull()?.instructions}")

                                            val updates = mapOf(
                                                "points" to encodedPolyline,
                                                "instructions" to (response.paths.firstOrNull()?.instructions ?: listOf()),
                                                "time" to (response.paths.firstOrNull()?.time ?: 0)
                                            )
                                            document.reference.update(updates)
                                                .addOnSuccessListener {
                                                    Log.d("Firestore33", "تم تحديث بيانات الرحلة بنجاح")
                                                }
                                                .addOnFailureListener {
                                                    Log.e("Firestore33", "فشل في تحديث البيانات: ${it.message}")
                                                }
                                        }
                                    }
                                    is ResultWrapper.Failure -> {
                                        Log.e("Directions", "فشل في جلب الاتجاهات: ${result.exception.message}")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error fetching trip data: ${e.message}")
                    } finally {
                        isDataLoading = false
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (mapStateViewModel.isTripInProgress.value) {
                    InProgressMap(
                        directions = directions,
                        currentLocation = currentLocation2,
                        previousLocation = previousLocation2,
                    )

                    if (isDataLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.7f)
                        ) {
                            CircularProgressIndicator(
                                color = Color.Blue,
                                strokeWidth = 5.dp
                            )
                        }
                    }
                } else if (shouldShowTracking) {
                    tripId?.let {
                        TrackDriverScreen(
                            tripId = it,
                            passengerLocation = passengerLocation2
                        )
                    }
                } else {
                    com.example.capital_taxi.utils.DriverMapView(
                        currentLocation = currentLocation2,
                        previousLocation = previousLocation2,
                    )

                }


                fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
                    val earthRadius = 6371.0 // كيلومتر

                    val dLat = Math.toRadians(lat2 - lat1)
                    val dLon = Math.toRadians(lon2 - lon1)

                    val a = sin(dLat / 2) * sin(dLat / 2) +
                            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                            sin(dLon / 2) * sin(dLon / 2)

                    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

                    return earthRadius * c
                }

                LaunchedEffect(tripState.isStart) {
                    while (tripState.isStart && !tripState.isAccepted && !tripState.isCancelled) {
                        delay(2000) // تأخير 2000 ملي ثانية

                        if (tripState.isStart && !tripState.isAccepted && !tripState.isCancelled) {
                            val driverLocation = currentLocation2 ?: continue

                            tripViewModel.fetchTripsFromFirestore(
                                onSuccess = { trips ->
                                    availableTrips = trips.filter { trip ->
                                        if (trip.status != "pending" || trip._id == tripId) return@filter false

                                        try {
                                            sharedPreferences.edit().putString("active_trip_id", trip._id).apply()

                                            val originMap = trip.originMap ?: return@filter false
                                            val passengerLat = (originMap["lat"] as? Number)?.toDouble() ?: return@filter false
                                            val passengerLng = (originMap["lng"] as? Number)?.toDouble() ?: return@filter false

                                            val distance = calculateDistance(
                                                driverLocation.latitude,
                                                driverLocation.longitude,
                                                passengerLat,
                                                passengerLng
                                            )

                                            val maxDistance = 5000000.0

                                            distance <= maxDistance
                                        } catch (e: Exception) {
                                            Log.e("DistanceFilter", "Error calculating distance", e)
                                            false
                                        }
                                    }
                                },
                                onError = { Log.e("driverHomeScreen", "❌ $it") },
                                driverId = driver_id
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (tripState.isStart && !tripState.isAccepted) {
                        tripLocation?.let {
                            updateLocationAndStatus(
                                driverId = driverId,
                                location = it
                            )
                        }
                        requestLocationUpdates(
                            fusedLocationClient = fusedLocationClient,
                            firestore = firestore,
                            driverId = driverId,
                            context = context
                        )
                    }
                    val tripDetailsViewModel: TripDetailsViewModel = viewModel()
                    val tripViewModel2: dataTripViewModel = viewModel()
                    val passengerData by tripDetailsViewModel.passengerData.collectAsState()
                    availableTrips.firstOrNull()?.let { trip ->
                        if (tripState.isStart && !tripState.isAccepted) {
                            TripListener(tripId = trip._id)
                            val token = sharedPreferences.getString("driver-token", null)

                            sharedPreferences.edit().putString("active_trip_id", trip._id).apply()

                            TripDetailsCard(
                                light = false,
                                trip = trip,
                                availableTrips = availableTrips,
                                tripViewModel = tripViewModel,
                                onTripAccepted = {
                                    mapStateViewModel.enableTracking()
                                    accepttrip.acceptTrip()
                                    sharedPreferences.edit().putString("active_trip_id", trip._id).apply()


                                    stateTripViewModel.setAccepted()
                                  tripDetailsViewModel.setTripId(trip._id)
                                    tripId = trip._id
                                    val destinationPoint = parseGeoPoint(trip.origin)
                                    val start = parseGeoPoint(trip.destination)
                                    tripViewModel2.setTripDetails(trip.origin, trip.destination)
                                    Log.d("TripDetails", "${trip.origin} ${trip.destination}")
                                    stateTripViewModel.setStart(false)
// خزّن الـ tripId
                                    availableTrips = availableTrips.filter { it._id != trip._id }
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (token != null) {
                                            fetchTripDirections(
                                                token = token,
                                                origin = Location(start.latitude, start.longitude),
                                                destination = Location(
                                                    destinationPoint.latitude,
                                                    destinationPoint.longitude
                                                ),
                                                directionsViewModel = directionsViewModel,
                                                onSuccess = { directionsResponse ->


                                                    sharedPreferences.edit().putString("active_trip_id", trip._id).apply()

                                                    Log.d(
                                                        "TripDirections",
                                                        "Successfully fetched directions: $directionsResponse"
                                                    )
                                                },
                                                onError = { errorMessage ->
                                                    Log.e(
                                                        "TripDirections",
                                                        "Error fetching directions: $errorMessage"
                                                    )
                                                }
                                            )
                                        }
                                    }

                                    driverLocation?.let { location ->
                                        saveDriverLocationToRealtimeDatabase(trip._id, location)
                                    } ?: run {
                                        Log.e("DriverLocation", "Driver location is null")
                                    }
                                },
                                onTripCancelled = {
                                    availableTrips = availableTrips.filter { it._id != trip._id }
                                },
                                userId2 = passengerData?.id ?: "",
                                rating = passengerData?.rating?.toString() ?: "0.0",

                            )
                        }
                    }
                }

                val balance = DriverViewModel.balance.value

                LaunchedEffect(driverId) {
                    DriverViewModel.observeDriverBalance(driverId)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopBar(
                            onOpenDrawer = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            },
                            navController = navController
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            onClick = {},
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(60.dp)
                                .padding(end = 80.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (balance != null) String.format("%.2f EGP", balance) else "Loading...",
                                    fontSize = responsiveTextSize(
                                        fraction = 0.06f,
                                        minSize = 14.sp,
                                        maxSize = 18.sp
                                    ),
                                    fontFamily = CustomFontFamily,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    if (tripState.inProgress ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(top = 500.dp, end = 16.dp)
                        ) {
                            Button(
                                onClick = {
                                    openGoogleMapsNavigation(currentLocation2!!, originPoint!!)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(40.dp)
                            ) {
                                Text(
                                    text = "Navigate to Passenger",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (tripState.isTripBegin ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(top = 500.dp, end = 16.dp)
                        ) {
                            Button(
                                onClick = {
                                    openGoogleMapsNavigation(currentLocation2!!, destinationPoint!!)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(40.dp)
                            ) {
                                Text(
                                    text = "Navigate to Destination",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(accepttripViewModel) {
                if (accepttripViewModel) {
                    delay(2000)
                    stateTripViewModel.setInProgress()
                }
            }

            LaunchedEffect(startTrip) {
                if (startTrip) {
                    stateTripViewModel.beginTrip()
                }
            }

            LaunchedEffect(EndTrip) {
                if (EndTrip) {
                    stateTripViewModel.TripEnd()
                }
            }

            if (!tripState.isAccepted && !tripState.inProgress) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(130.dp)
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                ) {
                    DriverControls(
                        onClick = {stateTripViewModel.setStart(true) },
                        onClick2 = {stateTripViewModel.setStart(false) },
                        driverId = driverId,
                        tripLocation = tripLocation,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }

            if (tripState.inProgress) {
                captainToPassenger(
                    context = context,
                    navController = navController,
                    tripId = tripId!!,
                    mapchangetoInPrograss = { mapStateViewModel.startTrip() },
                    onTripStarted = { accepttrip.startTrip() },
                    passengerName = passengerName ?: "Loading",
                    rating = rating.toString()?:"",
                    userId2 = passengerID
                )
            }

            if (tripState.isTripBegin) {
                StartTrip(
                    tripId!!, TripEnd = { accepttrip.EndTrip() }, driverId,
                    totalFare = fare!!
                )
            }

            if (tripState.isEnd) {
                TripArrivedCard2(
                    destination = destination ?: "",
                    fare = fare.toString(),
                    distance = distance.toString(),
                    tripId = tripId!!,
                    userId = passengerName,
                    driverId = driver_id ?: "1234",
                    onProblemSubmitted = {
                        Toast.makeText(context, "Problem reported successfully", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onclick = {
                        sharedPreferences.edit().remove("active_trip_id").apply()

                        storedPoints = null
                        stateTripViewModel.resetAll()
                        navController.navigate(Destination.DriverHomeScreen.route) {
                            popUpTo(Destination.DriverHomeScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    userIdToRate = passengerID!!
                )
            }
            if (showCancellationDialog) {
                AlertDialog(
                    onDismissRequest = {
                        sharedPreferences.edit().remove("active_trip_id").apply()

                        storedPoints=null

                        showCancellationDialog = false
                        stateTripViewModel.resetAll()
                        navController.navigate(Destination.DriverHomeScreen.route) {
                            popUpTo(Destination.DriverHomeScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    title = {
                        Text(text = "Trip Cancellation")
                    },
                    text = {
                        Text(text = "The trip has been cancelled by the passenger")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                sharedPreferences.edit().remove("active_trip_id").apply()

                                storedPoints=null

                                showCancellationDialog = false
                                stateTripViewModel.resetAll()
                                navController.navigate(Destination.DriverHomeScreen.route) {
                                    popUpTo(Destination.DriverHomeScreen.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("OK", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

class TripDetailsViewModel : ViewModel() {
    private val _tripId = MutableStateFlow<String?>(null)
    val tripId: StateFlow<String?> = _tripId.asStateFlow()

    private val _passengerData = MutableStateFlow<PassengerData?>(null)
    val passengerData: StateFlow<PassengerData?> = _passengerData.asStateFlow()

    private var firestoreListener: ListenerRegistration? = null

    fun setTripId(newTripId: String) {
        _tripId.value = newTripId
        setupFirestoreListeners(newTripId)
    }

    private fun setupFirestoreListeners(tripId: String) {
        firestoreListener?.remove()

        val query = FirebaseFirestore.getInstance()
            .collection("trips")
            .whereEqualTo("_id", tripId)

        firestoreListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TripDetails", "Error listening to trip", error)
                return@addSnapshotListener
            }

            snapshot?.documents?.firstOrNull()?.let { document ->
                // معالجة بيانات الرحلة
                val originMap = document.get("originMap") as? Map<String, Any>
                val destinationMap = document.get("destinationMap") as? Map<String, Any>
                val userId = document.get("userId") as? String

                // تحديث بيانات الراكب
                userId?.let { fetchPassengerData(it) }
            }
        }
    }

    private fun fetchPassengerData(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("id", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userDoc = querySnapshot.documents.firstOrNull()
                val ratingMap = userDoc?.get("rating") as? Map<*, *>

                val passengerData = PassengerData(
                    id = userId,
                    name = userDoc?.getString("name") ?: "مستخدم غير معروف",
                    rating = calculateRating(ratingMap)
                )

                _passengerData.value = passengerData
            }
            .addOnFailureListener { e ->
                Log.e("TripDetails", "Error fetching passenger data", e)
            }
    }

    private fun calculateRating(ratingMap: Map<*, *>?): Double {
        val count = (ratingMap?.get("count") as? Number)?.toInt() ?: 0
        val total = (ratingMap?.get("total") as? Number)?.toInt() ?: 0
        return if (count > 0) total.toDouble() / count else 0.0
    }

    override fun onCleared() {
        super.onCleared()
        firestoreListener?.remove()
    }
}

data class PassengerData(
    val id: String,
    val name: String,
    val rating: Double
)@Composable
fun TripListener(
    tripId: String?,
    tripViewModel: TripDetailsViewModel = viewModel()
) {
    DisposableEffect(tripId) {
        if (tripId != null) {
            tripViewModel.setTripId(tripId)
        }

        onDispose {
            // يتم التعامل مع إزالة الـ listeners داخل الـ ViewModel
        }
    }
}class DriverViewModel : ViewModel() {

    private val _balance = mutableStateOf<Double?>(null)
    val balance: State<Double?> = _balance

    private var listenerRegistration: ListenerRegistration? = null

    fun observeDriverBalance(driverId: String) {
        val db = Firebase.firestore

        // ألغِ أي Listener سابق
        listenerRegistration?.remove()

        listenerRegistration = db.collection("drivers")
            .whereEqualTo("id", driverId)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _balance.value = 0.0
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    val balanceValue = doc.getDouble("balance") ?: 0.0
                    _balance.value = balanceValue
                } else {
                    _balance.value = 0.0
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        // ألغِ الاستماع عند تدمير ViewModel
        listenerRegistration?.remove()
    }
}
