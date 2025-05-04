package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home

import TopBar
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint

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

    // Listen for trip status changes
    DisposableEffect(tripId) {
        var documentListener: ListenerRegistration? = null

        onDispose {
            documentListener?.remove()
            tripListener?.remove()
        }

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

                    documentListener = document.reference.addSnapshotListener { snapshot, error2 ->
                        if (error2 != null) {
                            Log.e("TripStatus", "Error listening to trip status", error2)
                            return@addSnapshotListener
                        }

                        snapshot?.let { doc ->
                            val status = doc.getString("status") ?: "pending"
                            if (status == "Cancelled" && !tripState.isCancelled) {
                                stateTripViewModel.setCancelled()
                                // Update the toast message to English
                                Toast.makeText(context, "Trip cancelled by passenger", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
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
            interval = 5000
            fastestInterval = 5000
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
    // يمكنك استخدام حالة أكثر تعقيدًا لإدارة الموقع إذا لزم الأمر
    // var smoothedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    // var rawLocation by remember { mutableStateOf<GeoPoint?>(null) }

    val fusedLocationClient2 = remember { LocationServices.getFusedLocationProviderClient(context) }

    // طلب تحديثات الموقع
    LaunchedEffect(Unit) {
        // تأكد من وجود إذن تحديد الموقع
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.create().apply {
                interval = 2000 // تحديث كل ثانيتين
                fastestInterval = 1000 // أسرع تحديث كل ثانية
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let { location ->
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        // تحديث الموقع السابق والحالي
                        previousLocation2 = currentLocation2
                        currentLocation2 = newPoint

                        // يمكنك إضافة منطق التنعيم هنا إذا أردت
                        // val smoothed = if (smoothedLocation == null) newPoint else interpolateLocation(smoothedLocation!!, newPoint, 0.3f)
                        // previousLocation = smoothedLocation
                        // currentLocation = smoothed
                        // smoothedLocation = smoothed
                    }
                }
            }

            // بدء طلب تحديثات الموقع
            fusedLocationClient2.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // يمكنك إضافة منطق لإيقاف التحديثات عند الخروج من الشاشة
            // currentCoroutineContext().job.invokeOnCompletion { fusedLocationClient.removeLocationUpdates(locationCallback) }
        } else {
            // طلب الإذن إذا لم يكن ممنوحًا
            // يجب التعامل مع حالة عدم منح الإذن
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
            var passengerLocation2 by remember { mutableStateOf(GeoPoint(30.0444, 31.2357)) }
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

                            // Fetch passenger name
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

                                            // Update trip with directions data
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
                        driverLocation = passengerLocation2,
                        Destination = driverLocation2
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

                LaunchedEffect(tripState.isStart) {
                    while (tripState.isStart && !tripState.isAccepted && !tripState.isCancelled) {
                        delay(2000)
                        if (tripState.isStart && !tripState.isAccepted && !tripState.isCancelled) {
                            tripViewModel.fetchTripsFromFirestore(
                                onSuccess = { trips ->
                                    availableTrips = trips.filter {
                                        it.status == "pending" && it._id != tripId
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

                    val tripViewModel2: dataTripViewModel = viewModel()
                    availableTrips.firstOrNull()?.let { trip ->
                        if (tripState.isStart && !tripState.isAccepted) {
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
                                }
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
                        onClick = { stateTripViewModel.setStart(true) },
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
                )
            }

            if (tripState.isTripBegin) {
                StartTrip(tripId!!, TripEnd = { accepttrip.EndTrip() })
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
                        Toast.makeText(context, "Problem reported successfully", Toast.LENGTH_SHORT).show()
                    },
                    onclick = {
                        stateTripViewModel.resetAll()
                        navController.navigate(Destination.DriverHomeScreen.route) {
                            popUpTo(Destination.DriverHomeScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            if (showCancellationDialog) {
                AlertDialog(
                    onDismissRequest = {
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