package com.example.capital_taxi.Presentation.ui.shared.new_password.Components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.capital_taxi.R

@Composable
fun NewPasswordDialog(dialogMessage: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.ok_button))
            }
        },
        title = { Text(stringResource(R.string.dialog_title)) },
        text = { Text(dialogMessage) }
    )
}