package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home

import TopBar
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Presentation.ui.Driver.Components.DriverControls
import com.example.capital_taxi.Presentation.ui.Driver.Components.MapStateViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.DriverArrivedCard
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.DriverNavigationDrawer
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.TripViewModel2
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.TripViewModel4
 import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.updateMapWithCurrentLocation
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.StartTrip
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.StartTripScreen
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.TripArrivedCard2
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.TripDetailsCard
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.captainToPassenger
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.dataTripViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverHomeScreenContent
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverlocation
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationDataStore
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationViewModel5
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.TrackDriverScreen

import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DirectionsViewModel
import com.example.capital_taxi.domain.Location
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.driver.model.acceptTripViewModel
import com.example.capital_taxi.domain.fetchTripDirections
import com.example.capital_taxi.domain.shared.TripViewModel
import com.example.capital_taxi.domain.shared.decodePolyline
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.IOException
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("UnrememberedMutableState")
@Composable
fun driverHomeScreen(navController: NavController) {
    var isStart by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val tripViewModel = remember { TripViewModel() }
    var availableTrips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var isTripAccepted by remember { mutableStateOf(false) }
    var isSearchingForTrip by remember { mutableStateOf(false) }
    var isCanceled by remember { mutableStateOf(false) }
    var startPoint = remember { mutableStateOf<GeoPoint?>(null) }
    var endPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val context = LocalContext.current
    val sharedPreferences =
        context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("driver_token", "") ?: ""
    val driver_id = sharedPreferences.getString("driver_id", "") ?: ""
    val directionsViewModel: DirectionsViewModel = viewModel()

    val viewmodel: driverlocation = viewModel()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val firestore = FirebaseFirestore.getInstance()
    val driverId = driver_id // يجب أن يكون معرف السائق الحقيقي
    var driverLocation = viewmodel.driverLocation.value
    var driverLocationState2 by remember { mutableStateOf<GeoPoint?>(null) }

    var driverLocationState by remember { mutableStateOf<GeoPoint?>(null) }
    var previousLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var carBearing by remember { mutableStateOf(0f) }
    val locationDataStore = LocationDataStore(context)
    var tripStatus by remember { mutableStateOf("pending") } // الحالة الابتدائية
    var tripId by remember { mutableStateOf<String?>(null) }
    val accepttrip: acceptTripViewModel = viewModel()
    val accepttripViewModel by accepttrip.isTripAccepted
    val startTrip by accepttrip.isTripStarted

     // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()
    var rotationAngle by mutableStateOf(0f)

    var rawLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var smoothedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    // تحديث الخريطة واستدعاء getDirections كل ثانيتين
    LaunchedEffect(isTripAccepted, startPoint.value, endPoint.value) {
        while (isTripAccepted && startPoint.value != null && endPoint.value != null) {
            delay(2000) // انتظر ثانيتين

            // تحديث الخريطة
            Log.d("MapUpdate", "Updating map with new points: $startPoint, $endPoint")

            // استدعاء getDirections
            val origin = Location(startPoint.value!!.latitude, startPoint.value!!.longitude)
            val destination = Location(endPoint.value!!.latitude, endPoint.value!!.longitude)

            val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("driver-token", null)

            if (token != null) {
                fetchTripDirections(
                    token = token,
                    origin = origin,
                    destination = destination,
                    directionsViewModel = directionsViewModel,
                    onSuccess = { directionsResponse ->
                        Log.d(
                            "TripDirections",
                            "Successfully fetched directions: $directionsResponse"
                        )
                    },
                    onError = { errorMessage ->
                        Log.e("TripDirections", "Error fetching directions: $errorMessage")
                    }
                )
            }
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
                    location?.let {
                        driverLocationState = GeoPoint(it.latitude, it.longitude) // تحديث الموقع
                    }
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
        context: Context // تمرير السياق للتحقق من الإذن
    ) {
        // التحقق من الأذونات
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // إذا لم يكن لدينا الإذن، لا نستدعي تحديثات الموقع
            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // تحديث كل 5 ثواني
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
    LaunchedEffect(Unit) {

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.create().apply {
                interval = 2000 // تحديث كل ثانية
                fastestInterval = 5000
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let { location ->
                        previousLocation = smoothedLocation
                        rawLocation = GeoPoint(location.latitude, location.longitude)

                        // تطبيق تنعيم إضافي
                        smoothedLocation = if (smoothedLocation == null) {
                            rawLocation
                        } else {
                            interpolateLocation(
                                smoothedLocation!!,
                                rawLocation!!,
                                0.5f // عامل التنعيم (0.1 - 0.5)
                            )
                        }
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
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
                GeoPoint(30.0444, 31.2357) // Default location (Cairo)
            }
        } else {
            Log.e("parseGeoPoint", "Invalid destination format: $destination")
            GeoPoint(30.0444, 31.2357) // Default location (Cairo)
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
            val driverDatabase = FirebaseDatabase.getInstance()

// ✅ تحقق مما إذا كان tripId غير null قبل الوصول إلى بيانات Firebase
            tripId?.let { id ->
                val driverTripRef = driverDatabase.getReference("trips").child(id)

                driverTripRef.child("passengerLocation").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val lat = snapshot.child("latitude").getValue(Double::class.java)
                        val lng = snapshot.child("longitude").getValue(Double::class.java)

                        if (lat != null && lng != null) {
                            val passengerGeoPoint = GeoPoint(lat, lng)
                            Log.d("DriverView", "📍 Passenger Location: $passengerGeoPoint")

                            driverLocationState2 = passengerGeoPoint
                        } else {
                            Log.e("DriverView", "❌ Passenger location not found in Firebase")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("DriverView", "❌ Error fetching passenger location: ${error.message}")
                    }
                })
            } ?: Log.e("DriverView", "❌ tripId is null, skipping Firebase reference")


            Box(modifier = Modifier.fillMaxSize()) {


                if ( shouldShowTracking) {
                    Log.d("LocationCheck2", "Before passing: ${passengerLocation.passengerLocation}")

                    tripId?.let {
                        Log.d("tripId", it)
                        Log.d("Location3", "✅pass location: ${ passengerLocation.passengerLocation?.let { latLng ->
                            GeoPoint(latLng.latitude, latLng.longitude) // ✅ تحويل LatLng إلى GeoPoint
                        }}")
                        TrackDriverScreen(
                            tripId = it,

                            passengerLocation = driverLocationState2?: GeoPoint(30.0444, 31.2357)
                        )
                    }
                }
               else {
                    DriverMapView(
                        currentLocation = smoothedLocation,
                        previousLocation = previousLocation,

                    )
                    Log.d("LocationCheck1", "Before passing: ${passengerLocation.passengerLocation}")

                }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(2000)
                        if (isStart && !isTripAccepted && !isCanceled) {
                            isSearchingForTrip = true

                            tripViewModel.fetchTripsFromFirestore(
                                onSuccess = { trips ->
                                    isSearchingForTrip = false
                                    availableTrips = trips.filter { it.status == "pending" }

                                    availableTrips.firstOrNull()?.let { trip ->
                                        driverLocation?.let { location ->
                                            endPoint.value = parseGeoPoint(trip.destination)
                                        }
                                    }
                                },
                                onError = { Log.e("driverHomeScreen", "❌ $it") }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isStart && !isTripAccepted) {
                        tripLocation?.let {
                            updateLocationAndStatus(
                                driverId =driverId,
                                location = it
                            )
                        }
                        requestLocationUpdates(
                            fusedLocationClient = fusedLocationClient,
                            firestore = firestore,
                            driverId = driverId,
                            context = context
                        )
                        if (isSearchingForTrip) {
                            Text(
                                text = "Searching for a ride for you...",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    val tripViewModel2: dataTripViewModel = viewModel()

                    availableTrips.firstOrNull()?.let { trip ->
                        if (isStart && !isTripAccepted) {
                            val sharedPreferences =
                                context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                            val token = sharedPreferences.getString("driver-token", null)

                            TripDetailsCard(
                                light = false,
                                trip = trip,
                                availableTrips = availableTrips,

                                 tripViewModel = tripViewModel,
                                onTripAccepted = {

                                    mapStateViewModel.enableTracking()
                                    accepttrip.acceptTrip()

                                    tripId=trip._id
                                    tripStatus="accepted"
                                    val destinationPoint = parseGeoPoint(trip.origin)
                                    val start = parseGeoPoint(trip.destination)
                                    tripViewModel2.setTripDetails(trip.origin, trip.destination)
                                    Log.d("TripDetails", "${trip.origin} ${trip.destination}")
                                    isStart = false
                                    isTripAccepted = true

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
                                    isCanceled = true
                                    availableTrips = availableTrips.filter { it._id != trip._id }
                                    isSearchingForTrip = true
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
                            onClick = {

                            },

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
          //  TripArrivedCard2()
         // StartTrip()
          //  DriverArrivedCard()
            var showCaptainToPassenger by remember { mutableStateOf(false) }
            var showStartTrip by remember { mutableStateOf(false) }

            LaunchedEffect(accepttripViewModel) {
                if (accepttripViewModel) {
                    delay(2000) // تأخير 1 ثانية (غير الرقم حسب احتياجك)
                    showCaptainToPassenger = true
                }
            }
            LaunchedEffect(startTrip) {
                if (startTrip) {

                    showStartTrip = true
                }
            }


            if (!accepttripViewModel) {
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
                        onClick = { isStart = true },
                        driverId = driverId,
                        tripLocation = tripLocation,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }

            if (showCaptainToPassenger) {
                captainToPassenger(
                    context = context,
                    navController = navController,
                    tripId = tripId!!,
                    onTripStarted =   {accepttrip.startTrip()}
                )
            }
            if (showStartTrip) {
              StartTrip()
            }

        }
    }
}







