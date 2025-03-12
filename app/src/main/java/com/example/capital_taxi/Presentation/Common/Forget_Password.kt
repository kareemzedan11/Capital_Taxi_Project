package com.example.capital_taxi.Presentation.Common

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R

@Composable
fun ForgetPassword(navController: NavController) {
    Text(
        textDecoration = TextDecoration.Underline,
        text = stringResource(R.string.ForgetPassword),
        modifier = Modifier.Companion

            .clickable { navController.navigate(Destination.NewPasswordScreen.route) },
        color = colorResource(R.color.primary_color),

        fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),

        fontFamily = CustomFontFamily,

        )
}