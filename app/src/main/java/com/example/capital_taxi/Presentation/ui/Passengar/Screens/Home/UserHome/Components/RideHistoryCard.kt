package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.getUserTrips
import com.example.capital_taxi.domain.parseLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RideHistoryCard(
    origin: String,
    destination: String,
    date: String,
    price: String,
    stop: String? = null
) {
    val originLocation = parseLocation(origin)
    val destinationLocation = parseLocation(destination)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(13.dp),
        elevation = 4.dp,
        backgroundColor = colorResource(R.color.secondary_color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${stringResource(R.string.From)} :", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = originLocation?.let { "Lat: ${it.lat}, Lng: ${it.lng}" } ?: "Unknown",
                    style = MaterialTheme.typography.body2
                )

                Spacer(modifier = Modifier.weight(1f))
                Text(text = date, style = MaterialTheme.typography.body2)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                stop?.let {
                    Text(text = "${stringResource(R.string.Stop)} 1 :", style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stop, style = MaterialTheme.typography.body2)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${stringResource(R.string.To)} :", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = destinationLocation?.let { "Lat: ${it.lat}, Lng: ${it.lng}" } ?: "Unknown",
                    style = MaterialTheme.typography.body2
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${stringResource(R.string.Price)}: $price EGP",
                style = MaterialTheme.typography.h6,
                color = colorResource(R.color.primary_color)
            )
        }
    }
}

@Composable
fun RideHistoryList(token: String) {
    var trips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = token) {
        coroutineScope.launch {
            try {
                // Print the token for debugging
                Log.d("RideHistoryList", "Token: $token")

                // Fetch trips from the server
                Log.d("RideHistoryList", "Fetching trips...")
                getUserTrips(token, coroutineScope,
                    onSuccess = { tripList ->
                        // Print the number of trips fetched
                        Log.d("RideHistoryList", "Trips fetched successfully: ${tripList.size}")

                        // Print each trip's details
                        tripList.forEach { trip ->
                            Log.d("RideHistoryList", "Trip: ${trip.user}, From: ${trip.origin}, To: ${trip.destination}, Fare: ${trip.fare}")
                        }

                        // Update state with trips
                        trips = tripList
                        isLoading = false
                    },
                    onError = { errorMessage ->
                        // Print the error message
                        Log.e("RideHistoryList", "Error fetching trips: $errorMessage")

                        // Update state
                        error = errorMessage
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                // Print the exception message
                Log.e("RideHistoryList", "Exception: ${e.message}")

                // Update state
                error = "An error occurred: ${e.message}"
                isLoading = false
            }
        }
    }

    if (isLoading) {
        // Show a loading indicator
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Loading...")
        }
    } else if (error != null) {
        // Show an error message
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: $error", color = Color.Red)
        }
    } else if (trips.isEmpty()) {
        // Show a message if no trips are available
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No trips found")
        }
    } else {
        LazyColumn {
            items(trips) { trip ->
                RideHistoryCard(
                    origin = trip.origin,
                    destination = trip.destination,
                    date = trip.createdAt,
                    price = trip.fare.toString(),
                    stop = trip.driver?.name
                )
            }
        }
    }
}