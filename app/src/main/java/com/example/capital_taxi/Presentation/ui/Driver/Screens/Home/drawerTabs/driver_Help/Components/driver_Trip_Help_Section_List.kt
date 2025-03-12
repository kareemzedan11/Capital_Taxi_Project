package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.driver_Help.Components

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
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.Components.HelpSection
import com.example.capital_taxi.R


@Composable
fun driverTripHelpSectionList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.Accepting_and_Declining_Trips))
            HelpSection(stringResource(id = R.string.How_to_Navigate_to_a_Passenger))
            HelpSection(stringResource(id = R.string.Managing_Scheduled_Trips))
            HelpSection(stringResource(id = R.string.Dealing_with_Fare_Disputes))
            HelpSection(stringResource(id = R.string.Handling_Cancellations))
            HelpSection(stringResource(id = R.string.Tips_for_Ensuring_Trip_Quality))
            HelpSection(stringResource(id = R.string.Safety_Tips_During_Trips))
        }
    }
}
