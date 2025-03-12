package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R


@Composable
fun TripArrivedCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Header
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.you_arrived),
                fontWeight = FontWeight.Bold,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 19.sp, maxSize = 24.sp),



                fontFamily = CustomFontFamily,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Trip Duration
        Text(
            text = stringResource(R.string.trip_duration),
            fontWeight = FontWeight.Bold,
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 18.sp, maxSize = 24.sp),



            fontFamily = CustomFontFamily,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Pickup and Drop-off Points
        InfoRow(icon = android.R.drawable.ic_menu_mylocation, label = "New Jersey, Delaware 2673")
        InfoRow(
            icon = android.R.drawable.ic_menu_directions,
            label = "Nezer Building, Addibas 3476"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Trip Review Link
        Text(
            text = stringResource(R.string.see_trip_review),
            color = colorResource(R.color.primary_color),
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 12.sp, maxSize = 14.sp),



            fontFamily = CustomFontFamily,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        androidx.compose.material.Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .height(50.dp),
            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.primary_color)
            )
        ) {
            androidx.compose.material.Text(
                stringResource(R.string.end_trip),
                color = Color.Black,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 16.sp),



                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}