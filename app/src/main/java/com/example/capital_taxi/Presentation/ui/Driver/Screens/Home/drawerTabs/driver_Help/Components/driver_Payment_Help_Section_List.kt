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
fun driverPaymentHelpSectionList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.How_to_Set_Up_Payment_Methods))
            HelpSection(stringResource(id = R.string.Updating_Bank_Details))
            HelpSection(stringResource(id = R.string.Payment_Cycle_and_Timelines))
            HelpSection(stringResource(id = R.string.Viewing_Earnings_and_Payments))
            HelpSection(stringResource(id = R.string.Handling_Payment_Disputes))
            HelpSection(stringResource(id = R.string.What_to_Do_if_a_Payment_is_Missing))
        }
    }
}
