package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.fromLocationToDestination
import com.example.capital_taxi.R


@Composable
fun TripDetailsBottomSheetContent() {
    Column(
        modifier = Modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.trip_details),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.padding(bottom = 10.dp))

        HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)

        Spacer(modifier = Modifier.weight(1f))

        fromLocationToDestination()

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.share_trip_status),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {},
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(23.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text(
                    stringResource(R.string.share),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.padding(bottom = 10.dp))

        HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth(.9f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray),
            shape = RoundedCornerShape(8.dp)
        ) {
            androidx.compose.material3.Text(
                text = stringResource(R.string.cancel_trip),
                color = Color.Red,
                fontSize = 18.sp,
            )
        }
        Spacer(modifier = Modifier.padding(bottom = 10.dp))
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth(.9f)


                .height(50.dp),
            colors = ButtonDefaults.buttonColors(Color.Black),
            shape = RoundedCornerShape(8.dp)

        ) {
            androidx.compose.material3.Text(
                text = stringResource(R.string.Done_Button),
                color = Color.White,
                fontSize = 18.sp,

                )
        }

        Spacer(modifier = Modifier.padding(bottom = 10.dp))

    }
}


