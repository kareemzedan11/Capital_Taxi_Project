package com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.capital_taxi.R

// Name Field Composable
@Composable
fun NameField(name: String, onNameChange: (String) -> Unit) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text(stringResource(R.string.name_label)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

