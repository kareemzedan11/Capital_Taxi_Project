package com.example.capital_taxi.Presentation.Common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R

@Composable
fun userMediaLoginOption() {

    // Social Media Login Options
    Row {

        SocialLoginButtons(onGoogleSignInClick = {}, icon = R.drawable.googleicon)

        Spacer(Modifier.width(30.dp))



        SocialLoginButtons(onGoogleSignInClick = {}, icon = R.drawable.xicon)

        Spacer(Modifier.width(30.dp))




        SocialLoginButtons(onGoogleSignInClick = {}, icon = R.drawable.facelogo)


        Spacer(modifier = Modifier.height(10.dp))

    }
}