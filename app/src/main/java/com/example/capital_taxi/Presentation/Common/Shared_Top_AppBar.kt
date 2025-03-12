package com.example.capital_taxi.Presentation.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.responsiveTextSize


import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import com.example.capital_taxi.R
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Shared_AppBar(
    navController: NavController,
    generalColor: Color,
    text:Int,

) {
    val layoutDirection = LocalLayoutDirection.current
    val icon = if (layoutDirection == LayoutDirection.Rtl) {
        R.drawable.baseline_arrow_forward_ios_24 // أيقونة معكوسة للغة العربية
    } else {
        R.drawable.baseline_arrow_back_ios_new_24 // أيقونة اللغة الإنجليزية
    }
    TopAppBar(

        title = {
            Text(
                text = stringResource(text),
                fontWeight = FontWeight.Bold,
                fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 18.sp,
                    maxSize = 20.sp
                ),
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
                        painter = painterResource(id =icon),
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = generalColor)
    )
}
@Composable
fun BackIcon() {
    val layoutDirection = LocalLayoutDirection.current
    val icon = if (layoutDirection == LayoutDirection.Rtl) {
        R.drawable. baseline_arrow_forward_ios_24 // أيقونة RTL
    } else {
        R.drawable.baseline_arrow_back_ios_new_24 // أيقونة LTR
    }

    Icon(
        painter = painterResource(id = icon),
        contentDescription = "Back Icon"
    )
}
