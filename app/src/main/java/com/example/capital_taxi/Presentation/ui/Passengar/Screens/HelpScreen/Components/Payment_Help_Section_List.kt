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
fun PaymentHelpSectionList() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(stringResource(id = R.string.How_to_Add_a_Payment_Method))
            HelpSection(stringResource(id = R.string.How_to_Remove_a_Payment_Method))
            HelpSection(stringResource(id = R.string.How_to_Update_Your_Payment_Details))
            HelpSection(stringResource(id = R.string.Checking_Your_Payment_History))
            HelpSection(stringResource(id = R.string.Understanding_Payment_Receipts))
            HelpSection(stringResource(id = R.string.What_to_Do_If_Your_Payment_Fails))
            HelpSection(stringResource(id = R.string.Payment_Refunds_and_Disputes))
        }
    }
}