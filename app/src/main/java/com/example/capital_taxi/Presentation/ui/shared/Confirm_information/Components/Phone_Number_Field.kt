package com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.Presentation.ui.shared.Phone_Verification_Screen.Components.CountryCodePickerView
import com.example.capital_taxi.R

// Phone Number Field Composable
@Composable
fun PhoneNumberField(phoneNumber: String, onPhoneNumberChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = .2f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountryCodePickerView(onCountrySelected = { /* Handle country code selection */ })
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text(stringResource(R.string.phone_number_label)) },
            placeholder = { Text("Enter your phone number") },
            modifier = Modifier
                .weight(1f)
                .background(Color.White.copy(alpha = .2f)),
            singleLine = true
        )
    }
}

