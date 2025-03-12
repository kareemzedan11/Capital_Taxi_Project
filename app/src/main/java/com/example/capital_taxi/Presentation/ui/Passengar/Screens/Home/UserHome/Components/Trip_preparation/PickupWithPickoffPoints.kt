@file:Suppress("NAME_SHADOWING")

package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.capital_taxi.Helper.PermissionViewModel
import com.example.capital_taxi.Helper.checkLocationPermission
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationAutocompleteField
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.fetchLocationCoordinates
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.fetchPlaceSuggestions
import com.example.capital_taxi.R
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint
import java.util.Locale

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupWithPickoffPoints(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var pickupLocation by remember { mutableStateOf("Fetching location...") }
    var destinationPoint by remember { mutableStateOf("") }
    var stopPoints by remember { mutableStateOf(listOf<String>()) }

    var pickupSuggestions by remember { mutableStateOf(emptyList<String>()) }
    var stopPointSuggestions by remember { mutableStateOf(emptyList<String>()) }
    var pickoffSuggestions by remember { mutableStateOf(emptyList<String>()) }
    var startGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var endGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }

    // Fetch current location
    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                pickupLocation = if (addressList != null && addressList.isNotEmpty()) {
                    addressList[0].getAddressLine(0)
                } else {
                    "Unable to fetch location"
                }
            } ?: run {
                pickupLocation = "Unable to fetch location"
            }
        }
    }

    Box(
        modifier = Modifier
            .background(color = colorResource(R.color.secondary_color), shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 🔹 Pickup Point Field
            LocationAutocompleteField(
                icon = { Icon(imageVector = Icons.Default.Place, contentDescription = "Pickup Point") },
                onClick = {},
                hint = stringResource(R.string.Pickup_Point),
                initialText = pickupLocation,
                query = pickupLocation,
                onQueryChanged = { text ->
                    pickupLocation = text
                    if (text.isNotEmpty()) {
                        fetchPlaceSuggestions(text) { result -> pickupSuggestions = result }
                    } else {
                        pickupSuggestions = emptyList()
                    }
                },
                onLocationSelected = { selected ->
                    pickupLocation = selected
                    pickupSuggestions = emptyList()
                },
                apiKey = stringResource(id = R.string.SignUp)
            )

            // 🔹 Suggestions List for Pickup
            SuggestionList(pickupSuggestions) { selected ->
                pickupLocation = selected
                pickupSuggestions = emptyList()
            }

            // 🔹 Stop Points Fields
            stopPoints.forEachIndexed { index, stopPoint ->
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

                LocationAutocompleteField(
                    icon = { Icon(imageVector = Icons.Default.Place, contentDescription = "Stop Point") },
                    onClick = {},
                    hint = "Stop Point ${index + 1}",
                    initialText = stopPoint,
                    query = stopPoint,
                    onQueryChanged = { text ->
                        stopPoints = stopPoints.toMutableList().apply { set(index, text) }
                        if (text.isNotEmpty()) {
                            fetchPlaceSuggestions(text) { result -> stopPointSuggestions = result }
                        } else {
                            stopPointSuggestions = emptyList()
                        }
                    },
                    onLocationSelected = { selected ->
                        stopPoints = stopPoints.toMutableList().apply { set(index, selected) }
                        stopPointSuggestions = emptyList()
                    },
                    apiKey = stringResource(id = R.string.SignUp)
                )

                // 🔹 Suggestions List for Stop Point
                SuggestionList(stopPointSuggestions) { selected ->
                    stopPoints = stopPoints.toMutableList().apply { set(index, selected) }
                    stopPointSuggestions = emptyList()
                }
            }

            // 🔹 Pickoff Point Field
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

            LocationAutocompleteField(
                icon = { Icon(imageVector = Icons.Default.Place, contentDescription = "Pickoff Point") },
                onClick = {},
                initialText = stringResource(R.string.PickOff_Point),
                hint = stringResource(R.string.PickOff_Point),
                query = destinationPoint,
                onQueryChanged = { text ->
                    destinationPoint = text
                    if (text.isNotEmpty()) {
                        fetchPlaceSuggestions(text) { result -> pickoffSuggestions = result }
                    } else {
                        pickoffSuggestions = emptyList()
                    }
                },
                onLocationSelected = { selected ->
                    destinationPoint = selected
                    pickoffSuggestions = emptyList()
                },
                apiKey = stringResource(id = R.string.SignUp)
            )

            // 🔹 Suggestions List for Pickoff
            SuggestionList(pickupSuggestions) { selected ->
                pickupLocation = selected
                pickupSuggestions = emptyList()

                fetchLocationCoordinates(selected) { lat, lon ->
                    startGeoPoint = GeoPoint(lat, lon)
                }
            }

            SuggestionList(pickoffSuggestions) { selected ->
                destinationPoint = selected
                pickoffSuggestions = emptyList()

                fetchLocationCoordinates(selected) { lat, lon ->
                    endGeoPoint = GeoPoint(lat, lon)
                }
            }

        }
    }
}

// 🔹 Reusable Suggestion List Component
@Composable
fun SuggestionList(suggestions: List<String>, onSelect: (String) -> Unit) {
    if (suggestions.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
        ) {
            items(suggestions) { suggestion ->
                Text(
                    text = suggestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(suggestion) }
                        .padding(10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Divider()
            }
        }
    }
}
