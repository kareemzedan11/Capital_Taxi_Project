package com.example.capital_taxi.Presentation.Common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R

@Composable
fun SocialLoginButtons(onGoogleSignInClick: () -> Unit, icon: Int) {

    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable {     // Launch the Google sign-in intent
            }
            .background(
                colorResource(R.color.secondary_color),
                shape = RoundedCornerShape(10.dp) // For rounded corners with 8.dp radius

            ), contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape),
            painter = painterResource(icon),
            contentDescription = "Google Logo"
        )
    }

}
