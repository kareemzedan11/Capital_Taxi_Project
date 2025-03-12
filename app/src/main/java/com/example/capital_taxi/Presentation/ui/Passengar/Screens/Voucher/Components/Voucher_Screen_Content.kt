package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Voucher.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.capital_taxi.R

@Composable
fun VoucherScreenContent(navController: NavController) {
    var voucherCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    val headerBackgroundColor = Color(ContextCompat.getColor(context, R.color.primary_color))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.primary_color)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(
            backgroundColor = headerBackgroundColor,
            onClose = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        PromoCodeInputField(
            value = voucherCode,
            onValueChange = { voucherCode = it }
        )

        Spacer(modifier = Modifier.height(40.dp))

        VoucherMessageSection()
    }
}
