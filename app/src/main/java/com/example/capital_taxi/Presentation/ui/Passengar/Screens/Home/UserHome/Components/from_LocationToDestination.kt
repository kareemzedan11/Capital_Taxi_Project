package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R

@Composable
fun FromLocationToDestination(
    origin: String,
    destination: String,
    onEditOrigin: () -> Unit = {},
    onEditDestination: () -> Unit = {},
    onAddStop: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Spacer(modifier = Modifier.padding(top = 10.dp))

        // Row for Origin
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(R.drawable.locationicon),
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(10.dp))

            Text(origin, fontSize = 20.sp, fontWeight = FontWeight.W600)

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(Color.LightGray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.pen),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
        }

        repeat(6) {
            VerticalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .height(5.dp)
                    .padding(start = 10.dp)
            )
        }

        // Row for Destination
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(R.drawable.travel),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.padding(10.dp))

            Text(destination, fontSize = 20.sp, fontWeight = FontWeight.W600)

            Spacer(modifier = Modifier.weight(1f))

            Row {
                // Edit Destination Button
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(Color.LightGray, shape = CircleShape)
                        .clickable(onClick = onEditDestination),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.pen),
                        contentDescription = "Edit Destination",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                // Add Stop Button
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(Color.LightGray, shape = CircleShape)
                        .clickable(onClick = onAddStop),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Stop",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(bottom = 10.dp))
        HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
    }
}