package com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.capital_taxi.R


// Email Field Composable
@Composable
fun EmailField(email: String) {
    OutlinedTextField(
        value = email,
        onValueChange = {},
        label = { Text(stringResource(R.string.Email_label)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = false
    )
}