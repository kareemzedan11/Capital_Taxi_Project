package com.example.capital_taxi.Presentation.ui.shared.Start.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun StartButtonDesign(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize(), Alignment.Center
    ) {
        var targetToken by remember { mutableStateOf("") }

        targetToken="doyABrnzSoymXqEDA3l5tA:APA91bFp01fc-6v6-YFHjPWTy4McKNdQxRhCmQLfCJEvTLwAkWwaCUsgsdqAlu7LFr_VB4VnZ1uLKP2nmWUvGfPzOdwj7STMcUMKwPKYAGZGTHAI6gchqX8"
        TextButton(
            onClick = {

                navController.navigate(Destination.OnboardingPager.route) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth()

                .height(60.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSurface),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,

                ) {
                Text(
                    stringResource(R.string.get_started_button),
                    fontWeight = FontWeight.Bold,
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 14.sp,
                        maxSize = 18.sp
                    ),

                    fontFamily = CustomFontFamily
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(

                    contentDescription = null,
                    painter = painterResource(R.drawable.baseline_arrow_right_alt_24)
                )
            }
        }
    }
}