package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize


@Composable
fun PassengerConnectionCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Connecting to your Passenger",
            fontWeight = FontWeight.Bold,
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),



            fontFamily = CustomFontFamily,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        // Linear Progress Indicator
        LinearProgressIndicator(
            color = Color.Blue,

            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .padding(bottom = 16.dp)
        )
        // Information Rows
        InfoRow(
            icon = android.R.drawable.ic_menu_mylocation,
            label = "Pickup Point",
            value = "New Jersey, Delaware 2673"
        )
        InfoRow(
            icon = android.R.drawable.ic_menu_directions,
            label = "Drop-off point",
            value = "Nezer Building, Addibas 3476"
        )
        InfoRow(
            icon = android.R.drawable.ic_menu_recent_history,
            label = "Drive time",
            value = "20mins"
        )
        InfoRow(icon = android.R.drawable.ic_menu_compass, label = "Distance", value = "24km")
        InfoRow(
            icon = android.R.drawable.ic_menu_myplaces,
            label = "Number of Persons",
            value = "3"
        )
        InfoRow(icon = android.R.drawable.ic_menu_manage, label = "Payment Type", value = "Cash")


    }
}


