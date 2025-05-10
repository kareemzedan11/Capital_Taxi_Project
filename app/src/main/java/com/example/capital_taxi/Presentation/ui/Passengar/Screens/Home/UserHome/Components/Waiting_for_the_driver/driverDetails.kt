package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DriverViewModel
import com.example.capital_taxi.domain.DriverViewModelFactory
import com.example.capital_taxi.domain.RetrofitClient.apiService
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun driverDetails(
    driverusername: String,
    rating: String,
    trips: String,
    driverId:String

) {
    var imageUrl by remember { mutableStateOf<String?>(null) }

    // استخدم DisposableEffect مع addSnapshotListener
    DisposableEffect(driverId) {
        val listener = FirebaseFirestore.getInstance()
            .collection("drivers")
            .whereEqualTo("id", driverId)
            .limit(1)
            .addSnapshotListener { snapshot, _ ->
                val document = snapshot?.documents?.firstOrNull()
                imageUrl = document?.getString("imageUrl")
            }

        onDispose {
            listener.remove()
        }
    }

    Row(modifier = Modifier.padding(horizontal = 10.dp)) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Driver Profile Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                placeholder = painterResource(R.drawable.person),
                error = painterResource(R.drawable.person)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.person),
                contentDescription = "Default Profile",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }

        Column {
            Row {
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    driverusername,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black.copy(alpha = .3f)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }

            Row {
                Spacer(modifier = Modifier.padding(5.dp))
                Icon(imageVector = Icons.Default.Star, contentDescription = null)
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    rating.take(3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black.copy(alpha = .3f)
                )
                Spacer(modifier = Modifier.padding(6.dp))
                Text(
                    "+$trips ${stringResource(R.string.Trips)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black.copy(alpha = .3f)
                )
            }
        }
    }
}
