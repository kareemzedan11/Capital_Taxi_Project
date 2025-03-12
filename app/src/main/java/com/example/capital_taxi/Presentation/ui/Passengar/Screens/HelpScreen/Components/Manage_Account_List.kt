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
fun ManageAccountList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.Updating_Personal_Information))
            HelpSection(stringResource(id = R.string.Password_Management))
            HelpSection(stringResource(id = R.string.Notification_Preferences))
            HelpSection(stringResource(id = R.string.Linked_Accounts))
            HelpSection(stringResource(id = R.string.Language_and_Region_Settings))
            HelpSection(stringResource(id = R.string.Account_Deactivation_or_Deletion))
        }
    }
}
