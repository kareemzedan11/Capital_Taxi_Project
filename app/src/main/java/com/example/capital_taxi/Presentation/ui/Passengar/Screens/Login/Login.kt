package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Login


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController


import androidx.compose.ui.res.colorResource
import com.example.capital_taxi.Presentation.Common.Shared_AppBar
import com.example.capital_taxi.Presentation.Common.appBar_background_curve

import com.example.capital_taxi.R
import userLoginContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLogin(navController: NavController) {
    val generalColor = colorResource(id = R.color.primary_color)


    Scaffold(
        topBar = {
            Shared_AppBar(navController, generalColor, text = R.string.passenger_Mode)
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    appBar_background_curve(generalColor)
                }
                userLoginContent(navController)

            }
        }
    }
}
