package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.driver_Help.Components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.Components.HelpSection
import com.example.capital_taxi.R

@Composable
fun driverManageAccountList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.Updating_Profile_and_Information))
            HelpSection(stringResource(id = R.string.Bank_Account_Management))
            HelpSection(stringResource(id = R.string.Notification_Settings))
            HelpSection(stringResource(id = R.string.Document_Upload_and_Verification))
            HelpSection(stringResource(id = R.string.Language_and_Region_Settings))
            HelpSection(stringResource(id = R.string.Deactivating_or_Deleting_Account))
        }
    }
}



