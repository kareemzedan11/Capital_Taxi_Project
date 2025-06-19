package com.example.capital_taxi.Presentation.ui.shared.OTP

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.shared.OTP.Components.OtpState
import com.example.capital_taxi.Presentation.ui.shared.OTP.Components.OtpViewModel
import com.example.capital_taxi.R
import kotlinx.coroutines.delay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    navController: NavController,


) {
    val context = LocalContext.current

    var otp by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(60) }
    val viewModel: OtpViewModel = viewModel() // ← هنا نستدعيه داخل Composable
    // Get verificationId and phone number from SharedPreferences
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val phoneNumber = sharedPref.getString("phone_number", "")
    val otpState by viewModel.otpState.collectAsState()


    LaunchedEffect(Unit) {
        while (timer > 0) {
            delay(1000)
            timer--
        }
    }

    LaunchedEffect(otpState) {
        when (otpState) {
            is OtpState.Success -> {
                navController.navigate(Destination.UserHomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

            }
            is OtpState.Error -> {
                val error = (otpState as OtpState.Error).message
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OTP Verification") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter OTP sent to $phoneNumber",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OtpTextField(
                otpText = otp,
                onOtpTextChange = { otp = it },
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = if (timer > 0) "Resend OTP in $timer seconds" else "Resend OTP",
                modifier = Modifier.clickable(enabled = timer == 0) {
                    if (timer == 0) {
                        timer = 60
                    viewModel.sendOtp(phoneNumber!!)
                    }
                },
                color = if (timer == 0) MaterialTheme.colorScheme.primary else Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.verifyOtp(
                        phone = phoneNumber!!,
                        enteredCode = otp,
                        onResult = { success ->
                            if (success) {
                                navController.navigate(Destination.UserHomeScreen.route) {
                                    popUpTo("phone_verification_screen") { inclusive = true }
                                    Toast.makeText(context, "Correct OTP", Toast.LENGTH_SHORT).show()

                                }

                            } else {
                                Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = otp.length ==4 && otpState != OtpState.Loading
            ) {
                if (otpState == OtpState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Verify OTP")
                }
            }

        }
    }
}

@Composable
fun OtpTextField(
    otpText: String,
    onOtpTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = otpText,
        onValueChange = {
            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                onOtpTextChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                width = 1.dp,
                                color = if (index == otpText.length) MaterialTheme.colorScheme.primary
                                else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (index < otpText.length) otpText[index].toString() else "",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun OtpInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorResource(R.color.primary_color), cursorColor = colorResource(R.color.primary_color)),
        value = value,
        onValueChange = { newValue ->
            // Allow only digits and a single character
            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        ),
        modifier = modifier
            .background(Color.White)
            .border(1.dp, Color.Gray)
    )
}














/*
refactor fun

@Composable
fun OtpScreen(navController: NavController, otpViewModel: OtpViewModel = viewModel()) {
    val timer = otpViewModel.timer
    val otpValues = otpViewModel.otpValues
    val progress by animateFloatAsState(
        targetValue = timer / 30f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    // Start the countdown timer on first composition
    LaunchedEffect(key1 = timer) {
        if (timer == 30) otpViewModel.startTimer()
    }

    Scaffold(
        topBar = { OtpTopBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.primary_color)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    painter = painterResource(R.drawable.otp),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = stringResource(R.string.otp_verification_title),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.otp_placeholder),
                    fontSize = 18.sp,
                    color = Color(0XFFF2F2F2),
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight(.6f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 100.dp))
                        .background(Color.White),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Spacer(modifier = Modifier.height(60.dp))

                        // OTP Input Boxes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            otpValues.forEachIndexed { index, value ->
                                OtpInputBox(
                                    value = value,
                                    onValueChange = { newValue ->
                                        otpViewModel.onOtpValueChange(index, newValue)
                                    },
                                    focusRequester = FocusRequester.Default,
                                    modifier = Modifier
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Countdown Timer and Progress Bar
                        CountdownTimer(progress = progress, timer = timer) {}

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { navController.navigate(Destination.SelectTheMode.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.verify_now_button),
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

 */



