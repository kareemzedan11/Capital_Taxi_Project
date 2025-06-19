package com.example.capital_taxi.Presentation.ui.shared.new_password

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.shared.new_password.Components.GradientButton
import com.example.capital_taxi.Presentation.ui.shared.new_password.Components.PasswordStrengthIndicator
import com.example.capital_taxi.R
import com.example.capital_taxi.data.source.AuthViewModel
import kotlinx.coroutines.delay
@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo), // تأكد من وجود هذه الصورة في مجلد drawable
        contentDescription = "App Logo",
        modifier = modifier.size(120.dp)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPAndNewPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(60) }
    var isEmailVerified by remember { mutableStateOf(false) }
    val viewModel: AuthViewModel = viewModel()
    LaunchedEffect(viewModel.message) {
        if (viewModel.message.lowercase().contains("password reset")) {
            navController.navigate(Destination.UserLogin.route) {
                popUpTo(Destination.UserLogin.route) { inclusive = true }
            }
            viewModel.message = "" // Reset عشان مينقلش تاني
        }
    }

    // Countdown timer
    LaunchedEffect(key1 = countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
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

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = stringResource(if (isEmailVerified) R.string.create_new_password else R.string.verify_email),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = if (isEmailVerified)
                "enter new password below"
            else
           "enter email to get otp",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email Field (visible always)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            placeholder = { Text("example@domain.com") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // OTP Section (visible if email is not verified)
        if (!isEmailVerified) {
            Spacer(modifier = Modifier.height(16.dp))

            // OTP Fields
            OtpTextField(
                otpText = otp,
                onOtpTextChange = { value, _ -> otp = value },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Resend OTP
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "didn't receive code",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.width(4.dp))

                TextButton(
                    onClick = {
                        if (countdown == 0) {
                            countdown = 60
                            // Resend OTP logic here
                        }
                    },
                    enabled = countdown == 0
                ) {
                    Text(
                        text = if (countdown > 0)
                            stringResource(R.string.resend_in, countdown)
                        else
                         "resend",
                        color = if (countdown == 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }

        // Password Section (visible if email is verified)
        if (isEmailVerified) {
            Spacer(modifier = Modifier.height(16.dp))

            // New Password Field
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("new password") },
                placeholder = { Text("enter new password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Password Strength Indicator
            PasswordStrengthIndicator(
                password = newPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(R.string.confirm_password)) },
                placeholder = { Text("re enter password") },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = isError && confirmPassword.isNotEmpty(),
                trailingIcon = {

                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (isError && confirmPassword.isNotEmpty()) {
                Text(
                    text = "passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }
        }
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
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Button
        GradientButton(
            text = if (isEmailVerified) "Save Password" else "Verify Email",
            enabled = if (isEmailVerified)
                newPassword.isNotEmpty() && confirmPassword.isNotEmpty()
            else
                email.isNotEmpty() && otp.length == 6,
            onClick = {
                if (isEmailVerified) {
                    isError = newPassword != confirmPassword
                    if (!isError) {
                        viewModel.resetPassword(email, otp, newPassword)
                    }
                } else {
                    // في الحالة الحقيقية هتتأكد من الـ OTP من السيرفر
                    isEmailVerified = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 24.dp)
        )

    }
}

// OtpTextField.kt
@Composable
fun OtpTextField(
    otpText: String,
    onOtpTextChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = otpText,
        onValueChange = { newValue ->
            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                onOtpTextChange(newValue, newValue.length == 6)
            }
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val char = when {
                        index >= otpText.length -> ""
                        else -> otpText[index].toString()
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                width = 1.dp,
                                color = if (index == otpText.length)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    )
}


