package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import ChatScreen
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallAndChat(navController: NavController,
                chatId:String,userId:String
                ,tripid:String
) {
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        fetchDriverPhoneFromTrip(tripid) { phone ->
            phoneNumber = phone
        }
    }

    val isDriver = false
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,

        )

    var showChatSheet by remember { mutableStateOf(false) }

    if (showChatSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),

            onDismissRequest = { showChatSheet = false },
            sheetState = sheetState
        ) {
            ChatScreen(
                navController = navController,
                rideId = chatId,
                currentUserType ="passenger"
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.secondary_color))
                .clickable(enabled = phoneNumber != null) {
                    phoneNumber?.let {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$it")
                        }
                        context.startActivity(intent)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Call,
                contentDescription = "call",
                tint = Color.Black
            )
        }


        Spacer(modifier = Modifier.width(20.dp))

        // Chat Icon
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.secondary_color))
                .clickable {
                    showChatSheet = true
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.messageicon),
                contentDescription = "message icon"
            )
        }
    }
}


val db = FirebaseFirestore.getInstance()

fun fetchDriverPhoneFromTrip(tripId: String, onResult: (String?) -> Unit) {
    db.collection("trips")
        .whereEqualTo("_id", tripId)
        .get()
        .addOnSuccessListener { tripSnapshot ->
            val tripDoc = tripSnapshot.documents.firstOrNull()
            if (tripDoc != null) {
                val driverId = tripDoc.getString("driver")
                if (driverId != null) {
                    db.collection("drivers")
                        .whereEqualTo("id", driverId)
                        .get()
                        .addOnSuccessListener { driverSnapshot ->
                            val driverDoc = driverSnapshot.documents.firstOrNull()
                            if (driverDoc != null) {
                                val phone = driverDoc.getString("phone")
                                onResult(phone)
                            } else {
                                onResult(null)
                            }
                        }
                        .addOnFailureListener { onResult(null) }
                } else {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener { onResult(null) }
}
