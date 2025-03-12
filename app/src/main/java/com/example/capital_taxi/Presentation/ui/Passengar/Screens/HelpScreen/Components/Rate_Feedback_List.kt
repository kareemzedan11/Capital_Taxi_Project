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
fun RateFeedbackList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.How_to_Rate_Your_Ride))
            HelpSection(stringResource(id = R.string.Leaving_Comments))
            HelpSection(stringResource(id = R.string.Driver_Compliments))
            HelpSection(stringResource(id = R.string.Reporting_Issues))
            HelpSection(stringResource(id = R.string.Anonymous_Feedback))
            HelpSection(stringResource(id = R.string.Customer_Support_Follow_Up))
        }
    }
}