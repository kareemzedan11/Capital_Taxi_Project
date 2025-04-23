package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


import android.content.Context

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R

import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import kotlin.math.roundToInt
import android.location.Geocoder
import android.widget.TextView
import androidx.compose.material3.Text
import java.util.Locale
@Composable
fun GoogleMapsDesign(
    context: Context,
    modifier: Modifier = Modifier,
    navController: NavController,
    onLocationFetched: (String) -> Unit // Receive the callback here
) {
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Initialize the camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f) // Default camera position
    }

    // Fetch user's location and address when composable is launched
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    userLocation = latLng
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    locationName = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
                    // Update camera position to user's location
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)

                    // Call the callback with the location name
                    onLocationFetched(locationName ?: "Unknown location")
                } else {
                    Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Render the Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            // Add a marker for user's location
            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )

                // Optional: Add a circle around user's location
                Circle(
                    center = location,
                    radius = 500.0,
                    fillColor = Color(0x2200FF00),
                    strokeColor = Color(0x2200FF00),
                    strokeWidth = 2f
                )
            }
        }
    }
}


@Composable
fun GoogleMapsWithSimulatedMovement(
    context: Context,
    origin: LatLng,
    destinations: List<LatLng>,
    apiKey: String
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.builder()
            .target(origin)
            .zoom(12f)
            .build()
    }

    // State to hold route polyline
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var simulatedPosition by remember { mutableStateOf(origin) } // Marker position

    // Fetch directions when the composable launches
    LaunchedEffect(origin, destinations) {
        if (destinations.isNotEmpty()) {
            val allPoints = mutableListOf<LatLng>()
            var currentOrigin = origin
            for (destination in destinations) {
                val segmentPoints = fetchDirections(context, currentOrigin, destination, apiKey)
                if (segmentPoints.isNotEmpty()) {
                    allPoints.addAll(segmentPoints)
                    currentOrigin = destination
                }
            }
            routePoints = allPoints
        }
    }

    // Simulate movement
    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            for (point in routePoints) {
                simulatedPosition = point
                cameraPositionState.position = CameraPosition.builder()
                    .target(point)
                    .zoom(15f)
                    .build()
                delay(500) // Adjust delay for smoothness
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapType = MapType.NORMAL),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        // Draw origin marker
        Marker(
            state = MarkerState(position = origin),
            title = "Origin",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        )

        // Draw markers for all destinations
        destinations.forEachIndexed { index, destination ->
            Marker(
                state = MarkerState(position = destination),
                title = "Destination ${index + 1}",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }

        // Draw the simulated marker
        Marker(
            state = MarkerState(position = simulatedPosition),
            title = "Moving Position",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )

        // Draw polyline for the route
        if (routePoints.isNotEmpty()) {
            Polyline(
                points = routePoints,
                color = Color.Blue,
                width = 5f
            )
        }
    }
}
@Composable
fun SimulateMovement(context: Context, routePoints: List<LatLng>) {
    var currentPointIndex by remember { mutableStateOf(0) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.builder()
            .target(routePoints.firstOrNull() ?: LatLng(0.0, 0.0))
            .zoom(15f)
            .build()
    }

    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            for (i in routePoints.indices) {
                currentPointIndex = i
                cameraPositionState.position = CameraPosition.builder()
                    .target(routePoints[i])
                    .zoom(15f)
                    .build()
                delay(1000L) // Simulates movement every second
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Add moving marker
        if (routePoints.isNotEmpty()) {
            Marker(
                state = MarkerState(position = routePoints[currentPointIndex]),
                title = "Moving to Destination",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
        }
    }
}

suspend fun fetchDirections(
    context: Context,
    origin: LatLng,
    destination: LatLng,
    apiKey: String
): List<LatLng> {
    return withContext(Dispatchers.IO) {
        try {
            val url =
                "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&key=$apiKey"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "GET"

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonObject = JSONObject(response)
                val routes = jsonObject.getJSONArray("routes")
                if (routes.length() > 0) {
                    val overviewPolyline =
                        routes.getJSONObject(0).getJSONObject("overview_polyline")
                    val encodedPoints = overviewPolyline.getString("points")
                    decodePoly(encodedPoints)
                } else {
                    emptyList()
                }
            } else {
                Toast.makeText(context, "Failed to fetch directions", Toast.LENGTH_SHORT).show()
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            emptyList()
        }
    }
}
fun decodePoly(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
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

        val p = LatLng(
            lat / 1E5,
            lng / 1E5
        )
        poly.add(p)
    }
    return poly
}
@Composable
fun MapSection(modifier: Modifier = Modifier, navController: NavController) {
    val origin = LatLng(30.1508, 31.2357) // Example origin
    val destinations = listOf(
        LatLng(30.0444, 31.2357), // Destination 1
        LatLng(29.9827, 31.1682)  // Destination 2
    )
    val apiKey = "AIzaSyArzERc9xBRprPePwc4uTopBW9WMBymX74"

    // Define a function to handle the fetched location name
    val onLocationFetched: (String) -> Unit = { locationName ->
        // Handle the fetched location name (you can store it or use it as needed)
        println("Fetched Location: $locationName")
    }

    // Pass the callback to GoogleMapsDesign
    GoogleMapsDesign(
        modifier = modifier,
        context = LocalContext.current,
        navController = navController,
        onLocationFetched = onLocationFetched
    )
}


@Composable
fun DraggableIcon(iconSize: Dp = 50.dp,navController: NavController) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }


    val density = LocalDensity.current
    val iconSizePx = with(density) { iconSize.toPx() }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()

        // Initialize the starting position to the left of the screen, with half of the icon off-screen
        LaunchedEffect(Unit) {
            offsetX = -iconSizePx / 2 // Start with the icon half off the screen to the left
            offsetY = screenHeight / 2 - iconSizePx / 2
        }

        LaunchedEffect(isDragging) {
            if (!isDragging) {
                // When dragging ends, place the icon at the left or right based on position
                offsetX = when {
                    offsetX < screenWidth / 2 -> -iconSizePx / 2 // Place it off the left side if it's closer
                    else -> screenWidth - iconSizePx // Place it at the right side
                }
            }
        }

        val icon: Painter = painterResource(id = R.drawable.headphone_18080416)

        Box(
            modifier = Modifier

                .offset {
                    IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                }
                .size(iconSize * 1.5f)
                .clip(CircleShape) // Make the box circular
                .background(
                    color = Color.White.copy(alpha = 0.9f)
                    ,
                    shape = RoundedCornerShape(30.dp)
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            // Move icon based on drag and ensure it doesn't go out of screen bounds
                            offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidth - iconSizePx)
                            offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeight - iconSizePx)
                        }
                    )
                }
                .clickable {
                    navController.navigate(Destination.chatbot.route)
                }
            ,
            Alignment.Center
        ) {
            Image(
                painter = icon,
                contentDescription = "Chatbot Icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(iconSize)
                    .padding(8.dp),
                colorFilter = ColorFilter.tint(colorResource(R.color.primary_color)) // Apply a green tint to the icon

            )
        }
    }
}