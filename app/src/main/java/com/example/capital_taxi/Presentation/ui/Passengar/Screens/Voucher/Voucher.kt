package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Voucher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Voucher.Components.HeaderSection
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Voucher.Components.PromoCodeInputField
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Voucher.Components.VoucherMessageSection
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Voucher.Components.VoucherScreenContent
import com.example.capital_taxi.R

@Composable
fun voucherScreen(navController: NavController) {
    VoucherScreenContent(navController = navController)
}

