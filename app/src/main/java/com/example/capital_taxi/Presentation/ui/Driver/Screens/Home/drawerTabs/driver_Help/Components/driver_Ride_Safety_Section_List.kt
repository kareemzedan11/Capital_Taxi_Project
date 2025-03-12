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
fun driverRideSafetySectionList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.Safety_Tips_for_Drivers))
            HelpSection(stringResource(id = R.string.Reporting_Incidents))
            HelpSection(stringResource(id = R.string.Lost_and_Found_Procedures))
            HelpSection(stringResource(id = R.string.Vehicle_Safety_Requirements))
            HelpSection(stringResource(id = R.string.Data_Privacy_and_Security))
        }
    }
}