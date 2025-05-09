package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import ChatScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallAndChat(navController: NavController,
                chatId:String,userId:String) {
    val context = LocalContext.current


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

        // Call Icon
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.secondary_color)),
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
