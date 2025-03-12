package com.example.capital_taxi.Presentation.ui.shared.OTP

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(navController: NavController) {
    var timer by remember { mutableStateOf(30) }
    var otpValues = remember { mutableStateListOf("", "", "", "") }

    val focusRequesters = List(4) { FocusRequester() }
    val localFocusManager = LocalFocusManager.current

    val progress by animateFloatAsState(
        targetValue = timer / 30f,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    // Countdown logic
    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1000L)
            timer--
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.otp_verification_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Transparent)
                                .border(4.dp, color = Color.Black, RoundedCornerShape(30.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(26.dp),
                                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = stringResource(R.string.back),
                                tint = Color.Black
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
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
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 24.sp,
                        maxSize = 32.sp
                    ),
                    fontFamily = CustomFontFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.otp_placeholder),
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 14.sp,
                        maxSize = 18.sp
                    ),
                    fontFamily = CustomFontFamily,
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
                                        if (newValue.length <= 1) {
                                            otpValues[index] = newValue
                                            // Move to next field if input is not empty
                                            if (newValue.isNotEmpty() && index < focusRequesters.lastIndex) {
                                                focusRequesters[index + 1].requestFocus()
                                            } else if (newValue.isEmpty() && index > 0) {
                                                focusRequesters[index - 1].requestFocus()
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(4.dp)
                                        .focusRequester(focusRequesters[index])
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            textDecoration = TextDecoration.Underline,
                            text = if (timer > 0) stringResource(R.string.resend_code_message, timer) else stringResource(R.string.resend_code_now_message),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Linear Progress Indicator
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            color = colorResource(R.color.primary_color)
                        )

                        Spacer(modifier = Modifier.weight(1f))

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
                                fontSize = responsiveTextSize(
                                    fraction = 0.06f,
                                    minSize = 14.sp,
                                    maxSize = 18.sp
                                ),
                                fontFamily = CustomFontFamily,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
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



