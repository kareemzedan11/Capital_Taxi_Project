package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components


import AcceptanceMap
import TopBar
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import drawerContent
import kotlinx.coroutines.launch
import androidx.compose.material.rememberBottomSheetScaffoldState

import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.Helper.PartialBottomSheet
import com.example.capital_taxi.Helper.PermissionViewModel
import com.example.capital_taxi.Helper.checkLocationPermission
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation.FindDriverCard
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation.PickupWithDropOffButtons
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_request.searchAboutADriver
import com.example.capital_taxi.domain.Location
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.StatusTripViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverlocation
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation.LocationViewModel
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver.RideDetailsBottomSheetContent
import com.example.capital_taxi.domain.DirectionsViewModel
import com.example.capital_taxi.domain.FareViewModel
import com.example.capital_taxi.domain.TripResponse


import com.example.capital_taxi.domain.shared.TripViewModel


import com.example.myapplication.MapViewComposable

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.io.IOException

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.TripViewModel2
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.getAddressFromLatLng
import com.example.capital_taxi.data.repository.graphhopper_response.Details
import com.example.capital_taxi.data.repository.graphhopper_response.Hints
import com.example.capital_taxi.data.repository.graphhopper_response.Info
import com.example.capital_taxi.data.repository.graphhopper_response.Instruction
import com.example.capital_taxi.data.repository.graphhopper_response.Path
import com.example.capital_taxi.data.repository.graphhopper_response.graphhopper_response
import com.example.capital_taxi.domain.shared.TripInfoViewModel
import com.google.firebase.firestore.SetOptions
import com.google.maps.android.PolyUtil
import findNearestIndex
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "location_prefs")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun homeScreenContent(navController: NavController) {
    var isConfirmed by remember { mutableStateOf(false) }
    var isSearch by remember { mutableStateOf(false) }
    var menuIconShow by remember { mutableStateOf(true) }

    val tripViewModel2: TripViewModel2 = viewModel()
    val selectedTripId by tripViewModel2.selectedTripId.observeAsState()

    val locationViewModel: LocationViewModel = viewModel()
    val pickupLatLng = locationViewModel.pickupLocation
    val dropoffLatLng = locationViewModel.dropoffLocation

    val fareViewModel: FareViewModel = viewModel()
    val fare by fareViewModel.fare.observeAsState(0.0)
    val permissionViewModel: PermissionViewModel = viewModel()
    val context = LocalContext.current

    val tripInfoViewmodel: TripInfoViewModel = viewModel()

    LaunchedEffect(context) {
        checkLocationPermission(context, permissionViewModel)
    }

    val isLocationGranted by permissionViewModel.isLocationGranted.collectAsState()
    val StatusTripViewModel: StatusTripViewModel = viewModel()

    val scope = rememberCoroutineScope()

    var tripStatus by remember { mutableStateOf("pending") } // الحالة الابتدائية

    // BottomSheetScaffoldState
    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    // DrawerState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val gesturesEnabled = drawerState.isOpen

    // Check if location service is enabled
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    var tripDetails by remember { mutableStateOf<TripResponse?>(null) }

    // Remember updated state of location enabled and granted
    val currentIsLocationEnabled = rememberUpdatedState(isLocationEnabled)
    val currentIsLocationGranted = rememberUpdatedState(isLocationGranted)
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("USER_TOKEN", null)

    // FusedLocationProviderClient to get current location
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var locationName by remember { mutableStateOf("Fetching location...") }
    var destinationLat by remember { mutableStateOf(0.0) }
    var destinationLng by remember { mutableStateOf(0.0) }
    val tripViewModel: TripViewModel = viewModel()


    var tripId by remember { mutableStateOf<String?>(null) }

    var startPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val endPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val reloadMap = remember { mutableStateOf(false) }

    val directionsViewModel: DirectionsViewModel = viewModel()
    val distance by directionsViewModel.distance.collectAsState()
    val duration by directionsViewModel.duration.collectAsState()


    val locationDataStore = LocationDataStore(context)

// ✅ إنشاء ViewModel مرة واحدة داخل Composable
    val locationViewModel2: LocationViewModel5 = viewModel()

    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude) // ✅ تحويل الموقع إلى LatLng

                locationViewModel2.updatePassengerLocation(latLng) // ✅ تخزين الموقع مؤقتًا
                Log.d(
                    "Location", "✅pass location: ${
                        locationViewModel2.passengerLocation?.let { latLng ->
                            GeoPoint(
                                latLng.latitude,
                                latLng.longitude
                            ) // ✅ تحويل LatLng إلى GeoPoint
                        }
                    }"
                )
                CoroutineScope(Dispatchers.IO).launch {
                    locationDataStore.saveLocation(it.latitude, it.longitude)
                }
                val geocoder = Geocoder(context, Locale.getDefault())
                val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                locationName = if (addressList != null && addressList.isNotEmpty()) {
                    addressList[0].getAddressLine(0) // ✅ الحصول على العنوان
                } else {
                    "Unable to fetch location"
                }
            } ?: run {
                locationName = "Unable to fetch location"
            }
        }
    }

    val viewmodel3: driverlocation = viewModel()

// الحصول على الموقع المخزن من ViewModel
    val driverLocation = viewmodel3.driverLocation.value
    LaunchedEffect(pickupLatLng, dropoffLatLng) {
        startPoint.value = pickupLatLng?.let { GeoPoint(it.latitude, it.longitude) }

        startPoint.value?.let { geoPoint ->
            locationViewModel2.updatePassengerLocation(
                LatLng(
                    geoPoint.latitude,
                    geoPoint.longitude
                )
            ) // ✅ التحويل الصحيح
        }

        endPoint.value = dropoffLatLng?.let { GeoPoint(it.latitude, it.longitude) }
        reloadMap.value = !reloadMap.value // تغيير الحالة لإجبار إعادة تركيب الخريطة
    }


// إعادة تحميل الخريطة باستخدام `key`

    var showBottomSheet by remember { mutableStateOf(false) }

    PartialBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }) {
        PaymentMethodContent()
    }
    var driverLocationState by remember { mutableStateOf<LatLng?>(null) }
    // Main Container
    Box(modifier = Modifier.fillMaxSize()) {
        // Drawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.8f) // Set drawer width to 60% of screen
                ) {
                    drawerContent(navController)
                }
            }
        ) {
            BottomSheetScaffold(
                scaffoldState = bottomSheetState,
                sheetPeekHeight = if (isConfirmed) 200.dp else 500.dp,
                content = { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {

                        if (tripStatus != "accepted") {

                            MapViewComposable(
                                startPoint = startPoint.value,
                                endPoint = endPoint.value
                            )
                        }
                        if (tripStatus == "accepted") {
                            val database = FirebaseDatabase.getInstance()
                            val tripRef = database.getReference("trips").child(tripId!!)


                            startPoint.value?.let { geoPoint ->
                                val passengerLocationMap = mapOf(
                                    "latitude" to geoPoint.latitude,
                                    "longitude" to geoPoint.longitude
                                )

                                tripRef.child("passengerLocation").setValue(passengerLocationMap)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "FirebaseDB",
                                            "✅ Passenger location saved successfully!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(
                                            "FirebaseDB",
                                            "❌ Failed to save passenger location: ${e.message}"
                                        )
                                    }
                            }


                            tripId?.let {
                                Log.d("tripId", it)
                                TrackDriverScreen(
                                    tripId = it,


                                    passengerLocation = startPoint.value
                                )
                            }

                        }


                        LaunchedEffect("67b0b246322cf017e42a9d3c") {
                            val database = FirebaseDatabase.getInstance()
                            val driverLocationRef =
                                database.getReference("drivers").child("67b0b246322cf017e42a9d3c")
                                    .child("location")

                            driverLocationRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val lat = snapshot.child("lat").getValue(Double::class.java)
                                    val lng = snapshot.child("lng").getValue(Double::class.java)

                                    if (lat != null && lng != null) {
                                        driverLocationState = LatLng(lat, lng)
                                        Log.d(
                                            "FirebaseLocation",
                                            "✅ Driver Location Updated: ($lat, $lng)"
                                        )
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(
                                        "FirebaseLocation",
                                        "❌ Error fetching driver location: ${error.message}"
                                    )
                                }
                            })
                        }
                        if (menuIconShow == true) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.TopStart)
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


                                DraggableIcon(navController = navController)

                            }
                        }
                    }
                },
                sheetContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 2.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        if (tripStatus == "accepted") {
                            RideDetailsBottomSheetContent(navController,tripId!!)
                        } else {
                            Log.d("UI", "🚀 تم تغيير الرحلة: $selectedTripId ، الحالة: $tripStatus")

                            if (currentIsLocationEnabled.value && currentIsLocationGranted.value) {
                                if (!isConfirmed) {

                                    PickupWithDropOffButtons(
                                        navController = navController,
                                        locationName = locationName
                                    )
                                } else if (isConfirmed) {
                                    menuIconShow = false;
                                    confirmPickup(onclick = { isSearch = true })
                                }
                                if (isSearch) {
                                    isConfirmed = false
                                    searchAboutADriver()
                                }
                            } else {
                                EnableLocationServices(
                                    permissionViewModel = permissionViewModel,
                                    context = context
                                )
                            }
                        }

                    }
                }
            )

            LaunchedEffect(Unit) {
                while (true) {
                    delay(2000) // تحديث كل 2 ثانية

                    // تحقق من أن selectedTripId ليس null
                    tripId?.let {
                        tripViewModel.getTripStatusById(it, onSuccess = { newStatus ->
                            tripStatus = newStatus // ✅ تحديث الحالة باستمرار
                        }, onError = { errorMessage ->
                            Log.e("TripStatus", "❌ خطأ في جلب الحالة: $errorMessage + $tripId")
                        })
                    } ?: run {
                        Log.e("TripStatus", "❌ selectedTripId is null!")
                    }
                }
            }


            val context = LocalContext.current
            if (currentIsLocationEnabled.value && currentIsLocationGranted.value && !isConfirmed && !isSearch) {
                val Savedtoken =
                    token // Fetch or pass the token
                FindDriverCard(onclick = {
                    if (startPoint.value == null || endPoint.value == null) {
                        Toast.makeText(
                            context,
                            "Please select both pickup and drop-off locations",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@FindDriverCard
                    }
                    Log.d("TripScreen", "FindDriverCard clicked")

                    val sharedPreferences =
                        context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getString("USER_ID", null)
                    val userBalance = sharedPreferences.getFloat("USER_BALANCE", 0f)

                    if (userId == null) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()

                    }
                    // Use the current location as the origin
                    val origin = getAddressFromLatLng(
                        context,

                        latitude = startPoint.value?.latitude ?: 0.0,
                        longitude = startPoint.value?.longitude ?: 0.0
                    )

                    // Use the selected destination as the destination
                    val destination =
                        getAddressFromLatLng(context,
                            latitude = endPoint.value?.latitude ?: 0.0,
                            longitude = endPoint.value?.longitude ?: 0.0

                        )
                    val fare = fare
                    val distanceInKm = distance
                    val paymentMethod = "cash"
                    val apiKey =
                        "71ab0bb4-9572-4423-ab8f-332deb2827a7" // Replace with your actual API key

                    Log.d(
                        "TripScreen",
                        "Requesting trip with origin: $origin, destination: $destination, fare: $fare, distance: $distanceInKm"
                    )


                    if (Savedtoken != null) {
                        if (distanceInKm != null) {
                            tripViewModel.createTrip(
                                context = context,
                                userId!!,
                                origin,
                                destination,
                                paymentMethod,
                                fare,
                                distanceInKm,
                                Savedtoken,
                                coroutineScope = CoroutineScope(Dispatchers.Main),
                                onSuccess = { tripResponse ->
                                    Log.d("tripResponse", "🚗 Trip id: ${tripResponse.trip._id}")
                                    tripId = tripResponse.trip._id
                                },
                                onError = { errorMessage ->
                                    Log.e("TripScreen", "❌ خطأ أثناء إنشاء الرحلة: $errorMessage")
                                }
                            )
                        }
                    }


                    // Set the endpoint when the button is clicked
                    endPoint.value = GeoPoint(destinationLat, destinationLng)

                    isConfirmed = true
                })
            }
        }
    }
}

class LocationViewModel5 : ViewModel() {
    var passengerLocation by mutableStateOf<LatLng?>(null)
        private set

    fun updatePassengerLocation(latLng: LatLng) {
        passengerLocation = latLng
    }
}

fun getLatLngFromAddressNominatim(address: String, onResult: (LatLng?) -> Unit) {
    val url = "https://nominatim.openstreetmap.org/search?format=json&q=${Uri.encode(address)}"

    val request = Request.Builder().url(url).header("User-Agent", "YourAppNam e").build()
    val client = OkHttpClient()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { json ->
                val jsonArray = JSONArray(json)
                if (jsonArray.length() > 0) {
                    val firstResult = jsonArray.getJSONObject(0)
                    val lat = firstResult.getDouble("lat")
                    val lon = firstResult.getDouble("lon")
                    onResult(LatLng(lat, lon))
                } else {
                    onResult(null)
                }
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            onResult(null)
        }
    })
}

@Composable
fun TrackDriverScreen(
    passengerLocation: GeoPoint?,
    tripId: String,
    context: Context = LocalContext.current
) {
    // Firebase references
    val tripsRef = remember { FirebaseFirestore.getInstance().collection("trips") }

    // State variables
    var driverLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var directionsFetched by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var directions by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }

    // دالة جلب الاتجاهات باستخدام OSRM
    fun fetchOSRMDirections(
        start: GeoPoint,
        end: GeoPoint,
        onSuccess: (List<GeoPoint>) -> Unit,
        onError: (String) -> Unit
    ) {
        isLoading = true
        Log.d("tripId 2 ", tripId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = DirectionsApi.getDirections(
                    start, end,
                    apiKey =  "71ab0bb4-9572-4423-ab8f-332deb2827a7",
                    tripId = tripId
                )

                withContext(Dispatchers.Main) {
                    when (response) {
                        is ResultWrapper.Success -> {
                            val path = response.value.paths.firstOrNull()
                            if (path != null) {
                                val geoPoints = PolyUtil.decode(path.points).map {
                                    GeoPoint(it.latitude, it.longitude)
                                }
                                directionsFetched = true
                                onSuccess(geoPoints)
                            }
                        }


                        is ResultWrapper.Failure -> {
                            onError(response.exception.message ?: "Unknown error")
                        }
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    onError("Network error: ${e.message ?: "Unknown error"}")
                }
            }
        }
    }

    // Listen for trip updates
    LaunchedEffect(tripId) {
        tripsRef.whereEqualTo("_id", tripId)
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    Log.e("Firestore", "❌ Error listening for updates: ${error.message}")
                    return@addSnapshotListener
                }

                documents?.let {
                    for (document in it) {
                        val driverLat = document.getDouble("driverLocation.latitude")
                        val driverLng = document.getDouble("driverLocation.longitude")

                        if (driverLat != null && driverLng != null) {
                            driverLocation = GeoPoint(driverLat, driverLng)
                            Log.d("Firestore", "✅ Driver Location Updated: $driverLocation")
                        }
                    }
                }
            }
    }

    // Fetch directions when locations are available
    LaunchedEffect(driverLocation, passengerLocation) {
        if (driverLocation != null && passengerLocation != null && !directionsFetched) {
            fetchOSRMDirections(
                start = driverLocation!!,
                end = passengerLocation!!,
                onSuccess = { routePoints ->
                    directions = routePoints
                    driverLocation?.let { findNearestIndex(current = it, path =routePoints ) }
                    directionsFetched = true  // ✅ ضروري يتكتب هنا أول ما النجاح يحصل
                    Log.d("OSRM Directions", "✅ Directions fetched: ${routePoints} points")
                },
                onError = { error ->
                    Log.e("OSRM Directions", "❌ Error: $error")
                    Toast.makeText(context, "خطأ في جلب الاتجاهات: $error", Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {



        AcceptanceMap(
            driverLocation = driverLocation,
            passengerLocation = passengerLocation,
            directions = directions
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

// ملف ResultWrapper.kt
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Failure(val exception: Throwable) : ResultWrapper<Nothing>()
}

object DirectionsApi {
    private const val BASE_URL = "https://graphhopper.com/api/1/route?"
    private val client = OkHttpClient()

    suspend fun getDirections(
        start: GeoPoint,
        end: GeoPoint,
        apiKey: String,
        context: Context? = null,
        tripId:String,

    ): ResultWrapper<graphhopper_response> = withContext(Dispatchers.IO) {
        try {
            val url = "${BASE_URL}point=${start.latitude},${start.longitude}" +
                    "&point=${end.latitude},${end.longitude}" +
                    "&instructions=true&points_encoded=true&key=$apiKey"

            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext ResultWrapper.Failure(Exception("API Error: ${response.code}"))
            }

            val json = JSONObject(
                response.body?.string()
                    ?: return@withContext ResultWrapper.Failure(Exception("Empty response"))
            )
            val result = parseGraphHopperResponse(json,  context,tripId)
            ResultWrapper.Success(result)

        } catch (e: Exception) {
            ResultWrapper.Failure(e)
        }
    }

      @SuppressLint("SuspiciousIndentation")
      fun parseGraphHopperResponse(
        json: JSONObject,
        context: Context? = null,
        tripId:String,
        tripViewModel: TripInfoViewModel ?=null

      ): graphhopper_response {
        val info = json.getJSONObject("info")
        val pathsJson = json.getJSONArray("paths")
        val paths = mutableListOf<Path>()

        fun JSONArray.toListString(): List<String> {
            return List(length()) { getString(it) }
        }

        val infoObj = Info(
            copyrights = info.getJSONArray("copyrights").toListString(),
            road_data_timestamp = info.getString("road_data_timestamp"),
            took = info.getInt("took")
        ).also { println("Info: $it") }

        for (i in 0 until pathsJson.length()) {
            val pathObj = pathsJson.getJSONObject(i)
            val instructions = mutableListOf<Instruction>()

            val instructionsJson = pathObj.getJSONArray("instructions")
            for (j in 0 until instructionsJson.length()) {
                val instObj = instructionsJson.getJSONObject(j)
                instructions.add(
                    Instruction(
                        distance = instObj.getDouble("distance"),
                        exit_number = instObj.optInt("exit_number", 0),
                        exited = instObj.optBoolean("exited", false),
                        interval = instObj.getJSONArray("interval").let { arr ->
                            List(arr.length()) { arr.getInt(it) }
                        },
                        last_heading = instObj.optDouble("last_heading", 0.0),
                        sign = instObj.getInt("sign"),
                        street_destination = instObj.optString("street_destination", ""),
                        street_name = instObj.optString("street_name", ""),
                        text = instObj.getString("text"),
                        time = instObj.getInt("time"),
                        turn_angle = instObj.optDouble("turn_angle", 0.0)
                    )
                )
            }

            val path = Path(
                ascend = pathObj.getDouble("ascend"),
                bbox = pathObj.getJSONArray("bbox").let { arr ->
                    List(arr.length()) { arr.getDouble(it) }
                },
                descend = pathObj.getDouble("descend"),
                details = Details(),
                distance = pathObj.getDouble("distance"),
                instructions = instructions,
                legs = emptyList(),
                points = pathObj.getString("points"),
                points_encoded = pathObj.getBoolean("points_encoded"),
                points_encoded_multiplier = pathObj.getDouble("points_encoded_multiplier"),
                snapped_waypoints = pathObj.getString("snapped_waypoints"),
                time = pathObj.getInt("time"),
                transfers = pathObj.getInt("transfers"),
                weight = pathObj.getDouble("weight")
            )

            Log.d("New", "New path:   time=${pathObj.getInt("time")}")
            paths.add(path)

// تجهيز التعليمات بصيغة Map
            val instructionList = instructions.map { inst ->
                mapOf(
                    "text" to inst.text,
                    "distance" to inst.distance,
                    "time" to inst.time,
                    "sign" to inst.sign,
                    "street_name" to inst.street_name,
                    "street_destination" to inst.street_destination,
                    "exit_number" to inst.exit_number,
                    "exited" to inst.exited,
                    "interval" to inst.interval,
                    "last_heading" to inst.last_heading,
                    "turn_angle" to inst.turn_angle
                )
            }

            val db = FirebaseFirestore.getInstance()

            val tripId = tripId
            val tripRef = db.collection("trips") // افترض أن الـ collection التي تحتوي على البيانات اسمها "trips"

// Coroutine للبحث المتكرر مع تأخير
            GlobalScope.launch {
                var attempts = 0
                val maxAttempts = 5  // عدد المحاولات
                var success = false

                while (attempts < maxAttempts && !success) {
                    try {
                        // استرجاع البيانات من Firebase باستخدام الـ Tripid
                        val querySnapshot = tripRef.whereEqualTo("_id", tripId).get().await()  // await تجعلها متماثلة مع التأخير

                        if (!querySnapshot.isEmpty) {
                            // إذا تم العثور على بيانات تطابق الـ Tripid
                            val document = querySnapshot.documents[0] // البيانات المتطابقة من Firebase
                            val data = document.data

                            // الآن تحقق إذا كان _id في البيانات الموجودة يساوي Tripid
                            if (data != null && data["_id"] == tripId) {
                                // البيانات تطابقت، يمكنك الآن إضافة بيانات جديدة أو تحديثها
                                val newTripInfo = hashMapOf(
                                    "distance" to path.distance,
                                    "points" to path.points,
                                    "time" to pathObj.getInt("time"),
                                    "instructions" to instructionList // ✅ تم إضافة التعليمات هنا
                                )

                                Log.d("Firebase", "New path: distance=${path.distance}, time=${path.time}")

                                // إرسال أو تحديث البيانات على Firebase تحت هذا الـ Tripid
                                document.reference.set(newTripInfo, SetOptions.merge()) // استخدام merge للتحديث بدون مسح البيانات السابقة

                                Log.d("Firebase", "Data successfully updated in Firebase!")
                                success = true  // لو تم التحديث بنجاح
                            } else {
                                Log.d("Firebase", "ID mismatch: _id does not match TripId")
                            }
                        } else {
                            Log.d("Firebase", "No matching Trip found with the given Trip ID.")
                        }
                    } catch (e: Exception) {
                        // في حالة حدوث أي خطأ
                        Log.e("Firebase", "Error retrieving trip data: ${e.message}")
                    }

                    if (!success) {
                        attempts++
                        Log.d("Firebase", "Attempt #$attempts failed, retrying in 3 seconds...")
                        delay(3000) // تأخير لمدة 3 ثواني قبل المحاولة مرة أخرى
                    }
                }

                if (!success) {
                    Log.e("Firebase", "Max attempts reached. Data update failed.")
                }

            }


        }

        val hints = Hints(
            visitedNodesSum = json.getJSONObject("hints").getInt("visited_nodes.sum"),
            visitedNodesAverage = json.getJSONObject("hints").getDouble("visited_nodes.average")
        )

          return graphhopper_response(info = infoObj, paths = paths, hints = hints)
    }


private fun decodePolyline(encoded: String): List<GeoPoint> {
        val poly = PolyUtil.decode(encoded)
        return poly.map { GeoPoint(it.latitude, it.longitude) }
    }
}

// LocationDataStore.kt
class LocationDataStore(private val context: Context) {
    private val LATITUDE_KEY = doublePreferencesKey("latitude")
    private val LONGITUDE_KEY = doublePreferencesKey("longitude")

    suspend fun saveLocation(latitude: Double, longitude: Double) {
        context.dataStore.edit { prefs ->
            prefs[LATITUDE_KEY] = latitude
            prefs[LONGITUDE_KEY] = longitude
        }
    }

    suspend fun getLocation(): Pair<Double, Double>? {
        val prefs = context.dataStore.data.first()
        val lat = prefs[LATITUDE_KEY] ?: return null
        val lng = prefs[LONGITUDE_KEY] ?: return null
        return Pair(lat, lng)
    }

    suspend fun clearLocation() {
        context.dataStore.edit { it.clear() }
    }
}

// Extension functions
fun LatLng.toDomainLocation(): Location {
    return Location(latitude, longitude)
}

suspend fun fetchGraphHopperSuggestions(
    query: String,
    apiKey: String,
    onResult: (List<String>) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url =
                "https://graphhopper.com/api/1/geocode?q=$encodedQuery&locale=en&limit=5&key=71ab0bb4-9572-4423-ab8f-332deb2827a7"

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d("GraphHopper", "Response: $response")

            val jsonObject = JSONObject(response)
            val hits = jsonObject.getJSONArray("hits")

            val suggestions = mutableListOf<String>()
            for (i in 0 until hits.length()) {
                val hit = hits.getJSONObject(i)
                suggestions.add(hit.getString("name"))
            }

            withContext(Dispatchers.Main) {
                onResult(suggestions)
            }
        } catch (e: Exception) {
            Log.e("GraphHopper", "Error fetching suggestions", e)
            withContext(Dispatchers.Main) {
                onResult(emptyList())
            }
        }
    }
}