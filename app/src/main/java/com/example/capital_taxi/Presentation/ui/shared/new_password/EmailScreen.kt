package com.example.capital_taxi.Presentation.ui.shared.new_password


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.capital_taxi.R
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.shared.new_password.Components.GradientButton
import com.example.capital_taxi.data.source.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailScreen(navController: NavController) {
    var email by remember { mutableStateOf(TextFieldValue()) }
    var isError by remember { mutableStateOf(false) }
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    val viewModel: AuthViewModel = viewModel()
    LaunchedEffect(viewModel.message) {
        if (viewModel.message.isNotEmpty()) {
            navController.navigate(Destination.OTPAndNewPasswordScreen.route)
            // Reset الرسالة عشان ميتنقلش تاني لو رجعت
            viewModel.message = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo
        AppLogo(modifier = Modifier.size(120.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text =  "Enter Email To Reset",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isError = it.text.isNotEmpty() && !emailRegex.matches(it.text)
            },
            label = { Text(stringResource(R.string.email)) },
            placeholder = { Text("example@domain.com") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            trailingIcon = {
                if (email.text.isNotEmpty()) {
                    IconButton(onClick = { email = TextFieldValue() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_close_24),
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        if (isError) {
            Text(
                text = "Invalid Email Format",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = if (viewModel.loading) "Sending..." else "Continue",
            enabled = email.text.isNotEmpty() && !isError && !viewModel.loading,
            onClick = {
                viewModel.sendResetCode(email.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        if (viewModel.error.isNotEmpty()) {
            Text(
                text = viewModel.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (viewModel.message.isNotEmpty()) {
            Text(
                text = viewModel.message,
                color = Color(0xFF4CAF50), // أخضر نجاح
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to Login
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text =  "Back To Login",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}