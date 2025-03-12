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
fun RideSafetySectionList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.In_Ride_Safety))
            HelpSection(stringResource(id = R.string.Accident_Reporting))
            HelpSection(stringResource(id = R.string.Lost_Items))
            HelpSection(stringResource(id = R.string.Safety_Measures_and_Policies))
            HelpSection(stringResource(id = R.string.Data_Privacy_and_Security))
        }
    }
}