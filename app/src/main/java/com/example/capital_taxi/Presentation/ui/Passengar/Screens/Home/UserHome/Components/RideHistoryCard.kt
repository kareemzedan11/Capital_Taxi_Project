package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.capital_taxi.Helper.GraphHopperService
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Priority
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.ProblemCategory
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.submitProblemToFirestore
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.Driver
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.shared.decodePolyline
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

@Composable
fun RideHistoryCard(
    origin: String,
    destination: String,
    date: String,
    price: String,
    driverName: String? = null,
    distance: String? = null,
    duration: String? = null,
    originLat: Double? = null,
    originLng: Double? = null,
    destinationLat: Double? = null,
    destinationLng: Double? = null,
    onCardClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(13.dp)
            .clickable { onCardClick() },
        elevation = 4.dp,
        backgroundColor = colorResource(R.color.secondary_color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = formatFirestoreDate(date),
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Origin Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.From)}: ",
                    style = MaterialTheme.typography.body1,
                    color = colorResource(R.color.primary_color)
                )
                Text(
                    text = origin,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Destination Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.To)}: ",
                    style = MaterialTheme.typography.body1,
                    color = colorResource(R.color.primary_color)
                )
                Text(
                    text = destination,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1f)
                )
            }

            // Driver and trip details
            Spacer(modifier = Modifier.height(8.dp))
            driverName?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Driver: ",
                        style = MaterialTheme.typography.body1,
                        color = colorResource(R.color.primary_color)
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            // Distance and Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                distance?.let {
                    Text(
                        text = "${stringResource(R.string.Distance)}: $it km",
                        style = MaterialTheme.typography.caption
                    )
                }

                duration?.let {
                    Text(
                        text = "${stringResource(R.string.Duration)}: $it min",
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Price
            Text(
                text = "${stringResource(R.string.Price)}: $price EGP",
                style = MaterialTheme.typography.h6,
                color = colorResource(R.color.primary_color),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideHistoryBottomSheet(
    context: Context,
    origin: String,
    destination: String,
    originLat: Double,
    originLng: Double,
    destinationLat: Double,
    destinationLng: Double,
    onDismiss: () -> Unit,
    onComplaintClick: () -> Unit,
    pathPoints: List<GeoPoint> = emptyList()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = colorResource(R.color.secondary_color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Map View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                if (pathPoints.isEmpty()) {
                    CircularProgressIndicator(
modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    OsmMapView(
                        context = context,
                        originLat = originLat,
                        originLng = originLng,
                        destinationLat = destinationLat,
                        destinationLng = destinationLng,
                        pathPoints = pathPoints,

                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Route details
            Text(
                text = "Route Details",
                style = MaterialTheme.typography.h6,
                color = colorResource(R.color.primary_color)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Origin",
                    tint = colorResource(R.color.primary_color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = origin, style = MaterialTheme.typography.body1)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Destination",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = destination, style = MaterialTheme.typography.body1)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Complaint button
            Button(
                onClick = onComplaintClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.primary_color),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Do you have a complaint?")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun OsmMapView(
    context: Context,
    originLat: Double,
    originLng: Double,
    destinationLat: Double,
    destinationLng: Double,
    pathPoints: List<GeoPoint> = emptyList()
) {
    AndroidView(
        factory = {
            Configuration.getInstance().load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                val origin = GeoPoint(originLat, originLng)
                val destination = GeoPoint(destinationLat, destinationLng)

                // Calculate bounds to fit both points
                val minLat = minOf(origin.latitude, destination.latitude)
                val maxLat = maxOf(origin.latitude, destination.latitude)
                val minLon = minOf(origin.longitude, destination.longitude)
                val maxLon = maxOf(origin.longitude, destination.longitude)

                // Set zoom and center
                controller.zoomToSpan(maxLat - minLat, maxLon - minLon)
                controller.setCenter(GeoPoint(
                    (origin.latitude + destination.latitude) / 2,
                    (origin.longitude + destination.longitude) / 2
                ))

                // Add markers
                val startMarker = Marker(this).apply {
                    position = origin
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Pickup"
                }
                overlays.add(startMarker)

                val endMarker = Marker(this).apply {
                    position = destination
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Drop-off"
                }
                overlays.add(endMarker)

                // Add route polyline if points are available

                    val routeLine = Polyline().apply {
                        color = android.graphics.Color.BLUE
                        width = 8f
                        setPoints(pathPoints)
                    }
                    overlays.add(routeLine)

            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RideHistoryList(userId: String, context: Context, onProblemSubmitted: () -> Unit ) {
    var trips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedTrip by remember { mutableStateOf<Trip?>(null) }
    var showComplaintDialog by remember { mutableStateOf(false) }
    var complaintText by remember { mutableStateOf("") }
    var pathPoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var showProblemReport by remember { mutableStateOf(false) } // التحكم في عرض قسم الإبلاغ عن المشكلة
    var problemDescription by remember { mutableStateOf("") } // نص وصف المشكلة
    var isSubmitting by remember { mutableStateOf(false) }
    val problemCategories = listOf(
        ProblemCategory("harassment", "تحرش", Priority.HIGH),
        ProblemCategory("abuse", "سب أو إهانة", Priority.HIGH),
        ProblemCategory("payment", "مشكلة في الدفع", Priority.MEDIUM),
        ProblemCategory("behavior", "سلوك غير لائق", Priority.MEDIUM),
        ProblemCategory("route", "مشكلة في الطريق", Priority.LOW),
        ProblemCategory("other", "أخرى", Priority.LOW)
    )
    var driverId by remember { mutableStateOf("") }
    var tripId by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf<ProblemCategory?>(null) }
    var showCategoryError by remember { mutableStateOf(false) }
    // Initialize Retrofit
    val graphHopperService = remember {
        Retrofit.Builder()
            .baseUrl("https://graphhopper.com/api/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GraphHopperService::class.java)
    }

    // Function to fetch route from GraphHopper
    suspend fun fetchRoute(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): List<GeoPoint> {
        return try {
            val response = graphHopperService.getRoute(
                startPoint = "$startLat,$startLng",
                endPoint = "$endLat,$endLng",
                vehicle = "car",
                pointsEncoded = true,
            )

            if (response.paths.isNullOrEmpty()) {
                Log.e("GraphHopper", "No paths in response:  ")
                emptyList()
            } else {
                val points = response.paths.first().points
                Log.d("GraphHopper", "Encoded points: $points")
                points?.let { decodePolyline(it) } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("GraphHopper", "Error fetching route: ${e.message}", e)
            emptyList()
        }
    }
    LaunchedEffect(key1 = userId) {
        try {
            val db = FirebaseFirestore.getInstance()

            val query = db.collection("trips")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Completed")
                .orderBy("createdAt", Query.Direction.DESCENDING)

            val snapshot = query.get().await()
            trips = snapshot.documents.mapNotNull { doc ->
                try {
                      driverId = doc.getString("driver") ?: return@mapNotNull null
                    val driverSnapshot = db.collection("drivers")
                        .whereEqualTo("id", driverId)
                        .limit(1)
                        .get()
                        .await()


                    tripId= doc.getString("_id")!!;
                    val driverDoc = driverSnapshot.documents.firstOrNull()
                    val driver = if (driverDoc != null) {
                        Driver(
                            _id = driverId,
                            name = driverDoc.getString("name") ?: "Unknown",
                            phone = driverDoc.getString("phone") ?: ""
                        )
                    } else {
                        Driver(_id = driverId, name = "Unknown", phone = "")
                    }

                    Trip(
                        _id = doc.id,
                        user = doc.getString("userId") ?: "",

                        driver = driver,
                        originMap = doc.get("originMap") as? Map<String, Any>,
                        destinationMap = doc.get("destinationMap") as? Map<String, Any>,
                        origin = doc.getString("origin") ?: "",
                        destination = doc.getString("destination") ?: "",
                        distanceInKm = doc.getDouble("distanceInKm") ?: 0.0,
                        fare = doc.getDouble("fare") ?: 0.0,
                        paymentMethod = doc.getString("paymentMethod") ?: "Cash",
                        status = doc.getString("status") ?: "Unknown",
                        createdAt = doc.getString("createdAt") ?: "",
                        updatedAt = doc.getString("updatedAt") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("RideHistoryList", "Error parsing trip: ${e.message}")
                    null
                }
            }
            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load trips: ${e.message}"
            isLoading = false
            Log.e("RideHistoryList", "Error: ${e.message}")
        }
    }

    // Handle trip selection and fetch route
    // Add this state variable
    var isRouteLoading by remember { mutableStateOf(false) }

// Update the LaunchedEffect
    LaunchedEffect(selectedTrip) {
        selectedTrip?.let { trip ->
            val originLat = trip.originMap?.get("lat") as? Double ?: 0.0
            val originLng = trip.originMap?.get("lng") as? Double ?: 0.0
            val destLat = trip.destinationMap?.get("lat") as? Double ?: 0.0
            val destLng = trip.destinationMap?.get("lng") as? Double ?: 0.0

            Log.d("RouteDebug", "Origin: ($originLat, $originLng)")
            Log.d("RouteDebug", "Destination: ($destLat, $destLng)")

            if (originLat != 0.0 && originLng != 0.0 && destLat != 0.0 && destLng != 0.0) {
                isRouteLoading = true
                pathPoints = fetchRoute(originLat, originLng, destLat, destLng)
                Log.d("RouteDebug", "Path points count: ${pathPoints.size}")
                isRouteLoading = false
            }
        }
    }

// Then in your bottom sheet content
    if (isRouteLoading) {
        CircularProgressIndicator( )
    }

    // Bottom sheet for selected trip
    selectedTrip?.let { trip ->
        RideHistoryBottomSheet(
            pathPoints = pathPoints,
            origin = trip.origin,
            destination = trip.destination,
            originLat = trip.originMap?.get("lat") as? Double ?: 0.0,
            originLng = trip.originMap?.get("lng") as? Double ?: 0.0,
            destinationLat = trip.destinationMap?.get("lat") as? Double ?: 0.0,
            destinationLng = trip.destinationMap?.get("lng") as? Double ?: 0.0,
            onDismiss = { selectedTrip = null },
            onComplaintClick = { showComplaintDialog = true },
            context = context
        )
    }

    // Complaint dialog
    if (showComplaintDialog) {
        AlertDialog(
            onDismissRequest = { showComplaintDialog = false },
            title = { Text("Submit Complaint") },

// داخل Text composable
            text = {
                Column {
                    Text("اختر نوع المشكلة:")

                    LazyRow {
                        items(problemCategories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = {
                                    selectedCategory = category
                                    showCategoryError = false
                                },
                                label = {
                                    Text(category.name)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colorResource(R.color.primary_color)
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }

                    if (showCategoryError) {
                        Text("يجب اختيار نوع المشكلة", color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("وصف المشكلة:")

                    OutlinedTextField(
                        value = complaintText,
                        onValueChange = { complaintText = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (complaintText.isNotBlank() && selectedCategory != null) {
                            isSubmitting = true
                            submitProblemToFirestore(
                                tripId = tripId,
                                driverId = driverId,
                                userId = userId, // تأكد أن userId ليست null
                                problemDescription = complaintText,
                                category = selectedCategory!!.id,
                                priority = selectedCategory!!.priority.name,
                                onSuccess = {
                                    isSubmitting = false
                                    showComplaintDialog = false
                                    problemDescription = ""
                                    selectedCategory = null
                                    onProblemSubmitted()
                                },
                                onFailure = { error ->
                                    isSubmitting = false
                                    Log.e("Complaint", "Error submitting: ${error?.message}")
                                    // عرض رسالة خطأ للمستخدم
                                }
                            )
                        } else {
                            // عرض رسالة للمستخدم لاختيار فئة وإدخال وصف
                            showCategoryError = selectedCategory == null
                        }
                    }
                ) {
                    if (isSubmitting) CircularProgressIndicator(color = Color.White)
                    else Text("Submit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showComplaintDialog = false
                        complaintText = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Loading your trips...")
            }
        }
        error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error!!, color = Color.Red)
            }
        }
        trips.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No completed trips found")
            }
        }
        else -> {
            LazyColumn {
                items(trips) { trip ->
                    RideHistoryCard(
                        origin = trip.origin,
                        destination = trip.destination,
                        date = trip.createdAt,
                        price = trip.fare.toString(),
                        driverName = trip.driver.name,
                        distance = String.format("%.1f", trip.distanceInKm),
                        duration = null,
                        originLat = trip.originMap?.get("lat") as? Double,
                        originLng = trip.originMap?.get("lng") as? Double,
                        destinationLat = trip.destinationMap?.get("lat") as? Double,
                        destinationLng = trip.destinationMap?.get("lng") as? Double,
                        onCardClick = { selectedTrip = trip }
                    )
                }
            }
        }
    }
}

// Helper function to format Firestore timestamp
private fun formatFirestoreDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString // Return original if parsing fails
    }
}