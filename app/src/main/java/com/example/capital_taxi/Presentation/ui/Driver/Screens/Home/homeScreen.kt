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
import com.example.myapplication.DriverMapView
import com.example.myapplication.interpolateLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
// بعد سطر تعبئة originPoint و destinationPoint في Listener
                Log.d("LocationPoints", "Origin: ${originPoint?.latitude},${originPoint?.longitude}")
                Log.d("LocationPoints", "Destination: ${destinationPoint?.latitude},${destinationPoint?.longitude}")
                // Navigation Button - Only shown when trip is accepted


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
                                            val originMap = trip.originMap ?: return@filter false
                                            val passengerLat = (originMap["lat"] as? Number)?.toDouble() ?: return@filter false
                                            val passengerLng = (originMap["lng"] as? Number)?.toDouble() ?: return@filter false

                                            val distance = calculateDistance(
                                                driverLocation.latitude,
                                                driverLocation.longitude,
                                                passengerLat,
                                                passengerLng
                                            )

                                            val maxDistance = 5.0

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
                            TripDetailsCard(
                                light = false,
                                trip = trip,
                                availableTrips = availableTrips,
                                tripViewModel = tripViewModel,
                                onTripAccepted = {
                                    mapStateViewModel.enableTracking()
                                    accepttrip.acceptTrip()
                                    stateTripViewModel.setAccepted()
                                  tripDetailsViewModel.setTripId(trip._id)
                                    tripId = trip._id
                                    val destinationPoint = parseGeoPoint(trip.origin)
                                    val start = parseGeoPoint(trip.destination)
                                    tripViewModel2.setTripDetails(trip.origin, trip.destination)
                                    Log.d("TripDetails", "${trip.origin} ${trip.destination}")
                                    stateTripViewModel.setStart(false)

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
                                    text = "0.00 EGB",
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
                                .padding(top = 270.dp, end = 16.dp)
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
                                .padding(top = 270.dp, end = 16.dp)
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

            if (!tripState.isAccepted) {
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
                    rating = rating!!.toString(),
                    userId2 = passengerID
                )
            }

            if (tripState.isTripBegin) {
                StartTrip(tripId!!, TripEnd = { accepttrip.EndTrip() },driverId)
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
}