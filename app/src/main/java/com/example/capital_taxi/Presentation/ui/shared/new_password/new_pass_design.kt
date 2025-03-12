package com.example.capital_taxi.Presentation.ui.shared.new_password

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.Presentation.ui.shared.new_password.Components.NewPasswordContent
import com.example.capital_taxi.Presentation.ui.shared.new_password.Components.NewPasswordDialog
import com.example.capital_taxi.Presentation.ui.shared.new_password.Components.NewPasswordTopBar
import com.example.capital_taxi.Presentation.viewmodel.shared.NewPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreen(navController: NavController) {
    // Access the ViewModel
    val viewModel: NewPasswordViewModel = viewModel()

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Pass the string resources to the ViewModel
    val validPasswordMessage = stringResource(R.string.please_enter_valid_password)
    val successPasswordMessage = stringResource(R.string.password_changed_successfully)

    // Set the messages in the ViewModel
    viewModel.setMessages(validPasswordMessage, successPasswordMessage)
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize().background(Color.White),
            topBar = { NewPasswordTopBar(navController) },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                NewPasswordContent(
                    password = viewModel.password,
                    onPasswordChange = { viewModel.onPasswordChange(it) },
                    onSaveClicked = {
                        viewModel.onSaveClicked { message ->
                            // Show snackbar message if password is empty
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    withDismissAction = true
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    if (viewModel.showDialog) {
        NewPasswordDialog(
            dialogMessage = viewModel.dialogMessage,
            onDismiss = { viewModel.showDialog = false },
            onConfirm = {
                viewModel.onDialogConfirm {
                    navController.navigate(Destination.UserLogin.route)
                }
            }
        )
    }
}