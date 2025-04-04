package com.example.capital_taxi.Presentation.ui.Driver.Components
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
import com.example.capital_taxi.domain.driver.model.DriverStatusViewModel
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
import kotlinx.coroutines.tasks.await
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
fun DriverControls(
    driverId: String,
    tripLocation: GeoPoint?,
    onClick:()->Unit ,
    modifier: Modifier = Modifier
) {
    val viewModel: DriverStatusViewModel = viewModel()
    val isOnline by viewModel.isOnline
    val isLoading by viewModel.isLoading
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Confirmation Dialog
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Going Offline") },
            text = {
                Text("Are you sure you want to go offline? " +
                        "Note that if you haven't completed your agreed working hours, " +
                        "there may be penalties. Your status will be reviewed by supervisors.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        viewModel.toggleStatus()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirm Offline")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Non-composable function for location updates
    fun updateDriverLocation(driverId: String, location: GeoPoint) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scope.launch(Dispatchers.IO) {
                try {
                    val lastLocation = fusedLocationClient.lastLocation.await()
                    lastLocation?.let { loc ->
                        val database = FirebaseDatabase.getInstance()
                            .getReference("drivers")
                            .child(driverId)

                        val locationMap = mapOf(
                            "latitude" to loc.latitude,
                            "longitude" to loc.longitude
                        )

                        database.child("location").setValue(locationMap)
                            .await()
                        Log.d("RealtimeDB", "Location stored successfully")
                    }
                } catch (e: Exception) {
                    Log.e("RealtimeDB", "Failed to store location", e)
                }
            }
        } else {
            Log.e("Permission", "Location permission not granted")
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(R.drawable.note),
            contentDescription = null,
            tint = colorResource(R.color.Icons_color)
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = colorResource(R.color.primary_color),
                strokeWidth = 3.dp)

        } else {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        if (isOnline) R.color.primary_color
                        else R.color.offline
                    )
                ),
                onClick = {
                    if (isOnline) {
                        showConfirmationDialog = true
                    } else {
                        onClick()
                        viewModel.toggleStatus()
                        tripLocation?.let {
                            updateDriverLocation(driverId, it) // Call non-composable function
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .fillMaxHeight(0.8f)
            ) {
                Text(
                    text = if (isOnline) "Online" else "Offline",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            tint = colorResource(R.color.Icons_color),
            modifier = Modifier.size(26.dp),
            painter = painterResource(R.drawable.tools),
            contentDescription = null
        )
    }
}