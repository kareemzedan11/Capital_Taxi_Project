package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home

import TopBar
import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.DriverNavigationDrawer
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.TripDetailsCard
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.captainToPassenger
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.dataTripViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverHomeScreenContent
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverlocation
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationDataStore
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationViewModel5
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.TrackDriverScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation.LocationViewModel

import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DirectionsViewModel
import com.example.capital_taxi.domain.Location
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.fetchTripDirections
import com.example.capital_taxi.domain.shared.TripViewModel
import com.example.capital_taxi.domain.shared.decodePolyline
import com.example.capital_taxi.domain.shared.saveDriverLocationToRealtimeDatabase
import com.example.myapplication.DriverMapView
import com.example.myapplication.MapViewComposable
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
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
    val tripViewModel2: TripViewModel2 = viewModel()
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

     // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()

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
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { location ->
                    val newLocation = GeoPoint(location.latitude, location.longitude)

                    // حساب الاتجاه (Bearing) بناءً على التغير في الإحداثيات
                    previousLocation?.let { oldLocation ->
                        carBearing = calculateBearing(oldLocation, newLocation)
                    }
                    previousLocation = newLocation
                    driverLocationState = newLocation
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    updateLocationAndStatus(driverId, geoPoint)
                }
            }

        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
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

    fun getLatLngFromAddress(context: Context, address: String, onResult: (GeoPoint?) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses!!.isNotEmpty()) {
                val location = addresses[0]
                val passengerLocation = GeoPoint(location.latitude, location.longitude)
                onResult(passengerLocation)
            } else {
                onResult(null)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            onResult(null)
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


            val decodedRoutePoints =
                decodePolyline("_sgvDotc_E?bBSZKJMFMDM@g@?WCKCICEGEEKe@Ce@DiLB{DFw@B[d@iCYIg@rCGZCZE~Cc@RO@QCMGMKGOEQ_@@B{Y@eTGKKEE?K@IHAfJ]??l@aAH[?[E[GYMWMQOWYU[")
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
                if (tripStatus != "accepted") {
                    DriverMapView(
                        driverLocation = driverLocationState ?: GeoPoint(30.0444, 31.2357),
                        bearing = carBearing // تمرير الاتجاه المحسوب
                    )
                    Log.d("LocationCheck1", "Before passing: ${passengerLocation.passengerLocation}")

                }

                if (tripStatus == "accepted") {
                    Log.d("LocationCheck2", "Before passing: ${passengerLocation.passengerLocation}")

                    tripId?.let {
                        Log.d("tripId", it)
                        Log.d("Location3", "✅pass location: ${ passengerLocation.passengerLocation?.let { latLng ->
                            GeoPoint(latLng.latitude, latLng.longitude) // ✅ تحويل LatLng إلى GeoPoint
                        }}")
                        TrackDriverScreen(
                            tripId = it,
                            directionsViewModel = directionsViewModel,
                            PassengerLocation = driverLocationState2?: GeoPoint(30.0444, 31.2357)
                        )
                    }
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




                    // TripInProgressCardSimplified()
                    // DriverArrivedCard()
                    //  RideInfoCard()
                    //  captainToPassengar(navController)
//                        DriverTripAcceptedScreen(
//                            userName = "Jane Doe",
//                            userRating = 4.5f,
//                            pickupLocation = "123 Main St, Cairo",
//                            dropoffLocation = "456 Elm St, Alexandria",
//                            etaToPickup = "10 mins",
//                            distanceToPickup = "5 km",
//                            onNavigate = { /* Handle Navigation */ },
//                            onCallUser = { /* Handle Call User */ },
//                            onMessageUser = { /* Handle Message User */ },
//                            onCancelTrip = { /* Handle Cancel Trip */ }
//                        )
                    //  TripArrivedCard()
                    //TripDetailsCard(light = false)
                    //TripArrivedCard()
                    // TripArrivedCard2()
                    // DriverArrivedCard()

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



            if (!isStart && !isTripAccepted) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),

                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),

                    ) {

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal =
                                10.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Icon(

                            modifier = Modifier.size(26.dp),
                            painter = painterResource(R.drawable.note),
                            contentDescription = null,
                            tint = colorResource(R.color.Icons_color)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                            onClick = {
                                isStart = true // تفعيل استقبال الرحلات
                                tripLocation?.let {
                                    updateLocationAndStatus(
                                        driverId =driverId,
                                        location = it
                                    )
                                }

                            },
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .fillMaxHeight(.8f)
                        ) {
                            Text(text = if (isStart) "Online" else "Start", color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(

                            tint = colorResource(R.color.Icons_color),
                            modifier = Modifier.size(26.dp),
                            painter = painterResource(R.drawable.tools), contentDescription = null
                        )
                    }
                }
            } else if (isStart) {


            }
        }
    }
}

class TripViewModel2 : ViewModel() {
    private val _selectedTripId = MutableLiveData<String>()
    val selectedTripId: MutableLiveData<String?> = MutableLiveData(null)

    fun setSelectedTripId(tripId: String) {
        _selectedTripId.value = tripId
    }
}

class TripViewModel4 : ViewModel() {
    var tripLocation = mutableStateOf<GeoPoint?>(null)
        private set

    fun updateTripLocation(location: GeoPoint) {
        tripLocation.value = location
    }
}


@Composable
fun driverMapViewComposable(
    routePoints: List<GeoPoint>? = null,
    driverLocation: MutableState<GeoPoint?> = mutableStateOf(null)
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val marker = remember { Marker(mapView) }

    LaunchedEffect(mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(15)
        mapView.controller.setCenter(GeoPoint(30.033, 31.233))


    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { mapView ->
            mapView.overlays.clear()

            // إضافة المسار على الخريطة
            routePoints?.let {
                val polyline = Polyline()
                polyline.setPoints(it)
                mapView.overlays.add(polyline)
            }

            // إضافة السيارة (السائق)
            driverLocation.value?.let { location ->
                marker.position = location
                marker.title = "Driver"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.icon = ContextCompat.getDrawable(context, R.drawable.uber)
                mapView.overlays.add(marker)
            }

            mapView.invalidate()
        }
    )

    // تحريك السيارة على المسار
    LaunchedEffect(routePoints) {
        routePoints?.let { points ->
            if (points.isNotEmpty()) {
                for (i in 0 until points.size - 1) {
                    val currentPoint = points[i]
                    val nextPoint = points[i + 1]

                    driverLocation.value = currentPoint

                    // حساب اتجاه السيارة بين النقطتين
                    val bearing = calculateBearing(currentPoint, nextPoint)
                    marker.rotation = bearing

                    delay(3000) // يتحرك كل 3 ثوانٍ
                }
            }
        }
    }
}

// حساب زاوية التوجيه بين نقطتين
fun calculateBearing(start: GeoPoint, end: GeoPoint): Float {
    val startLat = Math.toRadians(start.latitude)
    val startLng = Math.toRadians(start.longitude)
    val endLat = Math.toRadians(end.latitude)
    val endLng = Math.toRadians(end.longitude)

    val deltaLng = endLng - startLng
    val y = sin(deltaLng) * cos(endLat)
    val x = cos(startLat) * sin(endLat) - sin(startLat) * cos(endLat) * cos(deltaLng)
    return ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
}

/**
 * فك تشفير Polyline إلى قائمة من النقاط
 */
fun decodePolyline(encoded: String): List<GeoPoint> {
    val poly = mutableListOf<GeoPoint>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0

        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)

        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0

        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)

        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        poly.add(GeoPoint(lat.toDouble() / 1E5, lng.toDouble() / 1E5))
    }

    return poly
}

/**
 * تحريك السيارة على المسار الصحيح خلال 5 دقائق
 */
suspend fun animateCarOnRoute(
    routePoints: List<GeoPoint>,
    updateLocation: (GeoPoint) -> Unit
) {
    val duration = 300 // 5 دقائق (300 ثانية)
    val steps = routePoints.size

    if (steps < 2) return

    val delayTime = duration * 1000 / steps // وقت التأخير بين كل نقطة

    for (i in 0 until steps) {
        updateLocation(routePoints[i])
        delay(delayTime.toLong()) // تحرك ببطء وفقًا للمدة المحددة
    }
}

fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0) // أو addresses[0].locality للحصول على المدينة فقط
        } else {
            "Location not found"
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

//
//@Composable
//fun UserTripAcceptedScreen(
//    driverName: String,
//    driverRating: Float,
//    vehicleModel: String,
//    licensePlate: String,
//    pickupLocation: String,
//    dropoffLocation: String,
//    eta: String,
//    onCallDriver: () -> Unit,
//    onMessageDriver: () -> Unit,
//    onCancelTrip: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Driver and Vehicle Details
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(16.dp),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                // Driver Details
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    // Driver Photo
//                    Box(
//                        modifier = Modifier
//                            .size(64.dp)
//                            .clip(CircleShape)
//                            .background(Color.LightGray),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.person),
//                            contentDescription = "Driver Photo",
//                            modifier = Modifier.size(32.dp),
//                            tint = Color.White
//                        )
//                    }
//
//                    // Driver Name and Rating
//                    Column {
//                        Text(
//                            text = driverName,
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            Icon(
//                                painter = painterResource(R.drawable.baseline_star_24),
//                                contentDescription = "Rating",
//                                modifier = Modifier.size(16.dp),
//                                tint = Color.Yellow
//                            )
//                            Text(
//                                text = driverRating.toString(),
//                                fontSize = 14.sp,
//                                color = Color.Gray
//                            )
//                        }
//                    }
//                }
//
//                // Vehicle Details
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.uber
//                        ),
//                        contentDescription = "Vehicle",
//                        modifier = Modifier.size(32.dp),
//                        tint = Color.Black
//                    )
//                    Column {
//                        Text(
//                            text = vehicleModel,
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = Color.Black
//                        )
//                        Text(
//                            text = licensePlate,
//                            fontSize = 14.sp,
//                            color = Color.Gray
//                        )
//                    }
//                }
//            }
//        }
//
//        // Trip Details
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(16.dp),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                // Pickup Location
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.circle),
//                        contentDescription = "Pickup",
//                        modifier = Modifier.size(24.dp),
//                        tint = colorResource(R.color.primary_color)
//                    )
//                    Text(
//                        text = pickupLocation,
//                        fontSize = 16.sp,
//                        color = Color.Black
//                    )
//                }
//
//                // Dropoff Location
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.travel),
//                        contentDescription = "Dropoff",
//                        modifier = Modifier.size(24.dp),
//                        tint = colorResource(R.color.primary_color)
//                    )
//                    Text(
//                        text = dropoffLocation,
//                        fontSize = 16.sp,
//                        color = Color.Black
//                    )
//                }
//
//                // ETA
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.clock),
//                        contentDescription = "ETA",
//                        modifier = Modifier.size(24.dp),
//                        tint = colorResource(R.color.primary_color)
//                    )
//                    Text(
//                        text = "ETA: $eta",
//                        fontSize = 16.sp,
//                        color = Color.Black
//                    )
//                }
//            }
//        }
//
//        // Call and Message Buttons
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Button(
//                onClick = onCallDriver,
//                modifier = Modifier
//                    .weight(1f)
//                    .height(50.dp),
//                colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Text(text = "Call Driver", color = Color.White, fontSize = 16.sp)
//            }
//
//            Button(
//                onClick = onMessageDriver,
//                modifier = Modifier
//                    .weight(1f)
//                    .height(50.dp),
//                colors = ButtonDefaults.buttonColors(Color.Transparent),
//                border = BorderStroke(1.dp, colorResource(R.color.primary_color)),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Text(text = "Message", color = colorResource(R.color.primary_color), fontSize = 16.sp)
//            }
//        }
//
//        // Cancel Trip Button
//        Button(
//            onClick = onCancelTrip,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp),
//            colors = ButtonDefaults.buttonColors(Color.Red),
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            Text(text = "Cancel Trip", color = Color.White, fontSize = 16.sp)
//        }
//    }
//}
//UserTripAcceptedScreen(
//driverName = "John Doe",
//driverRating = 4.7f,
//vehicleModel = "Toyota Corolla",
//licensePlate = "ABC-1234",
//pickupLocation = "123 Main St, Cairo",
//dropoffLocation = "456 Elm St, Alexandria",
//eta = "10 mins",
//onCallDriver = { /* Handle Call Driver */ },
//onMessageDriver = { /* Handle Message Driver */ },
//onCancelTrip = { /* Handle Cancel Trip */ }
//)