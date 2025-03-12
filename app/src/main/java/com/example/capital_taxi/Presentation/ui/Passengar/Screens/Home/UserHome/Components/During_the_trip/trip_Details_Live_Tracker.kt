package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.During_the_trip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver.RoundedTimeDisplayWithFill
import com.example.capital_taxi.R

@Composable
fun TripDetailsLiveTracker() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.the_arrive_after),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            RoundedTimeDisplayWithFill()
        }

        Spacer(modifier = Modifier.padding(20.dp))

        HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth(.9f))
        Spacer(modifier = Modifier.padding(20.dp))


        Row(horizontalArrangement = Arrangement.Center) {
            Text(  stringResource(R.string.your_trip), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))

            Text("12:02 ${stringResource(R.string.Pm)}", fontSize = 18.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.padding(15.dp))
            Text("21:12:2024", fontSize = 18.sp, color = Color.DarkGray)


        }
        Spacer(modifier = Modifier.padding(20.dp))

        HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth(.9f))

        Spacer(modifier = Modifier.padding(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(10.dp)
                )
                .background(
                    color = Color(0XFFECECEC),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(20.dp)
        ) {


            TripInfoRow(
                distance = "2.4 miles",
                duration = "15:02",
                price = "£10.50"
            )


        }
        Spacer(modifier = Modifier.padding(20.dp))

        HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth(.9f))

        Spacer(modifier = Modifier.padding(20.dp))
        CarAndDriverInfoCard(
            carNumber = "KN63 ZZT",
            carBrand = "Volvo",
            carColor = "Silver",
            driverName = "Alex Smith",
            driverRating = 4.8f,
            tripsCount = 148,
            carImage = painterResource(id = R.drawable.uber),
            driverImage = painterResource(id = R.drawable.person)
        )
    }
}