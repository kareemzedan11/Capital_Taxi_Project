package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R


@Composable
fun callAndChat(navController: NavController) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically, // Center vertically within the Row
        horizontalArrangement = Arrangement.Start // Align content to the start (left side)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.secondary_color)),
            contentAlignment = Alignment.Center // Center the icon within the Box
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Call,
                contentDescription = "call",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.secondary_color))
                .clickable { navController.navigate(Destination.ChatScreen.route) },
            contentAlignment = Alignment.Center // Center the icon within the Box
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.messageicon),
                contentDescription = "message icon"
            )
        }
    }
}