package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R


@Composable
fun driverDetails(driverusername:String) {
    Row (modifier = Modifier.padding(horizontal = 10.dp)){
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(R.drawable.person),
            contentDescription = "person Icon"
        )

        Column {

            Row {
                Spacer(modifier = Modifier.padding(5.dp))

                Text(
                    driverusername,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black.copy(alpha = .3f)
                )
                Spacer(modifier = Modifier.padding(5.dp))

                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)

            }
            Row {
                Spacer(modifier = Modifier.padding(5.dp))
                Icon(imageVector = Icons.Default.Star, contentDescription = null)


                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    "4.9",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black.copy(alpha = .3f)
                )
                Spacer(modifier = Modifier.padding(6.dp))
                Text(
                    "+1 ${stringResource(R.string.Trips)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black.copy(alpha = .3f)
                )


            }

        }


    }
}
