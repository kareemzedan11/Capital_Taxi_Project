package com.example.capital_taxi.Presentation.viewmodel.shared

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capital_taxi.R
import kotlinx.coroutines.launch

class NewPasswordViewModel : ViewModel() {
    var password by mutableStateOf("")
    var showDialog by mutableStateOf(false)
    var dialogMessage by mutableStateOf("")
    var snackbarMessage by mutableStateOf("")

    // These will be set when the Composable provides them
    var validPasswordMessage by mutableStateOf("")
    var successPasswordMessage by mutableStateOf("")

    // Handle password change
    fun onPasswordChange(newPassword: String) {
        password = newPassword.trim()
    }

    // Handle save button click
    fun onSaveClicked(onSnackbar: (String) -> Unit) {
        if (password.isNotEmpty()) {
            dialogMessage = successPasswordMessage
            showDialog = true
        } else {
            snackbarMessage = validPasswordMessage
            onSnackbar(snackbarMessage)
        }
    }

    // Handle dialog confirmation (Navigate to login screen)
    fun onDialogConfirm(navigate: () -> Unit) {
        showDialog = false
        navigate()
    }

    // Function to set the strings for validation and success
    fun setMessages(validPassword: String, successPassword: String) {
        validPasswordMessage = validPassword
        successPasswordMessage = successPassword
    }
}
