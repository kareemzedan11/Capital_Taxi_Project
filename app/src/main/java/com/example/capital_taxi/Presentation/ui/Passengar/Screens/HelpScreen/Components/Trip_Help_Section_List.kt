package com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R


@Composable
fun TripHelpSectionList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.Trip_Routes_and_Navigation))
            HelpSection(stringResource(id = R.string.Trip_Scheduling_and_Cancellations))
            HelpSection(stringResource(id = R.string.Trip_Fare_Estimation))
            HelpSection(stringResource(id = R.string.Trip_Issues_and_Disputes))
            HelpSection(stringResource(id = R.string.Tracking_Your_Trip))
            HelpSection(stringResource(id = R.string.Ride_Comfort_and_Preferences))
            HelpSection(stringResource(id = R.string.Safety_During_Trips))
            HelpSection(stringResource(id = R.string.What_to_Do_if_Your_Driver_is_Lost))
        }
    }
}