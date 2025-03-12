package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Register

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.capital_taxi.R

import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.capital_taxi.Presentation.Common.Shared_AppBar
import com.example.capital_taxi.Presentation.Common.appBar_background_curve
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Register.Components.UserRegisterContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegister(navController: NavController) {

    val generalColor = colorResource(id = R.color.primary_color)
    Scaffold(
        topBar = {
            Shared_AppBar(navController, generalColor, text = R.string.passenger_Mode)

        }
    ) { innerPadding ->
        // Box to center content and handle keyboard interaction
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopEnd)
            ) {
               appBar_background_curve(generalColor)
                UserRegisterContent(navController)
            }
        }
    }
}
